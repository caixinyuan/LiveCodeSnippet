package com.cxy.livecodetemplet.Util;

import com.cxy.livecodetemplet.model.CodeTempletModel;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;

import java.util.ArrayList;
import java.util.List;

public class UtilState {

    private List<CodeTempletModel> codeTempletList = new ArrayList<>();

    private Project project;
    private final FileType javaFileType = FileTypeManager.getInstance().getFileTypeByExtension("java");

    private UtilState() {
    }

    private static class SingletonInstance {
        private static final UtilState INSTANCE = new UtilState();
    }

    public static UtilState getInstance() {
        return SingletonInstance.INSTANCE;
    }

    public void clearCodeTempletList() {
        this.codeTempletList.clear();
    }

    public void setCodeTempletList(List<CodeTempletModel> codeTempletList) {
        this.codeTempletList.addAll(codeTempletList);
    }

    public List<CodeTempletModel> getCodeTempletList() {
        return codeTempletList;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Project getProject() {
        if (project == null) {
            project = ProjectManager.getInstance().getDefaultProject();
        }
        return project;
    }

    public FileType getJavaFileType() {
        return javaFileType;
    }

}
