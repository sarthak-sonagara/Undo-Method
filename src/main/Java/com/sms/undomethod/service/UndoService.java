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
        if(isUnchanged(method))
            return Action.noAction;
        Stack<Action> methodStack = methodsMap.get(method);
        Action top = methodStack.pop();
        if(top.isSingleInsertion()){
            int length = 1;
            Action last = top;
            while(!methodStack.empty()){
                Action cur = methodStack.peek();
                if(!cur.isSingleInsertion() && cur.getRelativeStart()!=last.getRelativeStart()+1)
                    break;
                length++;
                last = methodStack.pop();
            }
            return new Action(last.getRelativeStart(),length,"");
        }
        else if(top.isSingleDeletion()){
            StringBuilder content = new StringBuilder(top.getContent());
            Action last = top;
            while(!methodStack.empty()){
                Action cur = methodStack.peek();
                if(!cur.isSingleDeletion() && cur.getRelativeStart()!=last.getRelativeStart()-1){
                    break;
                }
                content.append(cur.getContent());
                last = methodStack.pop();
            }
            return new Action(top.getRelativeStart(),0,content);
        }
        return top;
    }
}


