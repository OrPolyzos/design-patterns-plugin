package ore.plugins.idea.design.patterns.creational.builder;

import ore.plugins.idea.design.patterns.base.utilities.PsiModifierMapEnum;
import ore.plugins.idea.design.patterns.base.utilities.PsiModifierMapFactory;
import ore.plugins.idea.design.patterns.util.PsiClassGeneratorUtils;
import ore.plugins.idea.design.patterns.util.PsiMemberGeneratorUtils;
import com.intellij.psi.*;

import java.util.*;
import java.util.stream.Collectors;

import static ore.plugins.idea.design.patterns.util.FormatUtils.toLowerCaseFirstLetterString;
import static ore.plugins.idea.design.patterns.util.FormatUtils.toUpperCaseFirstLetterString;

class BuilderPatternGenerator {

    private static final Map<String, Boolean> publicNonStaticMap = PsiModifierMapFactory.getInstance().getPsiModifierMap(PsiModifierMapEnum.PUBLIC_NON_STATIC);
    private static final Map<String, Boolean> privateNonStaticMap = PsiModifierMapFactory.getInstance().getPsiModifierMap(PsiModifierMapEnum.PRIVATE_NON_STATIC);
    private static final Map<String, Boolean> publicStaticFinalMap = PsiModifierMapFactory.getInstance().getPsiModifierMap(PsiModifierMapEnum.PUBLIC_STATIC_FINAL);

    private final PsiClass parentClass;
    private final List<PsiField> includedFields;
    private final List<PsiField> mandatoryFields;
    private final String BUILDER_CLASS_NAME;
    private final String BUILDER_CLASS_NAME_SUFFIX = "Builder";

    BuilderPatternGenerator(PsiClass parentClass, List<PsiField> includedFields, List<PsiField> mandatoryFields) {
        this.parentClass = parentClass;
        this.includedFields = includedFields;
        this.mandatoryFields = mandatoryFields;
        this.BUILDER_CLASS_NAME = Objects.requireNonNull(parentClass.getName()).concat(BUILDER_CLASS_NAME_SUFFIX);
    }

    void generate() {
        prepareParentClass();
        PsiClass innerBuilderClass = generateStuffForBuilderClass();
        parentClass.add(innerBuilderClass);
    }

    private void prepareParentClass() {
        includedFields.forEach(includedField -> PsiMemberGeneratorUtils.modifyPsiMember(includedField, privateNonStaticMap));
        generateConstructorForParentClass();
        generateGettersAndSettersForParentClass();
        deletePreviousInstanceOfBuilderClass();
    }

    private void deletePreviousInstanceOfBuilderClass() {
        Arrays.stream(parentClass.getInnerClasses())
                .filter(innerClass -> Objects.equals(innerClass.getName(), BUILDER_CLASS_NAME))
                .forEach(PsiMember::delete);
    }

    private void generateConstructorForParentClass() {
        Arrays.stream(parentClass.getConstructors()).forEach(PsiMember::delete);
        PsiMethod constructorForParentClass = PsiMemberGeneratorUtils.generateConstructorForClass(parentClass, mandatoryFields);
        PsiMemberGeneratorUtils.modifyPsiMember(constructorForParentClass, privateNonStaticMap);
        parentClass.add(constructorForParentClass);
    }

    private void generateGettersAndSettersForParentClass() {
        List<PsiMethod> gettersAndSetters = PsiMemberGeneratorUtils.generateGettersAndSettersForClass(includedFields, parentClass);
        gettersAndSetters.forEach(getterOrSetter -> PsiMemberGeneratorUtils.modifyPsiMember(getterOrSetter, publicNonStaticMap));
        List<String> gettersAndSettersNames = gettersAndSetters.stream().map(PsiMethod::getName).collect(Collectors.toList());
        Arrays.stream(parentClass.getMethods())
                .filter(parentClassMethod -> gettersAndSettersNames.contains(parentClassMethod.getName()))
                .forEach(PsiMethod::delete);
        gettersAndSetters.forEach(parentClass::add);
    }

    private PsiClass generateStuffForBuilderClass() {
        PsiClass builderClass = generateBuilderClass();
        generateBuilderFields().forEach(builderClass::add);
        builderClass.add(generateBuilderConstructor(builderClass));
        builderClass.add(generateBuilderAccessMethod());
        List<PsiField> includedFieldsWithoutMandatoryFields = includedFields.stream().filter(includedField -> !mandatoryFields.contains(includedField)).collect(Collectors.toList());
        generateBuilderWithMethods(builderClass, includedFieldsWithoutMandatoryFields).forEach(builderClass::add);
        builderClass.add(generateBuildMethod(builderClass, includedFieldsWithoutMandatoryFields));
        return builderClass;
    }

    private PsiClass generateBuilderClass() {
        PsiClass builderClass = PsiClassGeneratorUtils.generateClassForProjectWithName(parentClass.getProject(), BUILDER_CLASS_NAME);
        PsiMemberGeneratorUtils.modifyPsiMember(builderClass, publicStaticFinalMap);
        return builderClass;
    }

    private List<PsiField> generateBuilderFields() {
        List<PsiField> builderFields = includedFields.stream()
                .map(includedField -> JavaPsiFacade.getElementFactory(parentClass.getProject()).createField(Objects.requireNonNull(includedField.getName()), includedField.getType()))
                .collect(Collectors.toList());
        builderFields.forEach(builderField -> PsiMemberGeneratorUtils.modifyPsiMember(builderField, privateNonStaticMap));
        return builderFields;
    }

    private PsiMethod generateBuilderConstructor(PsiClass builderClass) {
        PsiMethod builderConstructor = PsiMemberGeneratorUtils.generateConstructorForClass(builderClass, mandatoryFields);
        PsiMemberGeneratorUtils.modifyPsiMember(builderConstructor, privateNonStaticMap);
        return builderConstructor;
    }

    private PsiMethod generateBuilderAccessMethod() {
        StringBuilder builderAccessMethodSb = new StringBuilder();
        builderAccessMethodSb.append("public static ").append(parentClass.getName()).append("Builder a").append(parentClass.getName()).append("(");
        String argumentsWithTypesString = mandatoryFields.stream()
                .map(argument -> argument.getType().getCanonicalText() + " " + argument.getName()).collect(Collectors.joining(", "));
        builderAccessMethodSb.append(argumentsWithTypesString);
        builderAccessMethodSb.append("){");
        builderAccessMethodSb.append("return new ").append(BUILDER_CLASS_NAME).append("(");
        String argumentsWithoutTypesString = mandatoryFields.stream()
                .map(PsiField::getName).collect(Collectors.joining(", "));
        builderAccessMethodSb.append(argumentsWithoutTypesString).append(");}");
        return JavaPsiFacade.getElementFactory(parentClass.getProject()).createMethodFromText(builderAccessMethodSb.toString(), parentClass);
    }

    private PsiMethod generateBuildMethod(PsiClass builderClass, List<PsiField> includedFieldsWithoutMandatoryFields) {
        StringBuilder buildMethodSb = new StringBuilder();
        buildMethodSb.append("public ").append(parentClass.getName()).append(" build() {");
        buildMethodSb.append(parentClass.getName()).append(" ").append(toLowerCaseFirstLetterString(Objects.requireNonNull(parentClass.getName()))).append(" = ");
        buildMethodSb.append("new ").append(parentClass.getName()).append("(");
        String argumentsWithoutTypesString = mandatoryFields.stream()
                .map(PsiField::getName).collect(Collectors.joining(", "));
        buildMethodSb.append(argumentsWithoutTypesString).append(");");
        for (PsiField psiField : includedFieldsWithoutMandatoryFields) {
            buildMethodSb.append(toLowerCaseFirstLetterString(parentClass.getName())).append(".set").append(toUpperCaseFirstLetterString(Objects.requireNonNull(psiField.getName()))).append("(").append(toLowerCaseFirstLetterString(Objects.requireNonNull(psiField.getName()))).append(");");
        }
        buildMethodSb.append("return ").append(toLowerCaseFirstLetterString(parentClass.getName())).append(";}");
        return JavaPsiFacade.getElementFactory(parentClass.getProject()).createMethodFromText(buildMethodSb.toString(), builderClass);

    }

    private List<PsiMethod> generateBuilderWithMethods(PsiClass builderClass, List<PsiField> withFields) {
        List<PsiMethod> withMethods = new ArrayList<>();
        for (PsiField psiField : withFields) {
            StringBuilder withMethodSb = new StringBuilder();
            withMethodSb.append("public ").append(BUILDER_CLASS_NAME).append(" ").append("with").append((toUpperCaseFirstLetterString(Objects.requireNonNull(psiField.getName()))));
            withMethodSb.append("(").append(psiField.getType().getCanonicalText()).append(" ").append(psiField.getName()).append(") {");
            withMethodSb.append("this.").append(psiField.getName()).append(" = ").append(psiField.getName()).append(";");
            withMethodSb.append("return this;");
            withMethodSb.append("}");
            PsiMethod psiMethod = JavaPsiFacade.getElementFactory(parentClass.getProject()).createMethodFromText(withMethodSb.toString(), builderClass);
            withMethods.add(psiMethod);
        }
        return withMethods;
    }

}
