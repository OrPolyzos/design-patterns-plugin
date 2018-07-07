package com.design.patterns.singleton;

import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.design.patterns.util.FormatUtils;
import com.design.patterns.util.GeneratorUtils;

import java.util.Arrays;
import java.util.Objects;

public class SingletonPatternGenerator {

    private PsiClass psiClass;
    private String instanceFieldName;

    public SingletonPatternGenerator(PsiClass psiClass) {
        this.psiClass = psiClass;
        this.instanceFieldName = FormatUtils.toLowerCaseFirstLetterString(Objects.requireNonNull(psiClass.getName()).concat("Instance"));
    }

    public void generate() {
        prepareParentClass();
        psiClass.add(generateGetInstanceField());
        psiClass.add(generateGetInstanceMethod());
    }

    private PsiField generateGetInstanceField() {
        return JavaPsiFacade.getElementFactory(psiClass.getProject()).createField(instanceFieldName, JavaPsiFacade.getInstance(psiClass.getProject()).getElementFactory().createType(psiClass));
    }

    private void prepareParentClass() {
        GeneratorUtils.changeConstructorsToPrivateNonStatic(psiClass);
        psiClass.add(GeneratorUtils.generatePrivateNonStaticConstructor(psiClass));
        Arrays.stream(psiClass.getFields())
                .filter(field -> Objects.equals(field.getName(), instanceFieldName))
                .forEach(PsiField::delete);
        Arrays.stream(psiClass.getMethods())
                .filter(m -> m.getName().equals("getInstance"))
                .forEach(PsiMethod::delete);
    }

    private PsiMethod generateGetInstanceMethod() {
        StringBuilder getInstanceMethodSb = new StringBuilder();
        getInstanceMethodSb.append("private ").append(psiClass.getName()).append(" getInstance() {\n");
        getInstanceMethodSb.append("if (").append(instanceFieldName).append(" == null){\n");
        getInstanceMethodSb.append("this.").append(instanceFieldName).append(" = ").append("new ").append(psiClass.getName()).append("();\n");
        getInstanceMethodSb.append("}");
        getInstanceMethodSb.append("return this.").append(instanceFieldName).append(";\n");
        getInstanceMethodSb.append("}");
        return JavaPsiFacade.getElementFactory(psiClass.getProject()).createMethodFromText(getInstanceMethodSb.toString(), psiClass);
    }

}
