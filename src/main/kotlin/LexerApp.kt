import org.antlr.v4.runtime.BaseErrorListener
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.Recognizer
import org.antlr.v4.runtime.RecognitionException
import java.nio.file.Path
import kotlin.io.path.readText
import kotlin.io.path.writeText

data class LexToken(
    val type: String,
    val value: String,
    val line: Int,
    val column: Int,
)

sealed interface LexResult {
    data class Success(val tokens: List<LexToken>) : LexResult
    data class Failure(val errors: List<String>) : LexResult
}

object LexerApp {
    fun runOnFile(inputPath: Path): LexResult {
        val content = inputPath.readText()
        val lexer = KocamlLexer(CharStreams.fromString(content))

        val errors = mutableListOf<String>()
        lexer.removeErrorListeners()
        lexer.addErrorListener(object : BaseErrorListener() {
            override fun syntaxError(
                recognizer: Recognizer<*, *>?,
                offendingSymbol: Any?,
                line: Int,
                charPositionInLine: Int,
                msg: String?,
                e: RecognitionException?,
            ) {
                val description = msg ?: "unknown lexer error"
                errors += "line $line:$charPositionInLine $description"
            }
        })

        val tokenStream = CommonTokenStream(lexer)
        tokenStream.fill()

        if (errors.isNotEmpty()) {
            return LexResult.Failure(errors)
        }

        val tokens = tokenStream.tokens
            .filter { it.type != KocamlLexer.EOF }
            .map { token ->
                LexToken(
                    type = lexer.vocabulary.getSymbolicName(token.type) ?: "UNKNOWN",
                    value = token.text.escapeForOutput(),
                    line = token.line,
                    column = token.charPositionInLine,
                )
            }

        return LexResult.Success(tokens)
    }

    fun writeOutput(inputPath: Path, result: LexResult): Path {
        val outputPath = Path.of("${inputPath}.out")
        val body = when (result) {
            is LexResult.Success -> result.tokens.joinToString(separator = "\n") {
                "${it.type} '${it.value}' @ ${it.line}:${it.column}"
            }
            is LexResult.Failure -> {
                val message = result.errors.joinToString(separator = "\n")
                "LEXER_ERROR\n$message"
            }
        }
        outputPath.writeText("$body\n")
        return outputPath
    }

    private fun String.escapeForOutput(): String =
        this.replace("\\", "\\\\")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t")
            .replace("'", "\\'")
}
