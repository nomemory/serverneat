import net.andreinc.mockneat.unit.financial.CreditCards.creditCards
import net.andreinc.mockneat.unit.user.Names.names
import net.andreinc.serverneat.obj
import net.andreinc.serverneat.server

server {

    httpOptions {
        port = 8084
        host = "localhost"
    }

    globalHeaders {
        header("Content-Type", "application/json")
    }

    routes {

        get {
            path = "/test"
            response {
                header(name = "Header1", value = "Value2")
                statusCode = 200
                delay = 200
                plainText {
                    value = "Helo world 1"
                }
            }
        }

        get {
            path = "/test2"
            response {
                statusCode = 200
                delay = 100
                plainText {
                    value = "Hello World 2"
                }
            }
        }

        get {
            path = "/jsontest"
            response {
                header("test", "test")
                statusCode = 200
                json {
                    value = obj {
                        "persons" value obj {
                            "lastName" value names().last()
                            "firstName" value names().first()
                            "credit" value obj {
                                "card" value creditCards().list(10)
                            }
                            "constant" const 10
                        }.list(10)
                    }
                }
            }
        }

    }
}.start()