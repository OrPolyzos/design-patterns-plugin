package ore.plugins.idea.design.patterns.utils;

import com.intellij.psi.PsiClass;
import ore.plugins.idea.design.patterns.exception.DuplicateNameException;
import ore.plugins.idea.design.patterns.exception.InvalidNameException;

import java.util.Arrays;

public interface ClassNameValidator {
    String JAVA_FILE_EXTENSION = ".java";

    default String validateClassNameOrThrow(PsiClass psiClass, String selectedName) {
        makeSureSelectedNameIsNotEmpty(selectedName);
        makeSureFileDoesNotExist(psiClass, selectedName.concat(JAVA_FILE_EXTENSION));
        return selectedName;
    }

    default void makeSureSelectedNameIsNotEmpty(String selectedName) {
        if (selectedName == null || selectedName.length() <= 0) throw new InvalidNameException(selectedName);
    }

    default void makeSureFileDoesNotExist(PsiClass psiClass, String fileName) {
        if (Arrays.stream(psiClass.getContainingFile().getContainingDirectory().getFiles())
                .anyMatch(psiFile -> psiFile.getName().equals(fileName))) throw new DuplicateNameException(fileName);
    }
}
