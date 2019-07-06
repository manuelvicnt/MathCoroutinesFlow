package com.manuelvicnt.coroutinesflow.fibonacci.impl

import com.manuelvicnt.coroutinesflow.MainCoroutineRule
import com.manuelvicnt.coroutinesflow.fibonacci.impl.ColdFibonacciProducer
import com.manuelvicnt.coroutinesflow.runBlocking
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class ColdFibonacciProducerTest {

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Test
    fun `Fibonacci Flow starts again when it's called multiple times`() = mainCoroutineRule.runBlocking {
        val subject = ColdFibonacciProducer()

        val firstExecution = subject.fibonacci().first()
        assertEquals(2, firstExecution)

        val secondExecution = subject.fibonacci().first()
        assertEquals(2, secondExecution)
    }
}
