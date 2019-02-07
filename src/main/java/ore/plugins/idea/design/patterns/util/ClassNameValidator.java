package ore.plugins.idea.design.patterns.util;

import com.intellij.psi.PsiClass;
import ore.plugins.idea.design.patterns.exception.validation.DuplicateNameException;
import ore.plugins.idea.design.patterns.exception.validation.InvalidNameException;

import java.util.Arrays;

public interface ClassNameValidator {
    String JAVA_FILE_EXTENSION = ".java";

    default void makeSureSelectedNameIsNotEmpty(PsiClass psiClass, String selectedName) {
        if (selectedName == null || selectedName.length() <= 0) throw new InvalidNameException(psiClass, selectedName);
    }

    default void makeSureFileDoesNotExist(PsiClass psiClass, String fileName) {
        if (Arrays.stream(psiClass.getContainingFile().getContainingDirectory().getFiles())
                .anyMatch(psiFile -> psiFile.getName().equals(fileName))) throw new DuplicateNameException(psiClass, fileName);
    }

    default String validateClassNameOrThrow(PsiClass psiClass, String selectedName) {
        makeSureSelectedNameIsNotEmpty(psiClass, selectedName);
        makeSureFileDoesNotExist(psiClass, selectedName.concat(JAVA_FILE_EXTENSION));
        return selectedName;
    }
}
