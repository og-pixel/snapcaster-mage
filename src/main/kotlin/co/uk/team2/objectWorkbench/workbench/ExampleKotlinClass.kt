package co.uk.team2.objectWorkbench.workbench

import com.intellij.ide.BrowserUtil
import com.intellij.lang.Language
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys


/*
 * Copyright (c) 2019.
 * Author Kacper
 *
 * Just an example of simple Kotlin Class that will be removed latter on.
 *
 */
class ExampleKotlinClass: AnAction("") {

    override fun actionPerformed(e: AnActionEvent) {
        val file = e.getData(CommonDataKeys.PSI_FILE)
        val lang: Language = e.getData(CommonDataKeys.PSI_FILE)!!.language
        val languageTag = "+[" + lang.displayName.toLowerCase() + "]"

        val editor = e.getRequiredData(CommonDataKeys.EDITOR)
        val caretModel = editor.caretModel
        val selectedText = caretModel.currentCaret.selectedText

        val query = selectedText!!.replace(' ', '+') + languageTag
        BrowserUtil.browse("https://stackoverflow.com/search?q=" + query);
    }

    override fun update(e: AnActionEvent) {
//        val editor = e.getRequiredData(CommonDataKeys.EDITOR)
//        val caretModel = editor.caretModel
//        e.presentation.isEnabledAndVisible = caretModel.currentCaret.hasSelection()
    }

}