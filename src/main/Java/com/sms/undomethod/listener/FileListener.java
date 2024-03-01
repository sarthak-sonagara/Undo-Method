package com.sms.undomethod.listener;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.sms.undomethod.service.UndoService;
import org.jetbrains.annotations.NotNull;

public class FileListener implements FileEditorManagerListener {

    private final Project project;
    private final ChangeListener changeListener;

    public FileListener(Project project) {
        this.project = project;
        this.changeListener = new ChangeListener(project);
    }

    @Override
    public void fileOpened(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
        Document document = FileDocumentManager.getInstance().getDocument(file);
        if(document!=null)
            document.addDocumentListener(changeListener);
    }

    @Override
    public void fileClosed(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
        Document document = FileDocumentManager.getInstance().getDocument(file);
        if(document==null){
            return;
        }
        PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document);
        if(psiFile==null) {
            return;
        }
        ApplicationManager.getApplication().getService(UndoService.class).cleanUpMethods(psiFile);
        document.removeDocumentListener(changeListener);
    }
}
