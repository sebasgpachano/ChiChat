package com.team2.chitchat.data.usecase.preferences

import com.team2.chitchat.data.repository.DataProvider
import de.hdodenhof.circleimageview.CircleImageView
import javax.inject.Inject

class SaveProfilePictureUseCase @Inject constructor(private val dataProvider: DataProvider) {
    operator fun invoke(imageView: CircleImageView?) {
        return dataProvider.saveProfilePicture(imageView)
    }
}