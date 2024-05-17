package org.example

// Standard implementation of a binary search tree
abstract class AbstractBST<K : Comparable<K>, V> {
    var root: BSTNode<K, V>? = null

    abstract suspend fun search(key: K): V?

    abstract suspend fun insert(key: K, value: V?)

    abstract suspend fun delete(key: K): V?

    protected abstract suspend fun insertNode(key: K, value: V?): BSTNode<K, V>

    // Utility functions:

    // Standard BST find
    protected open suspend fun find(key: K, rootNode: BSTNode<K, V>?): BSTNode<K, V>? {
        if (rootNode == null) { return null }
        else if (key == rootNode.key) { return rootNode}
        else if (key < rootNode.key) { return find(key, rootNode.leftChildNode) }
        else { return find(key, rootNode.rightChildNode) }
    }

    // Standard BST deletion (No children, one child, two children)
    protected open fun deleteNode(node: BSTNode<K, V>) {
        if (node.leftChildNode == null && node.rightChildNode == null) {
            replaceNode(node, null)
        }   else if (node.leftChildNode == null || node.rightChildNode == null) {
            replaceNode(node, if (node.leftChildNode == null) node.rightChildNode else node.leftChildNode)
        }    else {
            val successor = getSuccessor(node)
            node.key = successor.key
            node.value = successor.value
            deleteNode(successor)
        }
    }

    private fun replaceNode(nodeBeingReplaced: BSTNode<K, V>, replacementNode: BSTNode<K, V>?) {
        val parent = nodeBeingReplaced.parentNode
        if (parent == null) {
            root = replacementNode
        } else {
            if (parent.leftChildNode == nodeBeingReplaced) {
                parent.leftChildNode = replacementNode
            }
            else {
                parent.rightChildNode = replacementNode
            }
        }
        replacementNode?.parentNode = parent
    }

    private fun getSuccessor(node: BSTNode<K, V>): BSTNode<K, V> {
        var successorNode = node.leftChildNode ?: throw Exception("Node is expected to have 2 children")
        while (successorNode.rightChildNode != null) {
            successorNode = successorNode.rightChildNode ?: throw Exception("Successor node is expetected to have the right child")
        }
        return successorNode
    }
}