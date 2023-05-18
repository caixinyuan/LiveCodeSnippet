package com.cxy.livecodetemplet.test;

import com.cxy.livecodetemplet.Util.UtilState;
import com.intellij.lang.Language;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.fileEditor.*;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileAdapter;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import org.intellij.plugins.markdown.ui.preview.MarkdownPreviewFileEditor;
import org.intellij.plugins.markdown.ui.preview.MarkdownPreviewFileEditorProvider;
import org.intellij.plugins.markdown.ui.preview.MarkdownTextEditorProvider;
import org.intellij.plugins.markdown.ui.preview.html.MarkdownUtil;
import org.intellij.plugins.markdown.ui.split.SplitTextEditorProvider;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.nio.file.Path;

public class TestMarkdownPreview2 extends AnAction {

    @NotNull
    protected final FileEditorProvider mySecondProvider = new MarkdownPreviewFileEditorProvider();

    @Override
    public void actionPerformed(AnActionEvent e) {
        VirtualFile virtualFile = VirtualFileManager.getInstance().findFileByNioPath(Path.of("/home/caixy/code/Temp/LiveCodeTemplet/CodeTemplet.md"));
        Document document = EditorFactory.getInstance().createDocument("SDSAGDJASDSADASDASD");

        VirtualFile virtualFile2 = FileDocumentManager.getInstance().getFile(document);

        PsiFile psiFile = PsiFileFactory.getInstance(UtilState.getInstance().getProject()).createFileFromText("MDRE", Language.findLanguageByID("JAVA"), "TTTTXTXTXTXTXTXXT");

        VirtualFile virtualFile3 = psiFile.getVirtualFile();

        JFrame frame = new JFrame("TestMarkdownPreview");
        JPanel jPanel = new JPanel();
        MarkdownPreviewFileEditor markdownPreviewFileEditor = new MarkdownPreviewFileEditor(UtilState.getInstance().getProject(), virtualFile);
        Editor editor = FileEditorManager.getInstance(UtilState.getInstance().getProject()).getSelectedTextEditor();
        markdownPreviewFileEditor.setMainEditor(editor);

        JComponent jComponent = markdownPreviewFileEditor.getComponent();
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem menuItem = new JMenuItem("Menu Item");
        popupMenu.add(menuItem);
        jComponent.setComponentPopupMenu(popupMenu);

        jPanel.add(jComponent);
        jComponent.setEnabled(true);
        frame.setContentPane(jPanel);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
