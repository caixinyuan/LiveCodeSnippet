package com.cxy.livecodetemplet.listener;

import com.cxy.livecodetemplet.Util.Util;
import com.cxy.livecodetemplet.Util.UtilState;
import com.cxy.livecodetemplet.model.CodeTempletModel;
import com.cxy.livecodetemplet.model.MarkdownTableModel;
import com.cxy.livecodetemplet.services.LiveCodeTempletService;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManagerListener;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.List;

public class LiveCodeTempletProjectManagerListener implements ProjectManagerListener {
    /**
     * 项目监听
     *
     * @param project
     */
    public void projectOpened(@NotNull Project project) {
        UtilState.getInstance().setProject(project);
        ProgressManager.getInstance().run(new Task.Backgroundable(UtilState.getInstance().getProject(), "正在解析模板") {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                new LiveCodeTempletService(project);
            }
        });

    }

}
