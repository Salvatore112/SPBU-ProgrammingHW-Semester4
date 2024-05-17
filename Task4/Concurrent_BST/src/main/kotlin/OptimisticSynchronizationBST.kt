package org.example

import kotlinx.coroutines.sync.Mutex

class OptimisticSyncBST<K : Comparable<K>, V> : AbstractBST<K, V>() {

    private val bstMutex = Mutex()
    override suspend fun delete(key: K): V? {
        bstMutex.lock()
        if (root?.key == key) {
            deleteNode(root!!)
            bstMutex.unlock()
            return root?.value
        }
        bstMutex.unlock()
        val node = find(key, root) ?: return null
        if (node == root) {
            deleteNode(node)
            return node.value
        }
        deleteNode(node)
        return node.value
    }
    override suspend fun insert(key: K, value: V?) {
        bstMutex.lock()
        if (root != null) {
            root?.lock()
            bstMutex.unlock()
            insertNode(key, value)
        } else {
            root = BSTNode(key, value)
            bstMutex.unlock()
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
                val nextNode = walkNode.rightChildNode ?: throw IllegalStateException("The case of null right child was already covered and can no longer happen")
                nextNode.lock()
                walkNode = nextNode
                val parent = walkNode.parentNode ?: throw IllegalStateException("Parent node is not supposed to be null")
                parent.unlock()
            } else if (key < walkNode.key) {
                if (walkNode.leftChildNode == null) {
                    val newNode = BSTNode(key, value)
                    walkNode.leftChildNode = newNode
                    newNode.parentNode = walkNode
                    walkNode.unlock()
                    return newNode
                }
                val nextNode = walkNode.leftChildNode ?: throw IllegalStateException("The case of null left child was already covered and can no longer happen")
                nextNode.lock()
                walkNode = nextNode
                val parent = walkNode.parentNode ?: throw IllegalStateException("Parent node is not supposed to be null")
                parent.unlock()
            } else throw Exception("Such key already exists in the tree")
        }
    }
    override suspend fun search(key: K): V? {
        val node = find(key, root)
        return node?.value
    }

    override suspend fun find(key: K, rootNode: BSTNode<K, V>?): BSTNode<K, V>? {
        var walkNode = rootNode
        while (true) {
            if (walkNode == null) {
                return null
            }
            if (key == walkNode.key) {
                walkNode.lock()
                if (validate(walkNode) && walkNode.key == key) {
                    walkNode.unlock()
                    return walkNode
                } else {
                    walkNode.unlock()
                    return null
                }
            } else if (key < walkNode.key) {
                walkNode = walkNode.leftChildNode
            } else {
                walkNode = walkNode.rightChildNode
            }
        }
    }
    private fun validate(childNode: BSTNode<K, V>): Boolean {
        if (root == null) return false
        var walkNode = root
        while (true) {
            if (walkNode == childNode) {
                return true
            } else if  (walkNode?.key!! < childNode.key) {
                walkNode = walkNode.rightChildNode
            } else {
                walkNode = walkNode.leftChildNode
            }
        }
    }
}