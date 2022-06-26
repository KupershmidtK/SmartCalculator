package calculator

interface IValidator {
    fun validate(inputStr: String): String
}

class InputValidator(val assignmentValidator: IValidator, val expressionValidator: IValidator) {
    fun validate(inputString: String): String? {
        if (inputString == EMPTY_STR
            || inputString == CMD_EXIT
            || inputString == CMD_HELP) return inputString

        if (inputString.first() == '/') {
            throw Exception( "Unknown command")
        }

        return if (inputString.find { it == '=' } != null) {
            assignmentValidator.validate(inputString)
        } else {
            expressionValidator.validate(inputString)
        }
    }
}

class AssignmentValidator: IValidator {
    override fun validate(inputString: String): String {
        if (!inputString.matches("\\s*[a-zA-Z]+\\s*=.*".toRegex())) {
            throw Exception("Invalid identifier")
        } else if (!inputString.matches("\\s*[a-zA-Z]+\\s*=\\s*-?(\\d+|[a-zA-Z]+)\\s*".toRegex())) {
            throw Exception("Invalid assignment")
        }
        return inputString.filterNot { it.isWhitespace() }
    }
}

class ExpressionValidator: IValidator {
    override fun validate(inputStr: String): String {
        val elements = inputStr
            .split("\\b".toRegex())
            .toMutableList()

        var parenthesesCount: Int = 0

        for (i in 0..elements.lastIndex) {
            if (elements[i].isEmpty()) continue // side effect of split by \b
            if (elements[i].isBlank()) throw Exception("Invalid expression") // not value and not operator

            val element = elements[i].filterNot { it.isWhitespace() } // remove all whitespaces

            if (element.first().isLetterOrDigit()) {
                if (!element.matches("([a-zA-Z]+|\\d+)".toRegex())) {
                    throw Exception("Invalid identifier")
                }
            } else {
                elements[i] = validateOperator(element) // may throw exception

                // delete first +
                if (i == 0) {
                    elements[i] = elements[i].removePrefix("+ ")
                    elements[i] = elements[i].replace("-", "#")
                }

                parenthesesCount += countParentheses(elements[i])
            }
        }

        if (parenthesesCount != 0) throw Exception("Invalid expression") // if count of ( != count of )

        // delete empty elements
        for (i in elements.lastIndex downTo 0) {
            if (elements[i].isEmpty()) elements.removeAt(i)
        }

        // check first and last symbols
        if (!elements.first().matches("^[a-zA-Z\\d(#].*".toRegex())
            || !elements.last().matches(".*[a-zA-Z\\d)]$".toRegex())
        ) {
            throw Exception("Invalid expression")
        }
        return elements.joinToString(" ")
    }

    private fun countParentheses(s: String) = s.count { it == '(' } - s.count { it == ')' }

    private fun validateOperator(element: String): String {
        // wrong expressions
        if (
            "[^/*^+\\-)(]".toRegex().find(element) != null // there is any symbol than /*^+\-)(
            || "[/*^]{2,}".toRegex().find(element) != null // symbol repeated more than one time  / * ^
            || "\\+-{2,}".toRegex().find(element) != null // sequence of symbols +----
            || "\\+[*/^]".toRegex().find(element) != null // sequence of symbols + and +/*^
            || "-[+*/^]".toRegex().find(element) != null // sequence of symbols - and +/*^
            || "\\([*/^]".toRegex().find(element) != null // sequence of symbols (* (/ or (^
        ) throw Exception("Invalid expression")


        var outStr = element
        // replace "---"(odd) -> "-" and "--"(even) -> "+"
        while (true) {
            val pos = "-{2,}".toRegex().find(outStr) ?: break
            if (pos.value.length % 2 == 0) {
                outStr = outStr.replaceRange(pos.range, "+")
            } else {
                outStr = outStr.replaceRange(pos.range, "-")
            }
        }
        // replace "+++" -> "+"
        outStr = outStr.replace("\\+{2,}".toRegex(), "+")
        // replace "+-" and "-+" -> "-"
        outStr = outStr.replace("(\\+-|-\\+)".toRegex(), "-")
        // replace "(+" -> "("
        outStr = outStr.replace("\\(\\+".toRegex(), "(")

        if (outStr.length != 1) outStr = outStr.replace("(-", "(#") // new symbol for unary minus

        return outStr.toCharArray().joinToString(" ")
    }
}