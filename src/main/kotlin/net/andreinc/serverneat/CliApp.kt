package net.andreinc.serverneat

import picocli.CommandLine.Command
import picocli.CommandLine.Option
import java.util.concurrent.Callable

@Command(
    name = "serverneat",
    version = ["1.0alpha"],
    mixinStandardHelpOptions = false
)
class CliApp : Callable<Int> {

    @Option(
        required = true,
        names = ["-f", "--file"],
        paramLabel = "FILE",
        description = ["The relative path to the '.kts' script containing the server"]
    )
    private lateinit var file : String

    @Option(
        required = false,
        names = [ "-e", "--evaluation-only" ],
        paramLabel = "EVALUATION_ONLY",
        description = [ "The .kts script is only evaluated, without compilation (can increase startup time)." ]
    )
    private var evaluationOnly : Boolean = false

    override fun call(): Int {
        KotlinScriptRunner.evalFile(file, readAsResource = false, compileFirst = !evaluationOnly);
        return 0
    }
}

