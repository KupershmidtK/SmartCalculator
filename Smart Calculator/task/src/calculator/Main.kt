package calculator

const val CMD_EXIT = "/exit"
const val CMD_HELP = "/help"
const val EMPTY_STR = ""

fun String.isVariable() = this.all { it.isLetter() }
fun String.isDigit() = this.toBigIntegerOrNull() != null
fun String.removeSpaces() = this.filterNot { it.isWhitespace() }

fun main() {
    val validator = InputValidator(AssignmentValidator(), ExpressionValidator())
    val calculator = Calculator()

    while (true) {
        try {
            val input = readln().let { validator.validate(it.removeSpaces()) }
            when (input) {
                EMPTY_STR -> continue
                CMD_HELP -> helpInfo()
                CMD_EXIT -> break
                else -> {
                    val res = input?.let { calculator.calculate(it) }
                    res?.let { println(it) }
                }
            }
        } catch (e: Exception) {
            println(e.message)
        }
    }
    println("Bye!")
}

fun helpInfo() = println("The program calculates the sum of numbers")
