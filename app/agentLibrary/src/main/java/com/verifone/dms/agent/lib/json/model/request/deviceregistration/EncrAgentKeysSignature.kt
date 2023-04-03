/*
 * Copyright (c) 2023 by VeriFone, Inc.
 * All Rights Reserved.
 * THIS FILE CONTAINS PROPRIETARY AND CONFIDENTIAL INFORMATION
 * AND REMAINS THE UNPUBLISHED PROPERTY OF VERIFONE, INC.
 *
 * Use, disclosure, or reproduction is prohibited
 * without prior written approval from VeriFone, Inc.
 */
package com.verifone.dms.agent.lib.json.model.request.deviceregistration

import com.google.gson.annotations.SerializedName

data class EncrAgentKeysSignature(
    @SerializedName("type") val type: String,
    @SerializedName("signAlgo") val signAlgo: String,
    @SerializedName("hashAlgo") val hashAlgo: String,
    @SerializedName("keyId") val keyIdId: String,
    @SerializedName("value") val value: String
)
