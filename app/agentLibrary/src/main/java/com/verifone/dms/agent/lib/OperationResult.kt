/*
 * Copyright (c) 2022 by VeriFone, Inc.
 * All Rights Reserved.
 * THIS FILE CONTAINS PROPRIETARY AND CONFIDENTIAL INFORMATION
 * AND REMAINS THE UNPUBLISHED PROPERTY OF VERIFONE, INC.
 *
 * Use, disclosure, or reproduction is prohibited
 * without prior written approval from VeriFone, Inc.
 */
package com.verifone.dms.agent.lib

import android.os.Parcel
import android.os.Parcelable

enum class OperationResult : Parcelable {
  SUCCESS,
  FAILURE_NO_CONNECTION,
  FAILURE_UNKNOWN,
  SOFTWARE_UNKNOW,
  LOCATIONID_NOT_AVAILABLE;

  override fun describeContents(): Int {
    return 0
  }

  override fun writeToParcel(dest: Parcel, flags: Int) {
      dest.writeString(name)
  }

  companion object CREATOR : Parcelable.Creator<OperationResult> {
    override fun createFromParcel(parcel: Parcel): OperationResult =
        OperationResult.valueOf(parcel.readString()!!)

    override fun newArray(size: Int): Array<OperationResult?> = arrayOfNulls(size)
  }
}
