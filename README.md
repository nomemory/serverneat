# serverneat

**IMPORTANT**: This project is still work in progress. Everything is subject to change.


In main:
```kotlin
 KotlinScriptRunner().evalFile("rest-crud-example.kts", readAsResource = true, compileFirst = true)
```

In the `resources` folder create a file `rest-crud-example.kts`:

```kotlin

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

```

Run main. 

Curl the APIs:

```
curl localhost:8081/user/list
```

Reponse:

```
{
  "users": [
    {
      "firstName": "Kristan",
      "lastName": "Pecinousky",
      "creditCards": [
        "375647171048833",
        "347920529111073",
        "371184549985144",
        "349473327586755",
        "370307238027578",
        "343580278024242",
        "348120393529927",
        "342973082782199",
        "378901579144455",
        "341116851066276"
      ]
    },
    {
      "firstName": "Jack",
      "lastName": "Carhart",
      "creditCards": [
        "372784190137876",
        "346877443230259",
        "343786405499880",
        "379487625914900",
        "375713954377888",
        "372430589980340",
        "343261663796534",
        "347858583414803",
        "340446503381463",
        "375318958497656"
      ]
    },
...
and so on
```
