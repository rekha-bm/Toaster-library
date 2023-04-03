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

data class ResponseOp(
    @SerializedName("id") val id: Int?,
    @SerializedName("name") val name: String?,
    @SerializedName("onError") val onError: String?,
    @SerializedName("onInstall") val onInstall: String?,
    @SerializedName("parameters") val parameters: com.verifone.dms.agent.lib.json.model.response.Parameters?
)
