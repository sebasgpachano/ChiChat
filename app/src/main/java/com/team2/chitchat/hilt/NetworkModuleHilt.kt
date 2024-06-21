package com.team2.chitchat.hilt

import com.team2.chitchat.data.repository.remote.backend.ApiService
import com.team2.chitchat.data.repository.remote.backend.RetrofitClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModuleHilt {
    @Provides
    @Singleton
    fun provideApiServicesBaseProject(retrofitClient: RetrofitClient): ApiService {
        return retrofitClient.retrofit.create(ApiService::class.java)
    }

}