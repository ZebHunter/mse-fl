@file:OptIn(kotlin.io.path.ExperimentalPathApi::class)

import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.extension
import kotlin.io.path.name
import kotlin.io.path.readText
import kotlin.io.path.walk
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LexerAppTest {

    private fun normalize(s: String) =
        s.replace("\r\n", "\n")
            .trimEnd()


    @Test
    fun `all valid inputs produce expected outputs`() {
        val validDir = Path.of("src/test/resources/lexer/valid")
        val inputFiles = validDir.walk()
            .filter { it.extension == "in" }
            .toList()

        assertTrue(inputFiles.isNotEmpty(), "No valid .in files found in $validDir")

        for (input in inputFiles) {
            val expectedPath = Path.of("$input.out")
            assertTrue(expectedPath.exists(), "Missing expected file: $expectedPath")

            val expected = expectedPath.readText()
            val result = LexerApp.runOnFile(input)
            val outputPath = LexerApp.writeOutput(input, result)
            val actual = outputPath.readText()

            assertTrue(result is LexResult.Success, "Expected success for ${input.name}")
            assertEquals(
                normalize(expected),
                normalize(actual),
                "Mismatch for ${input.name}"
            )
        }
    }

    @Test
    fun `all invalid inputs produce human readable lexer errors`() {
        val invalidDir = Path.of("src/test/resources/lexer/invalid")
        val inputFiles = invalidDir.walk()
            .filter { it.extension == "in" }
            .toList()

        assertTrue(inputFiles.isNotEmpty(), "No invalid .in files found in $invalidDir")

        for (input in inputFiles) {
            val expectedPath = Path.of("$input.out")
            assertTrue(expectedPath.exists(), "Missing expected file: $expectedPath")

            val expected = expectedPath.readText()
            val result = LexerApp.runOnFile(input)
            val outputPath = LexerApp.writeOutput(input, result)
            val actual = outputPath.readText()

            assertTrue(result is LexResult.Failure, "Expected failure for ${input.name}")
            assertEquals(
                normalize(expected),
                normalize(actual),
                "Mismatch for ${input.name}"
            )
        }
    }
}
