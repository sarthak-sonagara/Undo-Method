package com.sms.undomethod.action;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.sms.undomethod.service.UndoService;
import org.jetbrains.annotations.NotNull;

public class CleanUpAction extends AnAction {
    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        ApplicationManager.getApplication().getService(UndoService.class).cleanUpAllMethods();
    }
}
