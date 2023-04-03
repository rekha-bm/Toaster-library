/*
 * Copyright (c) 2023 by VeriFone, Inc.
 * All Rights Reserved.
 * THIS FILE CONTAINS PROPRIETARY AND CONFIDENTIAL INFORMATION
 * AND REMAINS THE UNPUBLISHED PROPERTY OF VERIFONE, INC.
 *
 * Use, disclosure, or reproduction is prohibited
 * without prior written approval from VeriFone, Inc.
 */
package com.verifone.dms.agent.lib.json.model.request.status

import com.google.gson.annotations.SerializedName

data class CommonStatusDeviceMessage(
    @SerializedName("header") val header: CommonStatusHeader?,
    @SerializedName("content") val content: CommonStatusContent,
    @SerializedName("encryption") val encryption: CommonStatusEncryption,
    @SerializedName("signature") val signature: CommonStatusSignature

)
