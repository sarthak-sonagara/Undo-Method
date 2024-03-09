package com.sms.undomethod.service;

import com.intellij.openapi.components.Service;
import com.intellij.psi.PsiMethod;
import com.sms.undomethod.entity.Action;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

@Service
public final class RedoService {
    private static final Map<PsiMethod, Stack<Action>> methodsMap = new HashMap<>();
    private boolean isCodeChange;

    public boolean isCodeChange() {
        return isCodeChange;
    }

    public void setCodeChange(boolean codeChange) {
        isCodeChange = codeChange;
    }

    public boolean isRedoAvailable(PsiMethod method) {
        return methodsMap.containsKey(method);
    }
    public void putUndoContent(PsiMethod method, Action action){
        if(!isRedoAvailable(method)){
            methodsMap.put(method,new Stack<>());
        }
        methodsMap.get(method).push(action);
    }
    public Action getUndoContent(PsiMethod method) {
        Stack<Action> methodStack = methodsMap.get(method);
        Action top = methodStack.pop();
        if(methodStack.isEmpty()){
            methodsMap.remove(method);
        }
        return top;
    }
    public void removeStackIfPresent(PsiMethod method) {
        methodsMap.remove(method);
    }
}
