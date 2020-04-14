package net.andreinc.serverneat

import MainContext.Companion.currentClassLoader
import MainContext.Companion.readResource
import mu.KotlinLogging
import java.io.File
import java.lang.IllegalStateException
import javax.script.Compilable
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager


private val logger = KotlinLogging.logger {  }

class KotlinScriptRunner {

    private val kotlinEngine: ScriptEngine =
        ScriptEngineManager(currentClassLoader())
            .getEngineByExtension("kts")


    fun eval(scriptContent: String, compileFirst: Boolean = true) {
        if (compileFirst) {
            when (kotlinEngine) {
                is Compilable -> {
                    logger.info { "Compiling script content "}
                    val compiledCode = kotlinEngine.compile(scriptContent)
                    logger.info { "Running compiled script content "}
                    compiledCode.eval()
                }
                else -> throw IllegalStateException("Kotlin is not an instance of Compilable. Cannot compile script.")
            }
        }
        else {
            logger.info { "Running script content (without compilation)"}
            kotlinEngine.eval(scriptContent)
        }
    }

    fun evalFile(file: String, readAsResource: Boolean, compileFirst: Boolean = true) {
        logger.info { ansi("Loading script: {file $file} ${if (readAsResource) "(as a resource)" else ""} with compilation flag set to {b true}") }
        val content : String = if (readAsResource) readResource(file) else File(file).readText()
        eval(content, compileFirst)
    }

}
