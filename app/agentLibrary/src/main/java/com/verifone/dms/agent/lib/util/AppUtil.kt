/*
 * Copyright (c) 2023 by VeriFone, Inc.
 * All Rights Reserved.
 * THIS FILE CONTAINS PROPRIETARY AND CONFIDENTIAL INFORMATION
 * AND REMAINS THE UNPUBLISHED PROPERTY OF VERIFONE, INC.
 *
 * Use, disclosure, or reproduction is prohibited
 * without prior written approval from VeriFone, Inc.
 */
package com.verifone.dms.agent.lib.util

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.verifone.dms.agent.lib.Constant
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

open class AppUtil {

  private var isDeviceRegistered = false
  private lateinit var customLayout: View
  lateinit var progressBarCustom: ProgressBar
  lateinit var percentTextView: TextView
  private lateinit var builder:AlertDialog.Builder

  val certificateUrl =
      "http://blr2wvvhqdev3:7354/device-registration-service/v1.1/getClientCertificate"
  /* getDeviceSerialNumber(): String
  This method will be used for calculating device serial number
  o/p : String - serial number
  */
  fun getDeviceSerialNumber(): String {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
      // Android 9 (or Android 10, privileged application only)
      // return Build.getSerial()
      return "804-831-359"
    } else {
      // Android 8 and earlier
      return Build.SERIAL
    }
  }
  /* getDateTime(): String
  This method will be used for calculating current date and time
  o/p : String - current date and time
  */
  @RequiresApi(Build.VERSION_CODES.O)
  fun getDateTime(): String {
    val current = LocalDateTime.now()

    val formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
    val formatted = current.format(formatter)
    return formatted
  }
  /* getClientId(): String
  This method will be used for calculating client id
  o/p : String - client id in format : <Customer ID>__<Device Model name>__<Serial Number>
  */
  fun getClientId(): String {
    val customerId = "CUS_678"
    val clientId =
        customerId.plus("__").plus(getDeviceModel()).plus("__").plus(getDeviceSerialNumber())
    return clientId
  }
  /* getDeviceModel(): String
  This method will be used for calculating Device Model
  o/p : String - Device model
  */
  fun getDeviceModel(): String {
    // return Build.MODEL
    return "CM5"
  }
  /* getDeviceCId(): String
  This method will be used for calculating Device cId
  o/p : String - cId in format : D-<SerialNumber>-YYYYMMDDHHMMSS e.g. D-123-456-789-20221021231010
  */
  @RequiresApi(Build.VERSION_CODES.O)
  fun getDeviceCId(): String {
    val cId = "D-".plus(getDeviceSerialNumber()).plus("-").plus(getDateTime())
    return cId
  }
  /* incrementdMsgId(context: Context): Int
  This method will be used for calculating dMsgId last sent dMsgId+1
  i/p : context
  o/p : Int last sent dMsgId+1
  */

  fun incrementdMsgId(context: Context): Int {
    var dMsgId: Int
    if (SharedPreferenceUtil.getIntegerPreferences(context, Constant.DMSG_ID_KEY) == null) {
      dMsgId = 0
      SharedPreferenceUtil.setIntegerPreferences(context, Constant.DMSG_ID_KEY, dMsgId)
    } else {
      dMsgId = SharedPreferenceUtil.getIntegerPreferences(context, Constant.DMSG_ID_KEY)
      if (dMsgId <= Int.MAX_VALUE)
          SharedPreferenceUtil.setIntegerPreferences(context, Constant.DMSG_ID_KEY, ++dMsgId)
      else {
        dMsgId = 0
        SharedPreferenceUtil.setIntegerPreferences(context, Constant.DMSG_ID_KEY, dMsgId)
      }
    }
    return dMsgId
  }

  /* checkDeviceRegistered(context: Context): String
  This method will be used for checking is device registration is done or not as device registration should perform only once
  i/p : context
  o/p : true : if registration is successful
        false : if registration is not successful
  */
  fun checkDeviceRegistered(context: Context): Boolean {
    isDeviceRegistered =
        SharedPreferenceUtil.getBooleanPreferences(context, Constant.IS_DEVICE_REGISTERED)
    return isDeviceRegistered
  }
  open fun getProgressDisplayLine(currentBytes: Long, totalBytes: Long): String? {
    return getBytesToMBString(currentBytes) + "/" + getBytesToMBString(totalBytes)
  }

  open fun getBytesToMBString(bytes: Long): String {
    return java.lang.String.format(Locale.ENGLISH, "%.2fMb", bytes / (1024.00 * 1024.00))
  }

  fun getCustomAlertDialog(context: Context): AlertDialog {
    builder = AlertDialog.Builder(context)
    val inflater: LayoutInflater =
      context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    customLayout =
      inflater.inflate(com.verifone.dms.agent.lib.R.layout.download_progress_layout, null)
    progressBarCustom =
      customLayout.findViewById(com.verifone.dms.agent.lib.R.id.progress_horizontal)
    percentTextView =
      customLayout.findViewById(com.verifone.dms.agent.lib.R.id.downloading_percentage)

    builder.setTitle("Download File")
    builder.setView(customLayout)
    builder.setPositiveButton("Ok") { dialog, which ->
      // send data from the AlertDialog to the Activity
      dialog.cancel()
      dialog.dismiss()
    }
    return builder.create()
  }
}

