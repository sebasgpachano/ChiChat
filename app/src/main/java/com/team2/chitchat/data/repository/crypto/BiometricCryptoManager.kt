package com.team2.chitchat.data.repository.crypto

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import com.team2.chitchat.data.repository.remote.request.users.LoginUserRequest
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.nio.charset.Charset
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.inject.Inject

class BiometricCryptoManager @Inject constructor() {
    companion object {
        private const val KEY_NAME = "my_key"
        private const val SEPARATOR = "-"
    }
    private lateinit var iv: ByteArray
    init {
        generateSecretKey(
            KeyGenParameterSpec.Builder(
                KEY_NAME,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                .setUserAuthenticationRequired(true)
                // Invalidate the keys if the user has registered a new biometric
                // credential, such as a new fingerprint. Can call this method only
                // on Android 7.0 (API level 24) or higher. The variable
                // "invalidatedByBiometricEnrollment" is true by default.
                .setInvalidatedByBiometricEnrollment(true)
                .build())
    }
    private fun generateSecretKey(keyGenParameterSpec: KeyGenParameterSpec) {
        if (isKeyCreated()) return
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        keyGenerator.init(keyGenParameterSpec)
        keyGenerator.generateKey()
    }
    private fun isKeyCreated(): Boolean {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
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
    fun decryptCipher(iv: String): Cipher {
        val ivString = Base64.decode(iv.split(SEPARATOR)[1], Base64.DEFAULT)
        val ivParameterSpec = IvParameterSpec(ivString)
        val cipher = getCipher()
        val secretKey = getSecretKey()
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec)
        return cipher
    }
    fun encrypt(cipher: Cipher, login: LoginUserRequest): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        val objectOutputStream = ObjectOutputStream(byteArrayOutputStream)
        objectOutputStream.writeObject(login)
        objectOutputStream.close()
        val myObjectByteArray = byteArrayOutputStream.toByteArray()
        iv = cipher.iv
        return "${Base64.encodeToString(cipher.doFinal(myObjectByteArray), Base64.DEFAULT)}$SEPARATOR${Base64.encodeToString(iv, Base64.DEFAULT)}"
    }
    fun decrypt(cipher: Cipher, cipherText: String): LoginUserRequest {
        val decodedCipherText = cipher.doFinal(Base64.decode(cipherText.split(SEPARATOR)[0], Base64.DEFAULT))
        val byteArrayInputStream = ByteArrayInputStream(decodedCipherText)
        val objectInputStream = ObjectInputStream(byteArrayInputStream)
        val login = objectInputStream.readObject() as LoginUserRequest
        objectInputStream.close()
        return login

    }
}