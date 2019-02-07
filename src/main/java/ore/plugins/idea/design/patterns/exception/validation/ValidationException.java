package ore.plugins.idea.design.patterns.exception.validation;

import com.intellij.psi.PsiClass;
import ore.plugins.idea.design.patterns.exception.DesignPatternRuntimeException;

public class ValidationException extends DesignPatternRuntimeException {

    private PsiClass psiClass;

    public ValidationException(PsiClass psiClass, String message) {
        super(message);
        this.psiClass = psiClass;
    }

    public PsiClass getPsiClass() {
        return psiClass;
    }
}
