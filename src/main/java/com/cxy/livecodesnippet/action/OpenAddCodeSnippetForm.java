package com.cxy.livecodesnippet.action;

import com.cxy.livecodesnippet.Util.UtilState;
import com.cxy.livecodesnippet.form.AddCodeSnippetForm;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.DumbAware;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

public class OpenAddCodeSnippetForm extends AnAction implements DumbAware {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        UtilState.getInstance().setProject(e.getProject());
        String selectText = StringUtils.EMPTY;
        if (e.getData(CommonDataKeys.EDITOR) != null) {
            selectText = e.getRequiredData(CommonDataKeys.EDITOR).getSelectionModel().getSelectedText();
        } else {
            Editor editor = FileEditorManager.getInstance(UtilState.getInstance().getProject()).getSelectedTextEditor();
            if (editor != null) {
                selectText = editor.getSelectionModel().getSelectedText();
            }
        }
        AddCodeSnippetForm addCodeSnippetForm;
        if (StringUtils.isNotEmpty(selectText)) {
            addCodeSnippetForm = new AddCodeSnippetForm(selectText);
        } else {
            addCodeSnippetForm = new AddCodeSnippetForm();
        }
        addCodeSnippetForm.show();
    }
}