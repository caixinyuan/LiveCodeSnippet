package com.cxy.livecodesnippet.action;

import com.cxy.livecodesnippet.Util.UtilState;
import com.cxy.livecodesnippet.form.AddCodeSnippetForm;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.DumbAware;
import org.jetbrains.annotations.NotNull;

public class OpenAddCodeSnippetForm extends AnAction implements DumbAware {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Editor mainEditor = FileEditorManager.getInstance(UtilState.getInstance().getProject()).getSelectedTextEditor();
        AddCodeSnippetForm addCodeSnippetForm;
        if (mainEditor != null && mainEditor.getSelectionModel().getSelectedText() != null) {
            addCodeSnippetForm = new AddCodeSnippetForm(mainEditor.getSelectionModel().getSelectedText());
        } else {
            addCodeSnippetForm = new AddCodeSnippetForm();
        };
        addCodeSnippetForm.show();
    }
}
