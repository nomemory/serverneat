package net.andreinc.serverneat.responses

import io.vertx.core.buffer.Buffer
import io.vertx.core.http.HttpMethod
import io.vertx.ext.web.client.HttpResponse
import io.vertx.ext.web.client.WebClient
import io.vertx.junit5.VertxTestContext
import io.vertx.junit5.web.TestRequest.*
import io.vertx.kotlin.core.json.get
import net.andreinc.serverneat.abstraction.RouteResponseAbstractTest
import net.andreinc.serverneat.mockneat.extension.obj
import net.andreinc.serverneat.server.server
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.File
import java.util.function.Consumer

private val getPersistentJsonPath = "tests/get-persistent.json"

private val currentServer = server {

    httpOptions {
        host = "localhost"
        port = 17880
    }

    globalHeaders {
        header("Content-Type", "application/json")
    }

    routes {

        get {
            path = "/simple/json/get"
            response {
                header("/simple/json/get", "get")
                json {
                    value = obj {
                        "firstName" const "Mike"
                        "lastName" const "Smith"
                        "pc" value obj {
                            "type" const "laptop"
                            "operatingSystem" const "linux"
                        }
                        "integers" const arrayOf(1,2,3)
                    }
                }
            }
        }

        get {
            path = "/simple/json/get/persistent"
            response {
                header("/simple/json/get/persistent", "get")
                json {
                    persistent = true
                    file = getPersistentJsonPath
                    value = obj {
                        "firstName" const "Mike"
                        "lastName" const "Smith"
                        "pc" value obj {
                            "type" const "laptop"
                            "operatingSystem" const "linux"
                        }
                        "integers" const arrayOf(1,2,3)
                    }
                }
            }
        }
    }

}

class RouteResponseSimpleJsonTest : RouteResponseAbstractTest(currentServer) {

    private fun correctJsonResponse () : Consumer<HttpResponse<Buffer>> {
        return Consumer { response ->
            val json = response.bodyAsJsonObject()

            assertNotNull(json.getString("firstName"))
            assertEquals(json.getString("firstName"), "Mike")

            assertNotNull(json.getString("lastName"))
            assertEquals(json.getString("lastName"), "Smith")

            assertNotNull(json.getJsonArray("integers"))
            assertEquals(json.getJsonArray("integers").size(), 3)
            assertEquals(json.getJsonArray("integers")[0], 1)
            assertEquals(json.getJsonArray("integers")[1], 2)
            assertEquals(json.getJsonArray("integers")[2], 3)

            assertNotNull(json.getJsonObject("pc"))
            assertEquals(json.getJsonObject("pc").getString("type"), "laptop")
            assertEquals(json.getJsonObject("pc").getString("operatingSystem"), "linux")
        }
    }

    @Test
    fun `Test if a GET route with Simple JSON response works correctly (status, body, header)` (webClient: WebClient, testContext: VertxTestContext) {
        testRequest(webClient, HttpMethod.GET, "/simple/json/get")
            .expect(
                statusCode(200),
                responseHeader("/simple/json/get", "get"),
                responseHeader("Content-Type", "application/json"),
                correctJsonResponse()
            )
            .send(testContext)
    }

    @Test
    fun `Test if a GET route with (Persistent) Simple JSON response works correctly (status, body, header)` (webClient: WebClient, testContext: VertxTestContext) {
        testRequest(webClient, HttpMethod.GET, "/simple/json/get/persistent")
            .expect(
                    statusCode(200),
                    responseHeader("/simple/json/get/persistent", "get"),
                    responseHeader("Content-Type", "application/json"),
                    correctJsonResponse(),
                    Consumer { assertTrue(File("data/dynamic/$getPersistentJsonPath").exists()) }
            )
            .send(testContext)
    }

    @AfterAll
    fun cleanUp() {
        File("data/dynamic/$getPersistentJsonPath").delete()
        File("data/dynamic/tests").delete()
    }
}