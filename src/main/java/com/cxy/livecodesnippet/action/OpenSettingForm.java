package com.cxy.livecodesnippet.action;

import com.cxy.livecodesnippet.Util.UtilState;
import com.cxy.livecodesnippet.form.PluginSettingForm;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.ui.popup.IconButton;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class OpenSettingForm extends AnAction implements DumbAware {

    //打开插件配置窗口
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        new PluginSettingForm().show();
    }

}
