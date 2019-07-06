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
            override suspend fun getUserAsync(): Deferred<String> {
                return CompletableDeferred(expected)
            }
        })

        assertEquals(expected, subject.loadUser())
    }

    @Test
    fun `GetUser sad path`() = mainCoroutineRule.runBlocking {
        val subject = UserViewModel(object: UserRepo {
            override suspend fun getUserAsync(): Deferred<String> {
                throw IllegalStateException()
            }
        })

        assertEquals("Error!", subject.loadUser())
    }
}
