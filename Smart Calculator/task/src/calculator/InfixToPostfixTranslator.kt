package calculator

object OperationPriority {
    private val mapOfOperations = mapOf(
        Pair('#', 5), // unary minus
        Pair('(', 4),
        Pair(')', 4),
        Pair('^', 3),
        Pair('*', 2),
        Pair('/', 2),
        Pair('+', 1),
        Pair('-', 1),
    )

    fun getPriority(oper: Char) = mapOfOperations[oper] ?: throw Exception("Invalid expression")
}

class InfixToPostfixTranslator {
    private val stack =  ArrayDeque<String>()
    private val queue = ArrayDeque<String>()

    fun translate(expr: String): String {
        clear()
        val elements = expr.split(" ")
        for(e in elements) {
            if (isOperand(e)) {
                addOperand(e)
            } else {
                addOperator(e)
            }
        }
        while (stack.isNotEmpty()) {
            addOperand(stack.removeFirst())
        }
        return queue.joinToString(" ")
    }

    private fun addOperator(e: String) {
        when (e) {
            "(" -> stack.addFirst(e)
            ")" -> {
                while (true) {
                    val element = stack.removeFirstOrNull() ?: throw Exception("Invalid expression")
                    if (element == "(") break
                    else addOperand(element)
                }
                val element = stack.firstOrNull() ?: ""
                if (element == "#") addOperand(stack.removeFirst())
            }
            else -> {
                val priority = OperationPriority.getPriority(e.first())
                while (true) {
                    if (stack.isEmpty() || stack.first() == "(") {
                        stack.addFirst(e)
                        break
                    } else {
                        val element = stack.first()
                        val stackElemPriority = OperationPriority.getPriority(element.first())
                        if (priority <= stackElemPriority) {
                            addOperand(stack.removeFirst())
                        } else {
                            stack.addFirst(e)
                            break
                        }
                    }
                }
            }
        }
    }

    private fun addOperand(e: String) = queue.addLast(e)

    private fun isOperand(e: String) = e.matches("([a-zA-Z]+|\\d+)".toRegex())

    private fun clear() {
        stack.clear()
        queue.clear()
    }
}
