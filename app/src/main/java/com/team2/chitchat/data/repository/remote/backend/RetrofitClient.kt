package com.team2.chitchat.data.repository.remote.backend

import android.util.Log
import com.google.gson.GsonBuilder
import com.team2.chitchat.data.constants.GeneralConstants.Companion.BASE_URL
import com.team2.chitchat.data.constants.GeneralConstants.Companion.RETROFIT_TIMEOUT_IN_SECOND
import com.team2.chitchat.data.sesion.DataUserSession
import com.team2.chitchat.utils.TAG
import okhttp3.CertificatePinner
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import javax.net.ssl.HostnameVerifier

@Singleton
class RetrofitClient @Inject constructor(
    val dataUserSession: DataUserSession
) {
    companion object {
        const val HEADER_KEY_TOKEN = "Authorization"
        private const val SHA256 = "sha256/fZvk9GM++p5HeiWVJ1hY0aJaVFcxROQe8P0EcMOhpeg="
    }

    val retrofit: Retrofit

    init {
        val httpClient: OkHttpClient.Builder = OkHttpClient.Builder()

        val certificatePinner = CertificatePinner.Builder()
            .add("mock-movilidad.vass.es", SHA256)
            .build()
        httpClient.certificatePinner(certificatePinner)

        val hostnamesAllow = listOf(
            "mock-movilidad.vass.es",
        )
        val hostnameVerifier = HostnameVerifier { hostname, session ->
            hostname in hostnamesAllow
        }
        httpClient.hostnameVerifier(hostnameVerifier)

        httpClient
            .connectTimeout(RETROFIT_TIMEOUT_IN_SECOND, TimeUnit.SECONDS)
            .readTimeout(RETROFIT_TIMEOUT_IN_SECOND, TimeUnit.SECONDS)
            .writeTimeout(RETROFIT_TIMEOUT_IN_SECOND, TimeUnit.SECONDS)

        httpClient.interceptors().clear()
        httpClient.interceptors().add(Interceptor { chain ->
            val original = chain.request()

            val request = when {
                needAddBearer(chain.request()) -> {
                    val build = original.newBuilder()
                        .header(HEADER_KEY_TOKEN, dataUserSession.token)
                        .method(original.method(), original.body())
                        .build()
                    build
                }

                else -> {
                    original.newBuilder()
                        .method(original.method(), original.body())
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
        request.body()?.writeTo(buffer)
        val requestUrl = request.url().toString()

        return when {
            requestUrl.endsWith("users/register", true) -> {
                Log.d(TAG, "l> No needAddBearer endsWith(oauth/v2/token")
                false
            }

            requestUrl.endsWith("oauth/v2/token/revoke", true) -> {
                Log.d(TAG, "l> No needAddBearer endsWith(oauth/v2/token/revoke")
                false
            }

            requestUrl.contains("R4PDFGenerator/Transferencia.do", true) -> {
                Log.d(TAG, "l> No needAddBearer contains(R4PDFGenerator/Transferencia.do)")
                false
            }

            dataUserSession.token.isNotBlank() -> {
                Log.d(TAG, "l> NeedAddBearer")
                true
            }

            else -> {
                Log.d(TAG, "l> No needAddBearer contemplated")
                false
            }
        }
    }
}