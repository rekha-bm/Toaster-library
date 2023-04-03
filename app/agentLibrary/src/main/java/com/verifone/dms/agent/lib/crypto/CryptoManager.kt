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
import android.util.Base64
import android.util.Log
import androidx.annotation.RequiresApi
import com.verifone.dms.agent.lib.json.model.request.deviceregistration.Content
import com.verifone.dms.agent.lib.json.model.request.deviceregistration.Header
import com.verifone.dms.agent.lib.json.model.request.status.CommonStatusContent
import com.verifone.dms.agent.lib.json.model.request.status.CommonStatusHeader
import com.verifone.dms.agent.lib.json.model.response.CommonContent
import com.verifone.dms.agent.lib.json.model.response.CommonHeader
import java.security.KeyStore
import java.security.PrivateKey
import java.security.PublicKey
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import org.bouncycastle.util.encoders.Hex
import javax.crypto.SecretKey
/**
 * A CryptoManager class.
 *
 * This class is used for managing all RSAUtil and AESUtil crypto operation.
 */
class CryptoManager {

  var rsaUtil: RSAUtil = RSAUtil()

  lateinit var macKey: String

  lateinit var encKey: String

  @RequiresApi(Build.VERSION_CODES.O)
  fun getMACKey(): String {
    val mac = AESUtil.generateAESMacKey()
    return mac.toString()
  }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getEncryptionKey(): String{
        val enc = AESUtil.generateAESEncryptionKey()
        return enc.toString()
    }

  @RequiresApi(Build.VERSION_CODES.O)
  fun generateAgentKeys(): String? {

    macKey = AESUtil.generateAESMacKey().toString()
    if (macKey == null) {
      return null
    }
    encKey = AESUtil.generateAESEncryptionKey().toString()

    if (encKey == null) return null

    val key =
        byteArrayOf(
            0x2b,
            0x7e,
            0x15,
            0x16,
            0x28,
            0xae.toByte(),
            0xd2.toByte(),
            0xa6.toByte(),
            0xab.toByte(),
            0xf7.toByte(),
            0x15,
            0x88.toByte(),
            0x09,
            0xcf.toByte(),
            0x4f,
            0x3c)
    var kcvMac = AESUtil.calculateAesKcv(macKey.toByteArray())
    var hexKcvMac = Hex.toHexString(kcvMac)
    Log.d("KCV MAC", "KCV MAC" + kcvMac)

    val kcvEnc = AESUtil.calculateAesKcv(encKey.toByteArray())
    var hexKcvEnc = Hex.toHexString(kcvEnc)
    Log.d("KCV ENC", "KCV ENC" + kcvEnc)

    val agentKeys =
        "<".plus("AgentKeys")
            .plus(">")
            .plus("\n")
            .plus("<MACKey type=")
            .plus("”AES128”")
            .plus(" ")
            .plus("KCV=”$hexKcvMac”>")
            .plus(macKey)
            .plus("</MACKey>")
            .plus("\n")
            .plus("<EncryptionKey type=”AES128” KCV=”$hexKcvEnc”>")
            .plus(encKey)
            .plus("</EncryptionKey>")
            .plus("\n")
            .plus("</AgentKey>")

    Log.d("AgentKeys", agentKeys)
    Log.d("MAC keys", macKey)
    Log.d("ENC keys", encKey)

    return agentKeys
  }

  fun encrptAgentKeys(data: String, keyAlias: String): String? {
    if (keyAlias != null) {
      return rsaUtil.encrypt(data, keyAlias)
    }
    return null
  }

  fun decryptAgetKeys(data: String, keyAlias: String): String? {
    if (keyAlias != null) {
      return rsaUtil.decrypt(data, keyAlias)
    }
    return null
  }

  @RequiresApi(Build.VERSION_CODES.M)
  fun agentKeySig(data: String, keyAlias: String): String? {
    if (keyAlias != null) {
      return rsaUtil.generateSignature(data, keyAlias)
    }
    return null
  }

  fun agentKeySignVerify(plainData: String, signatureData: String, keyAlias: String): Boolean {
    return rsaUtil.verifySignature(plainData, signatureData, keyAlias)
  }

  fun getCmacAES(header: Header, content: Content): String {

    val header = header
    val content = content

    val data = header.toString().plus(content)
    Log.d("data Result", data)
    val cmacAES = AESUtil.calculateCmac(macKey.toByteArray(), data.toByteArray())
    val hexCmacAES = Hex.toHexString(cmacAES)
    Log.d("CMAC Result", hexCmacAES)

    return hexCmacAES
  }

    fun getResponseCmacAES(header: CommonHeader, content: CommonContent): String {

        val header = header
        val content = content

        val data = header.toString().plus(content)
        Log.d("data Result", data)
        val cmacAES = AESUtil.calculateCmac(macKey.toByteArray(), data.toByteArray())
        val hexCmacAES = Hex.toHexString(cmacAES)
        Log.d("CMAC Result", hexCmacAES)

        return hexCmacAES
    }

  // TODO Verify MAC

  //    fun verifyCMACAES(data: String,serverSignature: String): Boolean{
  //        val calculatedCMACAES = getCmacAES()
  //        if (calculatedCMACAES == serverSignature){
  //            return true
  //        }
  //        return false
  //    }

  fun getKeyId(keyAlias: String, keyType: String): String? {

    val keyStore: KeyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }

    if (keyAlias == KEY_ALIAS_AGENTRSAKEYS && keyType == "publicKey") {
      val publicKey: PublicKey? = keyStore.getCertificate(keyAlias)?.publicKey
      val pubKeyId = rsaUtil.getPubKeyId(keyAlias, publicKey as RSAPublicKey)
      return pubKeyId
    }
//    else if (keyAlias == KEY_ALIAS_AGENTRSAKEYS && keyType == "privateKey") {
//      val privateKey: PrivateKey = keyStore.getKey(keyAlias, null) as PrivateKey
//
//      val pvtKeyId = rsaUtil.getPvtKeyId(keyAlias, privateKey as RSAPrivateKey)
//      return pvtKeyId
//    }
    else if (keyAlias == KEY_ALIAS_DMSRSAKEYS && keyType == "publicKey") {
      val publicKey: PublicKey? = keyStore.getCertificate(keyAlias)?.publicKey
      val pubKeyId = rsaUtil.getPubKeyId(keyAlias, publicKey as RSAPublicKey)
      return pubKeyId
    }
//    else if (keyAlias == KEY_ALIAS_DMSRSAKEYS && keyType == "privateKey") {
//      val privateKey: PrivateKey = keyStore.getKey(keyAlias, null) as PrivateKey
//      val pvtKeyId = rsaUtil.getPvtKeyId(keyAlias, privateKey as RSAPrivateKey)
//      return pvtKeyId
//    }
    return null
  }
  /** getAESKcv(macKey: ByteArray): ByteArray
  * This method will be used to calculate AES KCV
  * @param : MAC key in ByteArray
  * @return : AES KCV in ByteArray

   */
  fun getAESKcv(macKey: ByteArray): ByteArray {
    val aesKcv = AESUtil.calculateAesKcv(macKey)
    return aesKcv
  }

    fun getAESEncKcv(encKey: ByteArray): ByteArray {
        val aesEncKcv = AESUtil.calculateAesKcv(encKey)
        return aesEncKcv
    }

    fun getStatusCmacAES(header: CommonStatusHeader, content: CommonStatusContent): String {

        val header = header
        val content = content

        val data = header.toString().plus(content)
        Log.d("data Result", data)
        val cmacAES = AESUtil.calculateCmac(macKey.toByteArray(), data.toByteArray())
        val hexCmacAES = Hex.toHexString(cmacAES)
        Log.d("CMAC Result", hexCmacAES)

        return hexCmacAES
    }

    fun doAESEncryption(header: CommonStatusHeader, content: CommonStatusContent,
                        IV: ByteArray?): String{
        val header = header
        val content = content

        val data = header.toString().plus(content)
        val aesEncryption = AESUtil.aesEncrypt(data.toByteArray(),IV)
        return Base64.encodeToString(aesEncryption,Base64.DEFAULT)
    }

    fun doAESDecrypt(data: String,key: SecretKey, IV: ByteArray?): String{
        val aesDecryption = AESUtil.aesDecrypt(data.toByteArray(),key,IV)
        return Base64.encodeToString(aesDecryption?.toByteArray(),Base64.DEFAULT)
    }

    fun getAESSecretKey(): SecretKey{
        return AESUtil.generateSecretKey()
    }

    fun getIV(): ByteArray{
        return AESUtil.generateIV()
    }
}

private const val ANDROID_KEYSTORE = "AndroidKeyStore"
