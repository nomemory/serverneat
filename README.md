# ServerNeat

## Intro

*ServerNeat* is a Kotlin DSL for creating flexible Mock/Stub Http Servers. 

## Building the server

Gradle:
```groovy
gradle application
```

A standalone jar will be built inside the `build/libs` folder.

Running the server:

```
java -jar serverneat-all-1.0-SNAPSHOT.jar -f "server.kts"
``` 

Where `server.kts` is the Kotlin script where the Mock/Stub server is defined.

## Examples

### Plain text response

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
                header("plain", "text") // Adding a custom header to the response
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

The response for `curl localhost:8081/user/100` will look like:

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