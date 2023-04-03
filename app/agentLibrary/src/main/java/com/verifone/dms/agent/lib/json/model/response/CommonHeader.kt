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

data class CommonHeader(
    @SerializedName("cId") val cId: String,
    @SerializedName("dMsgId") val dMsgId: Int,
    @SerializedName("sMsgId") val sMsgId: Int,
    @SerializedName("msgType") val msgType: String,
    @SerializedName("ver") val ver: String
)
