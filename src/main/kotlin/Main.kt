import mu.KotlinLogging
import net.andreinc.serverneat.KotlinScriptRunner

private val logger = KotlinLogging.logger {  }

fun main(args: Array<String>) {
    //CommandLine(CliApp()).execute(*args)

    KotlinScriptRunner.evalFile("import net.andreinc.serverneat.mockneat.extension.obj\nimport net.andreinc.serverneat.server.server\n\nserver {\n\n    httpOptions {\n        host = \"localhost\"\n        port = 8081\n    }\n\n    globalHeaders {\n        header(\"Content-Type\", \"application/text\")\n    }\n\n    routes {\n        get {\n            path = \"/user/100\"\n            response {\n                header(\"plain\", \"text\") // Adding a custom header to the response\n                statusCode = 200\n                json {\n                    value = obj {\n                        \"firstName\" const \"Mike\"\n                        \"lastName\" const \"Smith\"\n                        \"someFiles\" const arrayOf(\"file1.txt\", \"file2.txt\")\n                        \"anotherObject\" const obj {\n                            \"someData\"  const \"someValue\"\n                        }\n                    }\n                }\n            }\n        }\n    }\n\n\n}.start()", true, true);
}