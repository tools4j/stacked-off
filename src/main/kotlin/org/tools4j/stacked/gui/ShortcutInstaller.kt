package org.tools4j.stacked.gui

import com.tulskiy.keymaster.common.Provider
import javafx.application.Platform
import javafx.scene.control.Control
import javafx.stage.Stage
import mu.KotlinLogging
import javax.swing.KeyStroke

/**
 * User: ben
 * Date: 10/05/2016
 * Time: 6:53 AM
 */
object ShortcutInstaller {
    val logger = KotlinLogging.logger {}

    fun install(stage: Stage, componentToFocus: Control?) {
        val provider = Provider.getCurrentProvider(false)
        val hotKeyListener = { hotKey ->
            Platform.runLater {
                stage.isIconified = false
                stage.requestFocus()
                componentToFocus?.requestFocus()
            }
        }

        val hotkeyCombinations = TiddleProperties.getInstance().getHotkeysShow()
        for (hotkeyCombination in hotkeyCombinations) {
            try {
                LOG.info("Registering hotkey $hotkeyCombination for window restore.")
                provider.register(KeyStroke.getKeyStroke(hotkeyCombination), hotKeyListener)
            } catch (t: Throwable) {
                LOG.error("Unable to assign hotkey: " + hotkeyCombination + " " + t.message, t)
            }

        }
    }
}
