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
    private static final String BUILDER_MANDATORY_FIELDS_DIALOG_TEXT = "Mandatory fields:";

    @Override
    public void actionPerformed(AnActionEvent e) {
        PsiClass psiClass = getPsiClassFromContext(e);
        SelectStuffDialog<PsiField> includedFieldsDialog = new SelectStuffDialog<>(psiClass, Arrays.asList(psiClass.getFields()), psiField -> true, BUILDER_DIALOG_TITLE, BUILDER_FIELDS_DIALOG_TEXT);
        if (includedFieldsDialog.isOK()) {
            SelectStuffDialog<PsiField> mandatoryFieldsDialog = new SelectStuffDialog<>(psiClass, includedFieldsDialog.getSelectedStuff(), psiField -> true, BUILDER_DIALOG_TITLE, BUILDER_MANDATORY_FIELDS_DIALOG_TEXT);
            if (mandatoryFieldsDialog.isOK()) {
                generateCode(psiClass, includedFieldsDialog.getSelectedStuff(), mandatoryFieldsDialog.getSelectedStuff());
            }
        }
    }

    private void generateCode(PsiClass psiClass, List<PsiField> includedFields, List<PsiField> mandatoryFields) {
        new WriteCommandAction.Simple(psiClass.getProject(), psiClass.getContainingFile()) {
            @Override
            protected void run() {
                new BuilderPatternGenerator(psiClass, includedFields, mandatoryFields).generate();
            }
        }.execute();
    }

}
