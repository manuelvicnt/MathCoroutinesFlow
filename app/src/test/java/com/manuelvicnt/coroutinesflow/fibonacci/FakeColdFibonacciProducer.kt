package com.manuelvicnt.coroutinesflow.fibonacci

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@ExperimentalCoroutinesApi
class FakeColdFibonacciProducer : ColdFibonacci {
    override fun fibonacci(): Flow<Long> = flowOf(2, 3)
}
