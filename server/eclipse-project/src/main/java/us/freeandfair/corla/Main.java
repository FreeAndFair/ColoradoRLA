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
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import javax.persistence.PersistenceException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.jetty.http.HttpStatus;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import spark.Request;
import spark.Response;
import spark.Service;

import us.freeandfair.corla.endpoint.Endpoint;
import us.freeandfair.corla.json.FreeAndFairNamingStrategy;
import us.freeandfair.corla.model.Administrator;
import us.freeandfair.corla.model.Contest;
import us.freeandfair.corla.model.County;
import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.query.CountyQueries;

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
   * The path to the default properties resource.
   */
  public static final String DEFAULT_PROPERTIES =
      "us/freeandfair/corla/default.properties";

  /**
   * The path to the resource containing the list of endpoint classes.
   */
  public static final String ENDPOINT_CLASSES = 
      "us/freeandfair/corla/endpoint/endpoint_classes";
  
  /**
   * The name of the property that specifies county IDs.
   */
  public static final String COUNTY_IDS = "county_ids";
  
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
    final List<Endpoint> endpoints = new ArrayList<>();
    try (InputStream endpoint_stream = 
        ClassLoader.getSystemResourceAsStream(ENDPOINT_CLASSES)) {
      if (endpoint_stream == null) {
        Main.LOGGER.error("could not load list of entity classes");          
      } else {
        final Scanner scanner = new Scanner(endpoint_stream, "UTF-8");
        while (scanner.hasNextLine()) {
          final String endpoint_class = scanner.nextLine();
          final Endpoint endpoint = 
              (Endpoint) Class.forName(endpoint_class).newInstance();
          endpoints.add(endpoint);
          Main.LOGGER.info("added endpoint class " + endpoint_class);
        }
        scanner.close();
      }
    } catch (final IOException e) {
      Main.LOGGER.error("error reading list of endpoint classes: " + e);
    } catch (final ClassNotFoundException | InstantiationException | 
                   IllegalAccessException | ClassCastException e) {
      Main.LOGGER.error("invalid endpoint class specified: " + e);
    }

    for (final Endpoint e : endpoints) {
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
   * Initializes the counties in the database using information from the 
   * county properties.
   */
  private void initializeCounties() {
    final Properties properties = new Properties();
    try {
      properties.load(ClassLoader.
                      getSystemResourceAsStream(my_properties.getProperty(COUNTY_IDS)));
    } catch (final IOException e) {
      throw new IllegalStateException("Error loading county IDs, aborting.", e);
    }
    try {
      for (final String s : properties.stringPropertyNames()) {
        // if the property name is an integer, we assume it's a county ID
        try {
          final Integer id = Integer.valueOf(s);
          final String name = properties.getProperty(s);
          County county = CountyQueries.byID(id);
          if (county == null) {
            county = 
                new County(name, id, new HashSet<Contest>(), new HashSet<Administrator>());
          } else if (!county.name().equals(name)) {
            // update the county's name while preserving the rest of its info
            Main.LOGGER.info("Updating " + county.name() + " county name to " + name);
            final County new_county = 
                new County(name, id, county.contests(), county.administrators());
            new_county.setID(county.id());
            county = new_county;
          }
          Persistence.saveOrUpdate(county);
        } catch (final NumberFormatException e) {
          // we skip this property because it wasn't numeric
        }
      }
    } catch (final PersistenceException e) {
      throw new IllegalStateException("Error loading county IDs, aborting.", e);
    }
  }
  
  /**
   * Starts a ColoradoRLA server.
   */
  public void start() {
    LOGGER.info("starting server with properties: " + my_properties);

    // provide properties to the persistence engine
    Persistence.setProperties(my_properties);

    if (Persistence.hasDB()) {
      initializeCounties();
    } else {
      LOGGER.error("could not open database connection");
      return;
    }
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

    // start the endpoints
    activateEndpoints();
  }
    
  // Static Methods
  
  /**
   * Creates a default set of properties.
   * 
   * @return The default properties.
   */
  public static Properties defaultProperties() {
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
    final Properties default_properties = defaultProperties();
    Properties properties = new Properties(default_properties);
    if (the_args.length > 0) {
      final File file = new File(the_args[0]);
      try {
        LOGGER.info("attempting to load properties from " + file);
        properties.load(new FileInputStream(file));
      } catch (final IOException e) {
        // could not load properties that way, let's try XML
        try {
          LOGGER.info("load failed, attempting to load XML properties from " + file);
          properties = new Properties(default_properties);
          properties.loadFromXML(new FileInputStream(file));
        } catch (final IOException ex) {
          // could not load properties that way either, let's abort
          LOGGER.error("could not load properties, exiting");
          return;
        }
      }
    } else {
      LOGGER.info("no property file specified, using default properties");
    }

    final Main main = new Main(properties);
    main.start();
  }
}
