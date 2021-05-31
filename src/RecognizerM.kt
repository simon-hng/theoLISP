object RecognizerM {
    fun tokenize(input: String): List<String> {
        val result = mutableListOf<String>()
        var brackets = 0
        var currentWord = ""
        for (c in input) {
            if (c == '(') {
                brackets++
                currentWord += c
            } else if (c == ')') {
                if (brackets == 1) {
                    result.add(currentWord + c)
                    currentWord = ""
                } else {
                    currentWord += c
                }
                brackets--
            } else if (c == ' ' && brackets == 0) {
                result.add(currentWord)
                currentWord = ""
            } else {
                currentWord += c
            }
        }

        result.add(currentWord)
        return result.filter { it != "" }
    }

    fun bracketed(token: String, innerFun: (String) -> Boolean): Boolean =
        (token.first() == '(' && token.last() == ')'
                && innerFun(token.removeSurrounding("(", ")")))

    fun isOperation(input: String, vararg funs: (String) -> Boolean): Boolean {
        val tokens = tokenize(input)
        return (tokens.size == funs.size
                && tokens.zip(funs).all { (t, f) -> f(t) })
    }

    fun isArithHelper(token: String, operation: String): Boolean =
        bracketed(token)
        { isOperation(it, { it == operation }, ::isArithmetic, ::isArithmetic) }

    // Actual productions
    fun isProgram(token: String) = bracketed(token, ::isExpr)

    fun isExpr(token: String) = listOf(::isArithmetic, ::isBedingung, ::isVarzuw).any { it(token) }

    fun isArithmetic(token: String): Boolean {
        return isArithHelper(token, "add") ||
                isArithHelper(token, "sub") ||
                isArithHelper(token, "mul") ||
                isVar(token) ||
                isNum(token)
    }

    fun isBedingung(token: String): Boolean {
        return bracketed(token) { isOperation(it, { it == "if" }, ::isProgram, ::isProgram) } ||
                bracketed(token) { isOperation(it, { it == "while" }, ::isArithmetic, ::isProgram) }
    }

    fun isVarzuw(token: String): Boolean {
        return bracketed(token) { isOperation(it, { it == "set" }, ::isVar, ::isArithmetic) }
    }

    fun isVar(token: String) = token.all { it.toInt() in IntRange(0x61, 0x7a) }
    fun isNum(token: String) = token.all { it.toInt() in IntRange(0x30, 0x39) }
}