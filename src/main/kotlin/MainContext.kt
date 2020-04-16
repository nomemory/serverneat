import java.nio.charset.Charset

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