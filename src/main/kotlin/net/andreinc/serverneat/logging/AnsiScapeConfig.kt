package net.andreinc.serverneat.logging

import mu.KotlinLogging
import net.andreinc.ansiscape.AnsiClass
import net.andreinc.ansiscape.AnsiScape
import net.andreinc.ansiscape.AnsiScapeContext
import net.andreinc.ansiscape.AnsiSequence.*

private val logger = KotlinLogging.logger {  }

val ansiScapeCtx : AnsiScapeContext =
    AnsiScapeContext()
        .add(AnsiClass.withName("file").add(UNDERLINE, YELLOW))
        .add(AnsiClass.withName("httpMethod").add(BOLD, GREEN))
        .add(AnsiClass.withName("logo").add(UNDERLINE, BLUE))
        .add(AnsiClass.withName("logoNeat").add(RED, BLUE_BG))
        .add(AnsiClass.withName("path").add(UNDERLINE, BLUE))


val ansiScape : AnsiScape = AnsiScape(ansiScapeCtx)

fun ansi(msg: String) : String {
    return ansiScape.format(msg)
}

fun main() {
    logger.info { ansi("{red Andrei}") }
}