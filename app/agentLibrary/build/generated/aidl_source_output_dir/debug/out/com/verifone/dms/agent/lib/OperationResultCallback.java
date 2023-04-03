/*
 * This file is auto-generated.  DO NOT MODIFY.
 */
package com.verifone.dms.agent.lib;
public interface OperationResultCallback extends android.os.IInterface
{
  /** Default implementation for OperationResultCallback. */
  public static class Default implements com.verifone.dms.agent.lib.OperationResultCallback
  {
    /**
         * Demonstrates some basic types that you can use as parameters
         * and return values in AIDL.
         */
    @Override public void onResultProvided(com.verifone.dms.agent.lib.OperationResult result) throws android.os.RemoteException
    {
    }
    @Override
    public android.os.IBinder asBinder() {
      return null;
    }
  }
  /** Local-side IPC implementation stub class. */
  public static abstract class Stub extends android.os.Binder implements com.verifone.dms.agent.lib.OperationResultCallback
  {
    private static final java.lang.String DESCRIPTOR = "com.verifone.dms.agent.lib.OperationResultCallback";
    /** Construct the stub at attach it to the interface. */
    public Stub()
    {
      this.attachInterface(this, DESCRIPTOR);
    }
    /**
     * Cast an IBinder object into an com.verifone.dms.agent.lib.OperationResultCallback interface,
     * generating a proxy if needed.
     */
    public static com.verifone.dms.agent.lib.OperationResultCallback asInterface(android.os.IBinder obj)
    {
      if ((obj==null)) {
        return null;
      }
      android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
      if (((iin!=null)&&(iin instanceof com.verifone.dms.agent.lib.OperationResultCallback))) {
        return ((com.verifone.dms.agent.lib.OperationResultCallback)iin);
      }
      return new com.verifone.dms.agent.lib.OperationResultCallback.Stub.Proxy(obj);
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
        case TRANSACTION_onResultProvided:
        {
          data.enforceInterface(descriptor);
          com.verifone.dms.agent.lib.OperationResult _arg0;
          if ((0!=data.readInt())) {
            _arg0 = com.verifone.dms.agent.lib.OperationResult.CREATOR.createFromParcel(data);
          }
          else {
            _arg0 = null;
          }
          this.onResultProvided(_arg0);
          reply.writeNoException();
          return true;
        }
        default:
        {
          return super.onTransact(code, data, reply, flags);
        }
      }
    }
    private static class Proxy implements com.verifone.dms.agent.lib.OperationResultCallback
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
      @Override public void onResultProvided(com.verifone.dms.agent.lib.OperationResult result) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          if ((result!=null)) {
            _data.writeInt(1);
            result.writeToParcel(_data, 0);
          }
          else {
            _data.writeInt(0);
          }
          boolean _status = mRemote.transact(Stub.TRANSACTION_onResultProvided, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().onResultProvided(result);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      public static com.verifone.dms.agent.lib.OperationResultCallback sDefaultImpl;
    }
    static final int TRANSACTION_onResultProvided = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
    public static boolean setDefaultImpl(com.verifone.dms.agent.lib.OperationResultCallback impl) {
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
    public static com.verifone.dms.agent.lib.OperationResultCallback getDefaultImpl() {
      return Stub.Proxy.sDefaultImpl;
    }
  }
  /**
       * Demonstrates some basic types that you can use as parameters
       * and return values in AIDL.
       */
  public void onResultProvided(com.verifone.dms.agent.lib.OperationResult result) throws android.os.RemoteException;
}
