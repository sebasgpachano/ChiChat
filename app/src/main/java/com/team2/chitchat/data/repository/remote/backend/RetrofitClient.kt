package com.team2.chitchat.data.repository.remote.backend

import android.util.Log
import com.google.gson.GsonBuilder
import com.team2.chitchat.data.constants.GeneralConstants.Companion.BASE_URL
import com.team2.chitchat.data.constants.GeneralConstants.Companion.RETROFIT_TIMEOUT_IN_SECOND
import com.team2.chitchat.data.repository.preferences.PreferencesDataSource
import com.team2.chitchat.ui.extensions.TAG
import okhttp3.CertificatePinner
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import javax.net.ssl.HostnameVerifier

@Singleton
class RetrofitClient @Inject constructor(
    private val preferencesDataSource: PreferencesDataSource
) {
    companion object {
        const val HEADER_KEY_TOKEN = "Authorization"
        const val URL = "mock-movilidad.vass.es"
        private const val SHA256 = "sha256/4a6cPehI7OG6cuDZka5NDZ7FR8a60d3auda+sKfg4Ng="
    }

    val retrofit: Retrofit

    init {
        val httpClient: OkHttpClient.Builder = OkHttpClient.Builder()

        val certificatePinner = CertificatePinner.Builder()
            .add(URL, SHA256)
            .build()
        httpClient.certificatePinner(certificatePinner)

        val hostnamesAllow = listOf(
            URL,
        )
        val hostnameVerifier = HostnameVerifier { hostname, _ ->
            hostname in hostnamesAllow
        }
        httpClient.hostnameVerifier(hostnameVerifier)

        httpClient
            .connectTimeout(RETROFIT_TIMEOUT_IN_SECOND, TimeUnit.SECONDS)
            .readTimeout(RETROFIT_TIMEOUT_IN_SECOND, TimeUnit.SECONDS)
            .writeTimeout(RETROFIT_TIMEOUT_IN_SECOND, TimeUnit.SECONDS)

        httpClient.interceptors().clear()
        val logging = HttpLoggingInterceptor()

        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        httpClient
            .addInterceptor(logging)
            .addInterceptor(Interceptor { chain ->
                val originalRequest: Request = chain.request()
                val builder: Request.Builder = originalRequest.newBuilder()
                val newRequest: Request = builder.build()
                chain.proceed(newRequest)
            })
            .interceptors().add(Interceptor { chain ->
                val original = chain.request()
                val token = preferencesDataSource.getAuthToken()
                Log.d(TAG, "%> token: $token")
                val request = when {
                    needAddBearer(chain.request()) -> {
                        val build = original.newBuilder()
                            .header(HEADER_KEY_TOKEN, token)
                            .method(original.method, original.body)
                            .build()
                        build
                    }

                    else -> {
                        original.newBuilder()
                            .method(original.method, original.body)
                            .build()
                    }
                }

                chain.proceed(request)
            })

        val gson = GsonBuilder().setLenient().create()

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(httpClient.build())
            .callbackExecutor(Executors.newSingleThreadExecutor())
            .build()
    }

    private fun needAddBearer(request: Request): Boolean {
        val buffer = okio.Buffer()
        request.body?.writeTo(buffer)

        return when {
            preferencesDataSource.getAuthToken().isNotBlank() -> {
                Log.d(TAG, "%> NeedAddBearer")
                true
            }

            else -> {
                Log.d(TAG, "%> No needAddBearer contemplated")
                false
            }
        }
    }
}