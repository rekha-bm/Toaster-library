/*
 * Copyright (c) 2023 by VeriFone, Inc.
 * All Rights Reserved.
 * THIS FILE CONTAINS PROPRIETARY AND CONFIDENTIAL INFORMATION
 * AND REMAINS THE UNPUBLISHED PROPERTY OF VERIFONE, INC.
 *
 * Use, disclosure, or reproduction is prohibited
 * without prior written approval from VeriFone, Inc.
 */
package com.verifone.dms.agent.lib.json.model.certificate

enum class TrustErrorCode(errorcode: Int, error_msg: String) {
  Certificate_Already_Issued(1001, "Certificate is already issued for this device."),
  Device_Not_Boarded(1002, "Device not found in DMS"),
  Customer_Not_Boarded(1003, "Invalid Customer Id"),
  Invalid_CSR_attribute(1004, "One or more CSR attribute values in request are invalid"),
  Missing_CSR_attribute(
      1005, "One or more mandatory CSR attributes or their values are missing from request"),
  Unauthorized_access(1006, "User or caller doesn't have the appropriate permissions."),
  Internal_Server_Error(1008, "")
}
