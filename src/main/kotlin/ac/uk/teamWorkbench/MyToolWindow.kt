package ac.uk.teamWorkbench

import com.intellij.openapi.wm.ToolWindow
import java.awt.event.ActionEvent
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel


class MyToolWindow(toolWindow: ToolWindow) {

    private var refreshToolWindowButton: JButton? = null
    private val hideToolWindowButton: JButton? = null
    private val helloLabel: JLabel? = null

    var content: JPanel? = null

    fun currentDateTime() {

    }



    init {
        hideToolWindowButton!!.addActionListener { e: ActionEvent? -> toolWindow.hide(null) }
        refreshToolWindowButton!!.addActionListener { e: ActionEvent? -> currentDateTime() }

        currentDateTime()
    }
}