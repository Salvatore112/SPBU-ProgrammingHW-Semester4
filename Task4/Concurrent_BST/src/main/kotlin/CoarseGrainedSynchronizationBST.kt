package org.example

import kotlinx.coroutines.sync.Mutex

// Locking the whole tree for each operation
class CoarseGrainedSynchronizationBST<K : Comparable<K>, V> : AbstractBST<K, V>() {
    private val mutex = Mutex()

    override suspend fun search(key: K): V? {
        mutex.lock()
        val node = find(key, root)
        mutex.unlock()
        return node?.value
    }

    override suspend fun insert(key: K, value: V?) {
        mutex.lock()
        insertNode(key, value)
        mutex.unlock()
    }

    override suspend fun insertNode(key: K, value: V?): BSTNode<K, V> {
        // No nodes were added before
        if (root == null) {
            val newNode = BSTNode(key, value)
            root = newNode
            return newNode
        }

        var walkNode = root ?: throw Exception("Walker node is not supposed to be null")
        while ( true ) {
            if (key > walkNode.key) {
                // Place to insert was found
                if (walkNode.rightChildNode == null) {
                    val newNode = BSTNode(key, value)
                    walkNode.rightChildNode = newNode
                    newNode.parentNode = walkNode
                    return newNode
                }
                // Going deeper
                walkNode = walkNode.rightChildNode ?: throw Exception("The case of null right child was already covered and can no longer happen")
            } else if (key < walkNode.key) {
                // Place to insert was found
                if (walkNode.leftChildNode == null) {
                    val newNode = BSTNode(key, value)
                    walkNode.leftChildNode = newNode
                    newNode.parentNode = walkNode
                    return newNode
                }
                // Going deeper
                walkNode = walkNode.leftChildNode ?: throw Exception("The case of null left child was already covered and can no longer happen")
            } else {
                throw Exception("Existing key was tried to be added")
            }
        }
    }

    override suspend fun delete(key: K): V? {
        mutex.lock()
        val node = find(key, root) ?: return null
        deleteNode(node)
        mutex.unlock()
        return node.value
    }
}