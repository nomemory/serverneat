package net.andreinc.serverneat.server

import MainContext.Companion.readResource
import com.google.gson.GsonBuilder
import mu.KotlinLogging
import net.andreinc.mockneat.abstraction.MockUnit
import net.andreinc.serverneat.logging.ansi
import net.andreinc.serverneat.mockneat.extension.ObjectMap
import java.io.File
import java.nio.charset.Charset

private val logger = KotlinLogging.logger {  }

@ServerNeatDslMarker
class RouteResponse {

    val customHeaders : MutableMap<String, String> = mutableMapOf()

    var delay: Long = 0
    var statusCode: Int = 200

    var contentObject : RouteResponseContent = RouteResponseEmptyContent()
        private set

    fun header(name: String, value: String) {
        customHeaders[name] = value
    }

    fun header(name: String, value: MockUnit<*>) {
        customHeaders[name] = value.mapToString().get()
    }

    fun plainText(init: RouteResponsePlainText.() -> Unit) {
        this.contentObject = RouteResponsePlainText().apply(init);
    }

    fun file(init: RouteResponseFileContent.() -> Unit) {
        this.contentObject = RouteResponseFileContent().apply(init)
    }

    fun fileDownload(init: RouteResponseFileDownload.() -> Unit) {
        this.contentObject = RouteResponseFileDownload().apply(init)
    }

    fun resource(init: RouteResponseResourceContent.() -> Unit) {
        this.contentObject = RouteResponseResourceContent().apply(init)
    }

    fun json(init: RouteResponseJsonContent.() -> Unit) {
        this.contentObject = RouteResponseJsonContent().apply(init)
    }

    fun empty (init : RouteResponseEmptyContent.() -> Unit) {}

    fun empty() {}

    fun content() : String {
        return contentObject.content()
    }
}

@ServerNeatDslMarker
abstract class RouteResponseContent {
    abstract fun content() : String
}

@ServerNeatDslMarker
class RouteResponsePlainText : RouteResponseContent() {

    lateinit var value : String

    override fun content(): String {
        return value
    }
}

@ServerNeatDslMarker
class RouteResponseFileDownload : RouteResponseContent() {

    override fun content(): String {
        return file;
    }

    lateinit var file : String
}

@ServerNeatDslMarker
class RouteResponseFileContent : RouteResponseContent() {

    var charSet : Charset = Charsets.UTF_8
    lateinit var file : String
    private lateinit var internalContent : String

    override fun content(): String {
        if (!this::internalContent.isInitialized) {
            val fileOnDisk = File(file)
            if (!fileOnDisk.exists()) {
                throw IllegalStateException("File: ${fileOnDisk.absolutePath} doesn't exist. Please check the configuration and try again.")
            }
            internalContent = File(file).readText(charSet)
            logger.info { ansi("File : {file ${fileOnDisk.canonicalPath}} content was loaded from the disk.") }
        }
        return internalContent
    }
}

@ServerNeatDslMarker
class RouteResponseResourceContent : RouteResponseContent() {

    lateinit var path : String
    var charSet : Charset = Charsets.UTF_8
    private lateinit var internalContent : String

    override fun content(): String {
        if (!this::internalContent.isInitialized) {
            internalContent = readResource(path, charSet)
        }
        return internalContent
    }

}

@ServerNeatDslMarker
class RouteResponseJsonContent : RouteResponseContent() {

    companion object {
        private val GSON = GsonBuilder().setPrettyPrinting().create()
    }

    var persistent : Boolean = false

    lateinit var value: ObjectMap
    lateinit var file: String

    private lateinit var internalValue : String

    override fun content(): String {
        if (!this::internalValue.isInitialized) {
            if (persistent) {
                val fileOnDisk = File("data/dynamic/$file")
                if (!fileOnDisk.exists()) {
                    logger.info { ansi("File : {file $fileOnDisk} doesn't exist on the disk. Creating a new one.") }

                    // If the file has a parent directory(ies) that doesn't exist on the disk
                    // Create them first, before creating the file
                    val parentDirectories = fileOnDisk.parentFile
                    if (parentDirectories != null && !parentDirectories.exists()) {
                        logger.info { ansi("File : {file $fileOnDisk} parent directory {file $parentDirectories} doesn't exist on the disk. Creating folder structure.") }
                        parentDirectories.mkdirs()
                    }

                    // Writing contents
                    val contentToWrite = GSON.toJson(value.get())
                    fileOnDisk.writeText(contentToWrite)
                    logger.info { ansi("File : {file ${fileOnDisk.canonicalPath}} successfully created.") }
                }
                internalValue = fileOnDisk.readText()
                logger.info { ansi("File : {file ${fileOnDisk.canonicalPath}} content was loaded from the disk.") }
            } else {
                internalValue = GSON.toJson(value.get())
            }
        }
        return internalValue
    }
}

@ServerNeatDslMarker
class RouteResponseEmptyContent : RouteResponseContent() {
    override fun content(): String {
        return ""
    }
}
