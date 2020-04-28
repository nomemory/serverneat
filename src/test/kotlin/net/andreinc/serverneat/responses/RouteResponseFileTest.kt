package net.andreinc.serverneat.responses

import io.vertx.core.buffer.Buffer
import io.vertx.core.http.HttpMethod
import io.vertx.ext.web.client.WebClient
import io.vertx.junit5.VertxTestContext
import io.vertx.junit5.web.TestRequest.*
import net.andreinc.serverneat.abstraction.RouteResponseAbstractTest
import net.andreinc.serverneat.server.server
import net.andreinc.serverneat.utils.temporaryFileWithContent
import org.junit.jupiter.api.Test

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
            path = "/file/response/get"
            response {
                header("/file/response/get", "get")
                statusCode = 200
                file {
                    path = temporaryFileWithContent("getFile", "Hello, Get!")
                }
            }
        }

        post {
            path = "/file/response/post"
            response {
                header("/file/response/post", "post")
                statusCode = 200
                file {
                    path = temporaryFileWithContent("postFile", "Hello, Post!")
                }
            }
        }

        put {
            path = "/file/response/put"
            response {
                header("/file/response/put", "put")
                statusCode = 200
                file {
                    path = temporaryFileWithContent("putFile", "Hello, Put!")
                }
            }
        }

        patch {
            path = "/file/response/patch"
            response {
                header("/file/response/patch", "patch")
                statusCode = 200
                file {
                    path = temporaryFileWithContent("patchFile", "Hello, Patch!")
                }
            }
        }

        delete {
            path = "/file/response/delete"
            response {
                header("/file/response/delete", "delete")
                statusCode = 200
                file {
                    path = temporaryFileWithContent("deleteFile", "Hello, Delete!")
                }
            }
        }
    }
}

class RouteResponseFileTest : RouteResponseAbstractTest(currentServer) {

    @Test
    fun `Test if a GET route with File response works correctly (status, body, header) ` (webClient: WebClient, testContext: VertxTestContext) {
        testRequest(webClient, HttpMethod.GET, "/file/response/get")
            .expect(
                statusCode(200),
                responseHeader("/file/response/get", "get"),
                bodyResponse(Buffer.buffer("Hello, Get!"), "application/text")
            )
            .send(testContext)
    }

    @Test
    fun `Test if a POST route with File response works correctly (status, body, header) ` (webClient: WebClient, testContext: VertxTestContext) {
        testRequest(webClient, HttpMethod.POST, "/file/response/post")
            .expect(
                statusCode(200),
                responseHeader("/file/response/post", "post"),
                bodyResponse(Buffer.buffer("Hello, Post!"), "application/text")
            )
            .send(testContext)
    }

    @Test
    fun `Test if a PUT route with File response works correctly (status, body, header) ` (webClient: WebClient, testContext: VertxTestContext) {
        testRequest(webClient, HttpMethod.PUT, "/file/response/put")
            .expect(
                statusCode(200),
                responseHeader("/file/response/put", "put"),
                bodyResponse(Buffer.buffer("Hello, Put!"), "application/text")
            )
            .send(testContext)
    }

    @Test
    fun `Test if a PATCH route with File response works correctly (status, body, header) ` (webClient: WebClient, testContext: VertxTestContext) {
        testRequest(webClient, HttpMethod.PATCH, "/file/response/patch")
            .expect(
                statusCode(200),
                responseHeader("/file/response/patch", "patch"),
                bodyResponse(Buffer.buffer("Hello, Patch!"), "application/text")
            )
            .send(testContext)
    }

    @Test
    fun `Test if a DELETE route with File response works correctly (status, body, header) ` (webClient: WebClient, testContext: VertxTestContext) {
        testRequest(webClient, HttpMethod.DELETE, "/file/response/delete")
            .expect(
                statusCode(200),
                responseHeader("/file/response/delete", "delete"),
                bodyResponse(Buffer.buffer("Hello, Delete!"), "application/text")
            )
            .send(testContext)
    }
}