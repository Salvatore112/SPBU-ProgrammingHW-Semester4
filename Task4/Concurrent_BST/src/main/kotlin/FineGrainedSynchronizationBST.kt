package org.example

import kotlinx.coroutines.sync.Mutex

class FineGrainedSynchronizationBST<K : Comparable<K>, V> : AbstractBST<K, V>() {

    private val bst_Mutex = Mutex()
    override suspend fun insert(key: K, value: V?) {
        bst_Mutex.lock()
        if (root != null) {
            root?.lock()
            bst_Mutex.unlock()
            insertNode(key, value)
        } else {
            root = BSTNode(key, value)
            bst_Mutex.unlock()
        }
    }

    override suspend fun insertNode(key: K, value: V?): BSTNode<K, V> {
        var walkNode = root ?: throw Exception("Walker node is not supposed to be null")
        while (true) {
            if (key > walkNode.key) {
                if (walkNode.rightChildNode == null) {
                    val newNode = BSTNode(key, value)
                    walkNode.rightChildNode = newNode
                    newNode.parentNode = walkNode
                    walkNode.unlock()
                    return newNode
                }
                val nextNode = walkNode.rightChildNode ?: throw Exception("The case of null right child was already covered and can no longer happen")
                nextNode.lock()
                walkNode = nextNode
                val parent = walkNode.parentNode ?: throw Exception("Parent node is not supposed to be null")
                parent.unlock()
            } else if (key < walkNode.key) {
                if (walkNode.leftChildNode == null) {
                    val newNode = BSTNode(key, value)
                    walkNode.leftChildNode = newNode
                    newNode.parentNode = walkNode
                    walkNode.unlock()
                    return newNode
                }
                val nextNode = walkNode.leftChildNode ?: throw Exception("The case of null left child was already covered and can no longer happen")
                nextNode.lock()
                walkNode = nextNode
                val parentNode = walkNode.parentNode ?: throw Exception("Parent node is not supposed to be null")
                parentNode.unlock()
            } else {
                throw Exception("Such key already exists in the tree")
            }
        }
    }
    override suspend fun find(key: K, rootNode: BSTNode<K, V>?): BSTNode<K, V>? {
        var walkNode = rootNode
        while (true) {
            if (walkNode == null) {
                return null
            }
            // Node was found
            if (key == walkNode.key) {
                walkNode.unlock()
                return walkNode
            } else if (key < walkNode.key) {
                val left = walkNode.leftChildNode
                left?.lock()
                walkNode.unlock()
                walkNode = left
            } else {
                val right = walkNode.rightChildNode
                right?.lock()
                walkNode.unlock()
                walkNode = right
            }
        }
    }

    override suspend fun delete(key: K): V? {
        bst_Mutex.lock()
        if (root?.key == key) {
            deleteNode(root!!)
            bst_Mutex.unlock()
            return root?.value
        } else if (root != null) {
            root?.lock()
            bst_Mutex.unlock()
        } else {
            bst_Mutex.unlock()
            return null
        }
        val node = find(key, root) ?: return null
        if (node == root) {
            deleteNode(node)
            return node.value
        }
        deleteNode(node)
        return node.value
    }
    override suspend fun search(key: K): V? {
        root?.lock()
        return find(key, root)?.value
    }
}