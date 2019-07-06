
## Differences between Flow and Channel

- When creating a Channel, you specify the Dispatcher it'll execute its code on whereas this is not
true when creating a Flow. In a Flow, you don't specify the dispatcher because it's executed
in the consumer's dispatcher by default and in case you want to modify it,
you have the `flowOn` and `flowWith` operators.

- Flow is a cold behavior, every time an observer applies a terminal operator on it, it'll start
executing the code from the beginning. Channels are hot and they run even if there are no observers
listening for events.

- With a Channel, only one observer will get the element emitted to it. With a BroadcastChannel,
all observers get the element emitted to it.



## Flow and Channels in the app

The behavior of the app showcases how Flows and Channels work. `ColdFibonnaci` is implemented with a
`Flow` and exposed to the View with a LiveData. Therefore, whenever the view is no longer present,
it'll unobserve the LiveData that will propagate that cancellation to the Flow. Whenever the View
is present again, the LiveData will start observing the Flow again, and because is a cold observable
behavior, it will start the sequence from the beginning.

`NeverEndingFibonacci` is implemented with a Channel instead of a Flow. Since Channels are hot,
it'll keep emitting Fibonacci numbers even if there are no consumers listening for the events. We
emit the items within a `launch` coroutine because we just want to start it and forget about it
(we don't want to return anything, we'll send the elements to the Channel). The View will
consume/subscribe to this Channel, if it unsubscribes from the Channel, it's ok, nothing happens.
Whenever it re-subscribes again (maybe after a configuration change), then it'll receive the last
item emitted to the Channel.



## Launching coroutines

There are two clearly-defined ways to create coroutines:
- Launch: This is "fire and forget" kind of Coroutine. It doesn't return any value. E.g. a coroutine
that logs something to console.
- Async: creates a Coroutine that returns a value. E.g. a coroutine that returns the response
of a network request.



## Learnings

- `async` creates a coroutine that returns a value.

- `launch` creates a coroutine meant as to "fire and forget".

- `channelFlow` is a `Flow` with a `Channel` built in. This gives you the behavior of a cold
stream (starts the block of code every time there's an observer) with the flexibility of a Channel
(being able to send elements between coroutines). We defined a `flowChannel` in the
`ColdFibonacciProducer.kt` class. We call `send` to emit the new calculated Fibonacci number to
the flow's observer.

- `ConflatedBroadcastChannel` re-emits the last value emitted by the Channel to a new consumer
that opens a subscription. This is what we use at `NeverEndingFibonacciProducer.kt` to create the
never-ending Fibonacci. The coroutine created by `launch` has its own scope so until you don't cancel
it's job, it'll continue producing numbers. All the numbers produced are sent to the Channel that
can be consumed from the outside. Whenever there's a new subscription, the observer will get the last
item emitted by the channel plus the new ones.

- `liveData` Coroutines builder. You can find the code in `MainViewModel.kt`. This builder creates
a new coroutine (with its own scope) so that now you can call suspend functions inside the builder.
In the builder, we call collect on the flow to consume the elements. The way we emit to the exposed
LiveData is with `emit`. We call `emit` every time we get an element from the flow. Notice that
LiveData only works when there's a listener on the other end. When there's an observer, LiveData will
start consuming the flow and the flow will start from the beginning of the sequence. Whenever the
View gets destroyed, the LiveData won't be observed and will propagate cancellation to the flow too.
This functionality is available in the `androidx.lifecycle:lifecycle-livedata-ktx` library.

- `lifecycleScope.launchWhenX` methods are available in `LifecycleOwners` such as Activities. For
example, in `MainActivity.kt` we use `lifecycleScope.launchWhenStarted` to create a coroutine that
will get executed when the LifecycleOwner is at least `Started`. Inside that coroutine, we can
consume the elements from the ColdFibonacci flow.
This functionality is available in the `androidx.lifecycle:lifecycle-runtime-ktx` library.

- We don't use `GlobalScope` in `NeverEndingFibonacciProducer.kt`, we create a custom scope that
we can cancel for testing. If you create coroutines with `GlobalScope` you manually have to track down
every coroutine you create, whereas with a custom scope you can track them all together.

- `supervisorScope`. You can find this in `UserRepository.kt`. If you notice, `getUserAsync()`
is a suspend function; we use `supervisorScope` to create a new scope out of the one that is calling
the method. And this is because we need a scope to create new coroutines!

