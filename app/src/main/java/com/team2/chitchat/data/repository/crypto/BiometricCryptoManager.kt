package com.team2.chitchat.data.repository.crypto

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import com.team2.chitchat.data.repository.preferences.PreferencesDataSource
import com.team2.chitchat.ui.extensions.TAG
import java.nio.charset.Charset
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BiometricCryptoManager @Inject constructor(
    private val preferencesDataSource: PreferencesDataSource
) {
    companion object {
        private const val KEY_NAME = "my_key"
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
    }
    init {
        generateSecretKey(
            KeyGenParameterSpec.Builder(
                KEY_NAME,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                .setUserAuthenticationRequired(false)
                .setInvalidatedByBiometricEnrollment(true)
                .build())
    }
    private fun generateSecretKey(keyGenParameterSpec: KeyGenParameterSpec) {
        if (isKeyCreated()) return
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
        keyGenerator.init(keyGenParameterSpec)
        keyGenerator.generateKey()
    }
    private fun isKeyCreated(): Boolean {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
        keyStore.load(null)
        return keyStore.containsAlias(KEY_NAME)
    }

    private fun getSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        // Before the keystore can be accessed, it must be loaded.
        keyStore.load(null)
        return keyStore.getKey(KEY_NAME, null) as SecretKey
    }

    private fun getCipher(): Cipher {
        return Cipher.getInstance(
            KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7)
    }
    fun encryptedCipher(): Cipher {
        val cipher = getCipher()
        val secretKey = getSecretKey()
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        return cipher
    }
    private fun decryptCipher(): Cipher {
        val iv = preferencesDataSource.getIvParam()
        Log.d(TAG, "%> decryptCipher iv: $iv")
        val ivParameterSpec = IvParameterSpec(Base64.decode(iv, Base64.DEFAULT))
        val cipher = getCipher()
        val secretKey = getSecretKey()
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec)
        return cipher
    }
    fun encrypt(token: String) {
        encryptedCipher().apply {
            val encryptedCipherText =
                doFinal(token.toByteArray(Charset.defaultCharset()))
            preferencesDataSource.saveAuthToken(Base64.encodeToString(encryptedCipherText,Base64.DEFAULT))
            preferencesDataSource.saveIvParam(Base64.encodeToString(iv,Base64.DEFAULT))
            Log.d(TAG, "%> encryptedCipherText: ${Base64.encodeToString(encryptedCipherText,Base64.DEFAULT)} -- $token")
        }
    }
    fun decrypt(): String {
        decryptCipher().apply {
            return try {
                var token = preferencesDataSource.getAuthToken()
                if(!token.isNullOrBlank()) {
                    Log.d(TAG, "%> decrypt accessBiometric: ${preferencesDataSource.getAccessBiometric()}")
                    Log.d(TAG, "%> decrypt Token: $token")
                    val tokenByteArray = Base64.decode(token, Base64.DEFAULT)
                    Log.d(TAG, "%> decrypt tokenByteArray: $tokenByteArray")
                    val decodedCipherText = doFinal(tokenByteArray)
                    val decryptText = String(decodedCipherText, Charset.defaultCharset())
                    Log.d(TAG, "%> decrypt decryptText: $decryptText -- ${preferencesDataSource.getAuthToken()}")
                    token = decryptText
                }
                token
            }catch (e: Exception) {
                Log.d(TAG, "%> decrypt error: ${e.message}")
                preferencesDataSource.getAuthToken()
            }

        }
    }
}