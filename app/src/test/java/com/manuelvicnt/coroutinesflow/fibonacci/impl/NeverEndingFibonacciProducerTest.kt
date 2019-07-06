package com.manuelvicnt.coroutinesflow.fibonacci.impl

import com.manuelvicnt.coroutinesflow.MainCoroutineRule
import com.manuelvicnt.coroutinesflow.fibonacci.impl.NeverEndingFibonacciProducer
import com.manuelvicnt.coroutinesflow.runBlocking
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class NeverEndingFibonacciProducerTest {

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Test
    fun `Never ending Fibonacci doesn't stop when called multiple times`() = mainCoroutineRule.runBlocking {
        val subject = NeverEndingFibonacciProducer()
        try {
            subject.startNeverEndingFibonacci(mainCoroutineRule.testDispatcher)
            val initialValue = subject.fibonacci().receive()
            assertEquals(2, initialValue)

            mainCoroutineRule.testDispatcher.advanceTimeBy(3000)

            val secondValue = subject.fibonacci().receive()
            assertEquals(3, secondValue)
        } finally {
            // Regardless of the assertions above, we always have to stop the job to not create a infinite loop
            subject.stopNeverEndingFibonacci()
        }
    }

}
