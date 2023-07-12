package com.cxy.livecodesnippet.form;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Validator;
import com.cxy.livecodesnippet.Util.Util;
import com.cxy.livecodesnippet.services.LiveCodeSnippetProjectService;
import com.cxy.livecodesnippet.storage.LiveCodeSnippetProjectStorageSetting;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class PluginSettingForm extends DialogWrapper {
    private JPanel mainJPanel;
    private JTextField pathInput;
    private JButton updateButton;
    private static final Logger log = Logger.getInstance(LiveCodeSnippetProjectService.class);

    public PluginSettingForm() {
        super(null);
        init();
        setTitle("Plugin Setting");
        setModal(false);
        initURLInputText();
        initUpdateButton();
    }

    /**
     * 更新按钮点击事件
     */
    public void updateButtonClick() {
        String path = pathInput.getText();
        if (StringUtils.isEmpty(path)) {
            Messages.showInfoMessage("输入的地址无效", "提示");
            return;
        }
        boolean isEffectivePath = false;
        //保存输入的URL地址并从输入的URL地址下载更新文件
        if (Validator.isUrl(path)) {
            isEffectivePath = true;
            Util.loadRemoteMd(path);
        } else if (FileUtil.isFile(path)) {
            isEffectivePath = true;
            Util.loadLocalMd(path);
        } else {
            Messages.showInfoMessage("输入的地址无效", "提示");
        }
        if (isEffectivePath) {
            Util.savePathSetting(path);
            close(1);
        }

    }

    private void initURLInputText() {
        if (LiveCodeSnippetProjectStorageSetting.getInstance().getState() != null) {
            pathInput.setText(LiveCodeSnippetProjectStorageSetting.getInstance().getState().getUrl());
        }
    }

    private void initUpdateButton(){
        updateButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                updateButtonClick();
            }
        });
    }




    @Override
    protected Action @NotNull [] createActions() {
        return new Action[]{getCancelAction()};
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return mainJPanel;
    }
}
