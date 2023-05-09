package com.cxy.livecodetemplet.action;

import com.cxy.livecodetemplet.form.CodeTempletForm;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;
import org.jetbrains.annotations.NotNull;

public class OpenCodeSnippetForm extends AnAction implements DumbAware {

    //打开代码片段窗口
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        CodeTempletForm.showUI();
    }
}
