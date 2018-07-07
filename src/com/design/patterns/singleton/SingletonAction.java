package com.design.patterns.singleton;

import com.design.patterns.base.DesignPatternAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.PsiClass;

public class SingletonAction extends DesignPatternAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        PsiClass psiClass = getPsiClassFromContext(e);
        generateCode(psiClass);
    }

    private void generateCode(PsiClass psiClass) {
        new WriteCommandAction.Simple(psiClass.getProject(), psiClass.getContainingFile()) {
            @Override
            protected void run() {
                new SingletonPatternGenerator(psiClass).generate();
            }
        }.execute();
    }
}
