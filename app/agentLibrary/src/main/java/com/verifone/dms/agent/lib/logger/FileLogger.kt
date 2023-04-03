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

import android.content.Context
import android.util.Log
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException

class FileLogger(name: String?) : Logger(name!!) {
  // NOTE : this should get init before logger use
  private val context: Context? = null
  override fun newLogger(name: String?): Logger {
    return FileLogger(name)
  }

  override fun init() {
    createFileInSpecifiedPath(LOG_FILE_NAME)
  }

  fun initWithFile(fileName: String) {
    createFileInSpecifiedPath(fileName)
  }

  private fun createFileInSpecifiedPath(fileName: String) {
    try {
      val rootPath: String = context?.filesDir?.absolutePath + LOG_FILE_PATH
      val root = File(rootPath)
      if (!root.exists()) {
        root.mkdirs()
      }
      file = File(rootPath, fileName)
      if (!file!!.exists()) {
        try {
          val fileStatus: Boolean = file!!.createNewFile()
          if (fileStatus) {
            logger!!.info("File is created successfully")
          }
        } catch (e: IOException) {
          Log.e("ERROR : ", " Create Log File IOException $e")
        }
      }
    } catch (e: Exception) {
      Log.e("ERROR : ", "Unable to open File I/O connection $e")
    }
  }

  override fun writeLog(logType: Int, msg: String?) {
    if (IS_CONSOLE_LOG) {
      when (logType) {
        ERROR -> Log.e("", msg!!)
        WARN -> Log.w("", msg!!)
        DEBUG -> Log.d("", msg!!)
        INFO,
        TRACE -> Log.i("", msg!!)
        DIAGNOSE -> Log.v("", msg!!)
        else -> {}
      }
    }
    if (file != null) {
      try {
        bufWriter = BufferedWriter(FileWriter(file, true))
        bufWriter!!.append(msg)
        bufWriter!!.newLine()
      } catch (e: IOException) {
        Log.e("ERROR : ", "FileLogger$e")
      } finally {
        close()
      }
    }
  }

  override fun close() {
    try {
      if (bufWriter != null) {
        bufWriter!!.close()
      }
    } catch (e: IOException) {
      Log.e("ERROR : ", "FileLogger closing $e")
    }
  }

  companion object {
    private const val IS_CONSOLE_LOG = false
    private const val LOG_FILE_PATH = "/data/data/com.agent.dmsagentapp/log/"
    private const val LOG_FILE_NAME = "dmsagent.log"
    private var file: File? = null
    private var bufWriter: BufferedWriter? = null
  }
}
