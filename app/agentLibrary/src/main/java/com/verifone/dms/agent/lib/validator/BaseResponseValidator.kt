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

import com.verifone.dms.agent.lib.json.model.response.CommonResponse

interface BaseResponseValidator {
  /* headerValidation(commonResponse: CommonResponse): Boolean
  This method will be used to validating the response header against device calculated cID
  i/p : CommonResponse - header from server response
  o/p : true : if validation is success
      false : if validation fails

   */
  fun headerValidation(commonResponse: CommonResponse): Boolean
  fun verifyCMACAES(commonResponse: CommonResponse): Boolean
  fun verifyDownloadedFileSize(fileSize:Long, downloadedFileSize: Long):Boolean
}
