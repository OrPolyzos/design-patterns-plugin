package ore.plugins.idea.design.patterns.exception.validation;

import com.intellij.psi.PsiClass;

public class InvalidNameException extends ValidationException {

    private static final String MESSAGE_TEMPLATE = "Class name '%s' is invalid.";

    public InvalidNameException(PsiClass psiClass, String selectedName) {
        super(psiClass, String.format(MESSAGE_TEMPLATE, selectedName));
    }
}
