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

package com.manuelvicnt.coroutinesflow.user

import com.manuelvicnt.coroutinesflow.MainCoroutineRule
import com.manuelvicnt.coroutinesflow.runBlocking
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.lang.IllegalStateException

@ExperimentalCoroutinesApi
class UserViewModelTest {

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Test
    fun `GetUser happy path`() = mainCoroutineRule.runBlocking {
        val expected = "Manuel"
        val subject = UserViewModel(object: UserRepo {
            override suspend fun getUserAsync(): Deferred<User> {
                return CompletableDeferred(expected)
            }
        })

        assertEquals("Hello $expected!", subject.loadUser())
    }

    @Test
    fun `GetUser sad path`() = mainCoroutineRule.runBlocking {
        val subject = UserViewModel(object: UserRepo {
            override suspend fun getUserAsync(): Deferred<User> {
                throw IllegalStateException()
            }
        })

        assertEquals("Error!", subject.loadUser())
    }
}
