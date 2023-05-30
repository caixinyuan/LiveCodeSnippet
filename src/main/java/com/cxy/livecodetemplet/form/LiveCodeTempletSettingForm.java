package com.cxy.livecodetemplet.form;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Validator;
import com.cxy.livecodetemplet.Util.Util;
import com.cxy.livecodetemplet.services.LiveCodeTempletService;
import com.cxy.livecodetemplet.storage.LiveCodeTempletStorageSetting;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ui.Messages;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.util.Arrays;
import java.util.Objects;


public class LiveCodeTempletSettingForm {
    private JButton updateButton;
    private JPanel mainJPanel;
    private JTextField pathInput;


    private static final Logger log = Logger.getInstance(LiveCodeTempletService.class);

    public LiveCodeTempletSettingForm() {
        updateButton.addActionListener(e -> updateButtonClick());
        initURLInputText();
    }

    /**
     * 更新按钮点击事件
     */
    public void updateButtonClick() {
        String path = pathInput.getText();
        if (StringUtils.isEmpty(path)) {
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
            Objects.requireNonNull(Arrays.stream(JFrame.getFrames()).filter(i -> StringUtils.equals(i.getName(), "LiveCodeTempletSetting")).findFirst().orElse(null)).dispose();
        }

    }


    private void initURLInputText() {
        if (LiveCodeTempletStorageSetting.getInstance().getState() != null) {
            pathInput.setText(LiveCodeTempletStorageSetting.getInstance().getState().getUrl());
        }
    }


    public static void showUI() {
        JFrame frame = new JFrame("LiveCodeTempletSetting");
        frame.setName("LiveCodeTempletSetting");
        frame.setContentPane(new LiveCodeTempletSettingForm().mainJPanel);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
