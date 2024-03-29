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
import com.sms.undomethod.entity.Action;
import com.sms.undomethod.service.RedoService;
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
        PsiFile file = e.getData(CommonDataKeys.PSI_FILE);
        if(editor==null || file==null){
            return;
        }
        int offset = editor.getCaretModel().getOffset();
        PsiElement element = file.findElementAt(offset);
        if(element==null){
            return;
        }
        PsiMethod method = PsiTreeUtil.getParentOfType(element,PsiMethod.class);
        UndoService undoService = ApplicationManager.getApplication().getService(UndoService.class);
        e.getPresentation().setEnabledAndVisible(method!=null && undoService.isUndoAvailable(method));
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        Editor editor = anActionEvent.getData(CommonDataKeys.EDITOR);
        PsiFile file = anActionEvent.getData(CommonDataKeys.PSI_FILE);
        assert editor != null;
        int offset = editor.getCaretModel().getOffset();
        assert file != null;
        PsiElement element = file.findElementAt(offset);
        PsiMethod method = PsiTreeUtil.getParentOfType(element,PsiMethod.class);
        Project project = anActionEvent.getProject();
        if(project==null){
            return;
        }
        UndoService undoService = ApplicationManager.getApplication().getService(UndoService.class);
        RedoService redoService = ApplicationManager.getApplication().getService(RedoService.class);
        assert method != null;
        // get old content
        Action userAction = undoService.getOldAction(method);
        Document document = editor.getDocument();
        int startOffset = method.getTextRange().getStartOffset() + userAction.getRelativeStart();
        // putting redo action
        redoService.putUndoContent(method,userAction);
        // write old content
        undoService.setCodeChange(true);
        WriteCommandAction.runWriteCommandAction(project, () ->
                document.replaceString(startOffset, startOffset+userAction.getNewContent().length(), userAction.getOldContent()));
        //set caret position
        editor.getCaretModel().moveToOffset(startOffset+userAction.getOldContent().length());
        //save document
        FileDocumentManager.getInstance().saveDocument(document);
    }
}
