/*
 * Copyright (c) 2023 by VeriFone, Inc.
 * All Rights Reserved.
 * THIS FILE CONTAINS PROPRIETARY AND CONFIDENTIAL INFORMATION
 * AND REMAINS THE UNPUBLISHED PROPERTY OF VERIFONE, INC.
 *
 * Use, disclosure, or reproduction is prohibited
 * without prior written approval from VeriFone, Inc.
 */
package com.verifone.dms.agent.lib.json.sevice

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.verifone.dms.agent.lib.Constant
import com.verifone.dms.agent.lib.DeviceRegistration
import com.verifone.dms.agent.lib.R
import com.verifone.dms.agent.lib.crypto.CSRGen
import com.verifone.dms.agent.lib.crypto.CryptoManager
import com.verifone.dms.agent.lib.json.model.request.deviceregistration.*
import com.verifone.dms.agent.lib.json.model.request.status.*
import com.verifone.dms.agent.lib.json.model.response.CommonResponse
import com.verifone.dms.agent.lib.util.AppUtil
import com.verifone.dms.agent.lib.validator.ResponseValidatorImpl
import java.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.pwall.json.JSONException
import net.pwall.json.schema.JSONSchema
import org.bouncycastle.util.encoders.Hex
import org.json.JSONObject

open class NetworkHelper(context: Context) {
  private lateinit var mqttJsonSubscribeRespone: CommonResponse
  private var appUtil: AppUtil = AppUtil()
  private val csrGen: CSRGen = CSRGen()
  private var cryptoManager: CryptoManager = CryptoManager()
  private var context: Context = context

  private val KEY_ALIAS_AGENTRSAKEYS = "AgentRSAKeys"
  private val KEY_ALIAS_DMSRSAKEYS = "DMSRSAKeys"
  private var dMsgId = 0
  private val headerValidatorImpl = ResponseValidatorImpl()

  @RequiresApi(Build.VERSION_CODES.O) private val agentKeys = cryptoManager.generateAgentKeys()

  private var mqttJsonDeviceRegistrationRequest: DeviceMessage? = null

  private var mqttJsonStatusRequest: CommonStatusRequest? = null

  @RequiresApi(Build.VERSION_CODES.O)
  /**
   * publishRegister(): String
   *
   * This method will be used for register packet formation and send request to server.
   *
   * @return String format of regiater json
   */
  suspend fun publishRegister(): String {
    val deviceRegistration = DeviceRegistration()
    deviceRegistration.generateRSAKeyPair()
    var encrptAgentKeys: String? = null
    var encAgentKeyId: String? = null
    if (agentKeys != null) {
      encrptAgentKeys = cryptoManager.encrptAgentKeys(agentKeys, KEY_ALIAS_DMSRSAKEYS).toString()
      Log.d("EncryptedAgentKey", encrptAgentKeys.toString())
      // TODO: need to calculate KeyId

      val encAgentKeyId = cryptoManager.getKeyId(KEY_ALIAS_DMSRSAKEYS, "publicKey")
      Log.d(
          "Encryption AgentKey Id",
          "Encryption AgentKey Id" + Hex.toHexString(encAgentKeyId?.toByteArray()))

      if (encrptAgentKeys != null) {
        val decryptAgentKeys = cryptoManager.decryptAgetKeys(encrptAgentKeys, KEY_ALIAS_DMSRSAKEYS)
        Log.d("DecryptedAgentKeys", decryptAgentKeys.toString())
      }
    }
    var signatureAgentKeys: String? = null
    if (agentKeys != null) {
      signatureAgentKeys = cryptoManager.agentKeySig(agentKeys, KEY_ALIAS_AGENTRSAKEYS)
      Log.d("signatureAgentKeys", signatureAgentKeys.toString())

      // TODO:Below sign verification is for testing purpose can be removed once tested with server
      if (signatureAgentKeys != null) {
        val verifySignature: Boolean =
            cryptoManager.agentKeySignVerify(agentKeys, signatureAgentKeys, KEY_ALIAS_AGENTRSAKEYS)
        Log.d("verifySignature", verifySignature.toString())
      }
    }

    val csrValue =
        csrGen.generateCSR(
            appUtil.getDeviceSerialNumber().plus("__").plus(appUtil.getDeviceModel()))
    val csrGenValue =
        csrGen.generateSelfSignedCertificate(
            csrValue, appUtil.getDeviceSerialNumber().plus("__").plus(appUtil.getDeviceModel()))
    // TODO: need to calculate keyId
    val encAgentKeyIdSignature = cryptoManager.getKeyId(KEY_ALIAS_AGENTRSAKEYS, "privateKey")
    val encrAgentKeysSignature: EncrAgentKeysSignature =
        EncrAgentKeysSignature(
            Constant.ENCRYPTED_AGENT_KEY_SIGNATURE_TYPE,
            Constant.ENCRYPTED_AGENT_KEY_SIGN_ALGO,
            Constant.ENCRYPTED_AGENT_KEY_HASH_ALGO,
            encAgentKeyIdSignature.toString(),
            signatureAgentKeys.toString())
    // TODO: need to calculate keyId
    val certKeyId = cryptoManager.getKeyId(KEY_ALIAS_AGENTRSAKEYS, "privateKey")
    val certificate: Certificate =
        Certificate(
            Constant.CERTIFICATE_SOURCE,
            certKeyId.toString(),
            Base64.getEncoder().encodeToString(csrGenValue.encoded))

    val encAgentKey: EncrAgentKeys =
        EncrAgentKeys(
            Constant.ENCRYPTED_AGENT_KEY_TYPE,
            Constant.ENCRYPTED_AGENT_KEY_ALGO,
            encAgentKeyId.toString(),
            encrptAgentKeys.toString())

    val registerContent: Content = Content(encAgentKey, certificate, encrAgentKeysSignature)

    val registerHeader: Header =
        Header(
            "CUS_123",
            appUtil.getDeviceModel(),
            appUtil.getDeviceSerialNumber(),
            appUtil.getDeviceCId(),
            appUtil.incrementdMsgId(context),
            0,
            Constant.REISTRATION_MSG_TYPE,
            Constant.VERSION.toDouble())
    val registerSignatureValue = cryptoManager.getCmacAES(registerHeader, registerContent)
    Log.d("Register Signature", registerSignatureValue)
    // val kcvMac = AESUtil.calculateAesKcv(cryptoManager.getMACKey().toByteArray())
    val kcvMac = cryptoManager.getAESKcv(cryptoManager.getMACKey().toByteArray())
    val hexKcvMac = Hex.toHexString(kcvMac)
    Log.d("KCV MAC", "KCV MAC" + kcvMac)
    val registerSignature: Signature =
        Signature(
            Constant.SIGNATURE_TYPE,
            Constant.SIGNATURE_ALGO,
            hexKcvMac.toString(),
            registerSignatureValue)
    mqttJsonDeviceRegistrationRequest =
        DeviceMessage(registerHeader, registerSignature, registerContent)
    val gson: Gson = Gson()
    val registerJson = gson.toJson(mqttJsonDeviceRegistrationRequest)
    return registerJson.toString()
  }

  /**
   * A method of mqttJsonSubscribe(context: Context, topic: String, msg: String): CommonResponse?.
   *
   * This method will be used for fetching server response for Management Plan and Server Response
   * topic.
   *
   * @return CommonResponse : json value ofserver response
   * @property context,topic
   */
  @RequiresApi(Build.VERSION_CODES.O)
  suspend fun mqttJsonSubscribe(context: Context, topic: String, msg: String): CommonResponse? {
    val jsonObj = JSONObject(msg)

    mqttJsonSubscribeRespone =
        GsonBuilder().create().fromJson(jsonObj.toString(), CommonResponse::class.java)
    validateJSONSchema(mqttJsonSubscribeRespone)

    val validateHeader = headerValidatorImpl.headerValidation(mqttJsonSubscribeRespone)
    // val aesCMACVerify = headerValidatorImpl.verifyCMACAES(mqttJsonSubscribeRespone)
    //      if (aesCMACVerify == true){
    //
    //      }
    //    if (aesCMACVerify == false) {
    //      withContext(Dispatchers.Main) {
    //        Toast.makeText(context, "Signature Error", Toast.LENGTH_LONG).show()
    //      }
    //    }
    if (!validateHeader) {
      withContext(Dispatchers.Main) {
        Toast.makeText(context, "Invalid Data", Toast.LENGTH_LONG).show()
      }
      return null
    }
    return mqttJsonSubscribeRespone
  }
  /**
   * A method for publishStatus(): String.
   *
   * This method will be used for creating packet for status request
   *
   * @return String
   */
  @RequiresApi(Build.VERSION_CODES.O)
  suspend fun publishStatus(mpMessage: CommonResponse, status: String): String {
    val mpName = mpMessage.serverMessage?.content?.opSets?.opSet?.get(0)?.ops?.get(0)?.name
    when (mpName) {
      "downloadFile" -> {
        return createDownloadFileResponse(mpMessage, status)
      }
      "getOSLogFile" -> {
        val getOSLogFile_str = getOSLogFile(mpMessage, status)
        return getOSLogFile_str
      }
        "setClock" -> {
            return createSetClockResponse(mpMessage, status)
        }
    }
    return ""
  }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createSetClockResponse(mpMessage: CommonResponse, status: String): String {
        val kcvMac = cryptoManager.getAESKcv(cryptoManager.getMACKey().toByteArray())
        val hexKcvMac = Hex.toHexString(kcvMac)
        Log.d("KCV MAC", "KCV MAC" + kcvMac)

        val kcvEnc = cryptoManager.getAESEncKcv(cryptoManager.getEncryptionKey().toByteArray())
        val hexKcvEnc = Hex.toHexString(kcvEnc)
        Log.d("KCV ENC", "KCV ENC" + kcvEnc)

        val statusHeader =
            CommonStatusHeader(
                "CUS_123",
                appUtil.getDeviceModel(),
                appUtil.getDeviceSerialNumber(),
                appUtil.getDeviceCId(),
                appUtil.incrementdMsgId(context),
                0,
                Constant.STATUS_MSG_TYPE,
                Constant.VERSION)

        val opsList: ArrayList<CommonStatusOpResult> = ArrayList()
        opsList.add(CommonStatusOpResult(null, null, status, "", null))
        var statusContent = CommonStatusContent("", opsList)
        val encryptedData =
            cryptoManager.doAESEncryption(statusHeader, statusContent, cryptoManager.getIV())
        Log.d("Encrypted Data", "EncryptedData$encryptedData")
        statusContent = CommonStatusContent(encryptedData, opsList)
        // TODO json canonicalisation
        /* val json = context.resources.openRawResource(R.raw.downloadfile)
            .bufferedReader().use { it.readText() }
        Log.d("jsonForm","jsonForm $json")
        val jc = JsonCanonicalizer(json)
        Log.d("JsonCanonicalizer","JsonCanonicalizer ${jc.getEncodedString()}")

        */
        val statusSignature =
            CommonStatusSignature(
                Constant.SIGNATURE_TYPE,
                Constant.SIGNATURE_ALGO,
                hexKcvMac,
                cryptoManager.getStatusCmacAES(statusHeader, statusContent))

        val statusEncryption =
            CommonStatusEncryption(
                Constant.ENCRYPTION_TYPE,
                Constant.ENCRYPTION_ALGO,
                hexKcvEnc,
                Hex.toHexString(cryptoManager.getIV()))
        Log.d("Hex IV", "Hex IV${cryptoManager.getIV()}")

        val statusDeviceMessage =
            CommonStatusDeviceMessage(statusHeader, statusContent, statusEncryption, statusSignature)

        mqttJsonStatusRequest = CommonStatusRequest(statusDeviceMessage)
        val gson: Gson = Gson()
        val statusJson = gson.toJson(mqttJsonStatusRequest)
        return statusJson.toString()
    }

  @RequiresApi(Build.VERSION_CODES.O)
  suspend fun getOSLogFile(mpMessage: CommonResponse, status: String): String {
    val kcvMac = cryptoManager.getAESKcv(cryptoManager.getMACKey().toByteArray())
    val hexKcvMac = Hex.toHexString(kcvMac)
    Log.d("KCV MAC", "KCV MAC" + kcvMac)

    val kcvEnc = cryptoManager.getAESEncKcv(cryptoManager.getEncryptionKey().toByteArray())
    val hexKcvEnc = Hex.toHexString(kcvEnc)
    Log.d("KCV ENC", "KCV ENC" + kcvEnc)

    val statusHeader =
        CommonStatusHeader(
            "CUS_123",
            appUtil.getDeviceModel(),
            appUtil.getDeviceSerialNumber(),
            appUtil.getDeviceCId(),
            appUtil.incrementdMsgId(context),
            0,
            Constant.STATUS_MSG_TYPE,
            Constant.VERSION)

    val opsList: ArrayList<CommonStatusOpResult> = ArrayList()
    opsList.add(CommonStatusOpResult(null, null, status, "", null))
    var statusContent = CommonStatusContent("", opsList)
    val encryptedData =
        cryptoManager.doAESEncryption(statusHeader, statusContent, cryptoManager.getIV())
    Log.d("Encrypted Data", "EncryptedData$encryptedData")
    statusContent = CommonStatusContent(encryptedData, opsList)
    // TODO json canonicalisation
    /* val json = context.resources.openRawResource(R.raw.downloadfile)
        .bufferedReader().use { it.readText() }
    Log.d("jsonForm","jsonForm $json")
    val jc = JsonCanonicalizer(json)
    Log.d("JsonCanonicalizer","JsonCanonicalizer ${jc.getEncodedString()}")

    */
    val statusSignature =
        CommonStatusSignature(
            Constant.SIGNATURE_TYPE,
            Constant.SIGNATURE_ALGO,
            hexKcvMac,
            cryptoManager.getStatusCmacAES(statusHeader, statusContent))

    val statusEncryption =
        CommonStatusEncryption(
            Constant.ENCRYPTION_TYPE,
            Constant.ENCRYPTION_ALGO,
            hexKcvEnc,
            Hex.toHexString(cryptoManager.getIV()))
    Log.d("Hex IV", "Hex IV${cryptoManager.getIV()}")

    val statusDeviceMessage =
        CommonStatusDeviceMessage(statusHeader, statusContent, statusEncryption, statusSignature)

    mqttJsonStatusRequest = CommonStatusRequest(statusDeviceMessage)
    val gson: Gson = Gson()
    val statusJson = gson.toJson(mqttJsonStatusRequest)
    return statusJson.toString()
  }

  @RequiresApi(Build.VERSION_CODES.O)
  private fun createDownloadFileResponse(mpMessage: CommonResponse, status: String): String {
    val kcvMac = cryptoManager.getAESKcv(cryptoManager.getMACKey().toByteArray())
    val hexKcvMac = Hex.toHexString(kcvMac)
    Log.d("KCV MAC", "KCV MAC" + kcvMac)

    val kcvEnc = cryptoManager.getAESEncKcv(cryptoManager.getEncryptionKey().toByteArray())
    val hexKcvEnc = Hex.toHexString(kcvEnc)
    Log.d("KCV ENC", "KCV ENC" + kcvEnc)
    val statusHeader =
        CommonStatusHeader(
            "CUS_123",
            appUtil.getDeviceModel(),
            appUtil.getDeviceSerialNumber(),
            appUtil.getDeviceCId(),
            appUtil.incrementdMsgId(context),
            0,
            Constant.STATUS_MSG_TYPE,
            Constant.VERSION)

    val opsList: ArrayList<CommonStatusOpResult> = ArrayList()
    opsList.add(CommonStatusOpResult(null, null, status, "", null))
    var statusContent = CommonStatusContent("", opsList)
    val encryptedData =
        cryptoManager.doAESEncryption(statusHeader, statusContent, cryptoManager.getIV())
    Log.d("Encrypted Data", "EncryptedData$encryptedData")
    statusContent = CommonStatusContent(encryptedData, opsList)
    val statusSignature =
        CommonStatusSignature(
            Constant.SIGNATURE_TYPE,
            Constant.SIGNATURE_ALGO,
            hexKcvMac,
            cryptoManager.getStatusCmacAES(statusHeader, statusContent))
    val statusEncryption =
        CommonStatusEncryption(
            Constant.ENCRYPTION_TYPE,
            Constant.ENCRYPTION_ALGO,
            hexKcvEnc,
            Hex.toHexString(cryptoManager.getIV()))

    val statusDeviceMessage =
        CommonStatusDeviceMessage(statusHeader, statusContent, statusEncryption, statusSignature)
    mqttJsonStatusRequest = CommonStatusRequest(statusDeviceMessage)
    val gson: Gson = Gson()
    val statusJson = gson.toJson(mqttJsonStatusRequest)
    return statusJson.toString()
  }
  /**
   * getRawFileData()
   *
   * This method will be used to fetch schema file from raw folder
   *
   * @output : path of schema file
   */
  @SuppressLint("DiscouragedApi")
  fun getRawFileData(): String {
    return (context.resources.openRawResource(R.raw.schema).bufferedReader().use { it.readText() })
  }

  /* validateJSONSchema(commonResponse: CommonResponse)
  This method will be used to validate the response against JSON Schema
  i/p : CommonResponse - server response
  o/p : true : if validation is success
        failed requirement : if validation fails
  */
  suspend fun validateJSONSchema(commonResponse: CommonResponse?) {
    val gson = Gson()
    val jsonString = gson.toJson(commonResponse)
    try {
      val request = JSONObject(jsonString)
      Log.e("valid response : ", request.toString())

      var str = getRawFileData()
      val schema = JSONSchema.parse(str)
      require(schema.validate(request.toString()))
      val output = schema.validateBasic(request.toString())
      if (output.valid) {
        withContext(Dispatchers.Main) {
          Toast.makeText(context, "Valid Json Schema", Toast.LENGTH_LONG).show()
        }
      } else {
        output.errors?.forEach {
          withContext(Dispatchers.Main) {
            Toast.makeText(context, "${it.error} - ${it.instanceLocation}", Toast.LENGTH_LONG)
                .show()
          }
        }
      }
      Log.e("valid response : ", "${output.valid}")

      output.errors?.forEach { println("${it.error} - ${it.instanceLocation}") }
    } catch (e: JSONException) {
      // TODO Auto-generated catch block
      e.printStackTrace()
    }
  }
}
