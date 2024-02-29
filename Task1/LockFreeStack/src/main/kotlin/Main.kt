fun main() {
    println("Hello World!")
    val stack = EliminationBackoffStack<Int>(100, 6)
    stack.push(15)
    stack.push(13)
    stack.push(15)
    stack.pop()
    println("Hello World!")
    println("Hello World!")

}