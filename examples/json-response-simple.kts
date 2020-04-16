import net.andreinc.serverneat.mockneat.extension.obj
import net.andreinc.serverneat.server.server

server {

    httpOptions {
        host = "localhost"
        port = 8081
    }

    globalHeaders {
        header("Content-Type", "application/json")
    }

    routes {
        get {
            path = "/user/100"
            response {
                header("plain", "text") // Adding a custom header to the response
                statusCode = 200
                json {
                    value = obj {
                        "firstName" const "Mike"
                        "lastName" const "Smith"
                        "someFiles" const arrayOf("file1.txt", "file2.txt")
                        "anotherObject" const obj {
                            "someData"  const "someValue"
                        }
                    }
                }
            }
        }
    }


}.start()