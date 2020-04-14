import net.andreinc.mockneat.unit.financial.CreditCards.creditCards
import net.andreinc.serverneat.obj
import net.andreinc.serverneat.server
import net.andreinc.mockneat.unit.user.Names.names

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
            path = "/user/list"
            response {
                statusCode = 200
                json {
                    persistent = true
                    file = "userList.json"
                    value = obj {
                            "users" value obj {
                                "firstName" value names().first()
                                "lastName" value names().last()
                                "creditCards" value creditCards().list(10)
                            }.list(10)
                    }
                }
            }
        }

        get {
            path = "/user/1000"
            response {
                statusCode = 200
                json {
                    persistent = true
                    file = "userProfile.json"
                    value = obj {
                        "user" value obj {
                            "firstName" const "Andrew"
                            "lastName" const "Smith"
                            "creditCards" value creditCards().list(15)
                            "userId" const 1000
                        }
                    }
                }
            }
        }

        head {
            path = "/user/sanity"
            response {
                statusCode = 200
            }
        }
    }

}.start()

