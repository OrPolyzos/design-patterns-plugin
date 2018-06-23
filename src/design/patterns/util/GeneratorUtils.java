package design.patterns.util;

import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static design.patterns.util.FormatUtils.toLowerCaseFirstLetterString;
import static design.patterns.util.FormatUtils.toUpperCaseFirstLetterString;

public class GeneratorUtils {

    public static PsiClass generateClassForProjectWithName(Project project, String className) {
        return JavaPsiFacade.getElementFactory(project).createClass(className);
    }

    public static PsiMethod generateConstructorForClass(PsiClass psiClass) {
        return JavaPsiFacade.getElementFactory(psiClass.getProject()).createConstructor(Objects.requireNonNull(psiClass.getName()));
    }

    public static List<PsiMethod> generateGettersAndSettersForClass(PsiClass psiClass) {
        List<PsiMethod> psiMethods = new ArrayList<>();
        for (PsiField psiField : psiClass.getFields()) {
            psiMethods.add(generateGetterForField(psiField, psiClass));
            psiMethods.add(generateSetterForField(psiField, psiClass));
        }
        return psiMethods;
    }

    public static List<PsiMethod> generateGettersForClass(PsiClass psiClass) {
        List<PsiMethod> getters = new ArrayList<>();
        for (PsiField psiField : psiClass.getFields()) {
            getters.add(generateGetterForField(psiField, psiClass));
        }
        return getters;
    }

    public static List<PsiMethod> generateSettersForClass(PsiClass psiClass) {
        List<PsiMethod> setters = new ArrayList<>();
        for (PsiField psiField : psiClass.getFields()) {
            setters.add(generateSetterForField(psiField, psiClass));
        }
        return setters;
    }

    public static PsiMethod generateGetterForField(PsiField psiField, PsiClass psiClass) {
        StringBuilder getterSb = new StringBuilder();
        getterSb.append("public " + psiField.getType().getCanonicalText() + " get" + toUpperCaseFirstLetterString(Objects.requireNonNull(psiField.getName())) + "(){\n");
        getterSb.append("return ").append(toLowerCaseFirstLetterString(psiField.getName())).append(";\n");
        getterSb.append("}");
        PsiMethod getMethod = JavaPsiFacade.getElementFactory(psiClass.getProject()).createMethodFromText(getterSb.toString(), psiClass);
        return getMethod;
    }

    public static PsiMethod generateSetterForField(PsiField psiField, PsiClass psiClass) {
        StringBuilder setterSb = new StringBuilder();
        setterSb.append("public void set").append(toUpperCaseFirstLetterString(psiField.getName()))
                .append("(").append(psiField.getType().getCanonicalText()).append(" ").append(toLowerCaseFirstLetterString(psiField.getName())).append(") {\n");
        setterSb.append("this.").append(toLowerCaseFirstLetterString(psiField.getName())).append(" = ").
                append(toLowerCaseFirstLetterString(psiField.getName())).append(";\n");
        setterSb.append("}");
        return JavaPsiFacade.getElementFactory(psiClass.getProject()).createMethodFromText(setterSb.toString(), psiClass);
    }
}
