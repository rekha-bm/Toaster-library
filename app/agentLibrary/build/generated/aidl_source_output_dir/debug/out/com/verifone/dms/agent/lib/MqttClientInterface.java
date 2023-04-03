/*
 * This file is auto-generated.  DO NOT MODIFY.
 */
package com.verifone.dms.agent.lib;
public interface MqttClientInterface extends android.os.IInterface
{
  /** Default implementation for MqttClientInterface. */
  public static class Default implements com.verifone.dms.agent.lib.MqttClientInterface
  {
    /**
         * Demonstrates some basic types that you can use as parameters
         * and return values in AIDL.
         */
    @Override public boolean onBrokerConnect(java.lang.String serverUri, java.lang.String client_id, com.verifone.dms.agent.lib.OperationResultCallback callback) throws android.os.RemoteException
    {
      return false;
    }
    @Override public void onConnect(com.verifone.dms.agent.lib.Token token) throws android.os.RemoteException
    {
    }
    @Override public java.lang.String getSerialNumber() throws android.os.RemoteException
    {
      return null;
    }
    @Override public void onDisconnect(com.verifone.dms.agent.lib.Token token) throws android.os.RemoteException
    {
    }
    @Override public boolean onBrokerDisconnect() throws android.os.RemoteException
    {
      return false;
    }
    @Override public boolean onPublish(java.lang.String topic, byte[] msg, int qos, boolean retained) throws android.os.RemoteException
    {
      return false;
    }
    @Override public boolean onSubscribe(java.lang.String topic, int qos, com.verifone.dms.agent.lib.OperationResultCallback callback) throws android.os.RemoteException
    {
      return false;
    }
    @Override public java.lang.String getCustomerId() throws android.os.RemoteException
    {
      return null;
    }
    @Override
    public android.os.IBinder asBinder() {
      return null;
    }
  }
  /** Local-side IPC implementation stub class. */
  public static abstract class Stub extends android.os.Binder implements com.verifone.dms.agent.lib.MqttClientInterface
  {
    private static final java.lang.String DESCRIPTOR = "com.verifone.dms.agent.lib.MqttClientInterface";
    /** Construct the stub at attach it to the interface. */
    public Stub()
    {
      this.attachInterface(this, DESCRIPTOR);
    }
    /**
     * Cast an IBinder object into an com.verifone.dms.agent.lib.MqttClientInterface interface,
     * generating a proxy if needed.
     */
    public static com.verifone.dms.agent.lib.MqttClientInterface asInterface(android.os.IBinder obj)
    {
      if ((obj==null)) {
        return null;
      }
      android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
      if (((iin!=null)&&(iin instanceof com.verifone.dms.agent.lib.MqttClientInterface))) {
        return ((com.verifone.dms.agent.lib.MqttClientInterface)iin);
      }
      return new com.verifone.dms.agent.lib.MqttClientInterface.Stub.Proxy(obj);
    }
    @Override public android.os.IBinder asBinder()
    {
      return this;
    }
    @Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
    {
      java.lang.String descriptor = DESCRIPTOR;
      switch (code)
      {
        case INTERFACE_TRANSACTION:
        {
          reply.writeString(descriptor);
          return true;
        }
        case TRANSACTION_onBrokerConnect:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          java.lang.String _arg1;
          _arg1 = data.readString();
          com.verifone.dms.agent.lib.OperationResultCallback _arg2;
          _arg2 = com.verifone.dms.agent.lib.OperationResultCallback.Stub.asInterface(data.readStrongBinder());
          boolean _result = this.onBrokerConnect(_arg0, _arg1, _arg2);
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          return true;
        }
        case TRANSACTION_onConnect:
        {
          data.enforceInterface(descriptor);
          com.verifone.dms.agent.lib.Token _arg0;
          _arg0 = com.verifone.dms.agent.lib.Token.Stub.asInterface(data.readStrongBinder());
          this.onConnect(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_getSerialNumber:
        {
          data.enforceInterface(descriptor);
          java.lang.String _result = this.getSerialNumber();
          reply.writeNoException();
          reply.writeString(_result);
          return true;
        }
        case TRANSACTION_onDisconnect:
        {
          data.enforceInterface(descriptor);
          com.verifone.dms.agent.lib.Token _arg0;
          _arg0 = com.verifone.dms.agent.lib.Token.Stub.asInterface(data.readStrongBinder());
          this.onDisconnect(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_onBrokerDisconnect:
        {
          data.enforceInterface(descriptor);
          boolean _result = this.onBrokerDisconnect();
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          return true;
        }
        case TRANSACTION_onPublish:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          byte[] _arg1;
          _arg1 = data.createByteArray();
          int _arg2;
          _arg2 = data.readInt();
          boolean _arg3;
          _arg3 = (0!=data.readInt());
          boolean _result = this.onPublish(_arg0, _arg1, _arg2, _arg3);
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          return true;
        }
        case TRANSACTION_onSubscribe:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          int _arg1;
          _arg1 = data.readInt();
          com.verifone.dms.agent.lib.OperationResultCallback _arg2;
          _arg2 = com.verifone.dms.agent.lib.OperationResultCallback.Stub.asInterface(data.readStrongBinder());
          boolean _result = this.onSubscribe(_arg0, _arg1, _arg2);
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          return true;
        }
        case TRANSACTION_getCustomerId:
        {
          data.enforceInterface(descriptor);
          java.lang.String _result = this.getCustomerId();
          reply.writeNoException();
          reply.writeString(_result);
          return true;
        }
        default:
        {
          return super.onTransact(code, data, reply, flags);
        }
      }
    }
    private static class Proxy implements com.verifone.dms.agent.lib.MqttClientInterface
    {
      private android.os.IBinder mRemote;
      Proxy(android.os.IBinder remote)
      {
        mRemote = remote;
      }
      @Override public android.os.IBinder asBinder()
      {
        return mRemote;
      }
      public java.lang.String getInterfaceDescriptor()
      {
        return DESCRIPTOR;
      }
      /**
           * Demonstrates some basic types that you can use as parameters
           * and return values in AIDL.
           */
      @Override public boolean onBrokerConnect(java.lang.String serverUri, java.lang.String client_id, com.verifone.dms.agent.lib.OperationResultCallback callback) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(serverUri);
          _data.writeString(client_id);
          _data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_onBrokerConnect, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().onBrokerConnect(serverUri, client_id, callback);
          }
          _reply.readException();
          _result = (0!=_reply.readInt());
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public void onConnect(com.verifone.dms.agent.lib.Token token) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeStrongBinder((((token!=null))?(token.asBinder()):(null)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_onConnect, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().onConnect(token);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public java.lang.String getSerialNumber() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.lang.String _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getSerialNumber, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getSerialNumber();
          }
          _reply.readException();
          _result = _reply.readString();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public void onDisconnect(com.verifone.dms.agent.lib.Token token) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeStrongBinder((((token!=null))?(token.asBinder()):(null)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_onDisconnect, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().onDisconnect(token);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public boolean onBrokerDisconnect() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_onBrokerDisconnect, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().onBrokerDisconnect();
          }
          _reply.readException();
          _result = (0!=_reply.readInt());
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public boolean onPublish(java.lang.String topic, byte[] msg, int qos, boolean retained) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(topic);
          _data.writeByteArray(msg);
          _data.writeInt(qos);
          _data.writeInt(((retained)?(1):(0)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_onPublish, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().onPublish(topic, msg, qos, retained);
          }
          _reply.readException();
          _result = (0!=_reply.readInt());
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public boolean onSubscribe(java.lang.String topic, int qos, com.verifone.dms.agent.lib.OperationResultCallback callback) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(topic);
          _data.writeInt(qos);
          _data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_onSubscribe, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().onSubscribe(topic, qos, callback);
          }
          _reply.readException();
          _result = (0!=_reply.readInt());
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public java.lang.String getCustomerId() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.lang.String _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getCustomerId, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getCustomerId();
          }
          _reply.readException();
          _result = _reply.readString();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      public static com.verifone.dms.agent.lib.MqttClientInterface sDefaultImpl;
    }
    static final int TRANSACTION_onBrokerConnect = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
    static final int TRANSACTION_onConnect = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
    static final int TRANSACTION_getSerialNumber = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
    static final int TRANSACTION_onDisconnect = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
    static final int TRANSACTION_onBrokerDisconnect = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
    static final int TRANSACTION_onPublish = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
    static final int TRANSACTION_onSubscribe = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
    static final int TRANSACTION_getCustomerId = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
    public static boolean setDefaultImpl(com.verifone.dms.agent.lib.MqttClientInterface impl) {
      // Only one user of this interface can use this function
      // at a time. This is a heuristic to detect if two different
      // users in the same process use this function.
      if (Stub.Proxy.sDefaultImpl != null) {
        throw new IllegalStateException("setDefaultImpl() called twice");
      }
      if (impl != null) {
        Stub.Proxy.sDefaultImpl = impl;
        return true;
      }
      return false;
    }
    public static com.verifone.dms.agent.lib.MqttClientInterface getDefaultImpl() {
      return Stub.Proxy.sDefaultImpl;
    }
  }
  /**
       * Demonstrates some basic types that you can use as parameters
       * and return values in AIDL.
       */
  public boolean onBrokerConnect(java.lang.String serverUri, java.lang.String client_id, com.verifone.dms.agent.lib.OperationResultCallback callback) throws android.os.RemoteException;
  public void onConnect(com.verifone.dms.agent.lib.Token token) throws android.os.RemoteException;
  public java.lang.String getSerialNumber() throws android.os.RemoteException;
  public void onDisconnect(com.verifone.dms.agent.lib.Token token) throws android.os.RemoteException;
  public boolean onBrokerDisconnect() throws android.os.RemoteException;
  public boolean onPublish(java.lang.String topic, byte[] msg, int qos, boolean retained) throws android.os.RemoteException;
  public boolean onSubscribe(java.lang.String topic, int qos, com.verifone.dms.agent.lib.OperationResultCallback callback) throws android.os.RemoteException;
  public java.lang.String getCustomerId() throws android.os.RemoteException;
}
