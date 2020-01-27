package co.uk.team2.objectWorkbench.workbench

import com.intellij.openapi.wm.ToolWindow
import java.awt.event.ActionEvent
import java.util.*
import javax.swing.ImageIcon
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel


class MyToolWindow(toolWindow: ToolWindow) {
    private val refreshToolWindowButton: JButton? = null
    private val hideToolWindowButton: JButton? = null
    private val currentDate: JLabel? = null
    private val currentTime: JLabel? = null
    private val timeZone: JLabel? = null
    val content: JPanel? = null

    fun currentDateTime() { // Get current date and time
        val instance = Calendar.getInstance()
        currentDate!!.text = (instance[Calendar.DAY_OF_MONTH].toString() + "/"
                + (instance[Calendar.MONTH] + 1).toString() + "/" + instance[Calendar.YEAR].toString())
        currentDate.icon = ImageIcon(javaClass.getResource("/img/icon.png"))
        val min = instance[Calendar.MINUTE]
        val strMin: String
        strMin = if (min < 10) {
            "0$min"
        } else {
            min.toString()
        }
        currentTime!!.text = instance[Calendar.HOUR_OF_DAY].toString() + ":" + strMin
        currentTime.icon = ImageIcon(javaClass.getResource("/img/icon.png"))
        // Get time zone
        val gmt_Offset = instance[Calendar.ZONE_OFFSET].toLong() // offset from GMT in milliseconds
        var str_gmt_Offset = (gmt_Offset / 3600000).toString()
        str_gmt_Offset = if (gmt_Offset > 0) "GMT + $str_gmt_Offset" else "GMT - $str_gmt_Offset"
        timeZone!!.text = str_gmt_Offset
        timeZone.icon = ImageIcon(javaClass.getResource("/img/icon.png"))
    }



    init {
        println("hello")
        hideToolWindowButton!!.addActionListener {
            e: ActionEvent? -> toolWindow.hide(null)
        }
        refreshToolWindowButton!!.addActionListener { e: ActionEvent? -> currentDateTime() }
        currentDateTime()
    }
}