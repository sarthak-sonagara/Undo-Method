package com.sms.undomethod.action;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.sms.undomethod.service.UndoService;
import org.jetbrains.annotations.NotNull;

public class UndoAction extends AnAction {

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        e.getPresentation().setEnabledAndVisible(editor!=null);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        Editor editor = anActionEvent.getData(CommonDataKeys.EDITOR);
        PsiFile file = anActionEvent.getData(CommonDataKeys.PSI_FILE);
        if(editor==null || file==null){
            return;
        }
        int offset = editor.getCaretModel().getOffset();
        PsiElement element = file.findElementAt(offset);
        if(element==null){
            return;
        }
        // get old content
        PsiMethod method = PsiTreeUtil.getParentOfType(element,PsiMethod.class);
        if(method==null || method.getBody()==null){
            return;
        }
        Project project = anActionEvent.getProject();
        if(project==null){
            return;
        }
        UndoService undoService = ApplicationManager.getApplication().getService(UndoService.class);
        if(undoService.isUnchanged(method)){
            return;
        }
        PsiElement oldContent = undoService.getUndoContent(method);
        // write old content
        WriteCommandAction.runWriteCommandAction(project, () -> {
            undoService.setCodeChange(true);
            method.getBody().replace(oldContent);
        });
        //save document
        Document document = editor.getDocument();
        FileDocumentManager.getInstance().saveDocument(document);
    }
}
