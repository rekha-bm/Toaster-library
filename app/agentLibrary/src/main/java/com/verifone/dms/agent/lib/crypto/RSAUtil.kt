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

import android.app.Activity
import android.app.KeyguardManager
import android.content.Intent
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyPermanentlyInvalidatedException
import android.security.keystore.KeyProperties
import android.security.keystore.UserNotAuthenticatedException
import android.util.Base64
import android.util.Log
import androidx.annotation.RequiresApi
import com.verifone.dms.agent.lib.Constant
import java.math.BigInteger
import java.security.*
import java.security.cert.Certificate
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.util.*
import javax.crypto.Cipher
import javax.security.auth.x500.X500Principal

class RSAUtil {

  private lateinit var keyguardManager: KeyguardManager

  private lateinit var signatureResult: String
  private var KEY_BIT_LENGTH_4096 = 4096

  //    private lateinit var resultTextView:TextView
  //    private lateinit var signButton:Button
  //    private lateinit var verifyButton:Button

  @RequiresApi(Build.VERSION_CODES.M)
  fun checkExistKey(keyAlias: String) {
    //        keyguardManager= context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
    //        if (!keyguardManager.isDeviceSecure) {
    //            Toast.makeText(context, "Secure lock screen hasn't set up.",
    // Toast.LENGTH_LONG).show()
    //        }
    if (!checkKeyExists(keyAlias)) {
      generateKey(keyAlias)
    }
  }

  //    override fun onCreate(savedInstanceState: Bundle?) {
  //        super.onCreate(savedInstanceState)
  ////        setContentView(R.layout.activity_main)
  ////        resultTextView= findViewById(R.id.resultTextView)
  ////        signButton =  findViewById(R.id.signButton)
  ////        verifyButton = findViewById(R.id.verifyButton)
  //        keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
  //
  //        //Check if lock screen has been set up. Just displaying a Toast here but it shouldn't
  // allow the user to go forward.
  //        if (!keyguardManager.isDeviceSecure) {
  //            Toast.makeText(this, "Secure lock screen hasn't set up.", Toast.LENGTH_LONG).show()
  //        }
  //
  //        //Check if the keys already exists to avoid creating them again
  //        if (!checkKeyExists()) {
  //            generateKey()
  //        }
  //
  //        signButton.setOnClickListener {
  //            signData()
  //        }
  //
  //        verifyButton.setOnClickListener {
  //            verifyData()
  //        }
  //    }

  @RequiresApi(Build.VERSION_CODES.M)
  fun generateKey(keyAlias: String) {
    // We create the start and expiry date for the key
    val startDate = GregorianCalendar()
    val endDate = GregorianCalendar()
    endDate.add(Calendar.YEAR, 1)

    // We are creating a RSA key pair and store it in the Android Keystore
    val keyPairGenerator: KeyPairGenerator =
        KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, ANDROID_KEYSTORE)

    if (keyAlias == KEY_ALIAS_AGENTRSAKEYS) {
      // We are creating the key pair with sign and verify purposes
      val parameterSpec: KeyGenParameterSpec =
          KeyGenParameterSpec.Builder(
                  keyAlias, KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY)
              .run {
                setCertificateSerialNumber(
                    BigInteger.valueOf(
                        777)) // Serial number used for the self-signed certificate of the generated
                // key pair, default is 1
                setCertificateSubject(
                    X500Principal(
                        "CN=$keyAlias")) // Subject used for the self-signed certificate of the
                // generated key pair, default is CN=fake
                setDigests(
                    KeyProperties
                        .DIGEST_SHA256) // Set of digests algorithms with which the key can be used
                setSignaturePaddings(
                    KeyProperties
                        .SIGNATURE_PADDING_RSA_PKCS1) // Set of padding schemes with which the key
                // can be used when signing/verifying
                setCertificateNotBefore(
                    startDate
                        .time) // Start of the validity period for the self-signed certificate of
                // the generated, default Jan 1 1970
                setCertificateNotAfter(
                    endDate
                        .time) // End of the validity period for the self-signed certificate of the
                // generated key, default Jan 1 2048
                setUserAuthenticationRequired(
                    false) // Sets whether this key is authorized to be used only if the user has
                // been authenticated, default false
                setKeySize(KEY_BIT_LENGTH_4096)
                setRandomizedEncryptionRequired(true)
                setUserAuthenticationValidityDurationSeconds(
                    30) // Duration(seconds) for which this key is authorized to be used after the
                // user is successfully authenticated
                build()
              }

      // Initialization of key generator with the parameters we have specified above
      keyPairGenerator.initialize(parameterSpec)
    } else if (keyAlias == KEY_ALIAS_DMSRSAKEYS) {

      val parameterSpec: KeyGenParameterSpec =
          KeyGenParameterSpec.Builder(
                  keyAlias, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
              .run {
                setCertificateSerialNumber(
                    BigInteger.valueOf(
                        777)) // Serial number used for the self-signed certificate of the generated
                // key pair, default is 1
                setCertificateSubject(
                    X500Principal(
                        "CN=$keyAlias")) // Subject used for the self-signed certificate of the
                // generated key pair, default is CN=fake
                setDigests(
                    KeyProperties
                        .DIGEST_SHA256) // Set of digests algorithms with which the key can be used
                setSignaturePaddings(
                    KeyProperties
                        .SIGNATURE_PADDING_RSA_PKCS1) // Set of padding schemes with which the key
                // can be used when signing/verifying
                setCertificateNotBefore(
                    startDate
                        .time) // Start of the validity period for the self-signed certificate of
                // the generated, default Jan 1 1970
                setCertificateNotAfter(
                    endDate
                        .time) // End of the validity period for the self-signed certificate of the
                // generated key, default Jan 1 2048
                setUserAuthenticationRequired(
                    false) // Sets whether this key is authorized to be used only if the user has
                // been authenticated, default false
                setKeySize(KEY_BIT_LENGTH_4096)
                setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                setUserAuthenticationValidityDurationSeconds(
                    30) // Duration(seconds) for which this key is authorized to be used after the
                // user is successfully authenticated
                build()
              }

      // Initialization of key generator with the parameters we have specified above
      keyPairGenerator.initialize(parameterSpec)
    }
    // Generates the key pair
    var keyPair: KeyPair = keyPairGenerator.genKeyPair()
    Log.d("keypairGeneartion", "keypair$keyPair")

    //    signData()

  }

  private fun checkKeyExists(keyAlias: String): Boolean {
    // We get the Keystore instance
    val keyStore: KeyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }

    // We get the private and public key from the keystore if they exists
    val privateKey: PrivateKey? = keyStore.getKey(keyAlias, null) as PrivateKey?
    Log.d("PrivateKey", "PrivateKey$privateKey")
    val publicKey: PublicKey? = keyStore.getCertificate(keyAlias)?.publicKey
    Log.d("PublicKey", "PublicKey$publicKey")

    return privateKey != null && publicKey != null
  }

  @RequiresApi(Build.VERSION_CODES.M)
  fun signData() {
    try {
      // We get the Keystore instance
      val keyStore: KeyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }

      // Retrieves the private key from the keystore
      val privateKey: PrivateKey = keyStore.getKey(KEY_ALIAS_AGENTRSAKEYS, null) as PrivateKey

      // We sign the data with the private key. We use RSA algorithm along SHA-256 digest algorithm
      val signature: ByteArray? =
          Signature.getInstance("SHA256withRSA").run {
            initSign(privateKey)
            update("TestString".toByteArray())
            sign()
          }

      if (signature != null) {
        // We encode and store in a variable the value of the signature
        signatureResult = Base64.encodeToString(signature, Base64.DEFAULT)
        // resultTextView.text = "Signed successfully"
        Log.d("SignatureResult", "SignatureResult$signatureResult")
        Log.d("Signature", "Signature$signature")
        Log.d("signData", "Signed successfully")
        verifyData()
      }
    } catch (e: UserNotAuthenticatedException) {
      // Exception thrown when the user has not been authenticated.
      showAuthenticationScreen()
    } catch (e: KeyPermanentlyInvalidatedException) {
      // Exception thrown when the key has been invalidated for example when lock screen has been
      // disabled.
      // Toast.makeText(this, "Keys are invalidated.\n" + e.message, Toast.LENGTH_LONG).show()
    } catch (e: Exception) {
      throw RuntimeException(e)
    }
  }

  private fun verifyData() {
    // We get the Keystore instance
    val keyStore: KeyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }

    // We get the certificate from the keystore
    val certificate: Certificate? = keyStore.getCertificate(KEY_ALIAS_AGENTRSAKEYS)

    if (certificate != null) {
      // We decode the signature value
      val signature: ByteArray = Base64.decode(signatureResult, Base64.DEFAULT)

      // We check if the signature is valid. We use RSA algorithm along SHA-256 digest algorithm
      val isValid: Boolean =
          Signature.getInstance("SHA256withRSA").run {
            initVerify(certificate)
            update("TestString".toByteArray())
            verify(signature)
          }

      if (isValid) {
        // resultTextView.text = "Verified successfully"
        Log.d("isValid", "Verified successfully")
      } else {
        // resultTextView.text = "Verification failed"
        Log.d("1isValid", "Verification failed")
      }
    }
  }

  private fun showAuthenticationScreen() {
    // This will open a screen to enter the user credentials (fingerprint, pin, pattern). We can
    // display a custom title and description
    val intent: Intent? =
        keyguardManager.createConfirmDeviceCredentialIntent(
            "Keystore Sign And Verify",
            "To be able to sign the data we need to confirm your identity. Please enter your pin/pattern or scan your fingerprint")
    if (intent != null) {
      // startActivityForResult(intent, REQUEST_CODE_FOR_CREDENTIALS)
    }
  }

  //    @RequiresApi(Build.VERSION_CODES.M)
  //    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
  //        super.onActivityResult(requestCode, resultCode, data)
  //        if (requestCode == REQUEST_CODE_FOR_CREDENTIALS) {
  //            if (resultCode == Activity.RESULT_OK) {
  //                signData()
  //            } else {
  //                Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
  //            }
  //        }
  //    }

  @RequiresApi(Build.VERSION_CODES.M)
  fun activityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    if (requestCode == REQUEST_CODE_FOR_CREDENTIALS) {
      if (resultCode == Activity.RESULT_OK) {
        signData()
      } else {
        // Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
      }
    }
  }
  @RequiresApi(Build.VERSION_CODES.M)
  fun getKeyPair(keyAlias: String): KeyPair? {
    val ks: KeyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
    val aliases: Enumeration<String> = ks.aliases()
    val keyPair: KeyPair?

    /** Check whether the keypair with the alias [KEYSTORE_ALIAS] exists. */
    if (aliases.toList().firstOrNull { it == keyAlias } == null) {
      // If it doesn't exist, generate new keypair
      val kpg: KeyPairGenerator =
          KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, ANDROID_KEYSTORE)
      val parameterSpec: KeyGenParameterSpec =
          KeyGenParameterSpec.Builder(
                  keyAlias, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
              .run {
                setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
                setKeySize(KEY_BIT_LENGTH_4096)
                build()
              }
      kpg.initialize(parameterSpec)

      keyPair = kpg.generateKeyPair()
    } else {
      // If it exists, load the existing keypair
      val entry = ks.getEntry(keyAlias, null) as? KeyStore.PrivateKeyEntry
      keyPair = KeyPair(entry?.certificate?.publicKey, entry?.privateKey)
    }
    return keyPair
  }

  @RequiresApi(Build.VERSION_CODES.M)
  fun getPublicKey(keyAlias: String): Key? {
    val keyPair = getKeyPair(keyAlias)
    val publicKey = keyPair?.public ?: return null
    Log.d("PUBLIC KEY", String(Base64.encode(publicKey.encoded, Base64.DEFAULT)))
    return publicKey
  }
  @RequiresApi(Build.VERSION_CODES.M)
  fun getPrivateKey(keyAlias: String): PrivateKey? {
    val keyPair = getKeyPair(keyAlias)
    return keyPair?.private
  }

  fun encrypt(data: String, keyAlias: String?): String {
    val keyStore: KeyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }

    val publicKey: PublicKey? = keyStore.getCertificate(keyAlias)?.publicKey

//    Log.d("PUBLIC KEY", String(Base64.encode(publicKey?.encoded, Base64.DEFAULT)))
    val cipher: Cipher = Cipher.getInstance(Constant.RSA_ENCRYPT_TRANSFORMATION)
    // TODO to check OEAP Padding
    // val cipher: Cipher = Cipher.getInstance("RSA/ECB/OAEPPadding")
    cipher.init(Cipher.ENCRYPT_MODE, publicKey)
    val bytes = cipher.doFinal(data.toByteArray())
    return Base64.encodeToString(bytes, Base64.DEFAULT)
  }

  fun decrypt(data: String, keyAlias: String?): String {
    val keyStore: KeyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
    val privateKey: PrivateKey? = keyStore.getKey(keyAlias, null) as PrivateKey?
    Log.d("PrivateKey", "PrivateKey$privateKey")
    val cipher: Cipher = Cipher.getInstance(Constant.RSA_DECRYPT_TRANSFORMATION)
    // val cipher: Cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding")
    cipher.init(Cipher.DECRYPT_MODE, privateKey)
    val encryptedData = Base64.decode(data, Base64.DEFAULT)
    val decodedData = cipher.doFinal(encryptedData)

    return String(decodedData)
  }

  @RequiresApi(Build.VERSION_CODES.M)
  fun generateSignature(data: String, keyAlias: String?): String {
    try {
      // We get the Keystore instance
      val keyStore: KeyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }

      // Retrieves the private key from the keystore
      val privateKey: PrivateKey = keyStore.getKey(keyAlias, null) as PrivateKey

      // We sign the data with the private key. We use RSA algorithm along SHA-256 digest algorithm
      val signature: ByteArray? =
          Signature.getInstance(Constant.RSA_SIGNATURE_ALGORITHM).run {
            initSign(privateKey)
            update(data.toByteArray())
            sign()
          }

      if (signature != null) {
        // We encode and store in a variable the value of the signature
        signatureResult = Base64.encodeToString(signature, Base64.DEFAULT)
        // resultTextView.text = "Signed successfully"
        Log.d("SignatureResult", "SignatureResult$signatureResult")
        Log.d("Signature", "Signature$signature")
      }
    } catch (e: UserNotAuthenticatedException) {
      // Exception thrown when the user has not been authenticated.
      showAuthenticationScreen()
    } catch (e: KeyPermanentlyInvalidatedException) {
      // Exception thrown when the key has been invalidated for example when lock screen has been
      // disabled.
      // Toast.makeText(this, "Keys are invalidated.\n" + e.message, Toast.LENGTH_LONG).show()
    } catch (e: Exception) {
      throw RuntimeException(e)
    }
    return signatureResult
  }

  fun verifySignature(plainData: String, signatureData: String, keyAlias: String?): Boolean {
    // We get the Keystore instance
    val keyStore: KeyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }

    // We get the certificate from the keystore
    val certificate: Certificate? = keyStore.getCertificate(keyAlias)

    if (certificate != null) {
      // We decode the signature value
      val signature: ByteArray = Base64.decode(signatureData, Base64.DEFAULT)

      // We check if the signature is valid. We use RSA algorithm along SHA-256 digest algorithm
      val isValid: Boolean =
          Signature.getInstance(Constant.RSA_SIGNATURE_ALGORITHM).run {
            initVerify(certificate)
            update(plainData.toByteArray())
            verify(signature)
          }

      if (isValid) {
        // resultTextView.text = "Verified successfully"
        Log.d("isValid", "Verified successfully")
        return true
      }
    }
    return false
  }
  fun getPubKeyId(keyAlias: String, publicKey: RSAPublicKey): String {
    val modulusBytes = publicKey.modulus.toByteArray()
    val exponentBytes = publicKey.publicExponent.toByteArray()
    val bytes = ByteArray(modulusBytes.size + exponentBytes.size)
    System.arraycopy(modulusBytes, 0, bytes, 0, modulusBytes.size)
    System.arraycopy(exponentBytes, 0, bytes, modulusBytes.size, exponentBytes.size)
    val digest = MessageDigest.getInstance(Constant.PUBLIC_KEY_ID_ALGO).digest(bytes)
    val keyIdBytes = digest.copyOfRange(0, 8)
    return keyIdBytes.joinToString("") { String.format("%02X", it) }
  }

  fun getPvtKeyId(keyAlias: String, privateKey: RSAPrivateKey): String {
    val modulusBytes = privateKey.modulus.toByteArray()
    val exponentBytes = privateKey.privateExponent.toByteArray()
    val bytes = ByteArray(modulusBytes.size + exponentBytes.size)
    System.arraycopy(modulusBytes, 0, bytes, 0, modulusBytes.size)
    System.arraycopy(exponentBytes, 0, bytes, modulusBytes.size, exponentBytes.size)
    val digest = MessageDigest.getInstance(Constant.PRIVATE_KEY_ID_ALGO).digest(bytes)
    val keyIdBytes = digest.copyOfRange(0, 8)
    return keyIdBytes.joinToString("") { String.format("%02X", it) }
  }
}

private const val ANDROID_KEYSTORE = "AndroidKeyStore"
const val KEY_ALIAS_AGENTRSAKEYS = "AgentRSAKeys"
const val KEY_ALIAS_DMSRSAKEYS = "DMSRSAKeys"
private const val REQUEST_CODE_FOR_CREDENTIALS = 1
