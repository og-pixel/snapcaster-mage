package co.uk.team2.objectWorkbench.workbench

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.Content
import com.intellij.ui.content.ContentFactory


class WorkbenchFactory: ToolWindowFactory {

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val myToolWindow = MyToolWindow(toolWindow)
        val contentFactory = ContentFactory.SERVICE.getInstance()
        val content: Content = contentFactory.createContent(myToolWindow.content, "Calendar", false)
        toolWindow.contentManager.addContent(content)

    }

    fun createWorkbench(choice: String): Workbench {
        return when (choice){
            "default" -> Workbench()

            else -> throw RuntimeException("This function cannot return null")
        }
    }

    fun createWindow(choice: String): Window {
        return when (choice) {
            "context" -> ContextWindow()
            "inspect" -> InspectWindow()

            else -> throw RuntimeException("This function cannot return null")
        }
    }


}