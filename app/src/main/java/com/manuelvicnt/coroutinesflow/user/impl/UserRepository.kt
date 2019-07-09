/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
