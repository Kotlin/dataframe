package org.jetbrains.kotlinx.dataframe.io

import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
internal inline fun <R> using(crossinline block: UsingResources<R>.() -> R): Catcher<R> {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    val manager = UsingResourcesImpl<R>()
    try {
        val result = manager.use(block)
        manager.result = result
    } catch (t: Throwable) {
        manager.throwable = t
    }
    return manager.getCatcher()
}

internal interface UsingResources<R> {

    fun <T : AutoCloseable> T.use(): T

    operator fun <T : AutoCloseable> T.unaryPlus(): T = use()

    operator fun <T : AutoCloseable> T.invoke(): T = use()
}

internal class UsingResourcesImpl<R> :
    AutoCloseable,
    UsingResources<R> {
    var throwable: Throwable? = null
    val resourceQueue = ConcurrentLinkedQueue<AutoCloseable>()

    var result: R? = null

    override fun <T : AutoCloseable> T.use(): T {
        resourceQueue.offer(this)
        return this
    }

    override fun close() {
        for (closeable in resourceQueue) {
            try {
                closeable.close()
            } catch (t: Throwable) {
                if (this.throwable == null) {
                    this.throwable = t
                } else {
                    this.throwable!!.addSuppressed(t)
                }
            }
        }
    }

    fun getCatcher(): Catcher<R> = Catcher(this)
}

internal class Catcher<R>(val manager: UsingResourcesImpl<R>) {
    var throwable: Throwable? = null
    var thrown: Throwable? = null

    init {
        throwable = manager.throwable
    }

    inline infix fun <reified T : Throwable> catch(block: (T) -> Unit): Catcher<R> {
        if (throwable is T) {
            try {
                block(throwable as T)
            } catch (thrown: Throwable) {
                this.thrown = thrown
            } finally {
                // It's been caught, so set it to null
                throwable = null
            }
        }
        return this
    }

    inline infix fun finally(block: () -> Unit): R {
        try {
            block()
        } catch (thrown: Throwable) {
            if (throwable == null) {
                // we've caught the exception, or none was thrown
                if (this.thrown == null) {
                    // No exception was thrown in the catch blocks
                    throw thrown
                } else {
                    // An exception was thrown in the catch block
                    this.thrown!!.let {
                        it.addSuppressed(thrown)
                        throw it
                    }
                }
            } else {
                // We never caught the exception
                // So therefore this.thrown is also null
                throwable!!.let {
                    it.addSuppressed(thrown)
                    throw it
                }
            }
        }

        // At this point the finally block did not thrown an exception
        // We need to see if there are still any exceptions left to throw
        throwable?.let { throwable ->
            thrown?.let { throwable.addSuppressed(it) }
            throw throwable
        }
        thrown?.let { throw it }

        return manager.result as R
    }
}
