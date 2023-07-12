package com.cxy.livecodesnippet.form;

import com.cxy.livecodesnippet.Util.MarkDownUtil;
import com.cxy.livecodesnippet.Util.PluginMessage;
import com.cxy.livecodesnippet.Util.SQLiteUtil;
import com.cxy.livecodesnippet.Util.UtilState;
import com.cxy.livecodesnippet.model.CodeSnippetModel;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.diagnostic.Logger;
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
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.JBPopupMenu;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.Gray;
import com.intellij.ui.ListSpeedSearch;
import com.intellij.ui.SpeedSearchComparator;
import com.intellij.ui.components.JBList;
import com.intellij.ui.jcef.JBCefBrowser;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import org.apache.commons.lang3.StringUtils;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.callback.CefContextMenuParams;
import org.cef.callback.CefMenuModel;
import org.cef.handler.CefContextMenuHandlerAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class CodeSnippetForm extends DialogWrapper {
    private JBList<CodeSnippetModel> codeTagList;
    private JPanel mainJPanel;
    private EditorTextField editorCodeText;
    private JButton inputButton;
    private JScrollPane listScrollPane;
    private JPanel snippetJPanel;
    private JPanel editorPanel;
    private JPanel markdownPreviewPanel;
    private final DefaultActionGroup actionGroup = new DefaultActionGroup();
    private final Integer additionalLinesCount = 2;
    private int myLockCounter;

    private JBCefBrowser jbCefBrowser;

    private final String inputButtonTextAll = "插入代码段";
    private final String inputButtonText = "插入已选择的内容";

    private static final Logger log = Logger.getInstance(CodeSnippetForm.class);

    private final DefaultListModel<CodeSnippetModel> defaultListModel = new DefaultListModel<>();


    public CodeSnippetForm(Object value) {
        super(null);
        initJBList();
        if (value != null) {
            if (value instanceof CodeSnippetModel) {
                codeTagList.setSelectedValue(value, true);
                codeTagListClick();
            }
        } else {
            codeTagList.setSelectedIndex(0);
            codeTagListClick();
        }
        inputButton.addActionListener(e -> inputButtonClick());
        init();
        setTitle("CodeSnippet");
        setModal(false);
    }

    private void setEditorCodeText() {
        CodeSnippetModel selectedItem;
        try {
            selectedItem = codeTagList.getSelectedValue();
            String text = SQLiteUtil.getInstance().getSnippetById(selectedItem.getId());
            WriteCommandAction.runWriteCommandAction(UtilState.getInstance().getProject(), () -> editorCodeText.getDocument().setText(text));
        } catch (Exception ex) {
            log.error("set EditorText Error", ex);
        }

    }


    private void inputButtonClick() {
        inputButtonClick(null);
    }

    /**
     * 插入代码段按钮点击事件
     */
    private void inputButtonClick(String text) {
        Editor editor = FileEditorManager.getInstance(UtilState.getInstance().getProject()).getSelectedTextEditor();
        if (editor != null) {
            WriteCommandAction.runWriteCommandAction(UtilState.getInstance().getProject(), () -> {
                CaretModel caretModel = editor.getCaretModel();
                int offset = caretModel.getOffset();
                Document document = editor.getDocument();
                if (StringUtils.isNotEmpty(text)) {
                    document.insertString(offset, text);
                    editor.getSelectionModel().setSelection(offset, offset + text.length());
                } else {
                    String insertText = editorCodeText.getText();
                    if (editorCodeText.getEditor() != null && editorCodeText.getEditor().getSelectionModel().hasSelection(true)) {
                        insertText = editorCodeText.getEditor().getSelectionModel().getSelectedText();
                    }
                    if (StringUtils.isNotEmpty(insertText)) {
                        document.insertString(offset, insertText);
                        editor.getSelectionModel().setSelection(offset, offset + insertText.length());
                    }
                }
            });
            close(0);
        } else {
            PluginMessage.notifyInfo("not found Editor");
        }
    }

    /**
     * 列表点击事件
     */
    private void codeTagListClick() {
        if (isLocked()) return;
        lock();
        try {
            if (StringUtils.equals("markdown", codeTagList.getSelectedValue().getCodeType())) {
                createJBCefBrowser();
                String snippet = SQLiteUtil.getInstance().getSnippetById(codeTagList.getSelectedValue().getId());
                jbCefBrowser.loadHTML(MarkDownUtil.getInstance().getMarkdownHtml(snippet));
                markdownPreviewPanel.removeAll();
                markdownPreviewPanel.add(jbCefBrowser.getComponent());
                if (!markdownPreviewPanel.isValid()) {
                    markdownPreviewPanel.setVisible(true);
                    editorPanel.setVisible(false);
                    inputButton.setVisible(false);
                }
            } else {
                if (markdownPreviewPanel.isValid()) {
                    inputButton.setVisible(true);
                    editorPanel.setVisible(true);
                    markdownPreviewPanel.setVisible(false);
                }
                setEditorCodeText();
            }
        } catch (Exception ex) {
            log.error("========== codeTagListClick Error ==========", ex);
        } finally {
            unlock();
        }

    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return mainJPanel;
    }

    @Override
    protected Action @NotNull [] createActions() {
        return new Action[0];
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
        createEditorPanel();
        createMarkdownPreviewPanel();
    }

    private void createEditorPanel() {
        editorPanel = new JPanel(new BorderLayout());
        editorPanel.add(editorCodeText, BorderLayout.CENTER);
    }

    private void createMarkdownPreviewPanel() {
        markdownPreviewPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        markdownPreviewPanel.setMaximumSize(new Dimension(2147483647, 2147483647));
        markdownPreviewPanel.setMinimumSize(new Dimension(0, 0));
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
                                    inputButton.setText(inputButtonText);
                                } else {
                                    inputButton.setText(inputButtonTextAll);
                                }
                            }
                        });
                    }
                });

                editorCodeText.setDocument(document);

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
        ListSpeedSearch<CodeSnippetModel> speedSearch = new ListSpeedSearch<>(codeTagList);
        speedSearch.setComparator(new SpeedSearchComparator());
    }

    private void initJBList() {
        List<CodeSnippetModel> codeSnippetModelListList = SQLiteUtil.getInstance().getTitleList();
        DefaultListCellRenderer defaultListCellRenderer = getListCellRendererComponent();
        codeTagList.setCellRenderer(defaultListCellRenderer);
        if (codeSnippetModelListList.size() > 0) {
            Component component = defaultListCellRenderer.getListCellRendererComponent(codeTagList, codeSnippetModelListList.get(0), 0, false, false);
            codeTagList.setPreferredSize(new Dimension(100, (codeSnippetModelListList.size() * (int) component.getPreferredSize().getHeight())));
        } else {
            codeTagList.setPreferredSize(new Dimension(100, 0));
        }
        defaultListModel.addAll(codeSnippetModelListList);
        codeTagList.setModel(defaultListModel);
        //codeTagList.setListData(codeSnippetModelListList.toArray(new CodeSnippetModel[0]));

        codeTagList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                codeTagListClick();
            }
        });

        JPopupMenu codeSnippetTagPopMenu = new JBPopupMenu();
        JMenuItem deleteItem = new JMenuItem("删除");
        JMenuItem EditItem = new JMenuItem("编辑");

        deleteItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                int result = Messages.showYesNoDialog("确定删除吗？", "提示", AllIcons.General.WarningDialog);
                if (result == 0) {
                    SQLiteUtil.getInstance().deleteCodeSnippetById(codeTagList.getSelectedValue().getId());
                    int selectIndex = codeTagList.getSelectedIndex();
                    defaultListModel.remove(selectIndex);
                    codeTagList.setSelectedIndex(selectIndex - 1);
                }
            }
        });
        EditItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                AddCodeSnippetForm addCodeSnippetForm = new AddCodeSnippetForm(
                        SQLiteUtil.getInstance().getSnippetModelById(codeTagList.getSelectedValue().getId())
                );
                addCodeSnippetForm.show();
            }
        });

        codeSnippetTagPopMenu.add(deleteItem);
        codeSnippetTagPopMenu.add(EditItem);
        codeTagList.setComponentPopupMenu(codeSnippetTagPopMenu);

    }

    private void createJBCefBrowser() {
        if (jbCefBrowser == null) {
            jbCefBrowser = JBCefBrowser.createBuilder().createBrowser();
            jbCefBrowser.getJBCefClient().addContextMenuHandler(new CefContextMenuHandlerAdapter() {
                @Override
                public void onBeforeContextMenu(CefBrowser browser, CefFrame frame, CefContextMenuParams params, CefMenuModel model) {
                    super.onBeforeContextMenu(browser, frame, params, model);
                    model.addSeparator();
                    model.addItem(10086, "插入选定的内容");
                }

                @Override
                public boolean onContextMenuCommand(CefBrowser browser, CefFrame frame, CefContextMenuParams params, int commandId, int eventFlags) {
                    if (commandId == 10086) {
                        String selectText = params.getSelectionText();
                        if (StringUtils.isNotEmpty(selectText)) {
                            SwingUtilities.invokeLater(() -> {
                                inputButtonClick(selectText);
                            });
                        }
                    }
                    return super.onContextMenuCommand(browser, frame, params, commandId, eventFlags);
                }
            }, jbCefBrowser.getCefBrowser());
        }
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
                if (value instanceof CodeSnippetModel) {
                    CodeSnippetModel valueModel = (CodeSnippetModel) value;
                    final Color fg;
                    fg = isSelected ? JBUI.CurrentTheme.List.Selection.foreground(true) : UIUtil.getListForeground();
                    final JLabel actionLabel = new JLabel(valueModel.getTitle(), null, LEFT);
                    actionLabel.setBackground(bg);
                    actionLabel.setForeground(fg);
                    actionLabel.setFont(actionLabel.getFont().deriveFont(Font.BOLD));
                    actionLabel.setBorder(JBUI.Borders.emptyLeft(4));
                    panel.add(actionLabel, BorderLayout.BEFORE_FIRST_LINE);
                    String description = valueModel.getDescribe();
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
                        descriptionLabel.setForeground(Gray._108);
                        descriptionLabel.setBorder(JBUI.Borders.emptyLeft(15));
                        panel.add(descriptionLabel, BorderLayout.AFTER_LAST_LINE);
                        panel.setToolTipText(normalizedDesc);
                    }
                }
                return panel;
            }
        };
    }


    void lock() {
        myLockCounter++;
    }

    void unlock() {
        if (myLockCounter > 0) myLockCounter--;
    }

    private boolean isLocked() {
        return myLockCounter > 0;
    }

}