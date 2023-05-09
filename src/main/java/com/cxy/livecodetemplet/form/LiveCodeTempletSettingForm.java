package com.cxy.livecodetemplet.form;

import cn.hutool.core.lang.Validator;
import com.cxy.livecodetemplet.Util.Util;
import com.cxy.livecodetemplet.storage.LiveCodeTempletStorageSetting;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.ui.Messages;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.util.Arrays;

public class LiveCodeTempletSettingForm {
    private JButton updateButton;
    private JPanel panel1;
    private JTextField urlInput;
    private JLabel localMdPathTextLable;
    private JTextField localMdPathInput;

    private LiveCodeTempletStorageSetting storageSetting = LiveCodeTempletStorageSetting.getInstance();

    public LiveCodeTempletSettingForm() {
        updateButton.addActionListener(e -> {
            updateButtonClick();
        });
        initURLInputText();
        updateLocalMdPathLableText();
    }

    /**
     * 更新按钮点击事件
     */
    public void updateButtonClick() {
        String url = urlInput.getText();
        //保存输入的URL地址并从输入的URL地址下载更新文件
        if (StringUtils.isNotEmpty(url)) {
            if (Validator.isUrl(url)) {
                if (storageSetting.getState() == null || !StringUtils.equals(storageSetting.getState().getUrl(), url)) {
                    storageSetting.getState().setUrl(url);
                    ApplicationManager.getApplication().saveSettings();
                }
                Arrays.stream(JFrame.getFrames()).forEach(i -> {
                    if (StringUtils.equals(i.getTitle(), "LiveCodeTempletSetting")) {
                        i.dispose();
                    }
                });
                Util.loadRemoteMd(url);
            } else {
                Messages.showInfoMessage("输入的地址无效", "提示");
            }
        }
    }

    private void initURLInputText() {
        if (storageSetting.getState() != null) {
            urlInput.setText(storageSetting.getState().getUrl());
        }
    }

    private void updateLocalMdPathLableText() {
        if (Util.loaclMdFileExists()) {
            localMdPathInput.setText(Util.localMdFilePath.getPath());
        }
    }

    public static void showUI() {
        JFrame frame = new JFrame("LiveCodeTempletSetting");
        frame.setContentPane(new LiveCodeTempletSettingForm().panel1);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
