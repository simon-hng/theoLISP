object Recognizer {
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

    fun isArithmetic(token: String, operation: String): Boolean =
        bracketed(token)
        { isOperation(it, { it == operation }, ::isTerm, ::isTerm) }

    // Actual productions
    fun isTerm(token: String): Boolean {
        val result = listOf(::isVariable, ::isNumber, ::isAdd, ::isSub, ::isMul).any { it(token) }
        if (!result) println(token)
        return result
    }

    fun isVariable(token: String) = token.all { it.toInt() in IntRange(0x61, 0x7a) }
    fun isNumber(token: String) = token.all { it.toInt() in IntRange(0x30, 0x39) }

    fun isAdd(token: String) = isArithmetic(token, "add")
    fun isSub(token: String) = isArithmetic(token, "sub")
    fun isMul(token: String) = isArithmetic(token, "mul")

    fun isStmtOuter(token: String): Boolean {
        val result = listOf(::isIf, ::isWhile, ::isSet).any { it(token) } ||
                bracketed(token, ::isStmtInner)
        if (!result) println(token)
        return result
    }

    fun isStmtInner(token: String): Boolean {
        return tokenize(token).all { isStmtOuter(it) }
    }

    fun isIf(token: String): Boolean =
        bracketed(token)
        { isOperation(it, { op -> op == "if" }, ::isTerm, ::isStmtOuter, ::isStmtOuter) }

    fun isWhile(token: String): Boolean =
        bracketed(token)
        { isOperation(it, { op -> op == "while" }, ::isTerm, ::isStmtOuter) }

    fun isSet(token: String): Boolean =
        bracketed(token)
        { isOperation(it, { op -> op == "set" }, ::isVariable, ::isTerm) }
}