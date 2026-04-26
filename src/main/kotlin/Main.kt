import java.nio.file.Path

fun main(args: Array<String>) {
    if (args.size != 1) {
        println("Usage: ./gradlew run --args=\"<input-file-path>\"")
        return
    }

    val inputPath = Path.of(args[0])
    val result = LexerApp.runOnFile(inputPath)
    val outputPath = LexerApp.writeOutput(inputPath, result)

    when (result) {
        is LexResult.Success -> {
            println("Lexing finished successfully.")
            println("Output written to: $outputPath")
            println("Token count: ${result.tokens.size}")
        }
        is LexResult.Failure -> {
            println("Lexing failed with ${result.errors.size} error(s).")
            println("Output written to: $outputPath")
        }
    }
}
