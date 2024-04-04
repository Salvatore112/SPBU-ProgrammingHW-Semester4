import org.jetbrains.kotlinx.lincheck.annotations.Operation
import org.jetbrains.kotlinx.lincheck.check
import org.jetbrains.kotlinx.lincheck.strategy.stress.StressOptions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals

class ParticularDataTests {
    @Test
    fun popOnEmptyStackShouldReturnNull() {
        val stack = LockFreeStack<Int>()
        assertEquals(stack.peek(), null)
    }

    @Test
    fun popPeekPushShouldWorkAsExpected() {
        val stack = LockFreeStack<Int>()
        stack.push(10)
        stack.push(100)
        stack.push(1000)
        assertEquals(stack.peek(), 1000)
        assertEquals(stack.peek(), 1000)
        assertEquals(stack.pop(), 1000)
        assertEquals(stack.pop(), 100)
    }
}

class RandomStressTesting {
    private val stack = LockFreeStack<Int>()

    @Operation
    fun push(x: Int) = stack.push(x)

    @Operation
    fun pop() = stack.pop()

    @Operation
    fun top() = stack.peek()

    @Test
    fun stressTest() = StressOptions()
        .iterations(10)
        .invocationsPerIteration(5000)
        .threads(3)
        .actorsPerThread(3)
        .check(this::class)
}
