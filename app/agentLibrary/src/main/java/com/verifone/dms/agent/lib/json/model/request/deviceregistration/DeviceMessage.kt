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

data class DeviceMessage(
    @SerializedName("header") val header: com.verifone.dms.agent.lib.json.model.request.deviceregistration.Header,
    @SerializedName("signature") val signature: com.verifone.dms.agent.lib.json.model.request.deviceregistration.Signature,
    @SerializedName("content") val content: com.verifone.dms.agent.lib.json.model.request.deviceregistration.Content
)
