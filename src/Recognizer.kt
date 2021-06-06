object Recognizer {
    val variables = hashMapOf<String, Int>();

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

    fun term(token: String): Int {
        val tokens = tokenize(token.removeSurrounding("(", ")"))
        if (isAdd(token))
            return term(tokens[1]) + term(tokens[2])
        if (isSub(token))
            return term(tokens[1]) - term(tokens[2])
        if (isMul(token))
            return term(tokens[1]) * term(tokens[2])
        if (isVariable(token))
            return variables.getOrDefault(token, 0)
        return token.toInt()
    }

    fun start(token: String) {
        befehle(token.removeSurrounding("(", ")"))
        if (variables.containsKey("result"))
            println(variables["result"])
    }

    fun befehle(token: String) {
        tokenize(token)
            .forEach { t -> befehl(t.removeSurrounding("(", ")")) }
    }

    fun befehl(token: String) {
        if (isSet(token))
            pSet(token) else
            if (isIf(token))
                pIf(token) else
                if (isWhile(token))
                    pWhile(token)
    }

    fun pSet(token: String) {
        val tokens = tokenize(token)
        variables[tokens[1]] = term(tokens[2])
    }

    fun pIf(token: String) {
        val tokens = tokenize(token)
        if (term(tokens[1]) > 0) start(tokens[2])
        else start(tokens[3])
    }

    fun pWhile(token: String) {
        val tokens = tokenize(token)
        while (term(tokens[1]) > 0)
            start(tokens[2])
    }

    fun isArithmetic(token: String, operation: String): Boolean =
        bracketed(token)
        { isOperation(it, { it == operation }, ::isTerm, ::isTerm) }

    fun isTerm(token: String): Boolean = listOf(::isVariable, ::isNumber, ::isAdd, ::isSub, ::isMul).any { it(token) }

    fun isVariable(token: String) = token.all { it.toInt() in IntRange(0x61, 0x7a) }
    fun isNumber(token: String) = token.all { it.toInt() in IntRange(0x30, 0x39) }

    fun isAdd(token: String) = isArithmetic(token, "add")
    fun isSub(token: String) = isArithmetic(token, "sub")
    fun isMul(token: String) = isArithmetic(token, "mul")

    fun isBefehle(token: String): Boolean = bracketed(token, ::isBefehl)

    fun isBefehl(token: String): Boolean = tokenize(token).all { isBefehle(it) } ||
            listOf(::isIf, ::isWhile, ::isSet).any { it(token) }

    fun isIf(token: String): Boolean =
        isOperation(token, { op -> op == "if" }, ::isTerm, ::isBefehle, ::isBefehle)

    fun isWhile(token: String): Boolean =
        isOperation(token, { op -> op == "while" }, ::isTerm, ::isBefehle)

    fun isSet(token: String): Boolean =
        isOperation(token, { op -> op == "set" }, ::isVariable, ::isTerm)
}