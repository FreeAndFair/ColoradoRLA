/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Jul 19, 2017
 * @copyright 2017 Colorado Department of State
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 * @creator Daniel M. Zimmerman <dmz@freeandfair.us>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla;

import static spark.Spark.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import javax.persistence.PersistenceException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.server.Server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import spark.Request;
import spark.Response;
import spark.Service;
import spark.embeddedserver.EmbeddedServers;
import spark.embeddedserver.jetty.EmbeddedJettyServer;
import spark.embeddedserver.jetty.JettyHandler;
import spark.http.matching.MatcherFilter;
import spark.route.Routes;
import spark.staticfiles.StaticFilesConfiguration;

import us.freeandfair.corla.asm.AbstractStateMachine;
import us.freeandfair.corla.asm.AuditBoardDashboardASM;
import us.freeandfair.corla.asm.CountyDashboardASM;
import us.freeandfair.corla.asm.DoSDashboardASM;
import us.freeandfair.corla.asm.PersistentASMState;
import us.freeandfair.corla.auth.AuthenticationInterface;
import us.freeandfair.corla.endpoint.CORSFilter;
import us.freeandfair.corla.endpoint.Endpoint;
import us.freeandfair.corla.json.FreeAndFairNamingStrategy;
import us.freeandfair.corla.json.InstantTypeAdapter;
import us.freeandfair.corla.json.VersionExclusionStrategy;
import us.freeandfair.corla.model.County;
import us.freeandfair.corla.model.CountyDashboard;
import us.freeandfair.corla.model.DoSDashboard;
import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.query.PersistentASMStateQueries;
import us.freeandfair.corla.util.SuppressFBWarnings;

/**
 * The main executable for the ColoradoRLA server. 
 * 
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @author Joseph R. Kiniry <kiniry@freeandfair.us>
 * @version 1.0.0
 */
@SuppressWarnings({"PMD.GodClass", "PMD.ExcessiveImports"})
@SuppressFBWarnings("ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD")
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
      registerTypeAdapter(Instant.class, new InstantTypeAdapter()).
      setFieldNamingStrategy(new FreeAndFairNamingStrategy()).
      setExclusionStrategies(new VersionExclusionStrategy()).
      setPrettyPrinting().create();
  
  /**
   * The version string.
   */
  public static final String VERSION;
  
  /**
   * Which authentication subsystem implementation are we to use?
   */
  private static AuthenticationInterface static_authentication;
  
  /**
   * The properties loaded from the properties file.
   */
  @SuppressWarnings("PMD.AssignmentToNonFinalStatic")
  private static Properties static_properties;
  
  // Version Initializer
  
  static {
    final String pom_location =
        "/META-INF/maven/us.freeandfair.production/colorado_rla/pom.xml";
    final File pom = new File("pom.xml");
    String version = "UNKNOWN";
    InputStream pom_stream = null;
    
    if (pom.exists()) {
      try {
        pom_stream = new FileInputStream(pom);
      } catch (final FileNotFoundException e) {
        // this can't happen because we tested that the file existed
      }
    } else {
      pom_stream = Main.class.getResourceAsStream(pom_location);
    }
    
    if (pom_stream != null) {
      try (InputStreamReader isr = new InputStreamReader(pom_stream, "UTF-8")) {
        final MavenXpp3Reader reader = new MavenXpp3Reader();
        final Model model = reader.read(isr);
        version = model.getVersion();
      } catch (final IOException | XmlPullParserException e) {
        LOGGER.info("could not obtain version number: " + e);
      }
    }
    VERSION = version;
  }
  
  // Constructors

  /**
   * Constructs a new ColoradoRLA server with the specified properties.
   * 
   * @param the_properties The properties.
   */
  public Main(final Properties the_properties) {
    static_properties = the_properties;
  }
  
  // Instance Methods
  
  /**
   * @return the version string of the system.
   */
  public static String version() {
    return VERSION;
  }
  
  /**
   * @return the implementation of `AuthenticationInterface` demanded by
   * the system's properties file and loaded at startup.
   */
  public static AuthenticationInterface authentication() {
    return static_authentication;
  }
  
  /**
   * @return a read-only view of the properties in use by the system at runtime.
   */
  public static Properties properties() {
    return new Properties(static_properties);
  }
  
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
   * Setup the authentication subsystem according to the property setting
   * `authentication_class` in the system's properties file.
   */
  private void setupAuthentication() {
    String authentication_class = null;
    try {
      // classload and attach to the authentication field the appropriate
      // implementation of `AuthenticationInterface`.
      authentication_class = static_properties.getProperty("authentication_class");
      if (authentication_class == null) {
        authentication_class = "us.freeandfair.corla.auth.DatabaseAuthentication";
      }
      static_authentication = (AuthenticationInterface) 
          Class.forName(authentication_class).newInstance();
      LOGGER.info("Loaded authentication subsystem `" + authentication_class + "'");
      static_authentication.setLogger(LOGGER);
      static_authentication.setGSON(GSON);
      final String authentication_server =
          static_properties.getProperty("authentication_server", "localhost");
      static_authentication.setAuthenticationServerName(authentication_server);
      LOGGER.info("Initialized authentication subsystem `" + authentication_class + "'");
    } catch (final ClassNotFoundException | 
        IllegalAccessException | InstantiationException e) {
      LOGGER.fatal("Authentication class '" + authentication_class + "' not found.");
      LOGGER.fatal("Check the value of `authentication_class` in your RLA Tool " + 
          "system properties.");
    }
  }
  
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
          Integer.parseInt(static_properties.getProperty(the_property, 
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
      final CORSFilter cors_and_before = 
          new CORSFilter(static_properties, (the_request, the_response) ->
          e.before(the_request, the_response));
      before(e.endpointName(), cors_and_before);
      after(e.endpointName(), (the_request, the_response) -> 
          e.after(the_request, the_response));
      afterAfter(e.endpointName(), (the_request, the_response) -> 
          e.afterAfter(the_request, the_response));
      switch (e.endpointType()) {
        case GET:
          get(e.endpointName(), (the_request, the_response) -> 
                  e.endpoint(the_request, the_response));
          break;
         
        case PUT:
          put(e.endpointName(), (the_request, the_response) ->
                  e.endpoint(the_request, the_response));
          break;
          
        case POST:
          post(e.endpointName(), (the_request, the_response) ->
                   e.endpoint(the_request, the_response));
          break;
          
        default:
      }
    }
  }
  
  /**
   * Restores an ASM's state or persists it in the database.
   * 
   * @param the_asm The ASM.
   * @param the_state The persistent state to restore, or null to persist
   *  the state to the database.
   * @exception PersistenceException if the state cannot be persisted.
   */
  private void restoreOrPersistState(final AbstractStateMachine the_asm,
                                     final PersistentASMState the_state) 
      throws PersistenceException {
    if (the_state == null) {
      // there is no such state in the database, so persist one
      Main.LOGGER.debug("no state found for " + the_asm + 
                        ", persisting one");
      final PersistentASMState new_state = PersistentASMState.stateFor(the_asm);
      Persistence.saveOrUpdate(new_state);
    } else {
      Main.LOGGER.debug(the_asm + " state found in db: " + the_state);
    }
  }
  
  /**
   * Initializes the ASMs. Each one for which no state exists in the database
   * has its state persisted in the database.
   * 
   * @param the_counties The counties to initialize ASMs for.
   * @exception PersistenceException if we can't initialize the ASMs.
   */
  private void initializeASMsAndDashboards(final List<County> the_counties) 
      throws PersistenceException {
    // first, check the DoS dashboard
    final PersistentASMState dos_state = 
        PersistentASMStateQueries.get(DoSDashboardASM.class, DoSDashboardASM.IDENTITY);
    restoreOrPersistState(new DoSDashboardASM(), dos_state);
    
    DoSDashboard dosdb = Persistence.getByID(DoSDashboard.ID, DoSDashboard.class);
    if (dosdb == null) {
      dosdb = new DoSDashboard();
      Persistence.saveOrUpdate(dosdb);
    }
    
    // next, iterate over the counties and check the county and 
    // audit board dashboards
    for (final County c : the_counties) {
      final String asm_id = String.valueOf(c.id());
      final PersistentASMState county_state =
          PersistentASMStateQueries.get(CountyDashboardASM.class, asm_id);
      restoreOrPersistState(new CountyDashboardASM(asm_id), county_state);

      final PersistentASMState audit_state =
          PersistentASMStateQueries.get(AuditBoardDashboardASM.class, asm_id);
      restoreOrPersistState(new AuditBoardDashboardASM(asm_id), audit_state);      
      
      CountyDashboard cdb = Persistence.getByID(c.id(), CountyDashboard.class);
      if (cdb == null) {
        cdb = new CountyDashboard(c);
        Persistence.saveOrUpdate(cdb);
      }
    }
  }
  
  /**
   * Initializes the counties in the database using information from the 
   * county properties.
   * 
   * @return the counties.
   */
  private List<County> initializeCounties() {
    final Properties properties = new Properties();
    try {
      properties.load(ClassLoader.
                      getSystemResourceAsStream(static_properties.getProperty(COUNTY_IDS)));
    } catch (final IOException e) {
      throw new IllegalStateException("Error loading county IDs, aborting.", e);
    }
    final List<County> result = new ArrayList<County>();
    try {
      for (final String s : properties.stringPropertyNames()) {
        // if the property name is an integer, we assume it's a county ID
        try {
          final Long id = Long.valueOf(s);
          final String name = properties.getProperty(s);
          County county = Persistence.getByID(id, County.class);
          if (county == null) {
            county = new County(name, id);
          } else if (!county.name().equals(name)) {
            // update the county's name while preserving the rest of its info
            Main.LOGGER.info("Updating " + county.name() + " county name to " + name);
            final County new_county = new County(name, id);
            new_county.setID(county.id());
            county = new_county;
          }
          Persistence.saveOrUpdate(county);
          result.add(county);
        } catch (final NumberFormatException e) {
          // we skip this property because it wasn't numeric
        }
      }
    } catch (final PersistenceException e) {
      throw new IllegalStateException("Error loading county IDs, aborting.", e);
    }
    return result;
  }
  
  /**
   * Generates a string representation of a Properties object, including all
   * properties (even default ones).
   * 
   * @param the_properties The Properties object.
   * @return the string representation.
   */
  private static String propertiesString(final Properties the_properties) {
    final StringBuilder sb = new StringBuilder();
    
    sb.append('{');
    final Enumeration<?> property_names = the_properties.propertyNames();
    boolean not_first = false;
    while (property_names.hasMoreElements()) {
      final Object prop = property_names.nextElement();
      if (not_first) {
        sb.append(", ");
      } else {
        not_first = true;
      }
      sb.append(prop.toString());
      sb.append('=');
      sb.append(the_properties.getProperty(prop.toString()));
    }
    sb.append('}');  
    
    return sb.toString();
  }
  
  /**
   * Starts a ColoradoRLA server.
   */
  public void start() {
    LOGGER.info("starting server version " + VERSION + " with properties: " + 
                propertiesString(static_properties));
    
    // provide properties to the persistence engine
    Persistence.setProperties(static_properties);

    if (Persistence.beginTransaction()) {
      initializeASMsAndDashboards(initializeCounties());
      try {
        Persistence.commitTransaction();
      } catch (final PersistenceException e) {
        throw new IllegalStateException("could not initialize data in database", e);
      }
    } else {
      LOGGER.error("could not open database connection");
      return;
    }
    
    // secure the session cookies by adding an embedded server handler
    EmbeddedServers.add(EmbeddedServers.Identifiers.JETTY, 
        (final Routes the_route_matcher, 
         final StaticFilesConfiguration the_static_files_config, 
         final boolean the_has_multiple_handler) -> {
        final MatcherFilter matcher_filter = 
            new MatcherFilter(the_route_matcher, the_static_files_config, 
                              false, the_has_multiple_handler);
        matcher_filter.init(null);

        final JettyHandler handler = new JettyHandler(matcher_filter);
        handler.getSessionCookieConfig().setHttpOnly(true);
        // secure cookies don't work if we're not using HTTPS
        // handler.getSessionCookieConfig().setSecure(true);

        return new EmbeddedJettyServer((int the_max_threads, 
                                        int the_min_threads, 
                                        int the_thread_timeout) -> {
          return new Server();
        }, handler);
      });
    
    // get the port numbers from properties
    final int http_port = parsePortNumber("http_port", DEFAULT_HTTP_PORT);
    final int https_port = parsePortNumber("https_port", DEFAULT_HTTPS_PORT);
    
    // get key store information from properties, if applicable
    String keystore_path = static_properties.getProperty("keystore", null);
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
    
    if (keystore_path == null) {
      port(http_port);
    } else {
      port(https_port);
      final String keystore_password = 
          static_properties.getProperty("keystore_password", null);
      secure(keystore_path, keystore_password, null, null);

      // redirect everything
      final Service redirect = Service.ignite();
      redirect.port(http_port);
      redirect.before((the_request, the_response) -> 
          httpsRedirect(the_request, the_response, https_port));
    }

    // authentication subsystem
    setupAuthentication();
    
    // static files location
    staticFileLocation("/us/freeandfair/corla/static");

    // start the endpoints
    activateEndpoints();
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
    // set headless mode - this prevents Apache POI from starting a GUI when
    // generating Excel files
    System.setProperty("java.awt.headless", "true");
    
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
    try {
      main.start();
    } catch (final IllegalStateException e) {
      LOGGER.error("unable to run: " + e);
    }
  }
}
