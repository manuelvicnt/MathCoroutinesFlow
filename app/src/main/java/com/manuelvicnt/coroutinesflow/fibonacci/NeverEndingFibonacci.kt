package com.manuelvicnt.coroutinesflow.fibonacci

import kotlinx.coroutines.channels.ReceiveChannel

interface NeverEndingFibonacci {

    fun fibonacci(): ReceiveChannel<Long>
}
