package com.manuelvicnt.coroutinesflow.fibonacci

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow

@ExperimentalCoroutinesApi
interface ColdFibonacci {
    fun fibonacci(): Flow<Long>
}
