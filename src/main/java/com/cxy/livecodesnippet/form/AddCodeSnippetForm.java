package com.cxy.livecodesnippet.form;

import com.cxy.livecodesnippet.Util.PluginMessage;
import com.cxy.livecodesnippet.Util.SQLiteUtil;
import com.cxy.livecodesnippet.Util.UtilState;
import com.cxy.livecodesnippet.model.CodeSnippetModel;
import com.intellij.lang.Language;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.LanguageTextField;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class AddCodeSnippetForm extends DialogWrapper {
    private JPanel mainJPanel;
    private JTextField titleTextField;
    private JTextField describeTextField;
    private JTextField tagsTextField;
    private JTextField createPeopleTextField;
    private JTextField versionTextField;
    private JTextField codeTypeTextField;
    private JLabel titleLabel;
    private JLabel describeLabel;
    private JLabel tagsLabel;
    private JLabel createPeopleLabel;
    private JLabel versionLabel;
    private JLabel codeTypeLabel;
    private LanguageTextField codeSnippetTextField;
    private JPanel codeSinppetTextFieldPanel;
    private String codeSinppetText;

    public AddCodeSnippetForm() {
        super(null);
        codeSinppetText = "";
        init();
        setTitle("Add CodeSnippet");
        setModal(false);
    }

    public AddCodeSnippetForm(String value) {
        super(null);
        codeSinppetText = value;
        init();
        setTitle("Add CodeSnippet");
        setModal(false);
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return mainJPanel;
    }

    @Override
    protected void doOKAction() {
        // 确认按钮点击事件的处理逻辑
        save();
    }

    @Override
    public void doCancelAction() {
        // 取消按钮点击事件的处理逻辑
        System.out.println("Cancel button clicked");
        super.doCancelAction();
        close(1);
    }

    private void save() {
        String title = titleTextField.getText();
        String describe = describeTextField.getText();
        String tags = tagsTextField.getText();
        String people = createPeopleTextField.getText();
        String version = versionTextField.getText();
        String codeType = codeTypeTextField.getText();
        String codeSnippetText = codeSnippetTextField.getText();
        if (StringUtils.isEmpty(title)) {
            Messages.showInfoMessage("标题不能为空", "提示");
            return;
        }
        try {
            Float.parseFloat(version);
        } catch (Exception ex) {
            Messages.showInfoMessage("请填写正确的版本 如：1.0", "提示");
            return;
        }

        CodeSnippetModel codeSnippetModel = new CodeSnippetModel();
        codeSnippetModel.setTitle(title);
        codeSnippetModel.setDescribe(describe);
        codeSnippetModel.setTag(tags);
        codeSnippetModel.setPeople(people);
        codeSnippetModel.setVersion(version);
        codeSnippetModel.setCodeType(codeType);
        codeSnippetModel.setCodeSnippet(codeSnippetText);
        SQLiteUtil.getInstance().insert(codeSnippetModel);
        PluginMessage.notifyInfo("Add Scuess");
        close(0);
    }


    private void createUIComponents() {
        createCodeSnippetField();
    }

    private void createCodeSnippetField() {
        codeSnippetTextField = new LanguageTextField(Language.findLanguageByID("java"), UtilState.getInstance().getProject(), codeSinppetText, false) {
            @Override
            protected @NotNull EditorEx createEditor() {
                final EditorEx ex = super.createEditor();
                ex.setHorizontalScrollbarVisible(true);
                ex.setVerticalScrollbarVisible(true);
                return ex;
            }
        };
        codeSnippetTextField.setAutoscrolls(true);
        codeSnippetTextField.setEnabled(true);
    }
}
