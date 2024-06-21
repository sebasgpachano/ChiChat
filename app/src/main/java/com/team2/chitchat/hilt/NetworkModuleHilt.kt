package com.team2.chitchat.hilt

import com.google.gson.GsonBuilder
import com.team2.chitchat.BuildConfig
import com.team2.chitchat.data.constants.GeneralConstants
import com.team2.chitchat.data.repository.remote.backend.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.CertificatePinner
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Protocol
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Collections
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import javax.net.ssl.HostnameVerifier

@Module
@InstallIn(SingletonComponent::class)
object NetworkModuleHilt {
    private const val HEADER_KEY_TOKEN = "Authorization"
    private const val SHA256 = "sha256/fZvk9GM++p5HeiWVJ1hY0aJaVFcxROQe8P0EcMOhpeg="
    @Provides
    @Singleton
    fun provideApiServicesBaseProject(retrofitClient: OkHttpClient): ApiService {
        val gson = GsonBuilder().setLenient().create()
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(retrofitClient)
            .build()
            .create(ApiService::class.java)
    }


    @Provides
    fun provideOkHttpClient(simpleApplication: SimpleApplication): OkHttpClient {
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
            .connectTimeout(GeneralConstants.RETROFIT_TIMEOUT_IN_SECOND, TimeUnit.SECONDS)
            .readTimeout(GeneralConstants.RETROFIT_TIMEOUT_IN_SECOND, TimeUnit.SECONDS)
            .writeTimeout(GeneralConstants.RETROFIT_TIMEOUT_IN_SECOND, TimeUnit.SECONDS)

        httpClient.interceptors().clear()
        httpClient.interceptors().add(Interceptor { chain ->
            val original = chain.request()

            val request = original.newBuilder()
                .header(HEADER_KEY_TOKEN, simpleApplication.getAuthToken())
                .method(original.method, original.body)
                .build()

            chain.proceed(request)
        })



        return httpClient
            .protocols(Collections.singletonList(Protocol.HTTP_1_1))
            .build()
    }

}