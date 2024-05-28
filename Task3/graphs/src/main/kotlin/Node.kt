package org.example

class Node(val id: Int, val distance: Int) : Comparable<Node> {
    override fun compareTo(other: Node) = distance.compareTo(other.distance)
}