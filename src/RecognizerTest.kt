import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import java.io.File

internal class RecognizerTest {

    @Test
    fun isVar() {
        assertTrue(Recognizer.isVariable("hallo"))
        assertFalse(Recognizer.isVariable("hall11111111111o"))
    }

    @Test
    fun isNum() {
        assertTrue(Recognizer.isNumber("1"))
        assertFalse(Recognizer.isNumber("hall11111111111o"))
    }

    @Test
    fun isSub() {
        assertTrue(Recognizer.isSub("(sub x 2021)"))
    }

    @Test
    fun isSet() {
        assertTrue(Recognizer.isSet("(set x 42)"))
    }

    @Test
    fun isSet01() {
        assertTrue(Recognizer.isSet("(set result (sub x 2021))"))
    }

    @Test
    fun isStmtO0() {
        assertTrue(Recognizer.isBefehle("(set result (sub x 2021))"))
    }

    @Test
    fun isStmtO1() {
        assertTrue(Recognizer.isBefehle("(set x 42)"))
    }

    @Test
    fun isStmtO2() {
        assertTrue(Recognizer.isBefehle("((set x 42)(set result (sub x 2021)))"))
    }

    @Nested
    class parser {
        @Nested
        class arithmetic {
            @Test
            fun arithmeticAdd() {
                val act = Recognizer.term("(add 1 2)")
                assertEquals(3, act)
            }

            @Test
            fun arithmeticSub() {
                val act = Recognizer.term("(sub 5 2)")
                assertEquals(3, act)
            }

            @Test
            fun arithmeticMul() {
                val act = Recognizer.term("(mul 3 2)")
                assertEquals(6, act)
            }
        }

        @Test
        fun a1() =
            Recognizer.start("((set x 42) (set result (sub x 2021)))")

        @Test
        fun a2() =
            Recognizer.start(
                "((set x 1) (while (sub 16 i) ((set x (mul x 2)) (set i (add i 1)))) (set result x))"
            )

        @Test
        fun a3() =
            Recognizer.start(
                "((set a 247) (set b 299) (while a ((set b (sub b a)) (if (sub a b) ((set t a) (set a b) (set b t)) ()))) (set result b))"
            )

        @Test
        fun c2() =
            Recognizer.start(
                "((set n 339) (while (sub 1 result) ((set nn (sub n 1)) (while (sub 1 flag) ((set dd 0) (while (sub nn 1) ((set dd (add dd 1)) (set nn (sub nn 2)))) (if nn ((set flag 1)) ((set nn dd) (set d dd) (set r (add r 1)))))) (set k 4) (while k ((set a (mul (add a 1) 89)) (while (sub a 112) ((set a (sub a 113)))) (set x 1) (set dd d) (while dd ((set x (mul x a)) (while (sub x (sub n 1)) ((set x (sub x n)))) (set dd (sub dd 1)))) (set xx (mul (sub x 1) (sub x (sub n 1)))) (set flag 0) (if (mul xx xx) ((set rr (sub r 1)) (while rr ((set x (mul x x)) (while (sub x (sub n 1)) ((set x (sub x n)))) (if (sub (sub n 1) x) () ((set flag 1) (set rr 0))) (set rr (sub rr 1))))) ((set flag 1))) (if (sub 1 flag) ((set k 0)) ()) (set k (sub k 1)))) (if (add k 1) ((set result n)) ((set n (add n 2)))))))"
            )
    }

    @Nested
    class testData {
        @TestFactory
        fun valid(): List<DynamicNode> =
            File("./src/data/valid")
                .walk(FileWalkDirection.BOTTOM_UP)
                .filter { it.isFile }
                .map { path ->
                    DynamicTest.dynamicTest(
                        path.toString()
                    ) { Recognizer.befehle(path.readText()) }
                }
                .toList()

        @TestFactory
        fun invalid(): List<DynamicNode> =
            File("./src/data/invalid")
                .walk(FileWalkDirection.BOTTOM_UP)
                .filter { it.isFile }
                .map { path ->
                    DynamicTest.dynamicTest(
                        path.toString()
                    ) { assertFalse(Recognizer.isBefehle(path.readText())) }
                }
                .toList()

        @Test
        fun marmortafel() {
            assertFalse(
                Recognizer.isBefehle(
                    (File("./src/data/invalid/c1").readText())
                )
            )
        }
    }
}