import net.andreinc.mockneat.unit.address.Cities.cities
import net.andreinc.mockneat.unit.financial.CreditCards.creditCards
import net.andreinc.mockneat.unit.time.LocalDates.localDates
import net.andreinc.mockneat.unit.user.Genders.genders
import net.andreinc.mockneat.unit.user.Names.names
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
            path = "/users"
            response {
                statusCode = 200
                json {
                    persistent = true // generated data will be stored in the file "usersList.json"
                    file = "dyanmic-example/usersList.json"
                    value = obj {
                        "users" value obj {
                            "firstName" value names().first()
                            "lastName" value names().last()
                            "gender" value genders()
                            "financialInformation" value obj {
                                "creditCard1" value creditCards().visa()
                                "creditCard2" value creditCards().amex()
                            }
                            "visits" value obj {
                                "time" value localDates().thisYear()
                                "city" value cities().capitalsEurope()
                            }.list(5)
                        }.list(50)
                    }
                }
            }
        }
    }
}.start()