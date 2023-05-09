package com.cxy.livecodetemplet.form;

import com.cxy.livecodetemplet.Util.PluginMessage;
import com.cxy.livecodetemplet.Util.UtilState;
import com.cxy.livecodetemplet.model.CodeTempletModel;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.highlighter.EditorHighlighter;
import com.intellij.openapi.editor.highlighter.EditorHighlighterFactory;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.ListSpeedSearch;
import com.intellij.ui.SpeedSearchComparator;
import com.intellij.ui.components.JBList;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class CodeTempletForm {
    private JBList<CodeTempletModel> codeTagList;
    private JPanel panel1;
    private EditorTextField editorCodeText;
    private JButton inputBUtton;
    private JScrollPane listScrollPane;
    private JPanel panel2;

    private final Integer additionalLinesCount = 2;

    public CodeTempletForm(Object value) {
        List<CodeTempletModel> codeTempletList = UtilState.getInstance().getCodeTempletList();
        codeTagList.setListData(codeTempletList.toArray(new CodeTempletModel[0]));
        if (value != null) {
            if (value instanceof CodeTempletModel && codeTempletList.contains(value)) {
                codeTagList.setSelectedValue(value, true);
                setEditorCodeText();
            }
        } else if (codeTempletList.size() > 0) {
            codeTagList.setSelectedIndex(0);
            setEditorCodeText();
        }
        codeTagList.setCellRenderer(getListCellRendererComponent());
        codeTagList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                setEditorCodeText();
            }
        });
        inputBUtton.addActionListener(e -> inputButtonClick());
        codeTagList.setPreferredSize(new Dimension(100, codeTempletList.size() * 60));
    }


    private void resetEditorCodeTextSize() {
        try {
            if (StringUtils.isEmpty(editorCodeText.getText()) || editorCodeText.getEditor() == null) {
                return;
            }
            Integer lineHeight = editorCodeText.getEditor().getLineHeight();
            Integer lineCount = editorCodeText.getDocument().getLineCount();
            int editorHeight = lineHeight * (lineCount + additionalLinesCount + 1);
            String text = Arrays.stream(editorCodeText.getText().split("\n")).max((i, k) -> {
                Integer a = i.length();
                Integer b = k.length();
                return a.compareTo(b);
            }).orElse("");
            int editorWidth = editorCodeText.getFontMetrics(editorCodeText.getFont()).stringWidth(text) + 10;
//            editorCodeText.setPreferredSize(new Dimension(editorWidth, editorHeight));
//            editorCodeText.revalidate();
//            editorCodeText.repaint();

        } catch (Exception ex) {
            PluginMessage.notifyWarning("重新设置编辑器大小时异常");
        }
    }

    private void setEditorCodeText(CodeTempletModel selectedItem) {
        if (selectedItem == null) {
            selectedItem = codeTagList.getSelectedValue();
        }
        if (selectedItem != null) {
            if (selectedItem.getCodeTemplet() != null) {
                editorCodeText.setText(selectedItem.getCodeTemplet());
            }
        }
    }

    private void setEditorCodeText() {
        setEditorCodeText(null);
    }


    private void inputButtonClick() {
        Editor editor = FileEditorManager.getInstance(UtilState.getInstance().getProject()).getSelectedTextEditor();
        if (editor != null) {
            WriteCommandAction.runWriteCommandAction(UtilState.getInstance().getProject(), () -> {
                CaretModel caretModel = editor.getCaretModel();
                int offset = caretModel.getOffset();
                Document document = editor.getDocument();
                document.insertString(offset, editorCodeText.getText());
                editor.getSelectionModel().setSelection(offset, offset + editorCodeText.getText().length());
            });
            Arrays.stream(JFrame.getFrames()).forEach(i -> {
                if (StringUtils.equals(i.getTitle(), "CodeTemplet")) {
                    i.dispose();
                }
            });
        } else {
            PluginMessage.notifyInfo("not found Editor");
        }
    }


    private void createUIComponents() {
        EditorFactory editorFactory = EditorFactory.getInstance();
        Document document = editorFactory.createDocument("");
        editorCodeText = new EditorTextField(document, null, UtilState.getInstance().getJavaFileType()) {
            @Override
            protected @NotNull EditorEx createEditor() {
                EditorEx editor = super.createEditor();
                editor.getSettings().setLineNumbersShown(true);
                editor.getSettings().setFoldingOutlineShown(true);
                editor.getSettings().setAdditionalLinesCount(additionalLinesCount);
                editor.setOneLineMode(false);
                editor.setViewer(true);
                editor.getSettings().setUseSoftWraps(false);
                EditorColorsScheme setting = EditorColorsManager.getInstance().getGlobalScheme();
                EditorHighlighter highlighter = EditorHighlighterFactory.getInstance().createEditorHighlighter(UtilState.getInstance().getJavaFileType(), setting, UtilState.getInstance().getProject());
                editor.setHighlighter(highlighter);
                editor.setHorizontalScrollbarVisible(true);
                editor.setVerticalScrollbarVisible(true);
                return editor;
            }
        };
        codeTagList = new JBList<>();
        // 创建 ListSpeedSearch 对象
        ListSpeedSearch<CodeTempletModel> speedSearch = new ListSpeedSearch<>(codeTagList);
        speedSearch.setComparator(new SpeedSearchComparator());
    }

    private DefaultListCellRenderer getListCellRendererComponent() {
        return new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                final JPanel panel = new JPanel(new BorderLayout());
                panel.setOpaque(true);
                panel.setBorder(JBUI.Borders.emptyRight(4));
                final Color bg = isSelected ? UIUtil.getListSelectionBackground(true) : UIUtil.getListBackground();
                panel.setBackground(bg);
                if (value instanceof CodeTempletModel) {
                    final Color fg;
                    fg = isSelected ? JBUI.CurrentTheme.List.Selection.foreground(true) : UIUtil.getListForeground();
                    final JLabel actionLabel = new JLabel(((CodeTempletModel) value).getTitle(), null, LEFT);
                    actionLabel.setBackground(bg);
                    actionLabel.setForeground(fg);
                    actionLabel.setFont(actionLabel.getFont().deriveFont(Font.BOLD));
                    actionLabel.setBorder(JBUI.Borders.emptyLeft(4));
                    panel.add(actionLabel, BorderLayout.BEFORE_FIRST_LINE);
                    String description = ((CodeTempletModel) value).getDescribe();
                    if (description != null) {
                        // truncate long descriptions
                        final String normalizedDesc;
                        if (description.length() > 20) {
                            normalizedDesc = description.substring(0, 20) + "...";
                        } else {
                            normalizedDesc = description;
                        }
                        final JLabel descriptionLabel = new JLabel(normalizedDesc);
                        descriptionLabel.setBackground(bg);
                        descriptionLabel.setForeground(fg);
                        descriptionLabel.setBorder(JBUI.Borders.emptyLeft(15));
                        panel.add(descriptionLabel, BorderLayout.AFTER_LAST_LINE);
                        panel.setToolTipText(((CodeTempletModel) value).getCodeTemplet());
                    }
                }
                return panel;
            }
        };
    }

    public static void showUI() {
        JFrame frame = new JFrame("CodeTemplet");
        JPanel jPanel = new CodeTempletForm(null).panel1;
        frame.setContentPane(jPanel);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void showUI(Object value) {
        JFrame frame = new JFrame("CodeTemplet");
        frame.setContentPane(new CodeTempletForm(value).panel1);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
