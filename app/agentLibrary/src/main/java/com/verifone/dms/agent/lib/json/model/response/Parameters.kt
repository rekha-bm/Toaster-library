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

data class Parameters(
    @SerializedName("dldProtocol") val dldProtocol: String?,
    @SerializedName("fileHash") val fileHash: String?,
    @SerializedName("fileName") val fileName: String?,
    @SerializedName("fileSize") val fileSize: Int?,
    @SerializedName("fileType") val fileType: String?,
    @SerializedName("hashAlgo") val hashAlgo: String?,
    @SerializedName("url") val url: String?,
    @SerializedName("dataSetName") val dataSetName: String?,
    @SerializedName("endPoint") val endPoint: String?,
    @SerializedName("uploadMethod") val uploadMethod: String?,
    @SerializedName("time") val time: Int?,
    @SerializedName("timeZone") val timeZone: String?,
    @SerializedName("utcTime") val utcTime: Int?
)
