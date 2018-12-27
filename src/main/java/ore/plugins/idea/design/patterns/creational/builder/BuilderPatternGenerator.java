package ore.plugins.idea.design.patterns.creational.builder;

import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMember;
import com.intellij.psi.PsiMethod;
import ore.plugins.idea.design.patterns.base.TemplateReader;
import ore.plugins.idea.design.patterns.base.utilities.PsiMemberModifierField;
import ore.plugins.idea.design.patterns.util.PsiClassGeneratorUtils;
import ore.plugins.idea.design.patterns.util.PsiMemberGeneratorUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ore.plugins.idea.design.patterns.util.FormatUtils.toLowerCaseFirstLetterString;
import static ore.plugins.idea.design.patterns.util.FormatUtils.toUpperCaseFirstLetterString;

class BuilderPatternGenerator implements TemplateReader {

    private static final String BUILDER_ACCESS_METHOD_TEMPLATE = "/templates/creational/builder/builder-access-method";
    private static final String BUILDER_BUILD_METHOD_TEMPLATE = "/templates/creational/builder/build-method";
    private static final String BUILDER_WITH_METHOD_TEMPLATE = "/templates/creational/builder/builder-with-method";
    private static final String BUILDER_BUILD_METHOD_SET_TEMPLATE = "%s.set%s(%s);";
    private static final String BUILDER_CLASS_NAME_SUFFIX = "Builder";


    private PsiClass parentClass;
    private List<PsiField> includedFields;
    private List<PsiField> mandatoryFields;
    private String builderClassName;

    private final String builderAccessMethodTemplate = getTemplate(BUILDER_ACCESS_METHOD_TEMPLATE);
    private final String buildMethodTemplate = getTemplate(BUILDER_BUILD_METHOD_TEMPLATE);
    private final String builderWithMethodTemplate = getTemplate(BUILDER_WITH_METHOD_TEMPLATE);

    public BuilderPatternGenerator(@NotNull PsiClass parentClass, List<PsiField> includedFields, List<PsiField> mandatoryFields) {
        this.parentClass = parentClass;
        this.includedFields = includedFields;
        this.mandatoryFields = mandatoryFields;
        this.builderClassName = Objects.requireNonNull(parentClass.getName()).concat(BUILDER_CLASS_NAME_SUFFIX);
    }

    void generate() {
        prepareParentClass();
        PsiClass innerBuilderClass = generateStuffForBuilderClass();
        parentClass.add(innerBuilderClass);
    }

    private void prepareParentClass() {
        PsiMethod constructor = generateConstructorForParentClass();
        List<PsiMethod> gettersAndSetters = generateGettersAndSettersForParentClass();
        deleteRelated(gettersAndSetters);
        includedFields.forEach(PsiMemberModifierField.PRIVATE::applyModifier);
        parentClass.add(constructor);
        gettersAndSetters.forEach(parentClass::add);
    }

    private PsiMethod generateConstructorForParentClass() {
        PsiMethod constructorForParentClass = PsiMemberGeneratorUtils.generateConstructorForClass(parentClass, mandatoryFields);
        PsiMemberModifierField.PRIVATE.applyModifier(constructorForParentClass);
        return constructorForParentClass;
    }

    private List<PsiMethod> generateGettersAndSettersForParentClass() {
        List<PsiMethod> gettersAndSetters = PsiMemberGeneratorUtils.generateGettersAndSettersForClass(includedFields, parentClass);
        gettersAndSetters.forEach(PsiMemberModifierField.PUBLIC::applyModifier);
        return gettersAndSetters;
    }

    private void deleteRelated(List<PsiMethod> gettersAndSetters) {
        Arrays.stream(parentClass.getInnerClasses())
                .filter(innerClass -> innerClass.getName() != null && innerClass.getName().equals(builderClassName))
                .forEach(PsiMember::delete);
        Arrays.stream(parentClass.getConstructors())
                .forEach(PsiMember::delete);
        List<String> gettersAndSettersNames = gettersAndSetters.stream().map(PsiMethod::getName).collect(Collectors.toList());
        Arrays.stream(parentClass.getMethods())
                .filter(parentClassMethod -> gettersAndSettersNames.contains(parentClassMethod.getName()))
                .forEach(PsiMethod::delete);
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
        PsiClass builderClass = PsiClassGeneratorUtils.generateClassForProjectWithName(parentClass.getProject(), builderClassName);
        PsiMemberModifierField.PUBLIC_STATIC.applyModifier(builderClass);
        return builderClass;
    }

    private List<PsiField> generateBuilderFields() {
        List<PsiField> builderFields = includedFields.stream()
                .map(includedField -> JavaPsiFacade.getElementFactory(parentClass.getProject()).createField(Objects.requireNonNull(includedField.getName()), includedField.getType()))
                .collect(Collectors.toList());
        builderFields.forEach(PsiMemberModifierField.PRIVATE::applyModifier);
        return builderFields;
    }

    private PsiMethod generateBuilderConstructor(PsiClass builderClass) {
        PsiMethod builderConstructor = PsiMemberGeneratorUtils.generateConstructorForClass(builderClass, mandatoryFields);
        PsiMemberModifierField.PRIVATE.applyModifier(builderConstructor);
        return builderConstructor;
    }

    private PsiMethod generateBuilderAccessMethod() {
        String argumentsWithTypes = PsiMemberGeneratorUtils.generateArgumentsWithTypesFromFields(mandatoryFields);
        String argumentsWithoutTypes = PsiMemberGeneratorUtils.generateArgumentsWithoutTypesFromFields(mandatoryFields);
        String builderAccessMethodContent = String.format(builderAccessMethodTemplate, builderClassName, parentClass.getName(), argumentsWithTypes, builderClassName, argumentsWithoutTypes);
        PsiMethod builderAccessMethod = JavaPsiFacade.getElementFactory(parentClass.getProject()).createMethodFromText(builderAccessMethodContent, parentClass);
        PsiMemberModifierField.PUBLIC_STATIC.applyModifier(builderAccessMethod);
        return builderAccessMethod;
    }

    private PsiMethod generateBuildMethod(PsiClass builderClass, List<PsiField> includedFieldsWithoutMandatoryFields) {
        String parentClassName = Objects.requireNonNull(parentClass.getName());
        String lowercaseParentClassName = toLowerCaseFirstLetterString(parentClassName);
        String argumentsWithoutTypes = PsiMemberGeneratorUtils.generateArgumentsWithoutTypesFromFields(mandatoryFields);
        String setters = includedFieldsWithoutMandatoryFields.stream()
                .map(psiField -> String.format(BUILDER_BUILD_METHOD_SET_TEMPLATE, lowercaseParentClassName,
                        toUpperCaseFirstLetterString(Objects.requireNonNull(psiField.getName())),
                        toLowerCaseFirstLetterString(Objects.requireNonNull(psiField.getName()))))
                .collect(Collectors.joining("\n"));
        String buildMethodContent = String.format(buildMethodTemplate,
                parentClassName, parentClassName, lowercaseParentClassName, parentClassName, argumentsWithoutTypes, setters, lowercaseParentClassName);
        PsiMethod builderMethod = JavaPsiFacade.getElementFactory(parentClass.getProject()).createMethodFromText(buildMethodContent, builderClass);
        PsiMemberModifierField.PUBLIC.applyModifier(builderMethod);
        return builderMethod;
    }

    private List<PsiMethod> generateBuilderWithMethods(PsiClass builderClass, List<PsiField> withFields) {
        return withFields.stream()
                .map(psiField -> {
                    String upperName = toUpperCaseFirstLetterString(Objects.requireNonNull(psiField.getName()));
                    String name = psiField.getName();
                    String type = psiField.getType().getCanonicalText();
                    String content = String.format(builderWithMethodTemplate, builderClassName, upperName, type, name, name, name);
                    return JavaPsiFacade.getElementFactory(parentClass.getProject()).createMethodFromText(content, builderClass);
                })
                .collect(Collectors.toList());
    }

}
