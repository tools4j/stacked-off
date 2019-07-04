package org.tools4j.stacked.gui

import javafx.css.Styleable
import javafx.scene.layout.Pane
import javafx.stage.Stage


/**
 * User: ben
 * Date: 13/06/2016
 * Time: 5:56 PM
 */
class ExpandCollapseHelper(
    private val stage: Stage,
    private val textSearchPane: Styleable,
    private val contentPane: Pane
) {
    var isContentVisible = false
        private set
    private var sceneHeightListenerInitialized = false
    private var expandedHeight = 500.0
    private val collapsedHeight = 80.0

    fun setExpandedMode(visible: Boolean) {
        if (visible) {
            if (!sceneHeightListenerInitialized) {
                initSceneHeightListener()
            }
            stage.height = expandedHeight
            textSearchPane.styleClass.add("expandedMode")

        } else {
            stage.height = collapsedHeight
            textSearchPane.styleClass.removeAll("expandedMode")

        }
        contentPane.isVisible = visible
        isContentVisible = visible
    }

    private fun initSceneHeightListener() {
        stage.scene.heightProperty().addListener { observable, oldValue, newValue ->
            if (newValue.toDouble() > collapsedHeight) {
                expandedHeight = newValue.toDouble()
            }
        }
        sceneHeightListenerInitialized = true
    }
}
