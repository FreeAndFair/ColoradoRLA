package us.freeandfair.corla.query;

import java.util.Properties;

import java.io.IOException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import org.testng.annotations.*;
import static org.testng.Assert.*;

import us.freeandfair.corla.persistence.Persistence;

public class Setup {

  public static final String DEFAULT_PROPERTIES =
    "us/freeandfair/corla/default.properties";
  public static final String TEST_PROPERTIES =
    "test.properties";

  public static final String LOGGER_NAME = "corla";
  public static final Logger LOGGER = LogManager.getLogger(LOGGER_NAME);

  public static final Properties properties = new Properties();

  public static void setProperties(){
    try {
    properties.load(ClassLoader.getSystemResourceAsStream(DEFAULT_PROPERTIES));
    // overrides database name
    properties.load(ClassLoader.getSystemResourceAsStream(TEST_PROPERTIES));

    Persistence.setProperties(properties);
    } catch (final IOException e) {
      LOGGER.warn("could not read properties file: " + e);
    }
  }



}
