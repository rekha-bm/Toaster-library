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
import android.app.Application
import android.content.Context

class DmsApplication : Application() {


    override fun onCreate() {
        super.onCreate()
     context = applicationContext

    }

    companion object {
        var context: Context? = null
        fun getAppContext(): Context? {
            return context
        }
    }


   }