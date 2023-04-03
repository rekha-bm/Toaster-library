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
import android.util.Log
import androidx.annotation.RequiresApi
import com.verifone.dms.agent.lib.Constant
import java.math.BigInteger
import java.security.*
import java.security.cert.X509Certificate
import java.util.*
import org.bouncycastle.asn1.x500.X500Name
import org.bouncycastle.asn1.x509.*
import org.bouncycastle.cert.X509CertificateHolder
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder
import org.bouncycastle.operator.ContentSigner
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder
import org.bouncycastle.pkcs.PKCS10CertificationRequest
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder
import org.bouncycastle.util.encoders.Hex
/**
 * A CSRGen class.
 *
 * This class is used generating CSR certificate.
 */
class CSRGen {

  @RequiresApi(Build.VERSION_CODES.O)
  fun generateSelfSignedCertificate(
      csr: PKCS10CertificationRequest,
      commonName: String
  ): X509Certificate {
    // Generate key pair
    val keyPairGenerator = KeyPairGenerator.getInstance(Constant.CSR_KEYPAIR_ALGORITHM)
    keyPairGenerator.initialize(Constant.CSR_KEYPAIR_KEY_SIZE, SecureRandom())
    val keyPair = keyPairGenerator.generateKeyPair()

    // Generate distinguished name
    val dn = X500Name("CN=$commonName")

    // Generate serial number
    val serialNumber = BigInteger(Constant.CSR_SERIAL_NUMBER_BIT_SIZE, SecureRandom())

    // Generate validity dates
    val notBefore = Date(System.currentTimeMillis())
    val notAfter = Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 30 * 12 * 3)

    // Generate content signer
    val signerBuilder = JcaContentSignerBuilder(Constant.CSR_SIGNATURE_ALOGORITHM)
    val signer: ContentSigner = signerBuilder.build(keyPair.private)

    // Generate certificate
    val certBuilder =
        JcaX509v3CertificateBuilder(dn, serialNumber, notBefore, notAfter, dn, keyPair.public)
    val certHolder: X509CertificateHolder = certBuilder.build(signer)
    val cert: X509Certificate = JcaX509CertificateConverter().getCertificate(certHolder)

    // Self-sign certificate
    cert.verify(keyPair.public)
    val hexCert = Hex.toHexString(cert.encoded)
    Log.d("Certificate Hex", hexCert)
    Log.d("Certificate Base64", Base64.getEncoder().encodeToString(cert.encoded))

    return cert
  }

  fun generateCSR(commonName: String): PKCS10CertificationRequest {
    // Generate key pair
    val keyPairGenerator = KeyPairGenerator.getInstance(Constant.CSR_KEYPAIR_ALGORITHM)
    keyPairGenerator.initialize(Constant.CSR_KEYPAIR_KEY_SIZE, SecureRandom())
    val keyPair = keyPairGenerator.generateKeyPair()
    val publicKeyInfo = SubjectPublicKeyInfo.getInstance(keyPair.public.encoded)
    // TODO add customer id O is customer id
    val subject =
        X500Name(
            "CN=$commonName, OU=Verifone DMS Client, O=My Company" /*, L=My City, ST=My State, C=My Country*/)
    // val issuer = X500Name("CN=John Doe, OU=IT, O=My Company, L=My City, ST=My State, C=My
    // Country")

    // Generate distinguished name
    // val dn = X500Name("CN=$commonName")

    // Generate content signer
    val signerBuilder = JcaContentSignerBuilder(Constant.CSR_SIGNATURE_ALOGORITHM)
    val signer: ContentSigner = signerBuilder.build(keyPair.private)

    // Generate CSR
    val csrBuilder = PKCS10CertificationRequestBuilder(subject, publicKeyInfo)
    val csr: PKCS10CertificationRequest = csrBuilder.build(signer)

    Log.d("CSR", csr.toString())

    return csr
  }
}
