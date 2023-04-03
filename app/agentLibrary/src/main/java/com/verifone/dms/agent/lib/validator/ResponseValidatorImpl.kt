/*
 * Copyright (c) 2023 by VeriFone, Inc.
 * All Rights Reserved.
 * THIS FILE CONTAINS PROPRIETARY AND CONFIDENTIAL INFORMATION
 * AND REMAINS THE UNPUBLISHED PROPERTY OF VERIFONE, INC.
 *
 * Use, disclosure, or reproduction is prohibited
 * without prior written approval from VeriFone, Inc.
 */
package com.verifone.dms.agent.lib.validator

import android.os.Build
import androidx.annotation.RequiresApi
import com.verifone.dms.agent.lib.Constant
import com.verifone.dms.agent.lib.crypto.CryptoManager
import com.verifone.dms.agent.lib.json.model.response.CommonResponse
import com.verifone.dms.agent.lib.util.AppUtil

class ResponseValidatorImpl : BaseResponseValidator {
  val appUtil: AppUtil = AppUtil()
  @RequiresApi(Build.VERSION_CODES.O)
/* headerValidation(commonResponse: CommonResponse): Boolean
This method will be used to validating the response header against device calculated cID
i/p : CommonResponse - header from server response
o/p : true : if validation is success
    false : if validation fails

 */
override fun headerValidation(commonResponse: CommonResponse): Boolean {
  val responseCId = commonResponse.serverMessage.header.cId
  val responseSubstringCId = responseCId.subSequence(2, 13)

  val calculatedCId = appUtil.getDeviceCId()
  val subCalculatedCId = calculatedCId.subSequence(2, 13)
  if (commonResponse.serverMessage.header.ver.equals(Constant.VERSION) &&
      responseSubstringCId.equals(subCalculatedCId)) {
    return true
  }
  return false
}

override fun verifyCMACAES(commonResponse: CommonResponse): Boolean {
  val serverSignatureValue = commonResponse.serverMessage.signature.value
  val cryptoManager = CryptoManager()
  val calculatedMAC = cryptoManager.getResponseCmacAES(commonResponse.serverMessage.header,commonResponse.serverMessage.content)
  if (calculatedMAC.equals(serverSignatureValue)){
    return true
  }
  return false
}
  override fun verifyDownloadedFileSize(fileSize: Long, downloadedFileSize: Long): Boolean {
    return fileSize == downloadedFileSize
  }
}
