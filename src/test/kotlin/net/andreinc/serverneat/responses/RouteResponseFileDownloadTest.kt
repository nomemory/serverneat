package net.andreinc.serverneat.responses

import io.vertx.core.buffer.Buffer
import io.vertx.core.http.HttpMethod
import io.vertx.ext.web.client.HttpResponse
import io.vertx.ext.web.client.WebClient
import io.vertx.junit5.VertxTestContext
import io.vertx.junit5.web.TestRequest.*
import net.andreinc.serverneat.abstraction.RouteResponseAbstractTest
import net.andreinc.serverneat.server.Server
import net.andreinc.serverneat.server.server
import net.andreinc.serverneat.utils.temporaryFileWithContent
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.function.Consumer

private val fileToDownload : String =
    temporaryFileWithContent("download-get", "Hello, Get!")

private val currentServer : Server = server {

    httpOptions {
        host = "localhost"
        port = 17880
    }

    routes {
        get {
            path = "/file/download/get"
            response {
                header("/file/download/get", "get")
                fileDownload {
                    path = fileToDownload
                }
            }
        }
    }

}

class RouteResponseFileDownloadTest : RouteResponseAbstractTest(currentServer) {
    @Test
    fun `Test if a GET route with File Download works correctly (status, body, header) ` (webClient: WebClient, testContext: VertxTestContext) {
        testRequest(webClient, HttpMethod.GET, "/file/download/get")
            .expect(
                statusCode(200),
                responseHeader("content-type", "text/plain"),
                responseHeader("Content-Disposition", "attachment; filename=\"$fileToDownload\""),
                responseHeader("transfer-encoding", "chunked"),
                Consumer { httpResponseBuffer : HttpResponse<Buffer> ->
                    val fileContent = httpResponseBuffer.bodyAsString()
                    Assertions.assertEquals(fileContent, "Hello, Get!")
                }
            )
            .send(testContext)
    }
}