package com.team2.chitchat.data.mapper.users

import com.team2.chitchat.data.mapper.ResponseMapper
import com.team2.chitchat.data.repository.local.user.UserDB
import com.team2.chitchat.data.repository.remote.response.users.GetUserResponse

class GetContactsListMapper : ResponseMapper<ArrayList<GetUserResponse>, ArrayList<UserDB>> {
    override fun fromResponse(response: ArrayList<GetUserResponse>): ArrayList<UserDB> {
        val user: ArrayList<UserDB> = if (response.isEmpty()) {
            ArrayList()
        } else {
            ArrayList(response.map { userResponse ->
                UserDB(
                    id = userResponse.id ?: "",
                    nick = userResponse.nick ?: "",
                    avatar = userResponse.avatar ?: "",
                    online = userResponse.online ?: false,
                    block = false
                )
            })
        }
        return user
    }
}