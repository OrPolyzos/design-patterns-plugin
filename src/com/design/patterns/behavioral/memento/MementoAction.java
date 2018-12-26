package com.design.patterns.behavioral.memento;

import com.design.patterns.base.DesignPatternAction;
import com.design.patterns.base.dialog.InputValueDialog;
import com.design.patterns.base.dialog.SelectStuffDialog;
import com.design.patterns.util.ValidationUtils;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiModifier;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;

public class MementoAction extends DesignPatternAction {

    private static final String CARETAKER_SUFFIX = "CareTaker";
    private static final String JAVA_FILE_EXTENSION = ".java";
    private static final String MEMENTO_DIALOG_TITLE = "Memento Design Pattern";
    private static final String MEMENTO_FIELDS_DIALOG_TEXT = "Fields to include:";
    private static final String MEMENTO_CARETAKER_NAME_DIALOG_TEXT = "Give a name for the Caretaker class (\"Caretaker\" suffix will be automatically added):";
    private static final String EMPTY_FIELD_LIST_ERROR_MESSAGE = "You must choose at least one field";
    private static final String DUPLICATE_NAME_ERROR_MESSAGE = "There is already a file with the name: ";


    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        PsiClass psiClass = getPsiClassFromContext(anActionEvent);
        SelectStuffDialog<PsiField> mementoFieldsDialog = new SelectStuffDialog<>(psiClass, Arrays.asList(psiClass.getFields()), psiField -> !psiField.hasModifierProperty(PsiModifier.STATIC), MEMENTO_DIALOG_TITLE, MEMENTO_FIELDS_DIALOG_TEXT, ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        if (mementoFieldsDialog.isOK()) {
            List<PsiField> mementoFields = mementoFieldsDialog.getSelectedStuff();
            if (ValidationUtils.validatePsiFieldListIfNonEmpty(psiClass, mementoFields, EMPTY_FIELD_LIST_ERROR_MESSAGE)) {
                InputValueDialog careTakerNameDialog = new InputValueDialog(psiClass, MEMENTO_DIALOG_TITLE, MEMENTO_CARETAKER_NAME_DIALOG_TEXT);
                if (careTakerNameDialog.isOK()) {
                    String careTakerClassName = careTakerNameDialog.getInput();
                    careTakerClassName = careTakerClassName.replace(CARETAKER_SUFFIX, "");
                    careTakerClassName = careTakerClassName + CARETAKER_SUFFIX;
                    String careTakerFileName = careTakerClassName + JAVA_FILE_EXTENSION;
                    if (ValidationUtils.validateClassNameForDuplicate(psiClass, careTakerFileName, DUPLICATE_NAME_ERROR_MESSAGE + careTakerFileName)) {
                        generateCode(psiClass, careTakerClassName, mementoFields);
                    }
                }
            }
        }
    }

    private void generateCode(PsiClass psiClass, String caretakerClassName, List<PsiField> mementoFields) {
        new WriteCommandAction.Simple(psiClass.getProject(), psiClass.getContainingFile()) {
            @Override
            protected void run() {
                new MementoPatternGenerator(psiClass, caretakerClassName, mementoFields).generate();
            }
        }.execute();
    }

}
