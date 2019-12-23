package co.uk.team2.objectWorkbench.workbench

import java.lang.RuntimeException

object WorkbenchFactory {

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