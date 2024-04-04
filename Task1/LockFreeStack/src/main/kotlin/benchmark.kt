import kotlin.concurrent.thread
import kotlin.random.Random

class ProduceConsumeBenchmark(private val stack: ConcurrentStack<Int>, private val workload: Long) {

    @Volatile
    var run = false

    fun perform(time: Long, threadCount: Int): Int {
        run = false
        val operationsArray = Array(threadCount) {
            0
        }

        val threadArray = Array(threadCount) {
            thread(start = true) {
                while (!run) {
                }

                while (run) {
                    stack.push(15)
                    stack.pop()
                    operationsArray[it] += 2
                    Thread.sleep(Random.nextLong(0, workload))
                }
            }
        }

        run = true

        Thread.sleep(time)

        run = false
        threadArray.forEach { it.join() }

        return operationsArray.sum()
    }
}