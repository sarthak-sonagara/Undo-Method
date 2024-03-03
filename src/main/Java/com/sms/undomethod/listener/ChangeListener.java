package com.sms.undomethod.listener;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtil;
import com.sms.undomethod.entity.Action;
import com.sms.undomethod.service.UndoService;
import org.jetbrains.annotations.NotNull;

public final class ChangeListener implements DocumentListener{
    private final Project project;
    public ChangeListener(Project project) {
        this.project = project;
    }
    @Override
    public void beforeDocumentChange(@NotNull DocumentEvent event) {
        Document document = event.getDocument();
        int eventOffset = event.getOffset();
        PsiFile file = PsiDocumentManager.getInstance(project).getPsiFile(document);
        if(file==null)
            return;
        PsiElement element = PsiUtil.getElementAtOffset(file,eventOffset);
        PsiMethod method = PsiTreeUtil.getParentOfType(element,PsiMethod.class);
        if(method==null)
            return;
        UndoService undoService = ApplicationManager.getApplication().getService(UndoService.class);
        if(undoService.isCodeChange()) {
            undoService.setCodeChange(false);
            return;
        }
        int methodOffset = method.getTextRange().getStartOffset();
        int relativeOffset = eventOffset - methodOffset;
        Action oldAction = new Action(relativeOffset,event.getNewLength(), event.getOldFragment());
        undoService.putOldAction(method, oldAction);

    }
}