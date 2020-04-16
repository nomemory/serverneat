import mu.KotlinLogging
import net.andreinc.serverneat.CliApp
import net.andreinc.serverneat.logging.ansi
import picocli.CommandLine

private val logger = KotlinLogging.logger {  }

fun main(args: Array<String>) {
    logger.info { ansi("Initialising {logoNeat ServerNeat}") }
    CommandLine(CliApp()).execute(*args)
}