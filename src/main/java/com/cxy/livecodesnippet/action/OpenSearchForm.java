package com.cxy.livecodesnippet.action;

import com.cxy.livecodesnippet.Util.SQLiteUtil;
import com.cxy.livecodesnippet.Util.UtilState;
import com.cxy.livecodesnippet.form.CodeSnippetForm;
import com.cxy.livecodesnippet.model.CodeSnippetModel;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.TitlePanel;
import com.intellij.openapi.ui.popup.IconButton;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.SearchTextField;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
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
        SearchCodeSnippetDialog main = new SearchCodeSnippetDialog();
        JComponent mainPanel = (JComponent) main.getContentPanel().getComponent(0);
        jbPopup = JBPopupFactory.getInstance()
                .createComponentPopupBuilder(mainPanel, mainPanel)
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

    private class SearchCodeSnippetDialog extends DialogWrapper {
        private SearchTextField searchTitleField = new SearchTextField();
        private DefaultListModel<CodeSnippetModel> listModel = new DefaultListModel<>();
        private JBList<CodeSnippetModel> codeSnippetJBList = new JBList<>(listModel);
        private int myLockCounter;

        private int a = 0, b = 10;

        protected SearchCodeSnippetDialog() {
            super(null);
            init();
            setTitle("Search Dialog");
            setModal(false);
        }

        @Nullable
        @Override
        protected JComponent createCenterPanel() {
            CodeSnippetModel moreModel = new CodeSnippetModel() {{
                setTitle("... more");
            }};
            JPanel mainPanel = new JPanel();
            mainPanel.setLayout(new BorderLayout());
            List<CodeSnippetModel> data = SQLiteUtil.getInstance().getTitleList(a, b);
            if (data.size() > b) {
                data.add(moreModel);
            }
            InitJBList();
            InitSearchTextField();
            JBScrollPane scrollPane = new JBScrollPane(codeSnippetJBList);
            mainPanel.add(scrollPane, BorderLayout.CENTER);
            mainPanel.add(searchTitleField, BorderLayout.NORTH);
            // 初始化列表数据
            listModel.addAll(data);
            return mainPanel;
        }


        private @NotNull ListCellRenderer getListCellRenderer() {
            return (list, value, index, isSelected, cellHasFocus) -> {
                final JPanel panel = new JPanel(new BorderLayout());
                panel.setOpaque(true);
                panel.setBorder(JBUI.Borders.emptyRight(4));
                final Color bg = isSelected ? UIUtil.getListSelectionBackground(true) : UIUtil.getListBackground();
                panel.setBackground(bg);
                if (value instanceof CodeSnippetModel) {
                    CodeSnippetModel codeSnippetModelValue = (CodeSnippetModel) value;
                    if (StringUtils.equals("... more", codeSnippetModelValue.getTitle())) {
                        JBLabel moreLabel = new JBLabel("... more");
                        moreLabel.setForeground(new Color(223, 225, 229, 255));
                        moreLabel.setBackground(new Color(43, 45, 48, 255));
                        moreLabel.setFont(new Font("Inter", 0, 16));
                        panel.add(moreLabel, BorderLayout.AFTER_LAST_LINE);
                        return panel;
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
                    actionLabel.setFont(actionLabel.getFont().deriveFont(Font.BOLD));
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
            };
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

        private void appendListData(CodeSnippetModel data) {
            listModel.addElement(data);
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
                                    close(0);
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
}

