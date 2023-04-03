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

import android.util.Log

class ConsoleLogger : Logger {
  private var tag: String? = null

  constructor()
    constructor(name: String?) : super(name!!) {
    tag = name
  }

  override fun init() {
    logger!!.info("init called:")
  }

  override fun close() {
    logger!!.info("close called:")
  }

  protected fun writeLog(mesg: String) {
    logger!!.info("writeLog :$mesg")
  }

  override fun writeLog(logType: Int, logMessage: String?) {
    when (logType) {
      ERROR -> logMessage?.let { Log.e(tag, it) }
      WARN -> logMessage?.let { Log.w(tag, it) }
      DEBUG -> logMessage?.let { Log.d(tag, it) }
      INFO -> logMessage?.let { Log.i(tag, it) }
      DIAGNOSE -> logMessage?.let { Log.v(tag, it) }
      TRACE -> logMessage?.let { Log.i(tag, it) }
      else -> {}
    }
  }

  public override fun newLogger(name: String?): Logger {
    return ConsoleLogger(name)
  }
}
