package ore.plugins.idea.design.patterns.util;

import com.intellij.navigation.NavigationItem;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import ore.plugins.idea.design.patterns.util.domain.PsiHelperClass;
import ore.plugins.idea.design.patterns.util.domain.PsiHelperField;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PsiMemberGeneratorUtils {

    private static final String ARGUMENT_TEMPLATE = "%s %s";

    public static PsiMethod generateConstructorForClass(PsiClass psiClass, List<PsiField> constructorArguments) {
        return new PsiHelperClass(psiClass, constructorArguments).getConstructor();
    }

    public static List<PsiMethod> generateGettersAndSettersForClass(List<PsiField> psiFields, PsiClass psiClass) {
        List<PsiMethod> psiMethods = new ArrayList<>();
        psiMethods.addAll(generateGettersForClass(psiFields, psiClass));
        psiMethods.addAll(generateSettersForClass(psiFields, psiClass));
        return psiMethods;
    }

    public static String generateArgumentsWithTypesFromFields(List<PsiField> psiFields) {
        return psiFields.stream()
                .map(psiField -> String.format(ARGUMENT_TEMPLATE, psiField.getType().getCanonicalText(), psiField.getName()))
                .collect(Collectors.joining(", "));
    }

    public static String generateArgumentsWithoutTypesFromFields(List<PsiField> psiFields) {
        return psiFields.stream()
                .map(NavigationItem::getName)
                .collect(Collectors.joining(", "));
    }

    private static List<PsiMethod> generateGettersForClass(List<PsiField> psiFields, PsiClass psiClass) {
        return psiFields.stream()
                .map(psiField -> new PsiHelperField(psiField).getGetterMethod(psiClass))
                .collect(Collectors.toList());
    }

    private static List<PsiMethod> generateSettersForClass(List<PsiField> psiFields, PsiClass psiClass) {
        return psiFields.stream()
                .map(psiField -> new PsiHelperField(psiField).getSetterMethod(psiClass))
                .collect(Collectors.toList());
    }

}

