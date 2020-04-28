package net.andreinc.serverneat.responses

import io.vertx.core.http.HttpMethod
import io.vertx.ext.web.client.WebClient
import io.vertx.junit5.VertxTestContext
import io.vertx.junit5.web.TestRequest
import io.vertx.junit5.web.TestRequest.testRequest
import net.andreinc.mockneat.unit.types.Ints.ints
import net.andreinc.serverneat.abstraction.RouteResponseAbstractTest
import net.andreinc.serverneat.server.Server
import net.andreinc.serverneat.server.server
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.lang.Integer.parseInt
import java.util.function.Consumer

private val server : Server = server {

    httpOptions {
        host = "localhost"
        port = 17880
    }

    globalHeaders {
        header("dynamicHeader", ints().range(1,10))
        header("staticHeader", "static")
    }

    routes {
        get {
            path = "/dynamic/headers"
            response {
                header("dynamicLocalHeader", ints().range(10, 20))
                header("staticLocalHeader", "static local")
                statusCode = 200
                empty {}
            }
        }
    }
}

class DynamicHeadersTest : RouteResponseAbstractTest(server) {

    @Test
    fun `Test to see if dynanamically generated global and response headers are correctly generated` (webClient: WebClient, testContext: VertxTestContext) {
        testRequest(webClient, HttpMethod.GET, "/dynamic/headers")
            .expect(
                Consumer { response ->
                    val dynamicHeader = response.getHeader("dynamicHeader")
                    val staticHeader = response.getHeader("staticHeader")
                    val dynamicLocalHeader = response.getHeader("dynamicLocalHeader")
                    val staticLocalHeader = response.getHeader("staticLocalHeader")

                    assertNotNull(dynamicHeader)
                    assertNotNull(staticHeader)
                    assertNotNull(dynamicLocalHeader)
                    assertNotNull(staticLocalHeader)

                    assertEquals(staticHeader, "static")
                    assertEquals(staticLocalHeader, "static local")

                    assertTrue(parseInt(dynamicHeader) < 10)
                    assertTrue(parseInt(dynamicHeader) >= 0)
                    assertTrue(parseInt(dynamicLocalHeader) >= 10)
                    assertTrue(parseInt(dynamicLocalHeader) < 20)
                }
            )
            .send(testContext)
    }
}