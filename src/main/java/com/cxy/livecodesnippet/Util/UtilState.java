package com.cxy.livecodesnippet.Util;

import com.cxy.livecodesnippet.model.CodeSnippetModel;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;

import java.util.ArrayList;
import java.util.List;

public class UtilState {

    private List<CodeSnippetModel> codeSnippetList = new ArrayList<>();
    private FileType javaFileType = null;

    private UtilState() {
    }

    private static class SingletonInstance {
        private static final UtilState INSTANCE = new UtilState();
    }

    public static UtilState getInstance() {
        return SingletonInstance.INSTANCE;
    }

    public void clearCodeSnippetList() {
        this.codeSnippetList.clear();
    }

    public void setCodeSnippetList(List<CodeSnippetModel> codeSnippetList) {
        this.codeSnippetList.addAll(codeSnippetList);
    }


    public Project getProject() {
        return ProjectManager.getInstance().getDefaultProject();
    }

    public FileType getJavaFileType() {
        if (javaFileType == null) {
            javaFileType = FileTypeManager.getInstance().getFileTypeByExtension("java");
        }
        return javaFileType;
    }

}
