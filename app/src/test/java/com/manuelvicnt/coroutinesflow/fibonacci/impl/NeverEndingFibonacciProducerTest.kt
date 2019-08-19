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

package com.manuelvicnt.coroutinesflow.fibonacci.impl

import com.manuelvicnt.coroutinesflow.MainCoroutineRule
import com.manuelvicnt.coroutinesflow.runBlocking
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.broadcastIn
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineScope
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class NeverEndingFibonacciProducerTest {

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    /**
     * For testing, if we want to test subsequent emissions to the Flow, a good idea is to convert it to a
     * Channel with the broadcastIn operator and consume events one by one.
     */
    @Test
    fun `Never ending Fibonacci doesn't stop when called multiple times`() = mainCoroutineRule.runBlocking {
        val subject = NeverEndingFibonacciProducer()
        subject.startNeverEndingFibonacci(mainCoroutineRule.testDispatcher)
        val channel = subject.fibonacci().broadcastIn(TestCoroutineScope(Job())).openSubscription()

        try {
            val initialValue = channel.receive()
            assertEquals(2, initialValue)

            mainCoroutineRule.testDispatcher.advanceTimeBy(3000)

            val secondValue = channel.receive()
            assertEquals(3, secondValue)
            mainCoroutineRule.testDispatcher.advanceTimeBy(1000)
        } finally {
            // Regardless of the assertions above, we always have to stop the channel to not create a infinite loop
            channel.cancel()
            subject.stopNeverEndingFibonacci()
        }
    }
}
