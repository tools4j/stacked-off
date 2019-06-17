package org.tools4j.stacked.web

import io.ktor.application.ApplicationCallPipeline
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.*
import io.ktor.gson.gson
import io.ktor.http.HttpStatusCode
import io.ktor.http.Parameters
import io.ktor.http.content.resource
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.request.path
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondRedirect
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.tools4j.stacked.index.*
import java.io.File
import java.text.DateFormat
import java.util.concurrent.atomic.AtomicReference


class Server {
    companion object {
        var instance = Instance()

        @JvmStatic
        fun main(args: Array<String>) {
            val loadInProgress = AtomicReference<JobStatus>(NullJobStatus())

            embeddedServer(Netty) {
                install(DefaultHeaders){
                    header("cacheDirSet", (instance.diContext.getIndexParentDir() != null).toString())
                }
                install(Compression)
                install(CallLogging)
                install(CORS) {
                    anyHost()
                }
                install(ContentNegotiation) {
                    gson {
                        setDateFormat(DateFormat.LONG)
                        setPrettyPrinting()
                    }
                }

                routing {
                    intercept(ApplicationCallPipeline.Setup) {
                        if (instance.diContext.getIndexParentDir() == null
                            && !call.request.path().startsWith("/admin")
                            && !call.request.path().startsWith("/rest/admin")
                            && !call.request.path().contains("static")
                            && !call.request.path().contains("favicon.ico")) {

                            call.respondRedirect("/admin", false)
                            return@intercept finish()
                        }
                    }

                    post("/rest/admin") {
                        val parameters = call.receive<Parameters>()
                        val parentIndexDir = parameters["parentIndexDir"]
                        if(parentIndexDir == null || parentIndexDir.isEmpty()) {
                            call.respond(HttpStatusCode.InternalServerError, "Please enter an index directory path")
                        } else if(!File(parentIndexDir).exists()){
                            call.respond(HttpStatusCode.InternalServerError, "Dir does not exist: $parentIndexDir")
                        } else if(!File(parentIndexDir).isDirectory()){
                            call.respond(HttpStatusCode.InternalServerError, "Path is not a directory: $parentIndexDir")
                        } else {
                            instance.diContext.setIndexParentDir(parentIndexDir)
                            instance = Instance()
                            val response = object {
                                val message = "Index directory has been updated."
                                val indexParentDir = parentIndexDir
                            }
                            call.respond(response)
                        }
                    }

                    get("/rest/admin") {
                        call.respond(instance.diContext.getIndexParentDir() ?: "")
                    }

                    get("/rest/sites") {
                        val sites = instance.indexes.indexedSiteIndex.getAll()
                        call.respond(sites)
                    }

                    get("/rest/questions/{id}") {
                        val post = instance.questionIndex.getQuestionByUid(call.parameters["id"]!!)
                        if (post == null)
                            call.respond(HttpStatusCode.NotFound)
                        else
                            call.respond(post)
                    }

                    get("/rest/search") {
                        val fromDocIndexInclusive = call.parameters["fromDocIndexInclusive"]?.toInt() ?: 0
                        val toDocIndexExclusive = call.parameters["toDocIndexExclusive"]?.toInt() ?: 10
                        val explain = call.parameters.contains("explain")

                        val searchResults = instance.questionIndex.searchForQuestionSummaries(
                            call.parameters["searchText"]!!,
                            fromDocIndexInclusive,
                            toDocIndexExclusive,
                            explain
                        )
                        call.respond(searchResults)
                    }

                    get("/rest/sedir") {
                        val seDirPath = call.parameters["path"]!!
                        try {
                            val seDirSites = SeDir(seDirPath).getContents().getSites()
                            call.respond(seDirSites)
                        } catch (e: Exception) {
                            call.respond(
                                HttpStatusCode.InternalServerError,
                                e.message ?: "Error parsing dir: [${seDirPath}]"
                            )
                        }
                    }

                    get("/rest/loadSites") {
                        val newLoadStatus = JobStatusImpl()
                        val currentLoadStatus = loadInProgress.updateAndGet({ previousJobStatus ->
                            if (previousJobStatus != null && previousJobStatus.running) previousJobStatus
                            else newLoadStatus
                        })
                        if (currentLoadStatus !== newLoadStatus) {
                            call.respond(HttpStatusCode.InternalServerError, "Job already running")
                        } else {
                            val seDirPath = call.parameters["path"]!!
                            val seDirSiteIds = call.parameters["seDirSiteIds"]!!.split(",")
                            Thread({
                                instance.seDirParser.parse(
                                    seDirPath,
                                    { seSite -> seDirSiteIds.contains(seSite.seSiteId) },
                                    newLoadStatus
                                )
                            }).start()
                            call.respond(HttpStatusCode.OK, newLoadStatus)
                        }
                    }

                    get("/rest/status") {
                        call.respond(loadInProgress.get())
                    }

                    get("/rest/indexes") {
                        call.respond(IndexStats(instance.indexes))
                    }

                    get("/rest/purgeSite/{id}") {
                        instance.indexes.questionIndex.purgeSite(call.parameters["id"]!!)
                        instance.indexes.indexedSiteIndex.purgeSite(call.parameters["id"]!!)
                        val sites = instance.indexes.indexedSiteIndex.getAll()
                        call.respond(sites)
                    }
                    resource("/", "webapp/index.html")
                    resource("/*", "webapp/index.html")
                    resource("/*/*", "webapp/index.html")
                    static("static") {
                        resources("webapp")
                    }
                    static("*/static") {
                        resources("webapp")
                    }
                }
            }.start(wait = true)
        }

        fun setupResourcesAndSettings(route: Route){
            route.resource("/", "webapp/index.html")
            route.resource("/*", "webapp/index.html")
            route.resource("/*/*", "webapp/index.html")
            route.static("static") {
                route.resources("webapp")
            }
            route.static("*/static") {
                route.resources("webapp")
            }
        }

    }
}