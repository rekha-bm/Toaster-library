/*
 * Copyright (c) 2023 by VeriFone, Inc.
 * All Rights Reserved.
 * THIS FILE CONTAINS PROPRIETARY AND CONFIDENTIAL INFORMATION
 * AND REMAINS THE UNPUBLISHED PROPERTY OF VERIFONE, INC.
 *
 * Use, disclosure, or reproduction is prohibited
 * without prior written approval from VeriFone, Inc.
 */ package com.verifone.dms.agent.lib.json.model.request.deviceregistration

import com.google.gson.annotations.SerializedName

data class Content(
    @SerializedName("encrAgentKeys") val encrAgentKeys: com.verifone.dms.agent.lib.json.model.request.deviceregistration.EncrAgentKeys,
    @SerializedName("certificate") val certificate: com.verifone.dms.agent.lib.json.model.request.deviceregistration.Certificate,
    @SerializedName("encrAgentKeysSignature") val encrAgentKeysSignature: com.verifone.dms.agent.lib.json.model.request.deviceregistration.EncrAgentKeysSignature
)
