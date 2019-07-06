package com.manuelvicnt.coroutinesflow.user

import kotlinx.coroutines.Deferred

interface UserRepo {
    suspend fun getUserAsync(): Deferred<String>
}
