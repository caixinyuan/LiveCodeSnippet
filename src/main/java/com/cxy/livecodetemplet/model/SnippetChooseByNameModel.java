package com.cxy.livecodetemplet.model;

import com.cxy.livecodetemplet.Util.UtilState;
import com.intellij.ide.util.gotoByName.ChooseByNameModel;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.util.NlsSafe;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static javax.swing.SwingConstants.LEFT;

public class SnippetChooseByNameModel implements ChooseByNameModel {

    /**
     * 显示在搜索框中的提示文本。
     *
     * @return
     */
    @Override
    public @Nls(capitalization = Nls.Capitalization.Sentence) String getPromptText() {
        return "请输入搜索关键字";
    }

    @Override
    public @NotNull @NlsContexts.Label String getNotInMessage() {
        return "getNotInMessage";
    }

    @Override
    public @NotNull @NlsContexts.Label String getNotFoundMessage() {
        return "NotFound";
    }

    @Override
    public @Nullable @NlsContexts.Label String getCheckBoxName() {
        return null;
    }

    @Override
    public boolean loadInitialCheckBoxState() {
        return true;
    }

    @Override
    public void saveInitialCheckBoxState(boolean state) {

    }

    @Override
    public @NotNull ListCellRenderer getListCellRenderer() {

        return (list, value, index, isSelected, cellHasFocus) -> {

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
                panel.add(actionLabel, BorderLayout.WEST);
                String description = ((CodeTempletModel) value).getCodeTemplet();
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
                    panel.setToolTipText(description);
                }
            } else {
                // E.g. "..." item
                @NlsSafe String text = value.toString();
                final JLabel actionLabel = new JLabel(text, null, LEFT);
                actionLabel.setBackground(bg);
                actionLabel.setForeground(UIUtil.getListForeground());
                actionLabel.setFont(actionLabel.getFont().deriveFont(Font.PLAIN));
                actionLabel.setBorder(JBUI.Borders.emptyLeft(4));
                panel.add(actionLabel, BorderLayout.WEST);
            }
            return panel;
        };
    }

    @Override
    public String @NotNull @Nls [] getNames(boolean checkBoxState) {
        List<String> codeTagList = new ArrayList<>();
        List<CodeTempletModel> codeTempletList = UtilState.getInstance().getCodeTempletList();
        codeTempletList.forEach(i -> codeTagList.addAll(i.getTag()));
        return codeTagList.toArray(new String[0]);
    }

    @Override
    public Object @NotNull [] getElementsByName(@NotNull String name, boolean checkBoxState, @NotNull String pattern) {
        return new String[]{"11111", "22222222"};
    }

    @Override
    public @Nullable String getElementName(@NotNull Object element) {
        return "null;";
    }

    @Override
    public String @NotNull [] getSeparators() {
        return new String[0];
    }

    @Override
    public @Nullable String getFullName(@NotNull Object element) {
        return null;
    }

    @Override
    public @Nullable @NonNls String getHelpId() {
        return null;
    }

    @Override
    public boolean willOpenEditor() {
        return false;
    }

    /**
     * 指示是否使用中间匹配模式进行搜索。
     * 当该属性为 true 时，选择项的名称将被匹配，只要搜索字符串出现在名称的中间位置即可被搜索到。
     * 当该属性为 false 时，选择项的名称将被完全匹配，只有当搜索字符串与名称完全匹配时才会被搜索到
     *
     * @return
     */
    @Override
    public boolean useMiddleMatching() {
        return false;
    }
}
