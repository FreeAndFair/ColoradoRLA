/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Jul 19, 2017
 * @copyright 2017 Free & Fair
 * @license GNU General Public License 3.0
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.jetty.http.HttpStatus;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import spark.Request;
import spark.Response;
import spark.Service;

import us.freeandfair.corla.endpoint.ACVRDownload;
import us.freeandfair.corla.endpoint.ACVRDownloadByCounty;
import us.freeandfair.corla.endpoint.ACVRUpload;
import us.freeandfair.corla.endpoint.BallotManifestDownload;
import us.freeandfair.corla.endpoint.BallotManifestDownloadByCounty;
import us.freeandfair.corla.endpoint.BallotManifestUpload;
import us.freeandfair.corla.endpoint.BallotStyleDownload;
import us.freeandfair.corla.endpoint.BallotStyleDownloadByCounty;
import us.freeandfair.corla.endpoint.BallotStyleDownloadByID;
import us.freeandfair.corla.endpoint.CVRDownload;
import us.freeandfair.corla.endpoint.CVRDownloadByCounty;
import us.freeandfair.corla.endpoint.CVRDownloadByID;
import us.freeandfair.corla.endpoint.CVRExportUpload;
import us.freeandfair.corla.endpoint.ContestDownload;
import us.freeandfair.corla.endpoint.ContestDownloadByCounty;
import us.freeandfair.corla.endpoint.ContestDownloadByID;
import us.freeandfair.corla.endpoint.Endpoint;
import us.freeandfair.corla.endpoint.Root;
import us.freeandfair.corla.gson.FreeAndFairNamingStrategy;
import us.freeandfair.corla.hibernate.Persistence;

/**
 * The main executable for the ColoradoRLA server. 
 * 
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 0.0.1
 */
// the endpoints are excessive imports, but this may be dealt with differently
// later, as for example by making a list of classes somewhere and instantiating
// the endpoints dynamically
@SuppressWarnings("PMD.ExcessiveImports")
public final class Main {
  /**
   * The name of the default properties resource.
   */
  public static final String DEFAULT_PROPERTIES =
    "us/freeandfair/corla/default.properties";

  /**
   * The name of the logger.
   */
  public static final String LOGGER_NAME = "corla";

  /**
   * The default HTTP port number (can be overridden by properties).
   */
  public static final int DEFAULT_HTTP_PORT = 8888;
  
  /**
   * The default HTTPS port number (can be overridden by properties).
   */
  public static final int DEFAULT_HTTPS_PORT = 8889;
  
  /**
   * The minimum valid port number.
   */
  public static final int MIN_PORT = 1024;
      
  /** 
   * The maximum valid port number.
   */
  public static final int MAX_PORT = 65535;
  
  /**
   * The logger.
   */
  public static final Logger LOGGER = LogManager.getLogger(LOGGER_NAME);
 
  /**
   * The Gson object to use for translation to and from JSON; since 
   * Gson is thread-safe, we only need one for the system. Note that
   * any custom Gson serializers/deserializers we use must also be
   * thread-safe.
   */
  // @review kiniry Should we configure Gson to serialize nulls via
  // serializeNulls() as well?  This will, of course, cost more in
  // bandwidth, but the tradeoff is completeness and clarity of wire
  // format. Perhaps we should just bandwidth and performance
  // benchmark with and without serializeNulls() and
  // setPrettyPrinting()?
  public static final Gson GSON = 
      new GsonBuilder().
      setFieldNamingStrategy(new FreeAndFairNamingStrategy()).
      setPrettyPrinting().create();
  
  /**
   * The "no spark" constant.
   */
  private static final Service NO_SPARK = null;
 
  /**
   * The properties loaded from the properties file.
   */
  private final Properties my_properties;
  
  /**
   * Our endpoints.
   */
  private final List<Endpoint> my_endpoints = new ArrayList<Endpoint>();
  
  /**
   * Our Spark service.
   */
  private Service my_spark = NO_SPARK;
  
  // Constructors

  /**
   * Constructs a new ColoradoRLA server with the specified properties.
   * 
   * @param the_properties The properties.
   */
  public Main(final Properties the_properties) {
    my_properties = the_properties;
  }
  
  // Instance Methods
  
  /**
   * Parse a port number from properties.
   * 
   * @param the_property The name of the property.
   * @param the_default The default port number.
   */
  private int parsePortNumber(final String the_property, final int the_default) {
    int result = the_default;
    
    try {
      final int prop_port =
          Integer.parseInt(my_properties.getProperty(the_property, 
                                                     String.valueOf(the_default)));
      if (MIN_PORT <= prop_port && prop_port < MAX_PORT) {
        result = prop_port;
      } else {
        LOGGER.info("invalid port number in property " + the_property + 
                    ", using default " + the_default);
      }
    } catch (final NumberFormatException e) {
      LOGGER.info("could not read port number property " + the_property + 
                  ", using default " + the_default);
    }
    
    return result;
  }
  
  /**
   * Redirect a request from HTTP to HTTPS.
   * 
   * @param the_request The request.
   * @param the_response The response.
   * @param the_port The HTTPS port.
   */
  private void httpsRedirect(final Request the_request, final Response the_response,
                             final int the_port) {
    try {
      final URL request_url = new URL(the_request.url());
      final URL redirect_url = new URL("https", request_url.getHost(), 
                                       the_port, request_url.getFile());
      the_response.redirect(redirect_url.toString());
    } catch (final MalformedURLException e) {
      // this should probably never happen, since we're getting the original
      // URL from a legitimate request
      the_response.status(HttpStatus.BAD_REQUEST_400);
    }
  }
  
  /**
   * Activate the endpoints.
   */
  private void activateEndpoints() {
    for (final Endpoint e : my_endpoints) {
      switch (e.endpointType()) {
        case GET:
          my_spark.get(e.endpointName(), (the_request, the_response) -> 
                       e.endpoint(the_request, the_response));
          break;
         
        case PUT:
          my_spark.put(e.endpointName(), (the_request, the_response) ->
                       e.endpoint(the_request, the_response));
          break;
          
        case POST:
          my_spark.post(e.endpointName(), (the_request, the_response) ->
                        e.endpoint(the_request, the_response));
          break;
          
        default:
      }
    }
  }
  
  /**
   * Starts a ColoradoRLA server.
   */
  public void start() {
    LOGGER.info("starting server with properties: " + my_properties);

    // provide properties to the persistence engine
    Persistence.setProperties(my_properties);

    // get the port numbers from properties
    final int http_port = parsePortNumber("http_port", DEFAULT_HTTP_PORT);
    final int https_port = parsePortNumber("https_port", DEFAULT_HTTPS_PORT);
    
    // get key store information from properties, if applicable
    String keystore_path = my_properties.getProperty("keystore", null);
    if (keystore_path != null && !(new File(keystore_path).exists())) {
      // the keystore property isn't an absolute or relative pathname that exists, so
      // let's try to load it as a resource
      final URL keystore_url = Main.class.getResource(keystore_path);
      if (keystore_url != null) {
        try {
          keystore_path = Paths.get(keystore_url.toURI()).toString();
        } catch (final URISyntaxException e) {
          // keystore_path stays null
        }
      }
    }

    // if we have a keystore, everything is on SSL except the redirect; otherwise,
    // everything is in plaintext
    
    my_spark = Service.ignite();
    if (keystore_path == null) {
      my_spark.port(http_port);
    } else {
      my_spark.port(https_port);
      final String keystore_password = 
          my_properties.getProperty("keystore_password", null);
      my_spark.secure(keystore_path, keystore_password, null, null);

      // redirect everything
      final Service redirect = Service.ignite();
      redirect.port(http_port);
      redirect.before((the_request, the_response) -> 
          httpsRedirect(the_request, the_response, https_port));
    }
    
    // static files location
    my_spark.staticFileLocation("/us/freeandfair/corla/static");

    // available endpoints
    
    my_endpoints.add(new Root());
    my_endpoints.add(new ACVRDownload());
    my_endpoints.add(new ACVRDownloadByCounty());
    my_endpoints.add(new ACVRUpload());
    my_endpoints.add(new CVRDownloadByCounty());
    my_endpoints.add(new CVRDownloadByID());
    my_endpoints.add(new CVRDownload());
    my_endpoints.add(new CVRExportUpload());
    my_endpoints.add(new BallotManifestUpload());
    my_endpoints.add(new BallotManifestDownloadByCounty());
    my_endpoints.add(new BallotManifestDownload());
    my_endpoints.add(new BallotStyleDownloadByCounty());
    my_endpoints.add(new BallotStyleDownloadByID());
    my_endpoints.add(new BallotStyleDownload());
    my_endpoints.add(new ContestDownloadByCounty());
    my_endpoints.add(new ContestDownloadByID());
    my_endpoints.add(new ContestDownload());
    activateEndpoints();
  }
    
  // Static Methods
  
  /**
   * Creates a default set of properties.
   * 
   * @return The default properties.
   */
  private static Properties defaultProperties() {
    final Properties properties = new Properties();
    try {
      properties.load(ClassLoader.getSystemResourceAsStream(DEFAULT_PROPERTIES));
    } catch (final IOException e) {
      throw new IllegalStateException
      ("Error loading default properties file, aborting.", e);
    }
    return properties;
  }

  /**
   * The main method. Starts the server using the specified properties
   * file.
   * 
   * @param the_args Command line arguments. Only the first one is
   * considered, and it is interpreted as the path to a properties
   * file. If no arguments are supplied, default properties are
   * used. If the specified properties file cannot be loaded, the
   * server does not start.
   */
  public static void main(final String... the_args) {
    Properties properties;
    if (the_args.length > 0) {
      final File file = new File(the_args[0]);
      try {
        LOGGER.info("attempting to load properties from " + file);
        properties = new Properties();
        properties.load(new FileInputStream(file));
      } catch (final IOException e) {
        // could not load properties that way, let's try XML
        try {
          LOGGER.info("load failed, attempting to load XML properties from " + file);
          properties = new Properties();
          properties.loadFromXML(new FileInputStream(file));
        } catch (final IOException ex) {
          // could not load properties that way either, let's abort
          LOGGER.error("could not load properties, exiting");
          return;
        }
      }
    } else {
      LOGGER.info("no property file specified, loading default properties");
      properties = defaultProperties();
    }

    final Main main = new Main(properties);
    main.start();
  }
}
