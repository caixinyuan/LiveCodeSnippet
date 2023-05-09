package com.cxy.livecodetemplet.action;

import com.cxy.livecodetemplet.Util.UtilState;
import com.cxy.livecodetemplet.form.CodeTempletForm;
import com.cxy.livecodetemplet.model.SnippetChooseByNameModel;
import com.cxy.livecodetemplet.provider.SnippetChooseByNameItemProvider;
import com.intellij.ide.util.gotoByName.ChooseByNamePopup;
import com.intellij.ide.util.gotoByName.ChooseByNamePopupComponent;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.DumbAware;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class OpenSearchForm extends AnAction implements DumbAware {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        SnippetChooseByNameModel chooseByNameModel = new SnippetChooseByNameModel();
        ChooseByNamePopup popup = ChooseByNamePopup.createPopup(UtilState.getInstance().getProject(), chooseByNameModel, new SnippetChooseByNameItemProvider());
        popup.setShowListForEmptyPattern(true);
        popup.setAdText("按Esc取消选择");
        popup.invoke(new ChooseByNamePopupComponent.Callback() {
            //在选择了搜索结果后会被调用，用于处理所选元素。
            @Override
            public void elementChosen(Object element) {
                ProgressManager.getInstance().run(new Task.Backgroundable(UtilState.getInstance().getProject(), "OpenSearchForm") {
                    @Override
                    public void run(@NotNull ProgressIndicator indicator) {
                        SwingUtilities.invokeLater(() -> {
                            CodeTempletForm.showUI(element);
                        });
                    }
                });
            }
        }, ModalityState.any(), true);
    }
}
