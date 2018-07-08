package com.design.patterns.creational.factory;

import com.design.patterns.base.DesignPatternAction;
import com.design.patterns.base.dialog.SelectStuffDialog;
import com.design.patterns.util.GeneratorUtils;
import com.design.patterns.util.ValidationUtils;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.PsiClass;

import java.util.List;
import java.util.stream.Collectors;

public class FactoryAction extends DesignPatternAction {

    private static final String FACTORY_DIALOG_TITLE = "Factory Design Pattern";
    private static final String FACTORY_INTERFACE_CHOICE_DIALOG_TEXT = "Based on interface:";
    private static final String FACTORY_IMPLEMENTORS_CHOICE_DIALOG_TEXT = "Implementors to include:";
    private static final String EMPTY_INTERFACES_LIST_ERROR_MESSAGE = "The class does not implement any interfaces.";
    private static final String EXACT_SIZE_INTERFACES_LIST_ERROR_MESSAGE = "You must choose exactly one interface.";
    private static final String EMPTY_IMPLEMENTORS_LIST_ERROR_MESSAGE = "You must choose at lease one implementor.";

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        PsiClass psiClass = getPsiClassFromContext(anActionEvent);
        List<PsiClass> interfacesList = GeneratorUtils.getInterfaces(psiClass);
        if (ValidationUtils.validateClassesListIfNonEmpty(psiClass, interfacesList, EMPTY_INTERFACES_LIST_ERROR_MESSAGE)) {
            SelectStuffDialog<PsiClass> factoryInterfaceChoiceDialog = new SelectStuffDialog<>(psiClass, interfacesList, candidateInterface -> true, FACTORY_DIALOG_TITLE, FACTORY_INTERFACE_CHOICE_DIALOG_TEXT);
            if (factoryInterfaceChoiceDialog.isOK()) {
                List<PsiClass> selectedInterfaces = factoryInterfaceChoiceDialog.getSelectedStuff();
                if (ValidationUtils.validateClassesListForExactSize(psiClass, selectedInterfaces, 1, EXACT_SIZE_INTERFACES_LIST_ERROR_MESSAGE)) {
                    PsiClass selectedInterface = selectedInterfaces.get(0);
                    List<PsiClass> candidateImplementors = getCandidateImplementors(psiClass, selectedInterface);
                    SelectStuffDialog<PsiClass> factoryImplementorsChoiceDialog = new SelectStuffDialog<>(psiClass, candidateImplementors, candidateImplementor -> true, FACTORY_DIALOG_TITLE, FACTORY_IMPLEMENTORS_CHOICE_DIALOG_TEXT);
                    if (factoryImplementorsChoiceDialog.isOK() && ValidationUtils.validateClassesListIfNonEmpty(psiClass, factoryImplementorsChoiceDialog.getSelectedStuff(), EMPTY_IMPLEMENTORS_LIST_ERROR_MESSAGE)) {
                        generateCode(psiClass, selectedInterface, factoryImplementorsChoiceDialog.getSelectedStuff());
                    }
                }
            }
        }
    }

    private List<PsiClass> getCandidateImplementors(PsiClass psiClass, PsiClass selectedInterface) {
        return GeneratorUtils.getAllClassesStartingWith(psiClass.getProject(), "")
                .stream()
                .filter(candidateImplementor ->
                        GeneratorUtils.getInterfaces(candidateImplementor).stream()
                                .map(PsiClass::getName)
                                .collect(Collectors.toList())
                                .contains(selectedInterface.getName()))
                .collect(Collectors.toList());
    }

    private void generateCode(PsiClass psiClass, PsiClass selectedInterface, List<PsiClass> selectedImplementors) {
        new WriteCommandAction.Simple(psiClass.getProject(), psiClass.getContainingFile()) {
            @Override
            protected void run() {
                new FactoryPatternGenerator(psiClass, selectedInterface, selectedImplementors).generate();
            }
        }.execute();
    }
}