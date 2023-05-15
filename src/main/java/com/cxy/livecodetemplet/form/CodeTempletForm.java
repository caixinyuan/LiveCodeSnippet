package com.cxy.livecodetemplet.form;

import com.cxy.livecodetemplet.Util.PluginMessage;
import com.cxy.livecodetemplet.Util.UtilState;
import com.cxy.livecodetemplet.model.CodeTempletModel;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.editor.event.SelectionEvent;
import com.intellij.openapi.editor.event.SelectionListener;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.highlighter.EditorHighlighter;
import com.intellij.openapi.editor.highlighter.EditorHighlighterFactory;
import com.intellij.openapi.editor.markup.HighlighterLayer;
import com.intellij.openapi.editor.markup.TextAttributes;
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
                editorCodeText.getDocument().setText(selectedItem.getCodeTemplet());
            }
        }
    }

    private void setEditorCodeText() {
        setEditorCodeText(null);
    }


    private void inputButtonClick() {
        if (StringUtils.isEmpty(editorCodeText.getText())) {
            return;
        }
        Editor editor = FileEditorManager.getInstance(UtilState.getInstance().getProject()).getSelectedTextEditor();
        if (editor != null) {
            WriteCommandAction.runWriteCommandAction(UtilState.getInstance().getProject(), () -> {
                CaretModel caretModel = editor.getCaretModel();
                int offset = caretModel.getOffset();
                Document document = editor.getDocument();
                String insertText = editorCodeText.getText();
                if (editorCodeText.getEditor() != null && editorCodeText.getEditor().getSelectionModel().hasSelection(true)) {
                    insertText = editorCodeText.getEditor().getSelectionModel().getSelectedText();
                }
                if (StringUtils.isNotEmpty(insertText)) {
                    document.insertString(offset, insertText);
                    editor.getSelectionModel().setSelection(offset, offset + insertText.length());
                }
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


    /**
     * 代码折叠支持类
     */
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


    /**
     * 查到需要折叠的代码
     *
     * @param text
     * @return
     */
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
        createEditorTextField();
        createJBList();
    }


    private void createEditorTextField() {
        EditorFactory editorFactory = EditorFactory.getInstance();
        Document document = editorFactory.createDocument("");
        editorCodeText = new EditorTextField(document, UtilState.getInstance().getProject(), UtilState.getInstance().getJavaFileType()) {
            @Override
            protected @NotNull EditorEx createEditor() {
                EditorEx editor = super.createEditor();
                editor.setOneLineMode(false);
                editor.setViewer(false);
                editor.setHorizontalScrollbarVisible(true);
                editor.setVerticalScrollbarVisible(true);
                EditorSettings settings = editor.getSettings();
                settings.setAdditionalColumnsCount(10);
                settings.setAdditionalLinesCount(additionalLinesCount);
                settings.setLineNumbersShown(true);
                settings.setFoldingOutlineShown(true);

                EditorColorsScheme colorSettings = EditorColorsManager.getInstance().getGlobalScheme();
                EditorHighlighter highlighter = EditorHighlighterFactory.getInstance().createEditorHighlighter(UtilState.getInstance().getJavaFileType(), colorSettings, UtilState.getInstance().getProject());

                TextAttributes attributes = new TextAttributes();
                attributes.setForegroundColor(new Color(98, 151, 85));
                attributes.setFontType(Font.BOLD);
                TextAttributesKey textAttributesKey = TextAttributesKey.createTextAttributesKey("MY_REGION_HIGHLIGHT");
                colorSettings.setAttributes(textAttributesKey, attributes);
                editor.setHighlighter(highlighter);

                //自定义折叠
                document.addDocumentListener(new DocumentListener() {
                    @Override
                    public void documentChanged(@NotNull DocumentEvent event) {
                        DocumentListener.super.documentChanged(event);
                        editor.getFoldingModel().runBatchFoldingOperation(() -> {
                            String text = event.getDocument().getText();
                            List<RegionDescriptor> regions = parseRegions(text);
                            regions.forEach(i -> {
                                FoldRegion foldRegion = editor.getFoldingModel().addFoldRegion(i.getStartOffset(), i.getEndOffset(), i.getPrompt());
                                //高亮显示内容
                                TextAttributes placeholderAttributes = new TextAttributes();
                                placeholderAttributes.copyFrom(attributes);
                                editor.getMarkupModel().addLineHighlighter(event.getDocument().getLineNumber(i.getStartOffset()), HighlighterLayer.SELECTION - 1, placeholderAttributes);
                                if (foldRegion != null) {
                                    foldRegion.setExpanded(false);
                                    foldRegion.setInnerHighlightersMuted(true);
                                }
                            });
                        });
                        editor.getSelectionModel().addSelectionListener(new SelectionListener() {
                            @Override
                            public void selectionChanged(@NotNull SelectionEvent e) {
                                SelectionListener.super.selectionChanged(e);
                                if (e.getEditor().getSelectionModel().hasSelection()) {
                                    inputBUtton.setText("插入已选择的内容");
                                } else {
                                    inputBUtton.setText("插入代码段");
                                }
                            }
                        });
                    }
                });

                editorCodeText.setDocument(document);
                DefaultActionGroup actionGroup = new DefaultActionGroup();
                AnAction editorInsetAction = new AnAction("插入选中的内容") {
                    @Override
                    public void actionPerformed(@NotNull AnActionEvent e) {
                        inputButtonClick();
                    }
                };
                AnAction EditorCopyAction = ActionManager.getInstance().getAction("EditorCopy");
                editorCodeText.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        super.mouseReleased(e);
                        if (SwingUtilities.isRightMouseButton(e)) {
                            actionGroup.removeAll();
                            if (editorCodeText.getEditor().getSelectionModel().hasSelection(true)) {
                                actionGroup.addAction(editorInsetAction);
                                actionGroup.addAction(EditorCopyAction);
                            }
                            ActionPopupMenu popupMenu = ActionManager.getInstance().createActionPopupMenu("MyPopupMenu", actionGroup);
                            popupMenu.getComponent().show(e.getComponent(), e.getX(), e.getY());
                        }
                    }
                });
                return editor;
            }
        };
    }

    private void createJBList() {
        codeTagList = new JBList<>();
        // 创建 ListSpeedSearch 对象
        ListSpeedSearch<CodeTempletModel> speedSearch = new ListSpeedSearch<>(codeTagList);
        speedSearch.setComparator(new SpeedSearchComparator());
    }


    private @NotNull DefaultListCellRenderer getListCellRendererComponent() {
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