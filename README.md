This sample showcases an Android app that uses both [Flow](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/-flow/) and [Channel](https://kotlinlang.org/docs/reference/coroutines/channels.html) from Kotlin Coroutines. 

It also includes tests! Very imporant to have a maintainable application.

## Differences between Flow and Channel

- Flow has a cold behavior, every time an observer applies a terminal operator on it, it'll start
executing the code from the beginning. Channels are hot and they run even if there are no observers
listening for events. With a regular Channel, only one observer will get the element emitted from the Channel. 
With a BroadcastChannel, all observers get the same element emitted, it broadcasts the emission of the element.

- Use Channels when the producer and the consumer have different lifetimes. For example, a View and a 
ViewModel have different lifetimes, you may not want to consume a Flow from the View because it'll start 
execution everytime that the View gets created (for instance) and there's no way to continue execution or get 
the last emitted value. For that, use Channels. Not maintaining state is really bad for configuration changes.

- Normally, when creating a Channel, you specify the Dispatcher it'll execute its code on. However, this is not
true when creating a Flow. In a Flow, you don't specify the dispatcher because it will be executed in the 
consumer's dispatcher by default. In case you want to modify it, you have the [`flowOn`](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/flow-on.html) and [`flowWith`](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/flow-with.html) operators.

## Flow and Channels in the app

The behavior of the app showcases how `Flow` and `Channel` work: 

- [`ColdFibonnaci`](https://github.com/manuelvicnt/MathCoroutinesFlow/blob/master/app/src/main/java/com/manuelvicnt/coroutinesflow/fibonacci/impl/ColdFibonacciProducer.kt) is implemented with a `Flow` and exposed to the View with a [`LiveData`](https://developer.android.com/topic/libraries/architecture/livedata). 
Therefore, whenever the  view is no longer present, it'll unobserve the `LiveData` that will propagate that cancellation 
to the Flow. Whenever the View is present, the `LiveData` will start observing the `Flow` again, and because it has a 
cold observable behavior, it will start the sequence from the beginning.

- [`NeverEndingFibonacci`](https://github.com/manuelvicnt/MathCoroutinesFlow/blob/master/app/src/main/java/com/manuelvicnt/coroutinesflow/fibonacci/impl/NeverEndingFibonacciProducer.kt) is implemented with a `Channel` instead of a `Flow`. Since Channels are hot,
it'll keep emitting Fibonacci numbers even if there are no consumers listening for the events. We create the loop
to emit items within a `launch` coroutine because we just want to start and forget about it, we don't want to 
return anything, we'll send the elements to the Channel. The View will consume/subscribe to this Channel to listen for
number updates, if it unsubscribes from the Channel, it's ok, nothing happens, it'll keep producing numbers. 
Whenever it re-subscribes again (maybe after a configuration change), then it'll receive the last item emitted 
to the Channel and the new ones as they're produced.

- [`UserRepository`](https://github.com/manuelvicnt/MathCoroutinesFlow/blob/master/app/src/main/java/com/manuelvicnt/coroutinesflow/user/impl/UserRepository.kt)
has the use case of returning a deferred computation. However, although it's not fully implemented, it has the logic
of how you could expose a stream of User objects. Imagine that you want to handle user sessions and want to expose
to the rest of the application the User that is logged in at any point. As with `NeverEndingFibonacci`, this functionality
is agnostic of View lifecycle events and has its own lifetime and that's why it's also implemented with a 
`ConflatedBroadcastChannel`. 

<img src="https://github.com/manuelvicnt/MathCoroutinesFlow/blob/master/app_running.gif" width="300">

## Launching coroutines

There are two clearly-defined ways to create coroutines:

- Launch: This is "fire and forget" kind of coroutine. It doesn't return any value. E.g. a coroutine
that logs something to console. We use this in [`ColdFibonacciProducer.kt`](https://github.com/manuelvicnt/MathCoroutinesFlow/blob/master/app/src/main/java/com/manuelvicnt/coroutinesflow/fibonacci/impl/ColdFibonacciProducer.kt)
to start our Fibonacci computation, here we don't need to return a value since we're sending the numbers to the `Channel`.

- Async: creates a Coroutine that returns a value. E.g. a coroutine that returns the response
of a network request. We use it in [`UserRepository.kt`](https://github.com/manuelvicnt/MathCoroutinesFlow/blob/master/app/src/main/java/com/manuelvicnt/coroutinesflow/user/impl/UserRepository.kt)
where we create a coroutine to obtain the user information. Why we create a coroutine? Retrieving that information can be expensive and we might want to do it on a background thread.

## Learnings

- `async` creates a coroutine that returns a value.

- `launch` creates a coroutine meant as to "fire and forget".

- `channelFlow` is a `Flow` with a `Channel` built in. This gives you the behavior of a cold
stream (starts the block of code every time there's an observer) with the flexibility of a `Channel`
(being able to send elements between coroutines). We defined a `flowChannel` in the
[`ColdFibonacciProducer.kt`](https://github.com/manuelvicnt/MathCoroutinesFlow/blob/master/app/src/main/java/com/manuelvicnt/coroutinesflow/fibonacci/impl/ColdFibonacciProducer.kt) file. We call `send` to emit the new calculated Fibonacci number to
the flow's observer.

- `ConflatedBroadcastChannel` re-emits the last value emitted by the Channel to a new consumer
that opens a subscription. This is what we use at [`NeverEndingFibonacciProducer.kt`](https://github.com/manuelvicnt/MathCoroutinesFlow/blob/master/app/src/main/java/com/manuelvicnt/coroutinesflow/fibonacci/impl/NeverEndingFibonacciProducer.kt) to create the
never-ending Fibonacci. The coroutine created by `launch` has its own scope so until you don't cancel
it's job, it'll continue producing numbers. All the numbers produced are sent to the Channel that
can be consumed from the outside. Whenever there's a new subscription, the observer will get the last
item emitted by the channel plus the new ones.

- `liveData` Coroutines builder. You can find the code in [`MainViewModel.kt`](https://github.com/manuelvicnt/MathCoroutinesFlow/blob/master/app/src/main/java/com/manuelvicnt/coroutinesflow/main/MainViewModel.kt). This builder creates
a new coroutine (with its own scope) so that now you can call suspend functions inside the builder.
In the builder, we call collect on the flow to consume the elements. The way we emit to the exposed
`LiveData` is with `emit`. We call `emit` every time we get an element from the flow. Notice that
`LiveData` only works when there's a listener on the other end. When there's an observer, `LiveData` will
start consuming the flow and the flow will start from the beginning of the sequence. Whenever the
View gets destroyed, the `LiveData` won't be observed and will propagate cancellation to the flow too.
This functionality is available in the `androidx.lifecycle:lifecycle-livedata-ktx` library.

- `lifecycleScope.launchWhenX` methods are available in `LifecycleOwners` such as Activities. For
example, in [`MainActivity.kt`](https://github.com/manuelvicnt/MathCoroutinesFlow/blob/master/app/src/main/java/com/manuelvicnt/coroutinesflow/main/MainActivity.kt) we use `lifecycleScope.launchWhenStarted` to create a coroutine that
will get executed when the LifecycleOwner is at least `Started`. Inside that coroutine, we can
consume the elements from the ColdFibonacci flow.
This functionality is available in the `androidx.lifecycle:lifecycle-runtime-ktx` library.

- We don't use `GlobalScope` in [`NeverEndingFibonacciProducer.kt`](https://github.com/manuelvicnt/MathCoroutinesFlow/blob/master/app/src/main/java/com/manuelvicnt/coroutinesflow/fibonacci/impl/NeverEndingFibonacciProducer.kt), we create a custom scope that
we can cancel (great for testing). If you create coroutines with `GlobalScope` you manually have to track down
every coroutine you create, whereas with a custom scope you can track them all together.

- `supervisorScope` & `coroutineScope`. You can find this in [`UserRepository.kt`](https://github.com/manuelvicnt/MathCoroutinesFlow/blob/master/app/src/main/java/com/manuelvicnt/coroutinesflow/user/impl/UserRepository.kt). If you notice, `getUserAsync()`
is a suspend function; we use `supervisorScope` to create a new scope out of the one that is calling
the method. And this is because we need a scope to create new coroutines! Find a `supervisorScope` vs `coroutineScope`
comparison in that file.
