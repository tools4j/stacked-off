package org.tools4j.stacked.gui

import com.airhacks.afterburner.injection.Injector
import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.scene.paint.Color
import javafx.stage.Stage
import javafx.stage.StageStyle
import mu.KotlinLogging

class Main : Application() {
    val logger = KotlinLogging.logger {}

    @Throws(Exception::class)
    override fun start(primaryStage: Stage) {
        val wikiFilePaths = TiddleProperties.getInstance().getWikiFilePaths()
        val wikiSearchService = WikiSearchService.build(wikiFilePaths)
        Injector.setModelOrService(WikiSearchService::class.java, wikiSearchService)
        Injector.setModelOrService(Stage::class.java, primaryStage)

        primaryStage.initStyle(StageStyle.UNDECORATED)
        primaryStage.initStyle(StageStyle.TRANSPARENT)

        val mainView = TiddleView()
        val scene = Scene(mainView.view)
        scene.stylesheets.add("tiddle/javafx/tiddle.css")
        scene.fill = Color.TRANSPARENT

        primaryStage.icons.add(Image(javaClass.getResourceAsStream("/tiddle.png")))
        primaryStage.title = "tiddle"
        primaryStage.scene = scene
        primaryStage.show()

        primaryStage.iconifiedProperty().addListener { observable, oldValue, newValue ->
            if (!newValue) {
                primaryStage.requestFocus()
            }
        }

        ResizeHelper.addResizeListener(primaryStage)
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Application.launch(*args)
        }
    }
}
