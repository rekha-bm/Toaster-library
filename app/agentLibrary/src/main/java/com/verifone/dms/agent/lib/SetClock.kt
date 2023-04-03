/*
 * Copyright (c) 2023 by VeriFone, Inc.
 * All Rights Reserved.
 * THIS FILE CONTAINS PROPRIETARY AND CONFIDENTIAL INFORMATION
 * AND REMAINS THE UNPUBLISHED PROPERTY OF VERIFONE, INC.
 *
 * Use, disclosure, or reproduction is prohibited
 * without prior written approval from VeriFone, Inc.
 */
package com.verifone.dms.agent.lib

import android.app.AlarmManager
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

/**
A group of members*.
*
* This class is used to set the device time
*/

class SetClock {
  /**
   *  This method will be used to set the device time
   * @param context is only input for this method
   * @return the boolean value weather device time is changed or not.
   *
   */
  fun setClock(context: Context): Boolean {
    val c = Calendar.getInstance()
    c[2021, 4, 25, 8, 23] = 1
    val am = context.getSystemService(ALARM_SERVICE) as AlarmManager
    am.setTime(c.timeInMillis)
    val sdf = SimpleDateFormat("dd/M/yyyy hh:mm")
    val currentDate = sdf.format(Date())
    val setDate = sdf.format(c.timeInMillis)
    Log.d(" S DATE is  ", setDate)
    Log.d(" C DATE is  ", currentDate)
    if (setDate.equals(currentDate)) {
      return true
    } else {
      return false
    }
  }
}
