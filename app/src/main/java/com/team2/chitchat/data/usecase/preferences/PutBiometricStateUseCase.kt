package com.team2.chitchat.data.usecase.preferences

import com.team2.chitchat.data.repository.DataProvider
import javax.inject.Inject

class PutBiometricStateUseCase @Inject constructor(
    private val dataProvider: DataProvider
) {
    operator fun invoke(access: Boolean): Unit = dataProvider.putAccessBiometric(access)
}