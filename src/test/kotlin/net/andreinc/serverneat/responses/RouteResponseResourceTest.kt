package net.andreinc.serverneat.responses

import io.vertx.core.buffer.Buffer
import io.vertx.core.http.HttpMethod
import io.vertx.ext.web.client.WebClient
import io.vertx.junit5.VertxTestContext
import io.vertx.junit5.web.TestRequest.*
import net.andreinc.serverneat.abstraction.RouteResponseAbstractTest
import net.andreinc.serverneat.server.server
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
            path = "/resource/response/get"
            response {
                header("/resource/response/get", "get")
                statusCode = 200
                resource {
                    path = "api-data/get-response.txt"
                }
            }
        }

        post {
            path = "/resource/response/post"
            response {
                header("/resource/response/post", "post")
                statusCode = 200
                resource {
                    path = "api-data/post-response.txt"
                }
            }
        }

        put {
            path = "/resource/response/put"
            response {
                header("/resource/response/put", "put")
                statusCode = 200
                resource {
                    path = "api-data/put-response.txt"
                }
            }
        }

        patch {
            path = "/resource/response/patch"
            response {
                header("/resource/response/patch", "patch")
                statusCode = 200
                resource {
                    path = "api-data/patch-response.txt"
                }
            }
        }

        delete {
            path = "/resource/response/delete"
            response {
                header("/resource/response/delete", "delete")
                statusCode = 200
                resource {
                    path = "api-data/delete-response.txt"
                }
            }
        }
    }
}

class RouteResponseResourceTest : RouteResponseAbstractTest(currentServer) {

    @Test
    fun `Test if a GET route with Resource response works correctly (status, body, header) ` (webClient: WebClient, testContext: VertxTestContext) {
        testRequest(webClient, HttpMethod.GET, "/resource/response/get")
            .expect(
                statusCode(200),
                responseHeader("/resource/response/get", "get"),
                bodyResponse(Buffer.buffer("Hello, Get!"), "application/text")
            )
            .send(testContext)
    }

    @Test
    fun `Test if a POST route with Resource response works correctly (status, body, header) ` (webClient: WebClient, testContext: VertxTestContext) {
        testRequest(webClient, HttpMethod.POST, "/resource/response/post")
            .expect(
                statusCode(200),
                responseHeader("/resource/response/post", "post"),
                bodyResponse(Buffer.buffer("Hello, Post!"), "application/text")
            )
            .send(testContext)
    }

    @Test
    fun `Test if a PUT route with Resource response works correctly (status, body, header) ` (webClient: WebClient, testContext: VertxTestContext) {
        testRequest(webClient, HttpMethod.PUT, "/resource/response/put")
            .expect(
                statusCode(200),
                responseHeader("/resource/response/put", "put"),
                bodyResponse(Buffer.buffer("Hello, Put!"), "application/text")
            )
            .send(testContext)
    }

    @Test
    fun `Test if a PATCH route with Resource response works correctly (status, body, header) ` (webClient: WebClient, testContext: VertxTestContext) {
        testRequest(webClient, HttpMethod.PATCH, "/resource/response/patch")
            .expect(
                statusCode(200),
                responseHeader("/resource/response/patch", "patch"),
                bodyResponse(Buffer.buffer("Hello, Patch!"), "application/text")
            )
            .send(testContext)
    }

    @Test
    fun `Test if a DELETE route with Resource response works correctly (status, body, header) ` (webClient: WebClient, testContext: VertxTestContext) {
        testRequest(webClient, HttpMethod.DELETE, "/resource/response/delete")
            .expect(
                statusCode(200),
                responseHeader("/resource/response/delete", "delete"),
                bodyResponse(Buffer.buffer("Hello, Delete!"), "application/text")
            )
            .send(testContext)
    }
}