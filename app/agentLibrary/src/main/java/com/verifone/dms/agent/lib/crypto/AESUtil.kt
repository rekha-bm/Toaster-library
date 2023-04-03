/*
 * Copyright (c) 2023 by VeriFone, Inc.
 * All Rights Reserved.
 * THIS FILE CONTAINS PROPRIETARY AND CONFIDENTIAL INFORMATION
 * AND REMAINS THE UNPUBLISHED PROPERTY OF VERIFONE, INC.
 *
 * Use, disclosure, or reproduction is prohibited
 * without prior written approval from VeriFone, Inc.
 */
package com.verifone.dms.agent.lib.crypto

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import androidx.annotation.RequiresApi
import com.verifone.dms.agent.lib.Constant
import java.util.*
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec
import org.bouncycastle.crypto.engines.AESEngine
import org.bouncycastle.crypto.macs.CMac
import org.bouncycastle.crypto.params.KeyParameter
import org.bouncycastle.util.encoders.Hex
import java.security.KeyStore
import java.security.SecureRandom
import javax.crypto.spec.IvParameterSpec

/**
 * An object of AESUtil.
 *
 * This class is used for all the AES Encryption and validation.
 */
object AESUtil {
  lateinit var hexEncKey: String

  @RequiresApi(Build.VERSION_CODES.O)
          /**
           * A method *generateAESMacKey()*.
           *
           * This method is used for generating AES MAC key
           *
           * @return Hex String of AES MAC key.
           */
  fun generateAESMacKey(): String? {
    val keygen = KeyGenerator.getInstance(Constant.AES_MAC_KEY_ALGO)
    keygen.init(Constant.CMAC_BIT_SIZE)

    val key = keygen.generateKey()
    val macSecretKey: SecretKey = key
    val encodedMacKey = Base64.getEncoder().encodeToString(macSecretKey.encoded)
    val decodedMacKey: ByteArray = Base64.getDecoder().decode(encodedMacKey)
    val hexMacKey: String = Hex.toHexString(decodedMacKey)
    Log.d("EncodeMACKey", "EncodeMACKey" + encodedMacKey)
    Log.d("HexMACKey", hexMacKey)

    return hexMacKey
  }

  @RequiresApi(Build.VERSION_CODES.O)
          /**
           * A method *generateAESEncryptionKey()*.
           *
           * This method is used for generating AES Encryption Key
           *
           * @return Hex String of AES Encryption key.
           */
  fun generateAESEncryptionKey(): String? {
    val keygen = KeyGenerator.getInstance(Constant.AES_ENCRYPTION_KEY_ALGO)
    keygen.init(Constant.AES_ENCRYPTION_KEY_SIZE)

    val key = keygen.generateKey()
    val encSecretKey: SecretKey = key

//    val keyStore = KeyStore.getInstance("AndroidKeyStore")
//    keyStore.load(null)
//
//    val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
//    keyGenerator.init(
//      KeyGenParameterSpec.Builder("MyKeyAlias", KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
//        .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
//        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
//        .setKeySize(256)
//        .build()
//    )
//
//    keyGenerator.generateKey()
//
//    val encSecretKey = keyStore.getKey("MyKeyAlias", null) as SecretKey



    val encodedEncKey = Base64.getEncoder().encodeToString(encSecretKey.encoded)
    val decodedEncKey: ByteArray = Base64.getDecoder().decode(encodedEncKey)
    hexEncKey = Hex.toHexString(decodedEncKey)

    Log.d("EncodedEncKey", encodedEncKey)

    Log.d("HexEncKey", hexEncKey)

    return hexEncKey
  }

  fun calculateCmac(key: ByteArray, data: ByteArray): ByteArray {
    val engine = AESEngine()
    val mac = CMac(engine, Constant.CMAC_BIT_SIZE)
    val keyParameter = KeyParameter(key)
    mac.init(keyParameter)
    val cmacBytes = ByteArray(mac.macSize)
    mac.update(data, 0, data.size)
    mac.doFinal(cmacBytes, 0)
    Log.d("CMAC", "CMAC" + Hex.toHexString(cmacBytes))
    return cmacBytes
  }

  fun calculateAesKcv(key: ByteArray): ByteArray {
    val cipher = Cipher.getInstance(Constant.AES_KCV_TRANSFORMATION)
    val secretKey = SecretKeySpec(key, Constant.AES_KCV_ALGO)
    cipher.init(Cipher.ENCRYPT_MODE, secretKey)
    val kcv = cipher.doFinal(byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0))
    Log.d("KCV", "KCV" + Hex.toHexString(kcv.copyOfRange(0, 3)))
    return kcv.copyOfRange(0, 3)
  }
//TODO add secret key in Shared Preference
  @Throws(java.lang.Exception::class)
  fun aesEncrypt(
    plaintext: ByteArray?,
    IV: ByteArray?
  ): ByteArray? {
    val cipher: Cipher = Cipher.getInstance(Constant.AES_ENC_TRANSFORMATION)

    val keySpec = SecretKeySpec(hexEncKey.toByteArray(), Constant.AES_ENC_ALGO)
    val ivSpec = IvParameterSpec(IV)
    cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec)
    return cipher.doFinal(plaintext)
  }

  fun generateSecretKey(): SecretKey{
    var  keyGenerator = KeyGenerator.getInstance(Constant.AES_ENCRYPTION_KEY_ALGO)
    keyGenerator.init(Constant.AES_ENCRYPTION_KEY_SIZE)
    var  secretKey = keyGenerator.generateKey()
    return secretKey
  }

  fun generateIV(): ByteArray{
    val IV = ByteArray(Constant.IV_BYTE_SIZE)
    var random: SecureRandom

    random = SecureRandom()
    random.nextBytes(IV)
    return IV
  }

  fun aesDecrypt(cipherText: ByteArray?, key: SecretKey, IV: ByteArray?): String? {
    try {
      val cipher = Cipher.getInstance(Constant.AES_ENC_TRANSFORMATION)
      val keySpec = SecretKeySpec(key.encoded, Constant.AES_ENC_ALGO)
      val ivSpec = IvParameterSpec(IV)
      cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec)
      val decryptedText = cipher.doFinal(cipherText)
      return String(decryptedText)
    } catch (e: java.lang.Exception) {
      e.printStackTrace()
    }
    return null
  }
}
