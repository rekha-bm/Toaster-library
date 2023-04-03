/*
 * Copyright (c) 2023 by VeriFone, Inc.
 * All Rights Reserved.
 * THIS FILE CONTAINS PROPRIETARY AND CONFIDENTIAL INFORMATION
 * AND REMAINS THE UNPUBLISHED PROPERTY OF VERIFONE, INC.
 *
 * Use, disclosure, or reproduction is prohibited
 * without prior written approval from VeriFone, Inc.
 */
package com.verifone.dms.agent.lib.mqtt.model

import android.os.RemoteException
import android.util.Log
import com.verifone.dms.agent.lib.MqttClientInterface
import com.verifone.dms.agent.lib.OperationResult
import com.verifone.dms.agent.lib.OperationResultCallback
import com.verifone.dms.agent.lib.Token
import com.verifone.dms.agent.lib.mqtt.exception.ClientAbandonedException
import com.verifone.dms.agent.lib.mqtt.service.Consumer
import com.verifone.dms.agent.lib.mqtt.service.MqttApi
import org.eclipse.paho.android.service.MqttAndroidClient

internal class BoundMqttClientImpl(
  private val clientInterface: MqttClientInterface,
  private val token: Token
) : MqttApi.BoundMqttClient {

  private lateinit var mqttClient: MqttAndroidClient
  val TAG = "MQTTClient"

  var result: String = ""

  @Volatile private var abandoned = false

  fun abandon() {
    abandoned = true
  }

  override fun isAbandoned(): Boolean = abandoned
  override fun onBrokerConnect(
      serverURI: String,
      clientId: String,
      onResult: Consumer<OperationResult>
  ): Boolean {
    if (isAbandoned()) {
      throw ClientAbandonedException(
          "Client has been abandoned. " +
              "Please use the latest instance provided through " +
              "VHQApiImpl.observeClients()'s onResult callback.")
    }
    return try {
      return clientInterface.onBrokerConnect(
          serverURI,
          clientId,
          object : OperationResultCallback.Stub() {
            override fun onResultProvided(result: OperationResult?) {
              Log.e(TAG, "request connection: $result")
              onResult.accept(result ?: OperationResult.FAILURE_UNKNOWN)
            }
          })
    } catch (e: RemoteException) {
      Log.e(TAG, "Failed to request heartbeat: " + e.message)
      false
    }
  }

  override fun onBrokerDisconnect(): Boolean {
    if (isAbandoned()) {
      throw ClientAbandonedException(
          "Client has been abandoned. " +
              "Please use the latest instance provided through " +
              "VHQApiImpl.observeClients()'s onResult callback.")
    }
    return try {
      return clientInterface.onBrokerDisconnect()
    } catch (e: RemoteException) {
      Log.e(TAG, "Failed to request heartbeat: " + e.message)
      false
    }
  }

  override fun onPublish(topic: String, msg: ByteArray, qos: Int, retained: Boolean): Boolean {

    if (isAbandoned()) {
      throw ClientAbandonedException(
          "Client has been abandoned. " +
              "Please use the latest instance provided through " +
              "VHQApiImpl.observeClients()'s onResult callback.")
    }
    return try {
      return clientInterface.onPublish(topic, msg, qos, retained)
    } catch (e: RemoteException) {
      Log.e(TAG, "Failed to request heartbeat: " + e.message)
      false
    }
  }

  override fun onSubscribe(topic: String, qos: Int, onResult: Consumer<OperationResult>): Boolean {

    if (isAbandoned()) {
      throw ClientAbandonedException(
          "Client has been abandoned. " +
              "Please use the latest instance provided through " +
              "VHQApiImpl.observeClients()'s onResult callback.")
    }

    return try {
      return clientInterface.onSubscribe(
          topic,
          qos,
          object : OperationResultCallback.Stub() {
            override fun onResultProvided(result: OperationResult?) {
              Log.e(TAG, "request connection: $result")
              onResult.accept(result ?: OperationResult.FAILURE_UNKNOWN)
            }
          })
    } catch (e: RemoteException) {
      Log.e(TAG, "Failed to request heartbeat: " + e.message)
      false
    }
  }

  override fun getSerialNumber(): String? {
    if (isAbandoned()) {
      throw ClientAbandonedException(
          "Client has been abandoned. " +
              "Please use the latest instance provided through " +
              "VHQApiImpl.observeClients()'s onResult callback.")
    }
    return try {
      clientInterface.serialNumber
    } catch (e: RemoteException) {
      Log.e(TAG, "Failed to request heartbeat: " + e.message)
      ""
    }
  }
  // TODO get the customer id from application to library
  override fun getCustomerId(): String? {
    return try {
      clientInterface.serialNumber
    } catch (e: RemoteException) {
      Log.e(TAG, "Failed to request heartbeat: " + e.message)
      ""
    }
    TODO("Not yet implemented")
  }
}
