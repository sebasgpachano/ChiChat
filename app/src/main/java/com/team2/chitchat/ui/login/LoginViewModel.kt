package com.team2.chitchat.ui.login

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.team2.chitchat.data.repository.remote.request.users.LoginUserRequest
import com.team2.chitchat.data.repository.remote.response.BaseResponse
import com.team2.chitchat.data.usecase.remote.PostLoginUseCase
import com.team2.chitchat.ui.base.BaseViewModel
import com.team2.chitchat.ui.extensions.TAG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.nio.charset.Charset
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.inject.Inject

private const val KEY_NAME = "my_key"
private const val SEPARATOR = "-"
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val postLoginUseCase: PostLoginUseCase
): BaseViewModel(

) {

    private val loginMutableStateFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val loginStateFlow: StateFlow<Boolean> = loginMutableStateFlow

    fun getAuthenticationUser(loginUserRequest: LoginUserRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            loadingMutableSharedFlow.emit(true)
            postLoginUseCase(loginUserRequest).collect {baseResponse ->
                when(baseResponse) {
                    is BaseResponse.Error -> {
                        Log.d(this@LoginViewModel.TAG, "l> Error: ${baseResponse.error.message}")
                        loadingMutableSharedFlow.emit(false)
                        errorMutableSharedFlow.emit(baseResponse.error)
                    }
                    is BaseResponse.Success -> {
                        loadingMutableSharedFlow.emit(false)
                        loginMutableStateFlow.value = baseResponse.data
                    }
                }


            }
        }

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
        return Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                + KeyProperties.BLOCK_MODE_CBC + "/"
                + KeyProperties.ENCRYPTION_PADDING_PKCS7)
    }
    fun encryptedCipher(): Cipher {
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
        val cipher = getCipher()
        val secretKey = getSecretKey()
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        return cipher
    }
    fun decryptCipher(iv: ByteArray): Cipher {
        val cipher = getCipher()
        val secretKey = getSecretKey()
        cipher.init(Cipher.DECRYPT_MODE, secretKey, IvParameterSpec(iv))
        return cipher
    }
    fun encrypt(cipher: Cipher,plainText: String): String {
        return Base64.encodeToString(cipher.doFinal(plainText.toByteArray(Charset.defaultCharset())),Base64.DEFAULT) + SEPARATOR + Base64.encodeToString(cipher.iv, Base64.DEFAULT)
    }
    fun decrypt(cipherText: String): String {
        val cipher = decryptCipher(Base64.decode(cipherText.split(SEPARATOR)[1], Base64.DEFAULT))
        return String(cipher.doFinal(Base64.decode(cipherText.split(SEPARATOR)[0], Base64.DEFAULT)))
    }
}