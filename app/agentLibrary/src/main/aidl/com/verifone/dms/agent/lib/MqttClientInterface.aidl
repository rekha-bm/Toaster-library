/*
 * Copyright (c) 2023 by VeriFone, Inc.
 * All Rights Reserved.
 * THIS FILE CONTAINS PROPRIETARY AND CONFIDENTIAL INFORMATION
 * AND REMAINS THE UNPUBLISHED PROPERTY OF VERIFONE, INC.
 *
 * Use, disclosure, or reproduction is prohibited
 * without prior written approval from VeriFone, Inc.
 */
// MqttClientInterface.aidl
package com.verifone.dms.agent.lib;

// Declare any non-default types here with import statements
import com.verifone.dms.agent.lib.Token;
import com.verifone.dms.agent.lib.OperationResultCallback;
interface MqttClientInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
      boolean onBrokerConnect(in String serverUri, in String client_id,OperationResultCallback callback);
      void onConnect(in Token token);
      String getSerialNumber();
      void onDisconnect(in Token token);
      boolean onBrokerDisconnect();
      boolean onPublish(in String topic, in byte[] msg, in int qos, in boolean retained);
      boolean onSubscribe(in String topic, in int qos, OperationResultCallback callback);
      String getCustomerId();
}