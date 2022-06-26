package calculator

import java.math.BigInteger

interface ICalcEngine {
    fun calculate(expression: String): BigInteger
}

class PostfixCalcEngine: ICalcEngine {
    private val stack = ArrayDeque<BigInteger>()

    override fun calculate(expression: String): BigInteger {
        val elements = expression.split(" ")

        for (operation in elements) {
            if (operation.isDigit()) addValueToStack(operation)
            else executeOperation(operation)
        }

        if(stack.size != 1) throw Exception("Invalid expression")
        return stack.removeFirst()
    }

    private fun addValueToStack(value: String) {
        stack.addFirst(value.toBigInteger())
    }

    private fun executeOperation(operation: String) {
        var operand1: BigInteger = BigInteger.ZERO
        var operand2: BigInteger = BigInteger.ZERO

        try {
            operand2 = stack.removeFirst()
            if (operation != "#") // unary minus
                operand1 = stack.removeFirst()
        } catch (e: Exception) {
            throw Exception("Invalid expression")
        }

        when (operation) {
            "#" -> stack.addFirst(-operand2)
            "+" -> stack.addFirst(operand1 + operand2)
            "-" -> stack.addFirst(operand1 - operand2)
            "*" -> stack.addFirst(operand1 * operand2)
            "/" -> {
                if (operand2 == BigInteger.ZERO) throw Exception("Divide to zero")
                stack.addFirst(operand1 / operand2)
            } // !!!
            "^" -> stack.addFirst(operand1.pow(operand2.toInt()))
            else -> throw Exception("Invalid expression")
        }
    }
}