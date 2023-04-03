package com.verifone.dms.agent.lib

import com.verifone.dms.agent.lib.util.AppUtil

object Constant {
  var appUtil: AppUtil = AppUtil()
  const val ENCRYPTED_AGENT_KEY_SIGNATURE_TYPE = "PKI"
  const val ENCRYPTED_AGENT_KEY_SIGN_ALGO = "PKCS-1.5"
  const val ENCRYPTED_AGENT_KEY_HASH_ALGO = "SHA-256"
  const val CERTIFICATE_SOURCE = "Agent"
  const val ENCRYPTED_AGENT_KEY_TYPE = "encrypted/base64"
  const val ENCRYPTED_AGENT_KEY_ALGO = "RSA-OAEP"
  const val SIGNATURE_TYPE = "MAC"
  const val SIGNATURE_ALGO = "CMAC-AES"
  const val AES_ENCRYPTION_KEY_ALGO = "AES"
  const val AES_ENCRYPTION_KEY_SIZE = 128
  const val CMAC_BIT_SIZE = 128
  const val AES_KCV_TRANSFORMATION = "AES/CBC/PKCS5PADDING"
  const val AES_KCV_ALGO = "AES"
  const val CSR_SIGNATURE_ALOGORITHM = "SHA256withRSA"
  const val CSR_KEYPAIR_ALGORITHM = "RSA"
  const val CSR_KEYPAIR_KEY_SIZE = 4096
  const val CSR_SERIAL_NUMBER_BIT_SIZE = 128
  const val RSA_ENCRYPT_TRANSFORMATION = "RSA/ECB/PKCS1Padding"
  const val RSA_DECRYPT_TRANSFORMATION = "RSA/ECB/PKCS1Padding"
  const val RSA_SIGNATURE_ALGORITHM = "SHA256withRSA"
  const val PUBLIC_KEY_ID_ALGO = "SHA-256"
  const val PRIVATE_KEY_ID_ALGO = "SHA-256"
  const val VERSION = "0.10"
  const val REISTRATION_MSG_TYPE = "register"
  const val STATUS_MSG_TYPE = "status"
  const val DMSG_ID_KEY = "DMSG_ID"
  const val IS_DEVICE_REGISTERED = "is_Registered"
  val MP_TOPIC = "ServerMessage".plus("/").plus(appUtil.getClientId()).plus("/").plus("MP")

  val REGISTRATION_TOPIC =
      "DeviceMessage".plus("/").plus(appUtil.getClientId()).plus("/").plus("Register")

  val STATUS_REPORT_TOPIC =
      "DeviceMessage".plus("/").plus(appUtil.getClientId()).plus("/").plus("Status")

  val SERVER_RESPONSE_TOPIC =
      "ServerMessage".plus("/").plus(appUtil.getClientId()).plus("/").plus("ServerResponse")
    const val AES_MAC_KEY_ALGO = "AES"
    const val ENCRYPTION_TYPE = "ENC"
    const val ENCRYPTION_ALGO = "AES-CBC"
    const val AES_ENC_TRANSFORMATION = "AES/CBC/PKCS5PADDING"
    const val AES_ENC_ALGO = "AES/CBC/PKCS5PADDING"
    const val IV_BYTE_SIZE = 16
    const val ANDROID_KEYSTORE = "AndroidKeyStore"
}
