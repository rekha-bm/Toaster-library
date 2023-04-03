/*
 * Copyright (c) 2023 by VeriFone, Inc.
 * All Rights Reserved.
 * THIS FILE CONTAINS PROPRIETARY AND CONFIDENTIAL INFORMATION
 * AND REMAINS THE UNPUBLISHED PROPERTY OF VERIFONE, INC.
 *
 * Use, disclosure, or reproduction is prohibited
 * without prior written approval from VeriFone, Inc.
 */
package com.verifone.dms.agent.lib.json.sevice;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.Keep;
import androidx.annotation.Nullable;

import com.verifone.dms.agent.lib.MqttClientInterface;
import com.verifone.dms.agent.lib.OperationResult;
import com.verifone.dms.agent.lib.OperationResultCallback;
import com.verifone.dms.agent.lib.R;
import com.verifone.dms.agent.lib.Token;
import com.verifone.dms.agent.lib.util.SSLHelper;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class DMSAgentService extends Service {
    private static final int VHQ_SUCCESS = 0;
    private static final int VHQ_FAIL = 1;
    private final Set<OperationResultCallback> mConnectCallbacks = new HashSet<>();
    private final Set<OperationResultCallback> mSubscribeCallbacks = new HashSet<>();
    private final Object mMutex = new Object();
    String TAG = "DMSAgentService";
    MqttAndroidClient mqttClient;
    Boolean dmsResult = true;
    MqttConnectOptions options;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MqttClientInterface.Stub() {
            @Override
            public boolean onBrokerConnect(String serverUri, String clientId, OperationResultCallback callback) {
                Log.i(TAG, "onBrokerConnect called");
                mqttClient = new MqttAndroidClient(getApplicationContext(), serverUri, clientId);
                mqttClient.setCallback(new MqttCallback() {

                    @Override
                    public void connectionLost(Throwable cause) {
                        Log.d(TAG, "Connection lost ${cause.toString()}");
                    }

                    @Override
                    public void messageArrived(String topic, MqttMessage message) throws Exception {
                        // JSONObject jsonMsg = new JSONObject(new String(message.getPayload()));

                        Intent intent = new Intent();
                        intent.putExtra("Topic", topic);
                        intent.putExtra("Message", message.toString());
                        intent.setAction("com.dms.agent.message_receiver");
                        getApplicationContext().sendBroadcast(intent);

                    }

                    @Override
                    public void deliveryComplete(IMqttDeliveryToken token) {

                    }
                });
                options = new MqttConnectOptions();
                options.setConnectionTimeout(60 * 10);
                options.setKeepAliveInterval(60 * 5);
                options.setUserName("gaurav");
                options.setPassword("Test@123".toCharArray());
                SSLHelper sslHelper = new SSLHelper();
                try {
                    InputStream caCrtFile = getResources().openRawResource(R.raw.root);
                    InputStream crtFile = getResources().openRawResource(R.raw.clientcert);
                    InputStream keyFile = getResources().openRawResource(R.raw.clientkey);
                    options.setSocketFactory(sslHelper.getSocketFactory(caCrtFile, crtFile, keyFile, ""));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    Log.d(TAG, "Connect called");
                    mqttClient.connect(options, getApplicationContext(), new IMqttActionListener() {

                        @Override
                        public void onSuccess(IMqttToken asyncActionToken) {
                            onConnectProcessed(VHQ_SUCCESS);

                        }

                        @Override
                        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                            Log.d(TAG, "Connection fail");
                            onConnectProcessed(VHQ_FAIL);

                        }
                    });
                } catch (MqttException e) {
                    e.printStackTrace();
                    Log.d(TAG, e.toString());
                }
                Log.d(TAG, "return " + dmsResult);
                synchronized (mMutex) {
                    mConnectCallbacks.add(callback);
                }

                return true;
            }

            @Override
            public void onConnect(Token token) throws RemoteException {

                IBinder binder = token.asBinder();
                Log.i(TAG, "Client: has connected to DMSAgentService.");
                IBinder.DeathRecipient deathRecipient = () -> {
                    Log.i(TAG, "Client: has died - disconnecting from DMSAgentService.");

                };

                binder.linkToDeath(deathRecipient, 0);
            }

            @Override
            public String getSerialNumber() {
                String serialNumber;

                try {
                    Class<?> c = Class.forName("android.os.SystemProperties");
                    Method get = c.getMethod("get", String.class);

                    serialNumber = (String) get.invoke(c, "gsm.sn1");
                    if (serialNumber.equals(""))
                        serialNumber = (String) get.invoke(c, "ril.serialnumber");
                    if (serialNumber.equals(""))
                        serialNumber = (String) get.invoke(c, "ro.serialno");
                    if (serialNumber.equals(""))
                        serialNumber = (String) get.invoke(c, "sys.serialnumber");
                    if (serialNumber.equals("")) serialNumber = Build.SERIAL;

                    // If none of the methods above worked
                    if (serialNumber.equals("")) serialNumber = null;
                } catch (Exception e) {
                    e.printStackTrace();
                    serialNumber = null;
                }

                return serialNumber;
            }

            @Override
            public void onDisconnect(Token token) {

            }

            @Override
            public boolean onBrokerDisconnect() {
                try {
                    if (mqttClient != null) {
                        mqttClient.disconnect();
                    } else {
                        return false;
                    }
                } catch (MqttException e) {
                    e.printStackTrace();
                    return false;
                }
                Log.d(TAG, "return disconnect");
                return true;
            }

            @Override
            public boolean onPublish(String topic, byte[] msg, int qos, boolean retained) throws RemoteException {
                try {
                    MqttMessage message = new MqttMessage();
                    message.setPayload(msg);
                    message.setQos(qos);
                    message.setRetained(retained);

                    mqttClient.publish(topic, message, null, new IMqttActionListener() {
                        @Override
                        public void onSuccess(IMqttToken asyncActionToken) {
                            Log.d(TAG, "$msg published to $topic");
                        }

                        @Override
                        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                            Log.d(TAG, "Failed to publish $msg to $topic");
                        }
                    });

                } catch (MqttPersistenceException e) {
                    e.printStackTrace();
                } catch (MqttException e) {
                    e.printStackTrace();
                }

                return true;
            }


            @Override
            public boolean onSubscribe(String topic, int qos, OperationResultCallback callback) {

                try {
                    mqttClient.subscribe(topic, qos, null, new IMqttActionListener() {
                        @Override
                        public void onSuccess(IMqttToken asyncActionToken) {
                            Log.d(TAG, "Subscribed to $topic");
                            onSubscribeProcessed(VHQ_SUCCESS);
                        }

                        @Override
                        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                            Log.d(TAG, "Failed to subscribe $topic");
                            onSubscribeProcessed(VHQ_FAIL);
                        }
                    });
                } catch (MqttException e) {
                    e.printStackTrace();
                }

                synchronized (mMutex) {
                    mSubscribeCallbacks.add(callback);
                }

                return true;

            }

            @Override
            public String getCustomerId() throws RemoteException {
                return null;
            }

        };
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate() called");
        super.onCreate();

        startService(new Intent(this, DMSAgentService.class));
    }

    @Keep
    public void onConnectProcessed(int result) {
        Log.d(TAG, "[onConnectProcessed]");

        List<OperationResultCallback> callbacks;
        synchronized (mMutex) {
            callbacks = new ArrayList<>(mConnectCallbacks);
            mConnectCallbacks.clear();
        }

        Log.i(TAG, "Notifying " + callbacks.size() + " callbacks");
        for (OperationResultCallback callback : callbacks) {
            if (callback.asBinder().isBinderAlive()) {
                try {
                    switch (result) {
                        case VHQ_SUCCESS:
                            callback.onResultProvided(OperationResult.SUCCESS);
                            break;
                        case VHQ_FAIL:
                            callback.onResultProvided(OperationResult.FAILURE_NO_CONNECTION);
                            break;
                        default:
                            callback.onResultProvided(OperationResult.FAILURE_UNKNOWN);
                            break;
                    }
                } catch (RemoteException e) {
                    Log.e(TAG, "Failed to notify callback: " + callback.asBinder());
                }
            } else {
                Log.i(TAG, " Callback " + callback.asBinder() + " is dead, skipping.");
            }
        }
    }

    @Keep
    public void onSubscribeProcessed(int result) {
        Log.d(TAG, "[onConnectProcessed]");

        List<OperationResultCallback> callbacks;
        synchronized (mMutex) {
            callbacks = new ArrayList<>(mSubscribeCallbacks);
            mSubscribeCallbacks.clear();
        }

        Log.i(TAG, "Notifying " + callbacks.size() + " callbacks");
        for (OperationResultCallback callback : callbacks) {
            if (callback.asBinder().isBinderAlive()) {
                try {

                    switch (result) {
                        case VHQ_SUCCESS:
                            callback.onResultProvided(OperationResult.SUCCESS);
                            break;
                        case VHQ_FAIL:
                            callback.onResultProvided(OperationResult.FAILURE_NO_CONNECTION);
                            break;
                        default:
                            callback.onResultProvided(OperationResult.FAILURE_UNKNOWN);
                            break;
                    }
                } catch (RemoteException e) {
                    Log.e(TAG, "Failed to notify callback: " + callback.asBinder());
                }
            } else {
                Log.i(TAG, " Callback " + callback.asBinder() + " is dead, skipping.");
            }
        }
    }
}