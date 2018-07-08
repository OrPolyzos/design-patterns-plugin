package com.design.patterns.creational.builder;

import com.design.patterns.util.GeneratorUtils;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.design.patterns.util.FormatUtils.toLowerCaseFirstLetterString;
import static com.design.patterns.util.FormatUtils.toUpperCaseFirstLetterString;

public class BuilderPatternGenerator {

    private final PsiClass parentClass;
    private final List<PsiField> selectedFields;
    private final String BUILDER_CLASS_NAME;
    private final String BUILDER_CLASS_NAME_SUFFIX = "Builder";

    BuilderPatternGenerator(PsiClass parentClass, List<PsiField> selectedFields) {
        this.parentClass = parentClass;
        this.selectedFields = selectedFields;
        this.BUILDER_CLASS_NAME = Objects.requireNonNull(parentClass.getName()).concat(BUILDER_CLASS_NAME_SUFFIX);
    }

    PsiClass generate() {
        prepareParentClass();
        return generateStuffForBuilderClass();
    }

    private void prepareParentClass() {
        for (PsiField psiField : selectedFields) {
            PsiUtil.setModifierProperty(psiField, PsiModifier.PRIVATE, true);
            PsiUtil.setModifierProperty(psiField, PsiModifier.STATIC, false);
        }
        parentClass.add(GeneratorUtils.generatePrivateNonStaticConstructor(parentClass));
        generateGettersSettersForParentClass()
                .stream()
                .filter(getterOrSetter ->
                        !Arrays.stream(parentClass.getMethods())
                                .map(PsiMethod::getName)
                                .collect(Collectors.toList())
                                .contains(getterOrSetter.getName()))
                .forEach(parentClass::add);
        Arrays.stream(parentClass.getInnerClasses())
                .filter(innerClass -> Objects.equals(innerClass.getName(), BUILDER_CLASS_NAME))
                .forEach(PsiElement::delete);
    }

    private PsiClass generateStuffForBuilderClass() {
        PsiClass builderClass = generateBuilderClass();
        generateBuilderFields().forEach(builderClass::add);
        builderClass.add(generateBuilderConstructor(builderClass));
        builderClass.add(generateBuilderAccessMethod(parentClass));
        generateBuilderWithMethods(builderClass).forEach(builderClass::add);
        builderClass.add(generateBuildMethod(builderClass));
        return builderClass;
    }

    private List<PsiMethod> generateGettersSettersForParentClass() {
        List<PsiMethod> gettersAndSetters = new ArrayList<>();
        List<PsiMethod> getters = GeneratorUtils.generateGettersOfFieldsInClass(selectedFields, parentClass);
        List<PsiMethod> setters = GeneratorUtils.generateSettersOfFieldsInClass(selectedFields, parentClass);

        for (PsiMethod getter : getters) {
            PsiUtil.setModifierProperty(getter, PsiModifier.PUBLIC, true);
        }

        for (PsiMethod setter : setters) {
            PsiUtil.setModifierProperty(setter, PsiModifier.PRIVATE, true);
        }
        gettersAndSetters.addAll(getters);
        gettersAndSetters.addAll(setters);
        return gettersAndSetters;
    }

    private PsiMethod generateBuilderAccessMethod(PsiClass builderClass) {
        StringBuilder builderAccessMethodSb = new StringBuilder();
        builderAccessMethodSb.append("public static ").append(parentClass.getName()).append("Builder a").append(parentClass.getName()).append("() {\n");
        builderAccessMethodSb.append("return new ").append(BUILDER_CLASS_NAME).append("();\n");
        builderAccessMethodSb.append("}\n");
        return JavaPsiFacade.getElementFactory(parentClass.getProject()).createMethodFromText(builderAccessMethodSb.toString(), builderClass);
    }

    private PsiMethod generateBuildMethod(PsiClass builderClass) {
        StringBuilder buildMethodSb = new StringBuilder();
        buildMethodSb.append("public ").append(parentClass.getName()).append(" build() {\n");
        buildMethodSb.append(parentClass.getName()).append(" ").append(toLowerCaseFirstLetterString(Objects.requireNonNull(parentClass.getName()))).append(" = ");
        buildMethodSb.append("new ").append(parentClass.getName()).append("();\n");
        for (PsiField psiField : selectedFields) {
            buildMethodSb.append(toLowerCaseFirstLetterString(parentClass.getName())).append(".set").append(toUpperCaseFirstLetterString(Objects.requireNonNull(psiField.getName()))).append("(").append(toLowerCaseFirstLetterString(Objects.requireNonNull(psiField.getName()))).append(");\n");
        }
        buildMethodSb.append("return ").append(toLowerCaseFirstLetterString(parentClass.getName())).append(";}");
        return JavaPsiFacade.getElementFactory(parentClass.getProject()).createMethodFromText(buildMethodSb.toString(), builderClass);

    }

    private List<PsiMethod> generateBuilderWithMethods(PsiClass builderClass) {
        List<PsiMethod> withMethods = new ArrayList<>();
        for (PsiField psiField : selectedFields) {
            StringBuilder withMethodSb = new StringBuilder();
            withMethodSb.append("public ").append(BUILDER_CLASS_NAME).append(" ").append("with").append((toUpperCaseFirstLetterString(Objects.requireNonNull(psiField.getName()))));
            withMethodSb.append("(").append(psiField.getType().getCanonicalText()).append(" ").append(psiField.getName()).append(") {\n");
            withMethodSb.append("this.").append(psiField.getName()).append(" = ").append(psiField.getName()).append(";\n");
            withMethodSb.append("return this;\n");
            withMethodSb.append("}");
            PsiMethod psiMethod = JavaPsiFacade.getElementFactory(parentClass.getProject()).createMethodFromText(withMethodSb.toString(), builderClass);
            withMethods.add(psiMethod);
        }
        return withMethods;
    }

    private PsiMethod generateBuilderConstructor(PsiClass builderClass) {
        PsiMethod builderConstructor = GeneratorUtils.generateConstructorForClass(builderClass);
        PsiUtil.setModifierProperty(builderConstructor, PsiModifier.PRIVATE, true);
        return builderConstructor;
    }

    private List<PsiField> generateBuilderFields() {
        List<PsiField> psiFields = new ArrayList<>();
        for (PsiField psiFieldFromParentClass : selectedFields) {
            PsiField psiField = JavaPsiFacade.getElementFactory(parentClass.getProject()).createField(Objects.requireNonNull(psiFieldFromParentClass.getName()), psiFieldFromParentClass.getType());
            PsiUtil.setModifierProperty(psiField, PsiModifier.PRIVATE, true);
            psiFields.add(psiField);
        }
        return psiFields;
    }

    private PsiClass generateBuilderClass() {
        PsiClass builderClass = GeneratorUtils.generateClassForProjectWithName(parentClass.getProject(), BUILDER_CLASS_NAME);
        PsiUtil.setModifierProperty(builderClass, PsiModifier.PUBLIC, true);
        PsiUtil.setModifierProperty(builderClass, PsiModifier.STATIC, true);
        PsiUtil.setModifierProperty(builderClass, PsiModifier.FINAL, true);
        return builderClass;
    }

}
