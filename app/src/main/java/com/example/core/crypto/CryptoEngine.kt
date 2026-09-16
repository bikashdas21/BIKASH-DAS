package com.example.core.crypto

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.nio.charset.StandardCharsets
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.MessageDigest
import java.security.PrivateKey
import java.security.PublicKey
import java.security.Signature
import java.util.UUID
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

object CryptoEngine {
    private const val ANDROID_KEYSTORE = "AndroidKeyStore"
    private const val KEY_ALIAS_RSA = "zerogrid_identity_key"
    private const val KEY_ALIAS_AES = "zerogrid_storage_key"

    init {
        ensureKeysInitialized()
    }

    private fun ensureKeysInitialized() {
        try {
            val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
            keyStore.load(null)

            if (!keyStore.containsAlias(KEY_ALIAS_RSA)) {
                val kpg = KeyPairGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_RSA,
                    ANDROID_KEYSTORE
                )
                val parameterSpec = KeyGenParameterSpec.Builder(
                    KEY_ALIAS_RSA,
                    KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY
                )
                    .setDigests(KeyProperties.DIGEST_SHA256)
                    .setSignaturePaddings(KeyProperties.SIGNATURE_PADDING_RSA_PKCS1)
                    .setKeySize(2048)
                    .build()
                kpg.initialize(parameterSpec)
                kpg.generateKeyPair()
            }

            if (!keyStore.containsAlias(KEY_ALIAS_AES)) {
                val keyGen = KeyGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES,
                    ANDROID_KEYSTORE
                )
                val keyGenSpec = KeyGenParameterSpec.Builder(
                    KEY_ALIAS_AES,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setKeySize(256)
                    .build()
                keyGen.init(keyGenSpec)
                keyGen.generateKey()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getPublicKeyFingerprint(): String {
        return try {
            val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
            keyStore.load(null)
            val cert = keyStore.getCertificate(KEY_ALIAS_RSA)
            val pubKeyBytes = cert?.publicKey?.encoded ?: "ZERO_GRID_DEVICE_KEY".toByteArray()
            val md = MessageDigest.getInstance("SHA-256")
            val digest = md.digest(pubKeyBytes)
            digest.take(8).joinToString(":") { "%02X".format(it) }
        } catch (e: Exception) {
            "ZG:" + UUID.randomUUID().toString().take(8).uppercase()
        }
    }

    fun getPublicKeyBase64(): String {
        return try {
            val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
            keyStore.load(null)
            val cert = keyStore.getCertificate(KEY_ALIAS_RSA)
            val pubKeyBytes = cert?.publicKey?.encoded ?: byteArrayOf()
            Base64.encodeToString(pubKeyBytes, Base64.NO_WRAP)
        } catch (e: Exception) {
            "PUB_KEY_NOT_READY"
        }
    }

    fun signData(data: String): String {
        return try {
            val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
            keyStore.load(null)
            val privateKey = keyStore.getKey(KEY_ALIAS_RSA, null) as? PrivateKey ?: return ""
            val signature = Signature.getInstance("SHA256withRSA")
            signature.initSign(privateKey)
            signature.update(data.toByteArray(StandardCharsets.UTF_8))
            Base64.encodeToString(signature.sign(), Base64.NO_WRAP)
        } catch (e: Exception) {
            ""
        }
    }

    fun encryptPayload(plainText: String): String {
        return try {
            val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
            keyStore.load(null)
            val secretKey = keyStore.getKey(KEY_ALIAS_AES, null) as? SecretKey
                ?: return Base64.encodeToString(plainText.toByteArray(), Base64.NO_WRAP)

            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            val iv = cipher.iv
            val cipherText = cipher.doFinal(plainText.toByteArray(StandardCharsets.UTF_8))

            // Combine IV + ciphertext
            val combined = ByteArray(iv.size + cipherText.size)
            System.arraycopy(iv, 0, combined, 0, iv.size)
            System.arraycopy(cipherText, 0, combined, iv.size, cipherText.size)
            Base64.encodeToString(combined, Base64.NO_WRAP)
        } catch (e: Exception) {
            Base64.encodeToString(plainText.toByteArray(StandardCharsets.UTF_8), Base64.NO_WRAP)
        }
    }

    fun decryptPayload(encryptedBase64: String): String {
        return try {
            val combined = Base64.decode(encryptedBase64, Base64.NO_WRAP)
            val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
            keyStore.load(null)
            val secretKey = keyStore.getKey(KEY_ALIAS_AES, null) as? SecretKey
                ?: return String(combined, StandardCharsets.UTF_8)

            // IV is 12 bytes for GCM
            val iv = ByteArray(12)
            System.arraycopy(combined, 0, iv, 0, 12)
            val cipherTextSize = combined.size - 12
            val cipherText = ByteArray(cipherTextSize)
            System.arraycopy(combined, 12, cipherText, 0, cipherTextSize)

            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            val spec = GCMParameterSpec(128, iv)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)
            val plainBytes = cipher.doFinal(cipherText)
            String(plainBytes, StandardCharsets.UTF_8)
        } catch (e: Exception) {
            try {
                String(Base64.decode(encryptedBase64, Base64.NO_WRAP), StandardCharsets.UTF_8)
            } catch (ex: Exception) {
                encryptedBase64
            }
        }
    }

    fun hashPassword(password: String, salt: String = "ZeroGridSalt"): String {
        val md = MessageDigest.getInstance("SHA-256")
        val combined = password + salt
        val hash = md.digest(combined.toByteArray(StandardCharsets.UTF_8))
        return hash.joinToString("") { "%02x".format(it) }
    }

    fun computeChecksum(data: ByteArray): String {
        val md = MessageDigest.getInstance("SHA-256")
        val hash = md.digest(data)
        return hash.take(8).joinToString("") { "%02x".format(it) }
    }
}
