import net.andreinc.serverneat.server.server

server {

    httpOptions {
        host = "localhost"
        port = 8081
    }

    globalHeaders {
        header("Content-Type", "application/text")
    }

    routes {
        get {
            path = "/plainText"
            response {
                header("plain", "text") // Adding a custom header to the response
                statusCode = 200
                plainText {
                    value = "Hello World!"
                }
            }
        }
    }


}.start()