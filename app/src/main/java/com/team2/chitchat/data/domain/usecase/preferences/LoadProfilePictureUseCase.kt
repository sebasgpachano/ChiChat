package com.team2.chitchat.data.domain.usecase.preferences

import android.graphics.Bitmap
import com.team2.chitchat.data.repository.DataProvider
import javax.inject.Inject

class LoadProfilePictureUseCase @Inject constructor(private val dataProvider: DataProvider) {
    operator fun invoke(): Bitmap? {
        return dataProvider.loadProfilePicture()
    }
}