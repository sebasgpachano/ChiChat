package com.team2.chitchat.data.domain.usecase.preferences

import com.team2.chitchat.data.repository.DataProvider
import javax.inject.Inject

class ClearPreferencesUseCase @Inject constructor(private val dataProvider: DataProvider) {
    operator fun invoke() = dataProvider.clearPreferences()
}