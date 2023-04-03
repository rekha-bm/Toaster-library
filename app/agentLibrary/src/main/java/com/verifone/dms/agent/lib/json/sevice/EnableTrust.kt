/*
 * Copyright (c) 2023 by VeriFone, Inc.
 * All Rights Reserved.
 * THIS FILE CONTAINS PROPRIETARY AND CONFIDENTIAL INFORMATION
 * AND REMAINS THE UNPUBLISHED PROPERTY OF VERIFONE, INC.
 *
 * Use, disclosure, or reproduction is prohibited
 * without prior written approval from VeriFone, Inc.
 */
package com.verifone.dms.agent.json.sevice

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.GsonBuilder
import com.verifone.dms.agent.lib.crypto.CSRGen
import com.verifone.dms.agent.lib.json.model.certificate.CertificateResponse
import com.verifone.dms.agent.lib.util.AppUtil
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import org.json.JSONObject
import java.util.*
import kotlin.collections.HashMap

class EnableTrust(context: Context) {
    val requestQueue: RequestQueue = Volley.newRequestQueue(context)
    val csrGen: CSRGen = CSRGen()
    var appUtil: AppUtil = AppUtil()
    private var mCertificateResponse: CertificateResponse? = null
    suspend fun getClientCertificate(url: String?): CertificateResponse? =
        suspendCoroutine { result ->
            val certificateRequest =
                object :
                    JsonObjectRequest(
                        Method.POST,
                        url,
                        null,
                        Response.Listener { response ->
                            mCertificateResponse =
                                GsonBuilder()
                                    .create()
                                    .fromJson(response.toString(), CertificateResponse::class.java)
                            Log.d("okhttp", "mCertificateResponse: $mCertificateResponse")
                            result.resume(mCertificateResponse)
                        },
                        Response.ErrorListener { error ->
                            Log.e("TAG", "RESPONSE IS $error")
                            result.resume(null)
                        }) {
                    @RequiresApi(Build.VERSION_CODES.O)
                    override fun getBody(): ByteArray {
                        val parameters = HashMap<String, String>()
                        val csrValue = csrGen.generateCSR(appUtil.getDeviceSerialNumber().plus("__").plus(appUtil.getDeviceModel()))
                        //   val csrGenValue = csrGen.generateSelfSignedCertificate(csrValue, appUtil.getDeviceSerialNumber().plus("__").plus(appUtil.getDeviceModel()))
                        val csrValue_ = "-----BEGIN CERTIFICATE REQUEST-----\n"+ Base64.getEncoder().encodeToString(csrValue.encoded) + "\n-----END CERTIFICATE REQUEST-----"
                        parameters["certificateRequest"] = Base64.getEncoder().encodeToString(csrValue_.encodeToByteArray())

                        return JSONObject(parameters.toString()).toString().toByteArray()
                    }
                }

            Log.d("okhttp_request", "certificateRequest: $certificateRequest")
            Log.d("okhttp", "mCertificateResponse out: $mCertificateResponse")
            requestQueue.add(certificateRequest)
        }
}
