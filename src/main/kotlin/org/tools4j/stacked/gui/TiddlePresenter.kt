package org.tools4j.stacked.gui

import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.control.SplitPane
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Pane
import javafx.scene.layout.Region
import javafx.scene.layout.VBox
import javafx.scene.web.HTMLEditor
import javafx.scene.web.WebView
import javafx.stage.Stage
import mu.KotlinLogging
import org.tools4j.stacked.index.SearchService
import java.net.URL
import java.util.*
import javax.inject.Inject

/**
 * User: ben
 * Date: 8/01/15
 * Time: 7:14 AM
 */
class TiddlePresenter : Initializable {
    val logger = KotlinLogging.logger {}

    @FXML
    lateinit var textTitle: Label
    @FXML
    lateinit var textTags: Label
    @FXML
    lateinit var labelLogo: Label
    @FXML
    lateinit var listViewResultsParentPane: BorderPane
    @FXML
    lateinit var resultDetailsScrollPane: Region
    @FXML
    lateinit var splitPane: SplitPane
    @FXML
    lateinit var mainPane: VBox
    @FXML
    lateinit var textSearchPane: BorderPane
    @FXML
    lateinit var whiteSpaceCover: Pane
    @FXML
    lateinit var textSearchBox: TextField
    @FXML
    lateinit var listViewResults: ListView<ResultsListItem>
    @FXML
    lateinit var htmlResultDetails: HTMLEditor
    @FXML
    lateinit var contentPane: Pane
    @Inject
    lateinit private var searchService: SearchService
    @Inject
    lateinit private var stage: Stage

    private var listViewItems: ObservableList<ResultsListItem>? = null
    private var toolbarHidden = false
    private var expandCollapseHelper: ExpandCollapseHelper? = null

    override fun initialize(url: URL, resourceBundle: ResourceBundle) {
        try {
            ShortcutInstaller.install(stage, textSearchBox)
            expandCollapseHelper = ExpandCollapseHelper(stage, textSearchPane, contentPane)
            labelLogo.text = ">"
            listViewItems = FXCollections.observableArrayList()
            listViewResults.setItems(listViewItems)
            listViewResults.setCellFactory { listView -> ResultsListCell(listViewResultsParentPane) }
            expandCollapseHelper!!.setExpandedMode(false)

            hideToolbar()
            val pathToHtmlResultStylesheet = javaClass.getResource("/tiddle/javafx/result-style.css").toString()
            val pathToFxStylesheet = javaClass.getResource("/tiddle/javafx/tiddle.css").toString()

            htmlResultDetails.stylesheets.add(pathToHtmlResultStylesheet)

            textSearchBox.setOnKeyReleased { KeyEvent ->
                hideToolbar()
                if (textSearchBox.text != null && textSearchBox.text.length > 0) {
                    val results = searchService.searchForQuestionSummaries(textSearchBox.text)
                    if (results.questionSummaries.size > 0) {
                        listViewItems!!.clear()
                        if (!expandCollapseHelper!!.isContentVisible) {
                            expandCollapseHelper!!.setExpandedMode(true)
                        }
                        for (q in results.questionSummaries) {
                            val title = q.title
                            val htmlContent = q.searchResultText
                            listViewItems!!.add(ResultsListItem(title!!, htmlContent))
                        }
                        listViewResults.selectionModel.selectFirst()
                        listViewResults.stylesheets.addAll(pathToFxStylesheet)
                    }
                }
            }

            textSearchBox.setOnKeyPressed { e: KeyEvent ->
                if (e.code == KeyCode.DOWN) {
                    listViewResults.requestFocus()
                    listViewResults.selectionModel.selectNext()

                } else if (e.code == KeyCode.UP) {
                    listViewResults.requestFocus()
                    listViewResults.selectionModel.selectNext()

                } else if (e.code == KeyCode.ENTER) {
                    //TODO possibly launch browser

                } else if (e.code == KeyCode.ESCAPE) {
                    escapeKeyPressed()

                } else if (e.isControlDown && !e.isShiftDown) {
                    if (e.code == KeyCode.R) {
                        wikiSearchService = wikiSearchService!!.rebuild()

                    } else if (e.code == KeyCode.Q) {
                        Platform.exit()
                        System.exit(0)
                    }

                } else if (e.isControlDown && e.isShiftDown) {
                    if (e.code == KeyCode.D) {
                        debugGui(mainPane)
                    } else if (e.code == KeyCode.M) {
                        installDebugClickEventHandlerRecursivelyOnNode(mainPane!!)
                    }
                }
            }


            listViewResults.selectionModel.selectedItemProperty().addListener { observable, oldValue, newValue ->
                if (newValue != null) {
                    val content = newValue.htmlContent
                    textTitle!!.text = newValue.title
                    if (newValue.tags != null && newValue.tags.length > 0) {
                        textTags!!.text = "Tags: " + arrayOf(newValue.tags).joinToString(" ")
                    } else {
                        textTags!!.text = ""
                    }
                    htmlResultDetails.htmlText = content
                } else {
                    htmlResultDetails.htmlText = ""
                }
            }

            listViewResults.setOnKeyPressed { e: KeyEvent ->
                if (e.code == KeyCode.RIGHT) {
                    htmlResultDetails.requestFocus()

                } else if (e.code == KeyCode.ESCAPE) {
                    escapeKeyPressed()
                }
            }

            htmlResultDetails.setOnKeyPressed { e: KeyEvent ->
                if (e.code == KeyCode.LEFT || e.code == KeyCode.ESCAPE) {
                    listViewResults.requestFocus()
                }
            }


            val webView = htmlResultDetails.lookupAll(".web-view").iterator().next() as WebView
            webView.engine.userStyleSheetLocation = pathToHtmlResultStylesheet


            val allowableKeyCodes = HashSet<KeyCode>()
            allowableKeyCodes.add(KeyCode.UP)
            allowableKeyCodes.add(KeyCode.LEFT)
            allowableKeyCodes.add(KeyCode.RIGHT)
            allowableKeyCodes.add(KeyCode.DOWN)
            allowableKeyCodes.add(KeyCode.PAGE_UP)
            allowableKeyCodes.add(KeyCode.PAGE_DOWN)
            allowableKeyCodes.add(KeyCode.CONTROL)
            allowableKeyCodes.add(KeyCode.ESCAPE)
            allowableKeyCodes.add(KeyCode.SHIFT)

            val allowableControlCodes = HashSet<KeyCode>()
            allowableControlCodes.add(KeyCode.C)

            htmlResultDetails.addEventFilter(KeyEvent.KEY_PRESSED) { e ->
                if (allowableKeyCodes.contains(e.code) || e.isControlDown && allowableControlCodes.contains(e.code)) {
                    //Allow through
                } else {
                    e.consume()
                }
            }

            htmlResultDetails.addEventFilter(KeyEvent.KEY_TYPED) { e ->
                if (allowableKeyCodes.contains(e.code) || e.isControlDown && allowableControlCodes.contains(e.code)) {
                    //Allow through
                } else {
                    e.consume()
                }
            }

            labelLogo!!.setOnMousePressed { event ->
                xOffset = stage!!.x - event.screenX
                yOffset = stage.y - event.screenY
            }

            labelLogo!!.setOnMouseDragged { event ->
                stage!!.x = event.screenX + xOffset
                stage.y = event.screenY + yOffset
            }

            webView.onDragDropped = EventHandler<DragEvent> { it.consume() }
            whiteSpaceCover!!.toFront()


        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }

    }

    private fun installDebugClickEventHandlerRecursivelyOnNode(node: Node) {
        println("Installing mouseClicked event handler on node: $node")
        node.addEventHandler(MouseEvent.MOUSE_CLICKED) { event ->
            println(
                node.javaClass.simpleName
                        + " id:" + node.id
                        + " styleClass:" + node.styleClass
                        + " style:" + node.style
            )
        }
        if (node is Parent) {
            node.childrenUnmodifiable.forEach(Consumer<Node> { this.installDebugClickEventHandlerRecursivelyOnNode(it) })
        }
    }

    private fun debugGui(node: Node, currentIndentLevel: String = "") {
        println(
            currentIndentLevel
                    + node.javaClass.simpleName
                    + " id:" + node.id
                    + " styleClass:" + node.styleClass
                    + " style:" + node.style
        )
        if (node is Parent) {
            node.childrenUnmodifiable.forEach { child -> debugGui(child, "$currentIndentLevel    ") }
        }
    }

    private fun hideToolbar() {
        if (!toolbarHidden) {
            val nodes = htmlResultDetails!!.lookupAll(".tool-bar").toTypedArray()
            for (node in nodes) {
                node.isVisible = false
                node.isManaged = false
            }
            toolbarHidden = true
        }
    }

    private fun escapeKeyPressed() {
        if (expandCollapseHelper!!.isContentVisible) {
            clearAndReset()
            textSearchBox!!.clear()
            textSearchBox.requestFocus()
        } else {
            stage!!.isIconified = true
        }
    }


    private fun clearAndReset() {
        listViewItems!!.clear()
        htmlResultDetails!!.htmlText = ""
        expandCollapseHelper!!.setExpandedMode(false)
    }

    companion object {

        private var xOffset = 0.0
        private var yOffset = 0.0
    }
}
