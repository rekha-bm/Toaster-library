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

import android.os.Build
import androidx.annotation.RequiresApi
import com.verifone.dms.agent.lib.crypto.RSAUtil

class DeviceRegistration {

  var rsaUtil: RSAUtil = RSAUtil()

  private val KEY_ALIAS_AGENTRSAKEYS = "AgentRSAKeys"
  private val KEY_ALIAS_DMSRSAKEYS = "DMSRSAKeys"

  @RequiresApi(Build.VERSION_CODES.M)
  fun generateRSAKeyPair() {

    rsaUtil.generateKey(KEY_ALIAS_AGENTRSAKEYS)

    // TODO for testing purpose DMS RSA key generated once the server shares it's public key then
    // below
    // TODO key generation is not required
    rsaUtil.generateKey(KEY_ALIAS_DMSRSAKEYS)
  }
}
