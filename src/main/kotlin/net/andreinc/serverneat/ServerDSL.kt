package net.andreinc.serverneat

import MainContext.Companion.readResource
import com.google.gson.GsonBuilder
import io.vertx.core.Vertx
import io.vertx.core.http.HttpMethod
import io.vertx.core.http.HttpServerOptions
import io.vertx.ext.web.Router
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import mu.KotlinLogging
import net.andreinc.mockneat.abstraction.MockUnit
import net.andreinc.serverneat.mockneat.ObjectMap
import java.io.File
import java.nio.charset.Charset

private val logger = KotlinLogging.logger {  }

/**
 * Main entry point for the DSL
 */
fun server(initializer: Server.()  -> Unit) : Server {
    return Server()
        .apply(initializer)
}

@DslMarker
annotation class ServerNeatDslMarker

@ServerNeatDslMarker
class Server {

    init {
        logger.info { ansi("Preparing {logo Vert.x} server ") }
    }

    private val vertx: Vertx = Vertx.vertx()
    private val router: Router = Router.router(vertx)

    private val globalHeaders : GlobalHeaders = GlobalHeaders()
    private val routes: Routes = Routes(router, globalHeaders.globalHeaders)
    private val httpOptions : HttpServerOptions = HttpServerOptions()

    fun httpOptions(init: HttpServerOptions.() -> Unit) {
        this.httpOptions.apply(init)

    }

    fun globalHeaders(init: GlobalHeaders.()  -> Unit) {
        this.globalHeaders.apply(init)
    }

    fun routes(init: Routes.() -> Unit) {
        logger.info { ansi("Initialising {logo Vert.x} server router") }
        this.routes.apply(init)
    }

    fun start() {

        logger.info { ansi("Starting {logo Vert.x} server ")}

        vertx
            .createHttpServer(httpOptions)
            .requestHandler(router)
            .listen()

        logger.info { ansi("{logo Vert.x server} listening on port: {b ${httpOptions.port}}") }
    }
}

@ServerNeatDslMarker
class Routes(val router: Router, val globalHeaders: MutableMap<String, String>) {

    private val routes : MutableList<Route> = mutableListOf()

    fun get(init: Route.() -> Unit) {
        createRoute(init, HttpMethod.GET)
    }
    fun head(init: Route.()-> Unit) {
        createRoute(init, HttpMethod.HEAD)
    }
    fun post(init: Route.() -> Unit) {
        createRoute(init, HttpMethod.POST)
    }
    fun put(init: Route.() -> Unit) {
        createRoute(init, HttpMethod.PUT)
    }
    fun delete(init: Route.() -> Unit) {
        createRoute(init, HttpMethod.DELETE)
    }

    fun connect(init: Route.() -> Unit) {
        createRoute(init, HttpMethod.CONNECT)
    }
    fun options(init: Route.() -> Unit) {
        createRoute(init, HttpMethod.OPTIONS)
    }
    fun trace(init: Route.() -> Unit) {
        createRoute(init, HttpMethod.TRACE)
    }
    fun patch(init: Route.() -> Unit) {
        createRoute(init, HttpMethod.PATCH)
    }

    private fun createRoute(init: Route.() -> Unit, httpMethod: HttpMethod) {

        val r = Route(httpMethod);
        r.apply(init)

        if (r.method == HttpMethod.HEAD && r.response.contentObject !is RouteResponseEmptyContent) {
            logger.warn { "Found response for HEAD route. This will be ignored." }
        }

        logger.info { ansi("Adding new '{httpMethod ${r.method}}' route: '{path ${r.path}}'") }
        routes.add(r)

        router
            .route(r.path)
            .method(r.method)
            .handler{ ctx ->
                GlobalScope.launch {
                    val response = ctx.response()

                    // No need to set a delay if value is 0
                    if (r.response.delay > 0) {
                        delay(r.response.delay)
                    }

                    response.headers().addAll(globalHeaders)
                    response.headers().addAll(r.response.customHeaders)
                    response.statusCode = r.response.statusCode

                    // No need to return a body if it's HEAD
                    if (r.method == HttpMethod.HEAD) {
                        response.end()
                    }
                    else {
                        response.end(r.response.content())
                    }
                }
            }
    }
}

@ServerNeatDslMarker
class GlobalHeaders {
    val globalHeaders : MutableMap<String, String> = mutableMapOf()

    fun header(name: String, value: String) {
        globalHeaders[name] = value
    }

    fun header(name: String, value: MockUnit<*>) {
        globalHeaders[name] = value.mapToString().get()
    }
}

@ServerNeatDslMarker
class Route(val method: HttpMethod)  {

    lateinit var path : String
    var response : RouteResponse = RouteResponse()

    fun response(init: RouteResponse.() -> Unit) {
        this.response.apply(init)
    }
}

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
class RouteResponseFileContent : RouteResponseContent() {

    var charSet : Charset = Charsets.UTF_8
    lateinit var path : String
    private lateinit var internalContent : String

    override fun content(): String {
        if (!this::internalContent.isInitialized) {
            val fileOnDisk = File(path)
            if (!fileOnDisk.exists()) {
                throw IllegalStateException("File: ${fileOnDisk.absolutePath} doesn't exist. Please check the configuration")
            }
            internalContent = File(path).readText(charSet)
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
                        logger.info { ansi("File : {file $fileOnDisk} parent directory {file $parentDirectories} doesn't exist on the disk. Creating folder structure.")}
                        parentDirectories.mkdirs()
                    }

                    // Writing contents
                    val contentToWrite = GSON.toJson(value.get())
                    fileOnDisk.writeText(contentToWrite)
                    logger.info { ansi("File : {file ${fileOnDisk.canonicalPath}} successfully created.")}
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

fun obj(init: ObjectMap.() -> Unit) : ObjectMap {
    val result = ObjectMap()
    result.apply(init)
    return result
}

@ServerNeatDslMarker
class RouteResponseEmptyContent : RouteResponseContent() {
    override fun content(): String {
      return ""
    }
}
