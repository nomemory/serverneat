package net.andreinc.serverneat.responses

import io.vertx.core.buffer.Buffer.buffer
import io.vertx.core.http.HttpMethod
import io.vertx.ext.web.client.WebClient
import io.vertx.junit5.VertxTestContext
import io.vertx.junit5.web.TestRequest.*
import net.andreinc.serverneat.abstraction.RouteResponseAbstractTest
import net.andreinc.serverneat.server.server
import org.junit.jupiter.api.*

private val currentServer = server {

    httpOptions {
        host = "localhost"
        port = 17880
    }

    globalHeaders {
        header("Content-Type", "application/text")
    }

    routes {

        get {
            path = "/path/get"
            response {
                header("path/get", "get")
                statusCode = 200
                plainText {
                    value = "Hello, Get!"
                }
            }
        }

        head {
            path = "/path/head"
            response {
                header("path/head", "head")
                statusCode = 200
            }
        }

        post {
            path = "/path/post"
            response {
                header("path/post", "post")
                statusCode = 200
                plainText {
                    value = "Hello, Post!"
                }
            }
        }

        put {
            path = "/path/put"
            response {
                header("path/put", "put")
                statusCode = 200
                plainText {
                    value = "Hello, Put!"
                }
            }
        }

        delete {
            path = "/path/delete"
            response {
                header("path/delete", "delete")
                statusCode = 200
                plainText {
                    value = "Hello, Delete!"
                }
            }
        }

        connect {
            path = "/path/connect"
            response {
                header("path/connect", "connect")
                statusCode = 200
                plainText {
                    value = "Hello, Connect!"
                }
            }
        }

        options {
            path = "/path/options"
            response {
                header("path/options", "options")
                statusCode = 200
                plainText {
                    value = "Hello, Options!"
                }
            }
        }

        trace {
            path = "/path/trace"
            response {
                header("path/trace", "trace")
                statusCode = 200
                plainText {
                    value = "Hello, Trace!"
                }
            }
        }

        patch {
            path = "/path/patch"
            response {
                header("path/patch", "patch")
                statusCode = 200
                plainText {
                    value = "Hello, Patch!"
                }
            }
        }
    }
}

class RouteResponsePlainTextTest : RouteResponseAbstractTest(currentServer) {

    @Test
    fun `Test if a GET route with Plain Text response works correctly (status, body, header) ` (webClient: WebClient, testContext: VertxTestContext) {
        testRequest(webClient, HttpMethod.GET, "/path/get")
            .expect(
                statusCode(200),
                responseHeader("path/get", "get"),
                bodyResponse(buffer("Hello, Get!"), "application/text")
            )
            .send(testContext)
    }

    @Test
    fun `Test if a HEAD route with Plain Text response works correctly (status, header)` (webClient: WebClient, testContext: VertxTestContext) {
        testRequest(webClient, HttpMethod.HEAD, "/path/head")
            .expect(
                statusCode(200),
                responseHeader("path/head", "head")
            )
            .send(testContext)
    }

    @Test
    fun `Test if a POST route with Plain Text response works correctly (status, body, header)` (webClient: WebClient, testContext: VertxTestContext) {
        testRequest(webClient, HttpMethod.POST, "/path/post")
            .expect(
                statusCode(200),
                responseHeader("path/post", "post"),
                bodyResponse(buffer("Hello, Post!"), "application/text")
            )
            .send(testContext)
    }

    @Test
    fun `Test if a PUT route with Plain Text response works correctly (status, body, header)` (webClient: WebClient, testContext: VertxTestContext) {
        testRequest(webClient, HttpMethod.PUT, "/path/put")
            .expect(
                statusCode(200),
                responseHeader("path/put", "put"),
                bodyResponse(buffer("Hello, Put!"), "application/text")
            )
            .send(testContext)
    }

    @Test
    fun `Test if a DELETE route with Plain Text response works correctly (status, body, header)` (webClient: WebClient, testContext: VertxTestContext) {
        testRequest(webClient, HttpMethod.DELETE, "/path/delete")
            .expect(
                statusCode(200),
                responseHeader("path/delete", "delete"),
                bodyResponse(buffer("Hello, Delete!"), "application/text")
            )
            .send(testContext)
    }

    @Test
    fun `Test if a CONNECT route with Plain Text response works correctly (status, header)` (webClient: WebClient, testContext: VertxTestContext) {
        testRequest(webClient, HttpMethod.CONNECT, "/path/connect")
            .expect(
                statusCode(200),
                responseHeader("path/connect", "connect")
            )
            .send(testContext)
    }

    @Test
    fun `Test if a OPTIONS route with Plain Text response works correctly (status, header)` (webClient: WebClient, testContext: VertxTestContext) {
        testRequest(webClient, HttpMethod.OPTIONS, "/path/options")
            .expect(
                statusCode(200),
                responseHeader("path/options", "options")
            )
            .send(testContext)
    }

    @Test
    fun `Test if a TRACE route with Plain Text response works correctly (status, header)` (webClient: WebClient, testContext: VertxTestContext) {
        testRequest(webClient, HttpMethod.TRACE, "/path/trace")
            .expect(
                statusCode(200),
                responseHeader("path/trace", "trace")
            )
            .send(testContext)
    }

    @Test
    fun `Test if a PATCH route with Plain Text response works correctly (status, header)` (webClient: WebClient, testContext: VertxTestContext) {
        testRequest(webClient, HttpMethod.PATCH, "/path/patch")
            .expect(
                statusCode(200),
                responseHeader("path/patch", "patch"),
                bodyResponse(buffer("Hello, Patch!"), "application/text")
            )
            .send(testContext)
    }
}