package calculator

import java.math.BigInteger

class Calculator {
    private val variables = mutableMapOf<String, BigInteger>()
    private val translator = InfixToPostfixTranslator()
    private val calcEngine: ICalcEngine = PostfixCalcEngine()

    fun calculate(inputString: String): String? {
        return if (inputString.find { it == '=' } != null) { // assign variable
            assignVariable(inputString.removeSpaces())
            return null
        } else { // calculate expression
            calculateExpression(inputString)
        }
    }

    private fun calculateExpression(input: String): String {
        val postfixStr = translator.translate(input)
        val postfixStrWithValues = initiateVariables(postfixStr)
        return calcEngine.calculate(postfixStrWithValues).toString()
    }

    private fun assignVariable(inputString: String) {
        val (variable, value) = inputString.split("=")
        if (value.isDigit()) {
            variables[variable] = value.toBigInteger()
        } else {
            if (variables.contains(value)) {
                variables[variable] = variables[value]!!
            } else {
                throw Exception("Unknown variable")
            }
        }
    }

    private fun initiateVariables(inputString: String): String {
        val elements = inputString.split("\\s+".toRegex()).toMutableList()
        for (i in 0 .. elements.lastIndex) {
            if (elements[i].isVariable()) {
                if (variables.contains(elements[i])) {
                    elements[i] = variables[elements[i]].toString()
                } else {
                    throw Exception("Unknown variable")
                }
            }
        }
        return elements.joinToString(" ")
    }

}
