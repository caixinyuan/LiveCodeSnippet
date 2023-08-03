package com.cxy.livecodesnippet.action;

import com.cxy.livecodesnippet.Util.SQLiteUtil;
import com.cxy.livecodesnippet.Util.UtilState;
import com.cxy.livecodesnippet.form.CodeSnippetForm;
import com.cxy.livecodesnippet.model.CodeSnippetModel;
import com.intellij.icons.AllIcons;
import com.intellij.ide.IdeBundle;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.ui.popup.IconButton;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.ColoredListCellRenderer;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.SearchTextField;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.JBInsets;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static javax.swing.SwingConstants.LEFT;

public class OpenSearchForm extends AnAction implements DumbAware {
    private JBPopup jbPopup;


    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        UtilState.getInstance().setProject(e.getProject());
        SearchCodeSnippetDialog main = new SearchCodeSnippetDialog();
        JComponent mainPanel = main.createCenterPanel();
        jbPopup = JBPopupFactory.getInstance()
                .createComponentPopupBuilder(mainPanel, main.getSearchTitleField())
                .setResizable(true)
                .setMovable(true)
                .setTitle("Search CodeSnippet")
                .setFocusable(true)
                .setCancelOnClickOutside(true)
                .setCancelButton(new IconButton("Cancel", AllIcons.Actions.Cancel))
                .setCancelCallback(() -> true)
                .setRequestFocus(true)
                .createPopup();
        jbPopup.showCenteredInCurrentWindow(UtilState.getInstance().getProject());
    }

    private class SearchCodeSnippetDialog {
        private final SearchTextField searchTitleField = new SearchTextField();
        private final DefaultListModel<CodeSnippetModel> listModel = new DefaultListModel<>();
        private final JBList<CodeSnippetModel> codeSnippetJBList = new JBList<>(listModel);
        private int myLockCounter;

        private int a = 0, b = 10;

        protected SearchCodeSnippetDialog() {
        }

        public SearchTextField getSearchTitleField() {
            return this.searchTitleField;
        }

        @Nullable
        protected JComponent createCenterPanel() {
            CodeSnippetModel moreModel = new CodeSnippetModel() {{
                setTitle("... more");
            }};
            JPanel mainPanel = new JPanel();
            mainPanel.setLayout(new BorderLayout());
            List<CodeSnippetModel> data = SQLiteUtil.getInstance().getTitleList(a, b);
            if (data.size() >= b) {
                data.add(moreModel);
            }
            InitJBList();
            InitSearchTextField();
            JBScrollPane scrollPane = new JBScrollPane(codeSnippetJBList);
            scrollPane.setPreferredSize(JBUI.size(670, 300));
            mainPanel.add(scrollPane, BorderLayout.CENTER);
            mainPanel.add(searchTitleField, BorderLayout.NORTH);
            // 初始化列表数据
            listModel.addAll(data);
            return mainPanel;
        }


        private @NotNull ListCellRenderer getListCellRenderer() {
            return new MyListRenderer();
        }

        private void filterList() {
            String searchText = searchTitleField.getText().toLowerCase();
            List<CodeSnippetModel> filteredData = new ArrayList<>(SQLiteUtil.getInstance().getTitleList(searchText));
            updateListData(filteredData);
        }

        private void updateListData(List<CodeSnippetModel> data) {
            listModel.clear();
            listModel.addAll(data);
        }

        private void appendListData(List<CodeSnippetModel> data, int index) {
            listModel.addAll(index, data);
        }

        private void InitJBList() {
            int visibleRowCount = 12;
            codeSnippetJBList.setVisibleRowCount(visibleRowCount);
            codeSnippetJBList.setCellRenderer(getListCellRenderer());
            codeSnippetJBList.setAutoscrolls(true);
            codeSnippetJBList.addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) {
                    codeSnippetJBListClick();
                }
            });
            codeSnippetJBList.setSelectionMode(2);
        }


        private void InitSearchTextField() {
            // 添加文本更改监听器，用于根据搜索框的内容过滤列表项
            searchTitleField.addDocumentListener(new DocumentAdapter() {
                @Override
                protected void textChanged(@NotNull DocumentEvent e) {
                    filterList();
                }
            });
        }


        private void codeSnippetJBListClick() {
            if (isLocked()) return;
            lock();
            try {
                if (codeSnippetJBList.getSelectedValue() != null) {
                    if (StringUtils.equals(codeSnippetJBList.getSelectedValue().getTitle(), "... more")) {
                        a += 10;
                        List<CodeSnippetModel> appendData = SQLiteUtil.getInstance().getTitleList(a, b);
                        final int index = listModel.indexOf(codeSnippetJBList.getSelectedValue());
                        if (appendData.size() < 10) {
                            listModel.removeElement(codeSnippetJBList.getSelectedValue());
                        }
                        appendListData(appendData, index);
                        codeSnippetJBList.setSelectedIndex(index);
                    } else {
                        ProgressManager.getInstance().run(new Task.Backgroundable(UtilState.getInstance().getProject(), "OpenSearchForm") {
                            @Override
                            public void run(@NotNull ProgressIndicator indicator) {
                                SwingUtilities.invokeLater(() -> {
                                    new CodeSnippetForm(codeSnippetJBList.getSelectedValue()).show();
                                    jbPopup.cancel();
                                });
                            }
                        });
                    }
                }
            } finally {
                unlock();
            }
        }


        private class MyListRenderer implements ListCellRenderer {
            final SimpleTextAttributes SMALL_LABEL_ATTRS = new SimpleTextAttributes(
                    SimpleTextAttributes.STYLE_SMALLER, UIUtil.getLabelDisabledForeground());
            final ListCellRenderer<Object> myMoreRenderer = new ColoredListCellRenderer<>() {
                @Override
                protected int getMinHeight() {
                    return -1;
                }

                @Override
                protected void customizeCellRenderer(@NotNull JList<?> list, Object value, int index, boolean selected, boolean hasFocus) {
                    setFont(UIUtil.getLabelFont().deriveFont(UIUtil.getFontSize(UIUtil.FontSize.SMALL)));
                    append(IdeBundle.message("search.everywhere.points.more"), SMALL_LABEL_ATTRS);
                    setIpad(JBInsets.create(1, 7));
                    setMyBorder(null);
                }
            };

            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                final JPanel panel = new JPanel(new BorderLayout());
                panel.setOpaque(true);
                panel.setBorder(JBUI.Borders.emptyRight(4));
                final Color bg = isSelected ? UIUtil.getListSelectionBackground(true) : UIUtil.getListBackground();
                panel.setBackground(bg);
                if (value instanceof CodeSnippetModel) {
                    CodeSnippetModel codeSnippetModelValue = (CodeSnippetModel) value;
                    if (StringUtils.equals("... more", codeSnippetModelValue.getTitle())) {
                        Component component = myMoreRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                        component.setPreferredSize(UIUtil.updateListRowHeight(component.getPreferredSize()));
                        return component;
                    }
                    final Color fg;
                    fg = isSelected ? JBUI.CurrentTheme.List.Selection.foreground(true) : UIUtil.getListForeground();
                    String title = codeSnippetModelValue.getTitle();
                    if (StringUtils.isNotEmpty(title)) {
                        if (title.length() > 40) {
                            title = title.substring(0, 40) + "...";
                        }
                    }
                    final JLabel actionLabel = new JLabel(title, null, LEFT);
                    actionLabel.setBackground(bg);
                    actionLabel.setForeground(fg);
                    actionLabel.setFont(actionLabel.getFont().deriveFont(Font.PLAIN));
                    actionLabel.setBorder(JBUI.Borders.emptyLeft(4));
                    panel.add(actionLabel, BorderLayout.WEST);
                    String description = codeSnippetModelValue.getDescribe();
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
                        panel.add(descriptionLabel, BorderLayout.EAST);
                    }
                }
                return panel;
            }
        }


        void lock() {
            myLockCounter++;
        }

        void unlock() {
            if (myLockCounter > 0) myLockCounter--;
        }

        boolean isLocked() {
            return myLockCounter > 0;
        }

    }
}

