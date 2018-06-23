package design.patterns.builder;

import com.intellij.psi.*;
import com.intellij.psi.util.PsiUtil;
import design.patterns.util.GeneratorUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static design.patterns.util.FormatUtils.toLowerCaseFirstLetterString;
import static design.patterns.util.FormatUtils.toUpperCaseFirstLetterString;

public class BuilderPatternGenerator {

    private final PsiClass parentClass;
    private final String builderClassName;

    public BuilderPatternGenerator(PsiClass parentClass) {
        this.parentClass = parentClass;
        this.builderClassName = parentClass.getName().concat("Builder");
    }

    public PsiClass generate() {
        generateStuffForParentClass();
        return generateStuffForBuilderClass();
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

    private void generateStuffForParentClass() {
        for (PsiField psiField : parentClass.getFields()) {
            PsiUtil.setModifierProperty(psiField, PsiModifier.PRIVATE, true);
            PsiUtil.setModifierProperty(psiField, PsiModifier.STATIC, false);
        }
        for (PsiMethod psiMethod : parentClass.getMethods()) {
            if (psiMethod.getName().contains("get") || psiMethod.getName().contains("set")) {
                PsiUtil.setModifierProperty(psiMethod, PsiModifier.PRIVATE, true);
                PsiUtil.setModifierProperty(psiMethod, PsiModifier.STATIC, false);
            }
        }
        parentClass.add(generateConstructorForParentClass());
        generateGettersSettersForParentClass().forEach(parentClass::add);
    }

    private PsiMethod generateConstructorForParentClass() {
        PsiMethod parentClassConstructor = GeneratorUtils.generateConstructorForClass(parentClass);
        PsiUtil.setModifierProperty(parentClassConstructor, PsiModifier.PRIVATE, true);
        return parentClassConstructor;
    }

    private List<PsiMethod> generateGettersSettersForParentClass() {
        List<PsiMethod> gettersAndSetters = new ArrayList<>();
        List<PsiMethod> getters = GeneratorUtils.generateGettersForClass(parentClass);
        List<PsiMethod> setters = GeneratorUtils.generateSettersForClass(parentClass);

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
        builderAccessMethodSb.append("return new ").append(builderClassName).append("();\n");
        builderAccessMethodSb.append("}\n");
        builderAccessMethodSb.append("\n");
        return JavaPsiFacade.getElementFactory(parentClass.getProject()).createMethodFromText(builderAccessMethodSb.toString(), builderClass);
    }

    private PsiMethod generateBuildMethod(PsiClass builderClass) {
        StringBuilder buildMethodSb = new StringBuilder();
        buildMethodSb.append("public ").append(parentClass.getName()).append(" build() {\n");
        buildMethodSb.append(parentClass.getName()).append(" ").append(toLowerCaseFirstLetterString(Objects.requireNonNull(parentClass.getName()))).append(" = ");
        buildMethodSb.append("new ").append(parentClass.getName()).append("();\n");
        for (PsiField psiField : parentClass.getFields()) {
            buildMethodSb.append(toLowerCaseFirstLetterString(parentClass.getName())).append(".set").append(toUpperCaseFirstLetterString(Objects.requireNonNull(psiField.getName()))).append("(").append(toLowerCaseFirstLetterString(Objects.requireNonNull(psiField.getName()))).append(");\n");
        }
        buildMethodSb.append("return ").append(toLowerCaseFirstLetterString(parentClass.getName())).append(";}");
        return JavaPsiFacade.getElementFactory(parentClass.getProject()).createMethodFromText(buildMethodSb.toString(), builderClass);

    }

    private List<PsiMethod> generateBuilderWithMethods(PsiClass builderClass) {
        List<PsiMethod> withMethods = new ArrayList<>();
        for (PsiField psiField : parentClass.getFields()) {
            StringBuilder withMethodSb = new StringBuilder();
            withMethodSb.append("public ").append(builderClassName).append(" ").append("with").append((toUpperCaseFirstLetterString(Objects.requireNonNull(psiField.getName()))));
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
        for (PsiField psiFieldFromParentClass : parentClass.getFields()) {
            PsiField psiField = JavaPsiFacade.getElementFactory(parentClass.getProject()).createField(Objects.requireNonNull(psiFieldFromParentClass.getName()), psiFieldFromParentClass.getType());
            PsiUtil.setModifierProperty(psiField, PsiModifier.PRIVATE, true);
            psiFields.add(psiField);
        }
        return psiFields;
    }

    private PsiClass generateBuilderClass() {
        PsiClass builderClass = GeneratorUtils.generateClassForProjectWithName(parentClass.getProject(), builderClassName);
        PsiUtil.setModifierProperty(builderClass, PsiModifier.PUBLIC, true);
        PsiUtil.setModifierProperty(builderClass, PsiModifier.STATIC, true);
        PsiUtil.setModifierProperty(builderClass, PsiModifier.FINAL, true);
        return builderClass;
    }

}
