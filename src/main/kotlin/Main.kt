import io.vertx.kotlin.core.json.Json
import mu.KotlinLogging
import net.andreinc.serverneat.KotlinScriptRunner
import java.nio.charset.Charset

val logger = KotlinLogging.logger {  }

class MainContext {
    companion object {
        fun readResource(resource: String, charset: Charset = Charsets.UTF_8) : String {
            val resourceUrl = this::class.java.getResource(resource)
            return resourceUrl.readText(charset)
        }
        fun currentClassLoader() : ClassLoader {
            return Thread.currentThread().contextClassLoader
        }
    }
}


fun main(args: Array<String>) {
    KotlinScriptRunner().evalFile("rest-crud-example.kts", readAsResource = true, compileFirst = true)
}