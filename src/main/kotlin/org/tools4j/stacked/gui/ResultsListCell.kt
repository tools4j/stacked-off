package org.tools4j.stacked.gui

import javafx.scene.control.Control
import javafx.scene.control.ListCell
import javafx.scene.control.OverrunStyle
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Region

/**
 * User: ben
 * Date: 28/04/2016
 * Time: 5:37 PM
 */
class ResultsListCell(parent: BorderPane) : ListCell<ResultsListItem>() {
    private val parent: Region

    init {
        this.parent = parent
    }

    public override fun updateItem(item: ResultsListItem?, empty: Boolean) {
        super.updateItem(item, empty)
        if (empty || item == null) {
            text = ""
        } else {
            text = item.title
            textOverrun = OverrunStyle.ELLIPSIS
            prefWidthProperty().bind(parent.widthProperty().subtract(2))
            maxWidth = Control.USE_PREF_SIZE
        }
    }
}
