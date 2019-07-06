package com.manuelvicnt.coroutinesflow.user

import androidx.lifecycle.ViewModel
import java.lang.Exception

class UserViewModel(private val userRepo: UserRepo) : ViewModel() {

    suspend fun loadUser(): String {
        return try {
            "Hello ${userRepo.getUserAsync().await()}!"
        } catch (e: Exception) {
            "Error!"
        }
    }
}
