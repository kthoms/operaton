package org.operaton.bpm.engine.test.junit5;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.Map.Entry;

public class ProcessEngineLoggingExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback {

  public static final String LOGGER_NOT_FOUND_ERROR = "no logger found with name ";
  public static final String NOT_WATCHING_ERROR = "not watching any logger with name: ";
  private static final String APPENDER_NAME = "defaultAppender";

  private Map<String, Logger> globallyWatched = new HashMap<>();
  private Level globalLevel = Level.DEBUG;

  private Map<String, Logger> allWatched = new HashMap<>();

  public ProcessEngineLoggingExtension watch(String... loggerName) {
    for (String logger : loggerName) {
      watch(logger, null);
    }
    return this;
  }

  public ProcessEngineLoggingExtension watch(String loggerName, Level level) {
    Logger logger = getLogger(loggerName);
    logger.setLevel(level);
    globallyWatched.put(logger.getName(), logger);
    return this;
  }

  public ProcessEngineLoggingExtension level(Level level) {
    globalLevel = level;
    return this;
  }

  private Logger getLogger(String loggerName) {
    Logger logger;
    try {
      logger = (Logger) LoggerFactory.getLogger(loggerName);
      if (logger.getLevel() == null || globalLevel.isGreaterOrEqual(logger.getLevel())) {
        logger.setLevel(globalLevel);
      }
    } catch (ClassCastException e) {
      throw new RuntimeException(LOGGER_NOT_FOUND_ERROR + loggerName);
    }
    return logger;
  }

  public List<ILoggingEvent> getLog(String loggerName) {
    Logger logger = allWatched.get(loggerName);
    if (logger == null) {
      throw new RuntimeException(NOT_WATCHING_ERROR + loggerName);
    }
    return ((ListAppender<ILoggingEvent>) logger.getAppender(APPENDER_NAME)).list;
  }

  public List<ILoggingEvent> getLog() {
    List<ILoggingEvent> allLogs = new ArrayList<>();
    for (String loggerName : allWatched.keySet()) {
      allLogs.addAll(getLog(loggerName));
    }
    Collections.sort(allLogs, (event1, event2) -> Long.valueOf(event1.getTimeStamp() - event2.getTimeStamp()).intValue());
    return allLogs;
  }

  public List<ILoggingEvent> getFilteredLog(String subString) {
    List<ILoggingEvent> log = getLog();
    return filterLog(log, subString);
  }

  public List<ILoggingEvent> getFilteredLog(String loggerName, String subString) {
    List<ILoggingEvent> log = getLog(loggerName);
    return filterLog(log, subString);
  }

  @Override
  public void beforeTestExecution(ExtensionContext context) throws Exception {
    Map<String, Logger> toWatch = new HashMap<>(globallyWatched);
    // Assuming a WatchLogger annotation processing setup if required, which would involve reflection if handled in JUnit 5.
    watchLoggers(toWatch);
  }

  @Override
  public void afterTestExecution(ExtensionContext context) throws Exception {
    // Reset logback configuration
    for (Logger logger : allWatched.values()) {
      logger.detachAppender(APPENDER_NAME);
      logger.setLevel(null);
    }
    allWatched.clear();
  }

  private void watchLoggers(Map<String, Logger> loggers) {
    for (Entry<String, Logger> loggerEntry : loggers.entrySet()) {
      ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
      listAppender.setName(APPENDER_NAME);
      listAppender.start();
      Logger logger = loggerEntry.getValue();
      if (logger.getLevel() == null) {
        logger.setLevel(globalLevel);
      }
      logger.addAppender(listAppender);
      allWatched.put(loggerEntry.getKey(), logger);
    }
  }

  private List<ILoggingEvent> filterLog(List<ILoggingEvent> log, String subString) {
    List<ILoggingEvent> filteredLog = new ArrayList<>();
    for (ILoggingEvent logEntry : log) {
      if (logEntry.getFormattedMessage().contains(subString)) {
        filteredLog.add(logEntry);
      }
    }
    return filteredLog;
  }

}
