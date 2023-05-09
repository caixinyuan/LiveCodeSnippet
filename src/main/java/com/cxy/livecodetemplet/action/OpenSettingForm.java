package com.cxy.livecodetemplet.action;

import com.cxy.livecodetemplet.form.LiveCodeTempletSettingForm;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;
import org.jetbrains.annotations.NotNull;

public class OpenSettingForm extends AnAction implements DumbAware {

    //打开插件配置窗口
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        LiveCodeTempletSettingForm.showUI();
    }

}
