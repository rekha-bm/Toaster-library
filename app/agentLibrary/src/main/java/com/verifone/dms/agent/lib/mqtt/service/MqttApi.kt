/*
 * Copyright (c) 2023 by VeriFone, Inc.
 * All Rights Reserved.
 * THIS FILE CONTAINS PROPRIETARY AND CONFIDENTIAL INFORMATION
 * AND REMAINS THE UNPUBLISHED PROPERTY OF VERIFONE, INC.
 *
 * Use, disclosure, or reproduction is prohibited
 * without prior written approval from VeriFone, Inc.
 */

package com.verifone.dms.agent.lib.mqtt.service

import android.content.Context
import com.verifone.dms.agent.lib.OperationResult
import com.verifone.dms.agent.lib.mqtt.PendingRequest
import com.verifone.dms.agent.lib.mqtt.model.MqttApiImpl

interface MqttApi {

  companion object Factory {
    fun createNew(context: Context): MqttApi = MqttApiImpl(context)
  }

  fun observeClients(onNewClient: Consumer<BoundMqttClient>): PendingRequest

  interface BoundMqttClient {
    fun isAbandoned(): Boolean
    fun onBrokerConnect(
        serverURI: String,
        clientId: String,
        onResult: Consumer<OperationResult>
    ): Boolean
    fun onBrokerDisconnect(): Boolean
    fun onPublish(topic: String, msg: ByteArray, qos: Int, retained: Boolean): Boolean
    fun onSubscribe(topic: String, qos: Int, onResult: Consumer<OperationResult>): Boolean
    fun getSerialNumber(): String?
    fun getCustomerId(): String?
  }
}
