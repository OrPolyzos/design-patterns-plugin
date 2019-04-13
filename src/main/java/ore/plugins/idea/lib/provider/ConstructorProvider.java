package ore.plugins.idea.lib.provider;

import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;

import java.util.List;
import java.util.stream.Collectors;

public interface ConstructorProvider {

    String CONSTRUCTOR_TEMPLATE = "%s(%s){%s}";
    String CONSTRUCTOR_ARGUMENT_TEMPLATE = "%s %s";
    String CONSTRUCTOR_ASSIGNMENT_TEMPLATE = "this.%s = %s;";

    default PsiMethod extractConstructorForClass(PsiClass psiClass, List<PsiField> ctrArgs, List<PsiField> ctrArgsToAssign, List<String> superArgs) {
        String constructorArgsPart = ctrArgs.stream()
                .map(psiField -> String.format(CONSTRUCTOR_ARGUMENT_TEMPLATE, psiField.getType().getCanonicalText(), psiField.getNameIdentifier().getText()))
                .collect(Collectors.joining(", "));

        ctrArgsToAssign.forEach(psiClass::add);
        String superPart = !superArgs.isEmpty() ? String.format("super(%s);\n", String.join(", ", superArgs)) : "";
        String constructorArgsAssignPart = ctrArgsToAssign.stream()
                .map(psiField -> String.format(CONSTRUCTOR_ASSIGNMENT_TEMPLATE, psiField.getNameIdentifier().getText(), psiField.getNameIdentifier().getText()))
                .collect(Collectors.joining(""));
        String bodyPart = superPart.concat(constructorArgsAssignPart);

        String constructor = String.format(CONSTRUCTOR_TEMPLATE, psiClass.getName(), constructorArgsPart, bodyPart);
        return JavaPsiFacade.getElementFactory(psiClass.getProject()).createMethodFromText(constructor, psiClass);
    }
}
