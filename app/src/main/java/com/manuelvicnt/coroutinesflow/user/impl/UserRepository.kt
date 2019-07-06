package com.manuelvicnt.coroutinesflow.user.impl

import com.manuelvicnt.coroutinesflow.user.UserRepo
import kotlinx.coroutines.*

/**
 * User Repository returns a deferred computation with the username
 *
 * This class could've returned the String straight-away but we're leaving it like that
 * so that we can test happy and sad paths in the UserViewModel
 */
class UserRepository : UserRepo {

    override suspend fun getUserAsync(): Deferred<String> {
        // We use supervisorScope to be able to create Coroutines inside this method.
        // For this specific code, it doesn't make a lot of sense (we could've just returned the String).
        // But what if we had to create multiple requests in parallel to get the information of our user??
        // Then we'd need supervisorScope!
        return supervisorScope {
            async {
                delay(5000)
                "Manuel"
            }
        }
    }
}
