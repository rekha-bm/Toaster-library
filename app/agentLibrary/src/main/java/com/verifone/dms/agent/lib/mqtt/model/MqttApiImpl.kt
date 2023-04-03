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

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import com.verifone.dms.agent.lib.BuildConfig
import com.verifone.dms.agent.lib.MqttClientInterface
import com.verifone.dms.agent.lib.Token
import com.verifone.dms.agent.lib.mqtt.PendingRequest
import com.verifone.dms.agent.lib.mqtt.service.Consumer
import com.verifone.dms.agent.lib.mqtt.service.DisconnectedMqttClient
import com.verifone.dms.agent.lib.mqtt.service.MqttApi

internal class MqttApiImpl(private val context: Context) : MqttApi {

  companion object {
    private const val TAG = "MqttApiImpl"
    private const val packageScheme = "package"
    private const val actionBindVhq = "com.verifone.mqtt.ACTION_BIND_MQTT"
  }

  override fun observeClients(onNewClient: Consumer<MqttApi.BoundMqttClient>): PendingRequest {

    var boundService: MqttClientInterface? = null
    val agentToken: Token = object : Token.Stub() {}
    val serviceConnection =
        object : ServiceConnection {
          private var lastClient: BoundMqttClientImpl? = null

          override fun onServiceDisconnected(name: ComponentName?) {
            Log.i(TAG, "onServiceDisconnected for: ${agentToken.hashCode()}")
            lastClient?.abandon()
            boundService = null
            onNewClient.accept(DisconnectedMqttClient)
          }

          override fun onServiceConnected(name: ComponentName?, service: IBinder?) {

            val receivedService = MqttClientInterface.Stub.asInterface(service)
            boundService = receivedService
            receivedService.onConnect(agentToken)
            BoundMqttClientImpl(receivedService, agentToken).apply {
              lastClient = this
              onNewClient.accept(this)
            }
          }
        }

    val bindService =
        func@{
          if (!bindDMSAgentService(serviceConnection)) {
            Log.i(TAG, "Binding failed check logcat for more information.")
            boundService = null
            return@func false
          }
          return@func true
        }
    val serviceBound = bindService()

    return object : PendingRequest {
      override fun cancel() {
        // Log.i(TAG, "${agentToken.hashCode()} is cleaning up.")
        boundService?.onDisconnect(agentToken)
        if (serviceBound) {
          context.unbindService(serviceConnection)
        } else {
          Log.e(TAG, "Could not unbind the service - it was never bound.")
        }
        try {
          // context.unregisterReceiver(receiver)
        } catch (e: Exception) {
          Log.e(TAG, "Could not unregister broadcast receiver for ${agentToken.hashCode()}")
        }
      }
    }
  }

  private fun bindDMSAgentService(serviceConnection: ServiceConnection): Boolean {
    val intent = Intent(actionBindVhq).setPackage(BuildConfig.VHQ_APPLICATION_ID)
    val bindRequested = context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    if (!bindRequested) {
      context.unbindService(serviceConnection)
      return false
    }
    return true
  }
}
