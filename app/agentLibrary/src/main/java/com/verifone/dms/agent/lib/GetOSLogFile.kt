/*
 * Copyright (c) 2023 by VeriFone, Inc.
 * All Rights Reserved.
 * THIS FILE CONTAINS PROPRIETARY AND CONFIDENTIAL INFORMATION
 * AND REMAINS THE UNPUBLISHED PROPERTY OF VERIFONE, INC.
 *
 * Use, disclosure, or reproduction is prohibited
 * without prior written approval from VeriFone, Inc.
 */
package com.verifone.dms.agent.lib

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.verifone.dms.agent.json.sevice.OsLogsUploads
import com.verifone.dms.agent.lib.json.model.response.CommonResponse
import com.verifone.dms.agent.lib.util.FilePathConstants.logsPaths
import com.verifone.dms.agent.lib.util.FilePathConstants.logsZipLocation
import com.verifone.dms.agent.lib.util.FilePathConstants.pathDirectory
import com.verifone.dms.agent.lib.util.ZipUnZipUtils
import com.verifone.dms.agent.lib.validator.ResponseValidatorImpl

class GetOSLogFile {
  @RequiresApi(Build.VERSION_CODES.O)
  suspend fun getOSLog(jsonObject: CommonResponse, applicationContext: Context): Boolean? {
    var validator = ResponseValidatorImpl()

    var oslogsUploads = OsLogsUploads()

    Log.e("Path", logsZipLocation)
    if (validator.headerValidation(jsonObject)) {
      ZipUnZipUtils.zipAll(
        logsPaths,
        logsZipLocation
      )
    }

    return if (getUploadURl(jsonObject,applicationContext)==null){
      false
    }else{
      oslogsUploads.uploadOSLogs(getUploadURl(jsonObject,applicationContext),pathDirectory, applicationContext)
    }
  }

  private fun getUploadURl(jsonObject: CommonResponse, context: Context) =
    jsonObject.serverMessage?.content?.opSets?.opSet?.get(0)?.ops?.get(0)?.parameters?.endPoint.toString()
}
