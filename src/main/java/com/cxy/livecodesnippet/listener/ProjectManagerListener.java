package com.cxy.livecodesnippet.listener;

import com.cxy.livecodesnippet.Util.UtilState;
import com.cxy.livecodesnippet.services.LiveCodeSnippetProjectService;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class ProjectManagerListener implements com.intellij.openapi.project.ProjectManagerListener {
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
                new LiveCodeSnippetProjectService(project);
            }
        });

    }

}
