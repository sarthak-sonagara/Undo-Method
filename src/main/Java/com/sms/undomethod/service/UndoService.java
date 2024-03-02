package com.sms.undomethod.service;

import com.intellij.openapi.components.Service;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiTreeUtil;
import com.sms.undomethod.entity.Action;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

@Service
public final class UndoService {
    private static final Map<PsiMethod, Stack<Action>> methodsMap = new HashMap<>();
    private boolean isCodeChange;

    public boolean isCodeChange() {
        return isCodeChange;
    }

    public void setCodeChange(boolean codeChange) {
        isCodeChange = codeChange;
    }

    public boolean isUnchanged(@NotNull PsiMethod method){
        return !methodsMap.containsKey(method) || methodsMap.get(method).empty();
    }
    public void cleanUpFileMethods(PsiFile file){
        Collection<PsiMethod> methods = PsiTreeUtil.collectElementsOfType(file,PsiMethod.class);
        for(PsiMethod method : methods){
            methodsMap.remove(method);
        }
    }

    public void cleanUpAllMethods(){
        methodsMap.clear();
    }
    public void putOldAction(PsiMethod method, Action oldAction){
        if(!methodsMap.containsKey(method)){
            methodsMap.put(method,new Stack<>());
        }
        methodsMap.get(method).push(oldAction);
    }
    public Action getOldAction(PsiMethod method){
        if(!methodsMap.containsKey(method))
            return Action.noAction;
        Stack<Action> methodStack = methodsMap.get(method);
        if(methodStack.empty())
            return Action.noAction;
        return methodStack.pop();
    }
}


