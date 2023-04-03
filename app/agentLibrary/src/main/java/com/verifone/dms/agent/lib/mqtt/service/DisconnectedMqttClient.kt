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

import com.verifone.dms.agent.lib.OperationResult
import com.verifone.dms.agent.lib.mqtt.exception.ClientAbandonedException

object DisconnectedMqttClient : MqttApi.BoundMqttClient {

  private fun throwError(): Nothing =
      throw ClientAbandonedException(
          "Client has been abandoned. " +
              "The API is currently not bound to the DMS Agent Service." +
              "Once the connection is restored, a new instance will be provided over the " +
              "BoundDMSAgentClientImpl.observeClients() that is not abandoned.")

  override fun isAbandoned(): Boolean = true

  override fun onBrokerConnect(
      serverURI: String,
      clientId: String,
      onResult: Consumer<OperationResult>
  ): Boolean = throwError()
  override fun onBrokerDisconnect(): Boolean = throwError()
  override fun onPublish(topic: String, msg: ByteArray, qos: Int, retained: Boolean): Boolean =
      throwError()
  override fun onSubscribe(topic: String, qos: Int, onResult: Consumer<OperationResult>): Boolean =
      throwError()

  override fun getSerialNumber(): String? = throwError()
  override fun getCustomerId(): String? = throwError()
}
