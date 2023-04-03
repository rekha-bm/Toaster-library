/*
 * Copyright (c) 2023 by VeriFone, Inc.
 * All Rights Reserved.
 * THIS FILE CONTAINS PROPRIETARY AND CONFIDENTIAL INFORMATION
 * AND REMAINS THE UNPUBLISHED PROPERTY OF VERIFONE, INC.
 *
 * Use, disclosure, or reproduction is prohibited
 * without prior written approval from VeriFone, Inc.
 */
package com.verifone.dms.agent.lib.json.model.response

import com.google.gson.annotations.SerializedName

data class ResponseOpSet(
    @SerializedName("execDT") val execDT: Int?,
    @SerializedName("execMW") val execMW: Boolean?,
    @SerializedName("forceInstall") val forceInstall: Boolean?,
    @SerializedName("id") val id: Int?,
    @SerializedName("installDT") val installDT: String?,
    @SerializedName("ops") val ops: List<com.verifone.dms.agent.lib.json.model.response.ResponseOp?>?
)
