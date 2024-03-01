package com.sms.undomethod.service;

import com.intellij.openapi.components.Service;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

@Service
public final class UndoService {
    private static final Map<PsiMethod, Stack<PsiElement>> methodsMap = new HashMap<>();
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

    private PsiElement getMethodBody(@NotNull PsiMethod method){
        if(method.getBody()==null){
            return null;
        }
        return method.getBody().copy();
    }
    public PsiElement getUndoContent(@NotNull PsiMethod method) {
        if(!methodsMap.containsKey(method))
            return getMethodBody(method);
        Stack<PsiElement> methodStack = methodsMap.get(method);
        if(methodStack.empty())
            return getMethodBody(method);
        return methodStack.pop();
    }
    public void putUndoContent(@NotNull PsiMethod method) {
        if(!methodsMap.containsKey(method)){
            methodsMap.put(method,new Stack<>());
        }
        methodsMap.get(method).push(getMethodBody(method));
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
}


