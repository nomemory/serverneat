package net.andreinc.serverneat.server

import io.vertx.core.Vertx
import io.vertx.core.http.HttpServerOptions
import io.vertx.ext.web.Router
import mu.KotlinLogging
import net.andreinc.serverneat.logging.ansi

private val logger = KotlinLogging.logger {  }

/**
 * Main entry point for the Server DSL.
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
    val httpOptions : HttpServerOptions = HttpServerOptions()

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

        logger.info { ansi("Starting {logo Vert.x} server ") }

        vertx
            .createHttpServer(httpOptions)
            .requestHandler(router)
            .listen()

        logger.info { ansi("{logo Vert.x server} listening on port: {b ${httpOptions.port}}") }
    }

    fun stop() {
        logger.info { ansi("Stopping {logo Vert.x server}...")}

        this.vertx.close()

        logger.info { ansi("{logo Vert.x server} stopped.")}
    }
}