# ServerNeat

## Introduction

*ServerNeat* is (not another) a Kotlin Web Server for mocking and stubbing Rest APIs.

It provides and easy to use DSL and seamless integration with [MockNeat](www.mockenat.com) for generating dynamic json responses.

*ServerNeat* can be used as standalone application, capable of loading, compiling and evaluating `kts` scripts, or as a Kotlin/Java library.   

## Building the standalone application

Gradle:
```groovy
gradle application
```

A standalone jar will be built inside the `build/libs` folder.

Running the server:

```
java -jar serverneat-all-1.0-SNAPSHOT.jar -f <path-to-kts-script>
``` 

## Examples

Check the `examples` folder.

### Plain text response - Hello world

```kotlin
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
                header("plain", "text")
                statusCode = 200
                plainText {
                    value = "Hello World!"
                }
            }
        }
    }


}.start()
```

### Json Response (Simple)

```kotlin
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
```

Calling the service `curl localhost:8081/user/100`:

```
{
  "firstName": "Mike",
  "lastName": "Smith",
  "anotherObject": {
    "map": {
      "someData": {}
    }
  },
  "someFiles": [
    "file1.txt",
    "file2.txt"
  ]
}
```

### Json Response (Dynamic - using [MockNeat](https://www.mockneat.com/))

The value object can be any [MockUnit](https://www.mockneat.com/tutorial/#everything-is-a-mockunitt).  

```kotlin
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
```

And the reponse will be

```json
{
    "users": [
        {
            "firstName": "Tommie",
            "lastName": "Pecinousky",
            "visits": [
                {
                    "city": "Copenhagen",
                    "time": {
                        "year": 2020,
                        "month": 1,
                        "day": 7
                    }
                },
                {
                    "city": "Monaco",
                    "time": {
                        "year": 2020,
                        "month": 11,
                        "day": 13
                    }
                },
                {
                    "city": "Tallinn",
                    "time": {
                        "year": 2020,
                        "month": 10,
                        "day": 22
                    }
                },
                {
                    "city": "Sarajevo",
                    "time": {
                        "year": 2020,
                        "month": 3,
                        "day": 7
                    }
                },
                {
                    "city": "Athens",
                    "time": {
                        "year": 2020,
                        "month": 12,
                        "day": 15
                    }
                }
            ],
            "financialInformation": {
                "creditCard2": "340529111074115",
                "creditCard1": "4647171048830798"
            },
            "gender": "Female"
        },
        {
            "firstName": "Sid",
            "lastName": "Falge",
            "visits": [
                {
                    "city": "Berlin",
                    "time": {
                        "year": 2020,
                        "month": 2,
                        "day": 22
                    }
                },
                {
                    "city": "Brussels",
                    "time": {
                        "year": 2020,
                        "month": 1,
                        "day": 27
                    }
                },
                {
                    "city": "Athens",
                    "time": {
                        "year": 2020,
                        "month": 12,
                        "day": 28
                    }
                },
                {
                    "city": "San Marino",
                    "time": {
                        "year": 2020,
                        "month": 10,
                        "day": 23
                    }
                },
                {
                    "city": "Stockholm",
                    "time": {
                        "year": 2020,
                        "month": 5,
                        "day": 12
                    }
                }
            ],
            "financialInformation": {
                "creditCard2": "373802757435803",
                "creditCard1": "4332758675303071"
            },
            "gender": "Male"
        }
...
```

*and son on for the rest of the users*

