/*
 * Copyright (c) 2023 by VeriFone, Inc.
 * All Rights Reserved.
 * THIS FILE CONTAINS PROPRIETARY AND CONFIDENTIAL INFORMATION
 * AND REMAINS THE UNPUBLISHED PROPERTY OF VERIFONE, INC.
 *
 * Use, disclosure, or reproduction is prohibited
 * without prior written approval from VeriFone, Inc.
 */
package com.verifone.dms.agent.lib.json.model.certificate

import com.google.gson.annotations.SerializedName

data class CertificateResponse(
    @SerializedName("status") var status: com.verifone.dms.agent.lib.json.model.certificate.Status? = com.verifone.dms.agent.lib.json.model.certificate.Status(),
    @SerializedName("data") var data: com.verifone.dms.agent.lib.json.model.certificate.DataResponse? = com.verifone.dms.agent.lib.json.model.certificate.DataResponse()
)
