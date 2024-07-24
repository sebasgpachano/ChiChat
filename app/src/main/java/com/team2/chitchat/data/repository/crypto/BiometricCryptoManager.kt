package com.team2.chitchat.data.repository.crypto

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.KeyGenerator
import javax.inject.Inject

class BiometricCryptoManager @Inject constructor() {
    companion object {
        private const val KEY_NAME = "my_key"
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
            KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        keyGenerator.init(keyGenParameterSpec)
        keyGenerator.generateKey()
    }
    private fun isKeyCreated(): Boolean {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)
        return keyStore.containsAlias(KEY_NAME)
    }
}