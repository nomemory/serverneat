package net.andreinc.serverneat.abstraction

import io.vertx.ext.web.client.WebClientOptions
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.web.VertxWebClientExtension
import io.vertx.junit5.web.WebClientOptionsInject
import net.andreinc.serverneat.server.Server
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith

@TestInstance(
    TestInstance.Lifecycle.PER_CLASS
)
@ExtendWith(
    VertxExtension::class,
    VertxWebClientExtension::class
)
abstract class RouteResponseAbstractTest(val server: Server) {

    @WebClientOptionsInject
    @JvmField
    var webClientOptions: WebClientOptions =
        WebClientOptions().apply {
            defaultHost = server.httpOptions.host
            defaultPort = server.httpOptions.port
        }

    @BeforeAll
    fun startServer() {
        this.server.start()
    }

    @AfterAll
    fun stopServer() {
        this.server.stop()
    }
}