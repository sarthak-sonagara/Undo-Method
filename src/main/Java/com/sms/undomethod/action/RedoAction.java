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
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiTreeUtil;
import com.sms.undomethod.entity.Action;
import com.sms.undomethod.service.RedoService;
import com.sms.undomethod.service.UndoService;
import org.jetbrains.annotations.NotNull;

public class RedoAction extends AnAction {

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
        RedoService redoService = ApplicationManager.getApplication().getService(RedoService.class);
        e.getPresentation().setEnabledAndVisible(method!=null && redoService.isRedoAvailable(method));
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
        RedoService redoService = ApplicationManager.getApplication().getService(RedoService.class);
        assert method != null;
        redoService.setCodeChange(true);
        Document document = editor.getDocument();
        Action userAction = redoService.getUndoContent(method);
        int startOffset = method.getTextRange().getStartOffset() + userAction.getRelativeStart();
        // redo
        WriteCommandAction.runWriteCommandAction(project, () ->
                document.replaceString(startOffset, startOffset+userAction.getOldContent().length(), userAction.getNewContent()));
        //set caret position
        editor.getCaretModel().moveToOffset(startOffset+userAction.getNewContent().length());
        //save document
        FileDocumentManager.getInstance().saveDocument(document);
    }
}
