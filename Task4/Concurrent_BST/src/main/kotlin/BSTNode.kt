package org.example

import kotlinx.coroutines.sync.Mutex

class BSTNode<K : Comparable<K>, V>(
    var key: K,
    var value: V? = null,
    var leftChildNode: BSTNode<K, V>? = null,
    var rightChildNode: BSTNode<K, V>? = null,
    var parentNode: BSTNode<K, V>? = null
) {
    private val mutex = Mutex()
    suspend fun lock() = mutex.lock()
    fun unlock() = mutex.unlock()
    fun nodeHoldsLock(): Boolean = mutex.isLocked
}