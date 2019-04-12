package org.tools4j.stacked.web

import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.*
import io.ktor.gson.gson
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.resource
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.routing
import org.tools4j.stacked.index.*
import java.text.DateFormat
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import java.lang.Exception
import java.util.concurrent.atomic.AtomicReference


class JsonServer {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val instance = Instance()
            val loadInProgress = AtomicReference<JobStatus>(NullJobStatus())

            embeddedServer(Netty) {
                install(DefaultHeaders)
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
                    get("/rest/sites") {
                        val sites = instance.indexes.indexedSiteIndex.getAll()
                        call.respond(sites)
                    }

                    get("/rest/posts/{id}") {
                        val post = instance.postService.getPost(call.parameters["id"]!!)
                        if (post == null)
                            call.respond(HttpStatusCode.NotFound)
                        else
                            call.respond(post)
                    }

                    get("/rest/questions/{id}") {
                        val question = instance.postService.getQuestion(call.parameters["id"]!!)
                        if (question == null)
                            call.respond(HttpStatusCode.NotFound)
                        else
                            call.respond(question)
                    }

                    get("/rest/search") {
                        val questions = instance.postService.search(call.parameters["searchText"]!!)
                        call.respond(questions)
                    }

                    get("/rest/sedir") {
                        val seDirPath = call.parameters["path"]!!
                        try {
                            val seDirSites = SeDir(seDirPath).getContents().getSites()
                            call.respond(seDirSites)
                        } catch (e: Exception){
                            call.respond(HttpStatusCode.InternalServerError, e.message ?: "Error parsing dir: [${seDirPath}]")
                        }
                    }

                    get("/rest/loadSites") {
                        val newLoadStatus = JobStatusImpl()
                        val currentLoadStatus = loadInProgress.updateAndGet({ previousJobStatus ->
                            if(previousJobStatus != null && previousJobStatus.running) previousJobStatus
                            else newLoadStatus
                        })
                        if(currentLoadStatus !== newLoadStatus){
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

                    get("/rest/purgeSite/{id}") {
                        instance.indexes.purgeSite(call.parameters["id"]!!)
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
    }
}