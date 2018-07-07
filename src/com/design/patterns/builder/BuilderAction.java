package com.design.patterns.builder;

import com.design.patterns.base.DesignPatternAction;
import com.design.patterns.base.dialog.SelectMembersDialog;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMember;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class BuilderAction extends DesignPatternAction {

    private static final String BUILDER_DIALOG_TITLE = "Strategy Design Pattern";
    private static final String BUILDER_FIELDS_DIALOG_TEXT = "Fields to include:";

    @Override
    public void actionPerformed(AnActionEvent e) {
        PsiClass psiClass = getPsiClassFromContext(e);
        Predicate<PsiMember> allFields = psiMember -> true;

        SelectMembersDialog builderDialog = new SelectMembersDialog(psiClass, Arrays.asList(psiClass.getFields()), allFields, BUILDER_DIALOG_TITLE, BUILDER_FIELDS_DIALOG_TEXT);
        builderDialog.show();
        if (builderDialog.isOK()) {
            generateCode(psiClass, builderDialog.getSelectedPsiMembers().stream().map(psiMember -> (PsiField) psiMember).collect(Collectors.toList()));
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
