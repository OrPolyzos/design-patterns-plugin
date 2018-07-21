package com.design.patterns.creational.singleton;

import com.design.patterns.util.FormatUtils;
import com.design.patterns.util.GeneratorUtils;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiUtil;

import java.util.ArrayList;
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
        PsiField getInstanceField = JavaPsiFacade.getElementFactory(psiClass.getProject()).createField(instanceFieldName, JavaPsiFacade.getInstance(psiClass.getProject()).getElementFactory().createType(psiClass));
        PsiUtil.setModifierProperty(getInstanceField, PsiModifier.PRIVATE, true);
        PsiUtil.setModifierProperty(getInstanceField, PsiModifier.STATIC, true);
        return getInstanceField;
    }

    private void prepareParentClass() {
        GeneratorUtils.changeConstructorsToPrivateNonStatic(psiClass);
        psiClass.add(GeneratorUtils.generatePrivateNonStaticConstructor(psiClass, new ArrayList<>()));
        Arrays.stream(psiClass.getFields())
                .filter(field -> Objects.equals(field.getName(), instanceFieldName))
                .forEach(PsiField::delete);
        Arrays.stream(psiClass.getMethods())
                .filter(m -> m.getName().equals("getInstance"))
                .forEach(PsiMethod::delete);
    }

    private PsiMethod generateGetInstanceMethod() {
        StringBuilder getInstanceMethodSb = new StringBuilder();
        getInstanceMethodSb.append("public static ").append(psiClass.getName()).append(" getInstance() {\n");
        getInstanceMethodSb.append("if (").append(instanceFieldName).append(" == null){\n");
        getInstanceMethodSb.append(instanceFieldName).append(" = ").append("new ").append(psiClass.getName()).append("();\n");
        getInstanceMethodSb.append("}");
        getInstanceMethodSb.append("return ").append(instanceFieldName).append(";\n");
        getInstanceMethodSb.append("}");
        return JavaPsiFacade.getElementFactory(psiClass.getProject()).createMethodFromText(getInstanceMethodSb.toString(), psiClass);
    }

}
