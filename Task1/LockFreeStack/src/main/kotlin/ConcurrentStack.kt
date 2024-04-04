interface ConcurrentStack<T> {
    fun push(value : T)
    fun pop() : T?
    fun empty() : Boolean
    fun peek() : T?
}