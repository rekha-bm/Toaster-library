/*
 * This file is auto-generated.  DO NOT MODIFY.
 */
package com.verifone.dms.agent.lib;
// Declare any non-default types here with import statements

public interface Token extends android.os.IInterface
{
  /** Default implementation for Token. */
  public static class Default implements com.verifone.dms.agent.lib.Token
  {
    @Override
    public android.os.IBinder asBinder() {
      return null;
    }
  }
  /** Local-side IPC implementation stub class. */
  public static abstract class Stub extends android.os.Binder implements com.verifone.dms.agent.lib.Token
  {
    private static final java.lang.String DESCRIPTOR = "com.verifone.dms.agent.lib.Token";
    /** Construct the stub at attach it to the interface. */
    public Stub()
    {
      this.attachInterface(this, DESCRIPTOR);
    }
    /**
     * Cast an IBinder object into an com.verifone.dms.agent.lib.Token interface,
     * generating a proxy if needed.
     */
    public static com.verifone.dms.agent.lib.Token asInterface(android.os.IBinder obj)
    {
      if ((obj==null)) {
        return null;
      }
      android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
      if (((iin!=null)&&(iin instanceof com.verifone.dms.agent.lib.Token))) {
        return ((com.verifone.dms.agent.lib.Token)iin);
      }
      return new com.verifone.dms.agent.lib.Token.Stub.Proxy(obj);
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
        default:
        {
          return super.onTransact(code, data, reply, flags);
        }
      }
    }
    private static class Proxy implements com.verifone.dms.agent.lib.Token
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
      public static com.verifone.dms.agent.lib.Token sDefaultImpl;
    }
    public static boolean setDefaultImpl(com.verifone.dms.agent.lib.Token impl) {
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
    public static com.verifone.dms.agent.lib.Token getDefaultImpl() {
      return Stub.Proxy.sDefaultImpl;
    }
  }
}
