package trees

import kotlin.random.Random
import kotlinx.coroutines.*
import org.example.AbstractBST
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test


abstract class GeneralTests<T : AbstractBST<Int, Int>>(
    treeType: () -> T,
    ) {
    private val nodes: Int = 500
    private fun sleepTime() = Random.nextLong(100)

    private val tree: T = treeType()
    private var randomNodes = (0..nodes).shuffled().take(nodes)



    @Test
    fun `deletion test`() {
        // Adding all the nodes to the tree
        runBlocking {
            repeat(nodes) {
                tree.insert(randomNodes[it], randomNodes[it])
            }
        }

        // Deleting added nodes
        runBlocking {
            randomNodes = randomNodes.shuffled(Random)
            repeat(nodes) {
                launch(Dispatchers.Default) {
                    delay(sleepTime())
                    tree.delete(randomNodes[it])
                }
            }
        }

        // Assuring that all nodes were deleted
        runBlocking {
            for (key in randomNodes) {
                assertEquals(null, tree.search(key))
            }
        }
    }

    @Test
    fun `parallel test`() {
        // Adding the nodes to the tree
        runBlocking {
            coroutineScope {
                repeat(nodes) {
                    launch(Dispatchers.Default) {
                        delay(sleepTime())
                        tree.insert(randomNodes[it], randomNodes[it])
                    }
                }
            }
        }

        val nodesToDelete = randomNodes.shuffled(Random).take(nodes / 2)

        runBlocking {
            coroutineScope {
                repeat(nodes / 2) {
                    launch(Dispatchers.Default) {
                        delay(sleepTime())
                        tree.delete(nodesToDelete[it])
                    }
                }
            }
        }

        runBlocking {
            for (key in randomNodes) {
                if (key !in nodesToDelete) {
                    assertEquals(key, tree.search(key))
                } else {
                    assertEquals(null, tree.search(key))
                }
            }
        }
    }

    @Test
    fun `insertion and search test`() {
        // Insert and search test
        runBlocking {
            coroutineScope {
                repeat(nodes) {
                    launch(Dispatchers.Default) {
                        delay(sleepTime())
                        tree.insert(randomNodes[it], randomNodes[it])
                    }
                }
            }
        }

        runBlocking {
            for (i in randomNodes) {
                assertEquals(i, tree.search(i))
            }
        }

    }

}