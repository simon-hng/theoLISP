import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import java.io.File
import java.nio.file.Files
import java.nio.file.Files.isReadable
import java.nio.file.Files.walk
import java.nio.file.Path
import java.util.stream.Stream

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
        assertTrue(Recognizer.isStmtOuter("(set result (sub x 2021))"))
    }

    @Test
    fun isStmtO1() {
        assertTrue(Recognizer.isStmtOuter("(set x 42)"))
    }

    @Test
    fun isStmtO2() {
        assertTrue(Recognizer.isStmtOuter("((set x 42)(set result (sub x 2021)))"))
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
                    ) { assertTrue(RecognizerM.isProgram(path.readText())) }
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
                    ) { assertFalse(RecognizerM.isProgram(path.readText())) }
                }
                .toList()

        @Test
        fun marmortafel() {
            assertFalse(
                RecognizerM.isProgram(
                    (File("./src/data/invalid/c1").readText())
                )
            )
        }
    }
}