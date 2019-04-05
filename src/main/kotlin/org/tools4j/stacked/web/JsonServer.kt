package org.tools4j.stacked.web

import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.*
import io.ktor.gson.gson
import io.ktor.http.HttpStatusCode
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
            val loadInProgress = AtomicReference<JobStatus>()

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
                    get("/v1/site") {
                        val sites = instance.indexes.indexedSiteIndex.getAll()
                        call.respond(sites)
                    }

                    get("/v1/post/{id}") {
                        val post = instance.postService.getPost(call.parameters["id"]!!)
                        if (post == null)
                            call.respond(HttpStatusCode.NotFound)
                        else
                            call.respond(post)
                    }

                    get("/v1/question/{id}") {
                        val question = instance.postService.getQuestion(call.parameters["id"]!!)
                        if (question == null)
                            call.respond(HttpStatusCode.NotFound)
                        else
                            call.respond(question)
                    }

                    get("/v1/question/{id}") {
                        val question = instance.postService.getQuestion(call.parameters["id"]!!)
                        if (question == null)
                            call.respond(HttpStatusCode.NotFound)
                        else
                            call.respond(question)
                    }

                    get("/v1/search") {
                        val questions = instance.postService.search(call.parameters["searchText"]!!)
                        call.respond(questions)
                    }

                    get("/v1/sedir") {
                        val seDirPath = call.parameters["path"]!!
                        try {
                            val seDirSites = SeDir(seDirPath).getContents().getSites()
                            call.respond(seDirSites)
                        } catch (e: Exception){
                            call.respond(HttpStatusCode.InternalServerError, e.message ?: "Error parsing dir: [${seDirPath}]")
                        }
                    }

                    get("/v1/loadSites") {
                        val newLoadStatus = JobStatusImpl()
                        val currentLoadStatus = loadInProgress.getAndUpdate({ previousJobStatus ->
                            if(previousJobStatus != null && previousJobStatus.running) previousJobStatus
                            else newLoadStatus
                        })
                        if(currentLoadStatus !== newLoadStatus){
                            call.respond(HttpStatusCode.InternalServerError, "Job already running")
                        }
                        val seDirPath = call.parameters["path"]!!
                        val seDirSiteIds = call.parameters["seDirSiteIds"]!!.split(",")
                        Thread({instance.seDirParser.parse(
                            seDirPath,
                            {seSite -> seDirSiteIds.contains(seSite.seSiteId)},
                            newLoadStatus)}).start()

                        call.respond(HttpStatusCode.OK, newLoadStatus)
                    }

                    get("/v1/status") {
                        call.respond(loadInProgress.get())
                    }
                }
            }.start(wait = true)
        }
    }
}