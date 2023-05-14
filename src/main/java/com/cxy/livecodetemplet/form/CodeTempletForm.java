package com.cxy.livecodetemplet.form;

import com.cxy.livecodetemplet.Util.PluginMessage;
import com.cxy.livecodetemplet.Util.UtilState;
import com.cxy.livecodetemplet.model.CodeTempletModel;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
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
import java.util.ArrayList;
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


    private static class RegionDescriptor {
        private final int startOffset;
        private int endOffset;
        private final String prompt;
        private RegionDescriptor parent;
        private List<RegionDescriptor> children = new ArrayList<>();

        public RegionDescriptor(int startOffset, int endOffset, String prompt) {
            this.startOffset = startOffset;
            this.endOffset = endOffset;
            this.prompt = prompt;
        }

        public int getStartOffset() {
            return startOffset;
        }

        public int getEndOffset() {
            return endOffset;
        }

        public void setEndOffset(int endOffset) {
            this.endOffset = endOffset;
        }

        public void addChild(RegionDescriptor child) {
            this.children.add(child);
            child.setParent(this);
        }

        public RegionDescriptor getParent() {
            return parent;
        }

        public void setParent(RegionDescriptor parent) {
            this.parent = parent;
        }

        public String getPrompt() {
            return this.prompt;
        }

    }


    private List<RegionDescriptor> parseRegions(String text) {
        final String REGION_PREFIX = "//region";
        final String REGION_SUFFIX = "//endregion";
        List<RegionDescriptor> regions = new ArrayList<>();
        RegionDescriptor currentRegion = null;
        int startOffset = 0;
        while (true) {
            int startIndex = text.indexOf(REGION_PREFIX, startOffset);
            int endIndex = text.indexOf(REGION_SUFFIX, startOffset);
            if (startIndex < 0 && endIndex < 0) {
                break;
            }
            if (endIndex < 0 || (startIndex >= 0 && startIndex < endIndex)) {
                int lineStartOffset = text.lastIndexOf('\n', startIndex);
                if (lineStartOffset < 0) {
                    lineStartOffset = 0;
                } else {
                    lineStartOffset++;
                }
                int lineEndOffset = text.indexOf('\n', startIndex);
                if (lineEndOffset < 0) {
                    lineEndOffset = text.length();
                }
                String prompt = text.substring(lineStartOffset, lineEndOffset).trim();
                if (prompt.startsWith(REGION_PREFIX)) {
                    prompt = prompt.substring(REGION_PREFIX.length()).trim();
                }
                RegionDescriptor newRegion = new RegionDescriptor(startIndex, -1, prompt);
                if (currentRegion != null) {
                    currentRegion.addChild(newRegion);
                }
                currentRegion = newRegion;
                regions.add(newRegion);
                startOffset = startIndex + REGION_PREFIX.length();
            } else {
                currentRegion.setEndOffset(endIndex + REGION_SUFFIX.length());
                currentRegion = currentRegion.getParent();
                startOffset = endIndex + REGION_SUFFIX.length();
            }
        }
        return regions;
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
                editor.getSettings().setAutoCodeFoldingEnabled(true);
                editor.getSettings().setAdditionalColumnsCount(5);
                editor.getSettings().setAdditionalLinesCount(additionalLinesCount);
                editor.setOneLineMode(false);
                editor.getSettings().setUseSoftWraps(false);
                editor.getFoldingModel().setFoldingEnabled(true);
                EditorColorsScheme setting = EditorColorsManager.getInstance().getGlobalScheme();
                EditorHighlighter highlighter = EditorHighlighterFactory.getInstance().createEditorHighlighter(UtilState.getInstance().getJavaFileType(), setting, UtilState.getInstance().getProject());
                editor.setHighlighter(highlighter);
                editor.setHorizontalScrollbarVisible(true);
                editor.setVerticalScrollbarVisible(true);


                document.addDocumentListener(new DocumentListener() {
                    @Override
                    public void documentChanged(@NotNull DocumentEvent event) {
                        DocumentListener.super.documentChanged(event);
                        editor.getFoldingModel().runBatchFoldingOperation(() -> {
                            String text = event.getDocument().getText();
                            List<RegionDescriptor> regions = parseRegions(text);
                            regions.forEach(i -> {
                                FoldRegion foldRegion = editor.getFoldingModel().addFoldRegion(i.getStartOffset(), i.getEndOffset(), i.getPrompt());
                                if (foldRegion!=null){
                                    foldRegion.setExpanded(false);
                                }
                            });
                        });
                    }
                });
                return editor;
            }
        };


        editorCodeText.setDocument(document);
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
