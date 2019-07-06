package com.manuelvicnt.coroutinesflow.fibonacci.impl

import androidx.annotation.VisibleForTesting
import com.manuelvicnt.coroutinesflow.fibonacci.NeverEndingFibonacci
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.channels.ReceiveChannel

@ExperimentalCoroutinesApi
class NeverEndingFibonacciProducer : NeverEndingFibonacci {

    private var started = false

    /**
     * We expose a ReceiveChannel because we don't want consumers to receive a
     * ConflatedBroadcastChannel type from our _neverEndingFibonacci variable.
     *
     * With this interface, you can only consume elements from the channel.
     */
    override fun fibonacci(): ReceiveChannel<Long> {
        if (!started) startNeverEndingFibonacci()
        return _neverEndingFibonacci.openSubscription()
    }

    /**
     * We use a ConflatedBroadcastChannel because we want the last item emitted to the Channel to
     * be received by the consumers when they start subscribing to it. For example, this is useful
     * when a View goes through a configuration change and wants to restore to the last state it was in.
     */
    private val _neverEndingFibonacci by lazy {
        ConflatedBroadcastChannel<Long>()
    }

    /**
     * When we start the never ending fibonacci, we create a coroutine with `launch`
     * which is a "fire and forget" kind of coroutine. We don't have to return any
     * value, we're going send what it produces to the ConflatedBroadcastChannel.
     *
     * This execution will stop when the `neverEndingFibonacciScope` gets cancelled.
     *
     * @param dispatcher Dispatcher to use for calculating Fibonacci numbers. Very useful for testing.
     */
    @VisibleForTesting
    fun startNeverEndingFibonacci(
            dispatcher: CoroutineDispatcher = Dispatchers.Default
    ) = neverEndingFibonacciScope.launch(dispatcher) {
        started = true
        var first = 1L
        var second = 1L
        while (true) {
            val next = first + second
            System.out.println(next.toString())
            _neverEndingFibonacci.send(next)
            first = second
            second = next
            delay(3000) // Since delay is a suspend function that handles cancellation,
                        // when the scope that started this coroutine (neverEndingFibonacciScope)
                        // is cancelled and the coroutine execution comes to this suspension point,
                        // the coroutine will stop and will finish executing. If this suspension
                        // point weren't here and we wouldn't delay it, then you'd have to use the
                        // property isActive and do something like `if (!isActive) break`
        }
    }

    /**
     * This will stop the never ending Fibonacci channel forever.
     * Once you cancel a Job, you cannot open it again.
     *
     * The job will cancel the scope in which it's used and it'll propagate the cancellation to
     * its children coroutines.
     */
    @VisibleForTesting
    fun stopNeverEndingFibonacci() {
        neverEndingFibonacciJob.cancel()
    }
}

private val neverEndingFibonacciJob = SupervisorJob()
private val neverEndingFibonacciScope = CoroutineScope(neverEndingFibonacciJob)
