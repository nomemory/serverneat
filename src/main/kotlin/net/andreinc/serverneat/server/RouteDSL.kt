package net.andreinc.serverneat.server

import io.vertx.core.http.HttpHeaders
import io.vertx.core.http.HttpMethod
import io.vertx.ext.web.Router
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import mu.KotlinLogging
import net.andreinc.mockneat.abstraction.MockUnit
import net.andreinc.serverneat.logging.ansi

private val logger = KotlinLogging.logger {  }

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

        logger.info { ansi("Adding new ({httpMethod ${r.method}}) route path='{path ${r.path}}'") }
        routes.add(r)

        if (r.method == HttpMethod.HEAD && r.response.contentObject !is RouteResponseEmptyContent) {
            logger.warn { ansi("{red Found response for {httpMethod HEAD} route. Response will be ignored.}") }
        }

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

                    logger.info { ansi("({httpMethod ${r.method}}) request for path='{path ${r.path}}'") }

                    // No need to return a body if it's HEAD
                    if (r.method == HttpMethod.HEAD) {
                        response.end()
                    }
                    // If the route is responsible for returning a file to download
                    else if (r.response.contentObject is RouteResponseFileDownload) {
                        response
                            .putHeader(HttpHeaders.CONTENT_TYPE, "text/plain")
                            .putHeader("Content-Disposition", "attachment; filename=\"${r.response.content()}\"")
                            .putHeader(HttpHeaders.TRANSFER_ENCODING, "chunked")
                            .sendFile(r.response.content())
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