package com.design.patterns.strategy;

import com.design.patterns.base.DesignPatternAction;
import com.design.patterns.base.dialog.InputValueDialog;
import com.design.patterns.base.dialog.MessageBoxDialog;
import com.design.patterns.base.dialog.SelectMembersDialog;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMember;
import com.intellij.psi.PsiMethod;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class StrategyAction extends DesignPatternAction {

    private static final String STRATEGY_DIALOG_TITLE = "Strategy Design Pattern";
    private static final String STRATEGY_METHODS_DIALOG_TEXT = "Methods to include:";
    private static final String STRATEGY_NAME_DIALOG_TEXT = "Give a name for the interface:";

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        PsiClass psiClass = getPsiClassFromContext(anActionEvent);
        InputValueDialog strategyNameDialog = new InputValueDialog(psiClass, STRATEGY_DIALOG_TITLE, STRATEGY_NAME_DIALOG_TEXT);
        strategyNameDialog.show();
        if (strategyNameDialog.isOK()) {
            String strategyName = strategyNameDialog.getInput().trim();
            if (strategyName.isEmpty()) {
                new MessageBoxDialog(psiClass, "Cannot accept empty name.").show();
            } else if (Arrays.stream(psiClass.getContainingFile().getContainingDirectory().getFiles())
                    .filter(psiFile -> psiFile.getName().contains(strategyName))
                    .collect(Collectors.toList()).size() > 0) {
                new MessageBoxDialog(psiClass, "There is already a file with the name: " + strategyName + ".").show();
            } else {
                Predicate<PsiMember> allMethodsExceptConstructors = psiMember -> {
                    PsiMethod psiMethod = (PsiMethod) psiMember;
                    return !psiMethod.isConstructor();
                };
                SelectMembersDialog strategyMethodsDialog = new SelectMembersDialog(psiClass, Arrays.asList(psiClass.getMethods()), allMethodsExceptConstructors, STRATEGY_DIALOG_TITLE, STRATEGY_METHODS_DIALOG_TEXT);
                strategyMethodsDialog.show();
                if (strategyMethodsDialog.isOK()) {
                    generateCode(psiClass, strategyName, strategyMethodsDialog.getSelectedPsiMembers().stream().map(psiMember -> (PsiMethod) psiMember).collect(Collectors.toList()));
                }
            }
        }
    }

    private void generateCode(PsiClass psiClass, String strategyName, List<PsiMethod> selectedMethods) {
        new WriteCommandAction.Simple(psiClass.getProject(), psiClass.getContainingFile()) {
            @Override
            protected void run() {
                new StrategyPatternGenerator(psiClass, strategyName, selectedMethods).generate();
            }
        }.execute();
    }
}
