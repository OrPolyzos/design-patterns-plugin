package com.design.patterns.creational.builder;

import com.design.patterns.base.DesignPatternAction;
import com.design.patterns.base.dialog.SelectStuffDialog;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;

import java.util.Arrays;
import java.util.List;

public class BuilderAction extends DesignPatternAction {

    private static final String BUILDER_DIALOG_TITLE = "Strategy Design Pattern";
    private static final String BUILDER_FIELDS_DIALOG_TEXT = "Fields to include:";

    @Override
    public void actionPerformed(AnActionEvent e) {
        PsiClass psiClass = getPsiClassFromContext(e);
        SelectStuffDialog<PsiField> builderDialog = new SelectStuffDialog<>(psiClass, Arrays.asList(psiClass.getFields()), psiField -> true, BUILDER_DIALOG_TITLE, BUILDER_FIELDS_DIALOG_TEXT);
        if (builderDialog.isOK()) {
            generateCode(psiClass, builderDialog.getSelectedStuff());
        }
    }

    private void generateCode(PsiClass psiClass, List<PsiField> selectedFields) {
        new WriteCommandAction.Simple(psiClass.getProject(), psiClass.getContainingFile()) {
            @Override
            protected void run() {
                psiClass.add(new BuilderPatternGenerator(psiClass, selectedFields).generate());
            }
        }.execute();
    }

}
