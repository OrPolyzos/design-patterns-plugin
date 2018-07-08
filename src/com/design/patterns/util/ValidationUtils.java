package com.design.patterns.util;

import com.design.patterns.base.dialog.MessageBoxDialog;
import com.intellij.psi.PsiClass;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ValidationUtils {

    public static boolean validateClassesListIfNonEmpty(PsiClass psiClass, List<PsiClass> classesList, String message) {
        if (classesList.isEmpty()) {
            new MessageBoxDialog(psiClass, message);
            return false;
        }
        return true;
    }

    public static boolean validateClassesListForExactSize(PsiClass psiClass, List<PsiClass> classesList, int exactSize, String message) {
        if (classesList.size() != exactSize) {
            new MessageBoxDialog(psiClass, message);
            return false;
        }
        return true;
    }

    public static boolean validateClassNameForDuplicate(PsiClass psiClass, String className, String message){
        if (Arrays.stream(psiClass.getContainingFile().getContainingDirectory().getFiles())
                .filter(psiFile -> psiFile.getName().equals(className))
                .collect(Collectors.toList()).size() > 0) {
            new MessageBoxDialog(psiClass, message);
            return false;
        }
        return true;
    }
}
