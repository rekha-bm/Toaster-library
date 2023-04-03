/*
 * Copyright (c) 2023 by VeriFone, Inc.
 * All Rights Reserved.
 * THIS FILE CONTAINS PROPRIETARY AND CONFIDENTIAL INFORMATION
 * AND REMAINS THE UNPUBLISHED PROPERTY OF VERIFONE, INC.
 *
 * Use, disclosure, or reproduction is prohibited
 * without prior written approval from VeriFone, Inc.
 */
package com.verifone.dms.agent.lib.logger

import java.util.*
import org.slf4j.LoggerFactory

abstract class Logger {
  private val BUILD_VERSION = "1.0"
  private val prefixes = arrayOf("SEVERE", "WARN", "DEBUG", "INFO", "DIAGNOSE", "TRACE")
  private var name = "DEFAULT"

  protected constructor()
    protected constructor(name: String) {
    this.name = name
    slf4jLogger = LoggerFactory.getLogger(name)
  }

  abstract fun init()
  protected abstract fun newLogger(name: String?): Logger
  protected abstract fun writeLog(logType: Int, msg: String?)
  abstract fun close()
  private fun log(level: Int, message: String) {
    val log = true

    //        if (level == DIAGNOSE)
    //            log = true;
    //        if (level == TRACE)
    //            log = true;
    //        if (Logger.level == ERROR) {
    //            if (level == ERROR)
    //                log = true;
    //        } else if (Logger.level == WARN) {
    //            if (level == ERROR || level == WARN)
    //                log = true;
    //        } else if (Logger.level == DEBUG) {
    //            if (level == ERROR || level == WARN || level == DEBUG)
    //                log = true;
    //        } else if (Logger.level == INFO) {
    //            log = true;
    //        }
    val logMsg =
        ("[" +
            now() +
            "] " +
            "[" +
            Thread.currentThread().name +
            "] " +
            "[" +
            BUILD_VERSION +
            "] " +
            " [" +
            prefixes[level] +
            "] " +
            " : " +
            message)
    if (log && ENABLE) {
      writeLog(level, logMsg)
    }
  }

  fun severe(message: String) {
    log(ERROR, message)
  }

  fun warn(message: String) {
    log(WARN, message)
  }

  fun debug(message: String) {
    log(DEBUG, message)
  }

  fun info(message: String) {
    log(INFO, message)
  }

  fun diagnose(message: String) {
    log(DIAGNOSE, message)
  }

  fun trace(message: String) {
    log(TRACE, message)
  }

  protected fun now(): String {
    val f = StringBuilder()
    val cal: Calendar = Calendar.getInstance()
    val mon = MONTHS[cal.get(Calendar.MONTH)]
    val day: Int = cal.get(Calendar.DAY_OF_MONTH)
    val year: Int = cal.get(Calendar.YEAR)
    val hr: Int = cal.get(Calendar.HOUR_OF_DAY)
    val min: Int = cal.get(Calendar.MINUTE)
    val sec: Int = cal.get(Calendar.SECOND)
    f.append(mon)
    f.append("-")
    f.append(day)
    f.append("-")
    f.append(year)
    f.append(" ")
    f.append(hr)
    f.append(":")
    f.append(min)
    f.append(":")
    f.append(sec)
    return f.toString()
  }

  companion object {
    private const val ENABLE = true

    const val ERROR = 0

    const val WARN = 1

    const val DEBUG = 2

    const val INFO = 3

    const val DIAGNOSE = 4

    const val TRACE = 5
    private var level = TRACE

    var logger: Logger? = null
    protected var slf4jLogger: org.slf4j.Logger? = null
    protected const val CONSOLE_LOG = "console"
    protected const val FILE_LOG = "file"
    private val MONTHS =
        arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "July", "Aug", "Sep", "Oct", "Nov", "Dec")

    /**
     * Use this method to get the logger when it is required to preserve the class and method names
     * in the logs when the library is obfuscated.
     *
     * @param name Fully qualified name of the class
     * @return Logger object
     */
    fun getNewLogger(name: String?): Logger {
      if (logger == null) {
        init(CONSOLE_LOG)
      }
      return logger!!.newLogger(name)
    }

    /**
     * Use this method to get logger when the library would not be obfuscated.
     *
     * @param obj Object specific logger
     * @return Logger object
     */
    fun getNewLogger(obj: Any): Logger {
      val name = obj.javaClass.name
      return logger!!.newLogger(name)
    }

    fun init(logger: String?) {
      if (CONSOLE_LOG.equals(logger, ignoreCase = true)) {
        Companion.logger = ConsoleLogger(logger)
      } else if (FILE_LOG.equals(logger, ignoreCase = true)) {
        val fileLogger = FileLogger(logger)
        fileLogger.init()
        Companion.logger = fileLogger
      }
    }

    fun init(logger: String?, fileName: String) {
      if (CONSOLE_LOG.equals(logger, ignoreCase = true)) {
        Companion.logger = ConsoleLogger()
      } else if (FILE_LOG.equals(logger, ignoreCase = true)) {
        val fileLogger = FileLogger(logger)
        fileLogger.initWithFile(fileName)
        Companion.logger = fileLogger
      }
    }

    /**
     * * When log level is set to ERROR, only error conditions that are errors are logged * When log
     *   level is set to WARN, the following are logged
     * * ERROR
     * * WARN
     * *
     * When log level is set to DEBUG, the following are logged
     * * ERROR
     * * WARN
     * * DEBUG
     * * When log level is set to INFO, everything is logged
     *
     * @param level Set Level for the entire logging system
     */
    fun setLevel(level: Int) {
      Companion.level = level
    }

    fun closeLog() {
      if (logger != null) {
        logger!!.close()
      }
    }
  }
}
