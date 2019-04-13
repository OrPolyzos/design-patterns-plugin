package ore.plugins.idea.design.patterns.wrapper;

import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class PsiClassWrapper {

    private static final String CONSTRUCTOR_TEMPLATE = "%s(%s){%s}";
    private static final String CONSTRUCTOR_ARGUMENT_TEMPLATE = "%s %s";
    private static final String CONSTRUCTOR_ASSIGNMENT_TEMPLATE = "this.%s = %s;";

    private PsiClass psiClass;
    private List<PsiField> constructorArguments;

    public PsiClassWrapper(PsiClass psiClass, List<PsiField> constructorArguments) {
        this.psiClass = Objects.requireNonNull(psiClass);
        this.constructorArguments = constructorArguments;
    }

    public PsiMethod getConstructor() {
        String constructorArgsPart = constructorArguments.stream()
                .map(psiField -> {
                    PsiFieldWrapper psiFieldWrapper = new PsiFieldWrapper(psiField);
                    return String.format(CONSTRUCTOR_ARGUMENT_TEMPLATE, psiFieldWrapper.getFieldType(), psiFieldWrapper.getLowerCaseFieldName());
                })
                .collect(Collectors.joining(", "));
        String constructorArgsAssignPart = constructorArguments.stream()
                .map(psiField -> {
                    PsiFieldWrapper psiFieldWrapper = new PsiFieldWrapper(psiField);
                    return String.format(CONSTRUCTOR_ASSIGNMENT_TEMPLATE, psiFieldWrapper.getLowerCaseFieldName(), psiFieldWrapper.getLowerCaseFieldName());
                })
                .collect(Collectors.joining(""));
        String constructor = String.format(CONSTRUCTOR_TEMPLATE, psiClass.getName(), constructorArgsPart, constructorArgsAssignPart);
        return JavaPsiFacade.getElementFactory(psiClass.getProject()).createMethodFromText(constructor, psiClass);
    }
}
