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
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Properties;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import spark.Spark;

/**
 * The main executable for the ColoradoRLA server. 
 * 
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 0.0.1
 */
public final class Main {
  /**
   * The name of the default properties resource.
   */
  public static final String DEFAULT_PROPERTIES = "us/freeandfair/corla/default.properties";

  /**
   * The name of the logger.
   */
  public static final String LOGGER_NAME = "corla";

  /**
   * The default port number (can be overridden by properties).
   */
  public static final int DEFAULT_PORT = 8888;
  
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
   * The properties loaded from the properties file.
   */
  private final Properties my_properties;
  
  // Constructors

  /**
   * Constructs a new ColoradoRLA server with the specified properties.
   * 
   * @param the_properties The properties.
   */
  public Main(final /*@ non_null */ Properties the_properties) {
    my_properties = the_properties;
  }
  
  // Instance Methods
  
  /**
   * Starts a ColoradoRLA server.
   */
  public void start() {
    LOGGER.info("starting server with properties: " + my_properties);
    
    // get the port number from properties
    int port_number = DEFAULT_PORT;
    try {
      final int prop_port_number =
          Integer.parseInt(my_properties.getProperty("port", String.valueOf(port_number)));
      if (MIN_PORT <= prop_port_number && prop_port_number < MAX_PORT) {
        port_number = prop_port_number;
      } else {
        LOGGER.info("invalid port number in properties, using default port");
      }
    } catch (final NumberFormatException e) {
      LOGGER.info("could not read port number from properties, using default port");
    }
    
    // get key store information from properties, if applicable
    final String keystore_password = my_properties.getProperty("keystore_password", null);
    String keystore_path = my_properties.getProperty("keystore", null);
    if (keystore_path != null && !(new File(keystore_path).exists())) {
      // the keystore property isn't an absolute or relative pathname that exists, so
      // let's try to load it as a resource
      final URL keystore_url = Main.class.getResource(keystore_path);
      if (keystore_url != null) {
        try {
          keystore_path = Paths.get(keystore_url.toURI()).toString();
        } catch (final URISyntaxException e) {
          // do nothing
        }
      }
    }
    
    // start Spark listening on our port
    Spark.port(port_number);
    
    // static files location
    Spark.staticFileLocation("/us/freeandfair/corla/static");
    
    // SSL
    if (keystore_path != null) {
      Spark.secure(keystore_path, keystore_password, null, null);
    }
    
    // available endpoints
    Spark.get("/", (the_request, the_response) -> {
      return "<title>ColoradoRLA Server</title><h1>ColoradoRLA Server</h1>";
    });
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
   * The main method. Starts the server using the specified properties file.
   * 
   * @param the_args Command line arguments. Only the first one is considered,
   *          and it is interpreted as the path to a properties file. If no
   *          arguments are supplied, default properties are used. If the 
   *          specified properties file cannot be loaded, the server does not
   *          start.
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
          throw new IllegalArgumentException
          ("Error loading specified properties file, aborting.", ex);
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
