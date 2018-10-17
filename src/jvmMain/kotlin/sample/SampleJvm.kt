package sample

import io.ktor.application.*
import io.ktor.html.*
import io.ktor.http.content.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.html.*
import java.io.*

actual class Sample {
    actual fun checkMe() = 42
}

actual object Platform {
    actual val name: String = "JVM"
}

fun main(args: Array<String>) {
    embeddedServer(Netty, port = 8080, host = "127.0.0.1") {
        val currentDir = File(".").absoluteFile
        environment.log.info("Current directory: $currentDir")

        val webDir = listOf(
            "web",
            "../src/jsMain/web",
            "src/jsMain/web"
        ).map {
            File(currentDir, it)
        }.firstOrNull { it.isDirectory }?.absoluteFile ?: error("Can't find 'web' folder for this sample")

        environment.log.info("Web directory: $webDir")

        routing {
            get("/") {
                call.respondHtml {
                    body {
                        +"${hello()} from Ktor. Check me value: ${Sample().checkMe()}"
                        div {
                            id = "js-response"
                            +"Loading..."
                        }
                        script(src = "/static/require.min.js") {
                        }
                        script {
                            +"require.config({baseUrl: '/static'});\n"
                            // ProjectName.js should be here
                            +"require(['/static/mpp-web-skeleton.js'], function(js) { js.sample.helloWorld('Hi'); });\n"
                        }
                    }
                }
            }
            static("/static") {
                files(webDir)
            }
        }
    }.start(wait = true)
}