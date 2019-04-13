package ore.plugins.idea.design.patterns.service;

import com.intellij.navigation.NavigationItem;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import ore.plugins.idea.design.patterns.utils.PsiClassGeneratorUtils;
import ore.plugins.idea.design.patterns.utils.PsiMemberGeneratorUtils;
import ore.plugins.idea.lib.service.JavaCodeGenerator;
import ore.plugins.idea.lib.utils.FormatUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static ore.plugins.idea.lib.utils.FormatUtils.toFirstLetterLowerCase;

public class FactoryPatternGenerator extends JavaCodeGenerator {

    private PsiPackageStatement psiPackageStatement;
    private PsiClass selectedInterface;
    private List<PsiClass> selectedImplementors;
    private String factoryClassName;
    private String enumClassName;

    public FactoryPatternGenerator(PsiClass psiClass, PsiClass selectedInterface, String factoryName, String enumName, List<PsiClass> selectedImplementors) {
        super(psiClass);
        this.selectedInterface = selectedInterface;
        this.selectedImplementors = selectedImplementors;
        this.factoryClassName = factoryName;
        this.enumClassName = enumName;
        this.psiPackageStatement = ((PsiJavaFile) psiClass.getContainingFile()).getPackageStatement();
    }


    @Override
    public void generateJavaClass() {
        setupSelectedImplementorsConstructors();
        List<String> enumNames = extractEnumNames();
        List<String> upperCaseEnumNames = extractUpperCaseEnumNames(enumNames);
        generateEnumClass(upperCaseEnumNames);
        generateFactoryClass(enumNames);
    }

    @NotNull
    private List<String> extractUpperCaseEnumNames(List<String> enumNames) {
        return enumNames.stream()
                .map(FormatUtils::camelCaseToUpperCaseWithUnderScore)
                .collect(Collectors.toList());
    }

    @NotNull
    private List<String> extractEnumNames() {
        return selectedImplementors.stream()
                .map(NavigationItem::getName)
                .collect(Collectors.toList());
    }

    private void setupSelectedImplementorsConstructors() {
        selectedImplementors.forEach(selectedImplementor -> {
                    PsiMethod selectedImplementorConstructor = PsiMemberGeneratorUtils.generateConstructorForClass(selectedImplementor, new ArrayList<>());
                    Arrays.stream(selectedImplementor.getConstructors())
                            .filter(constructor -> constructor.getParameterList().getParametersCount() == 0)
                            .forEach(PsiMethod::delete);
                    selectedImplementor.add(selectedImplementorConstructor);
                }
        );
    }

    private void generateEnumClass(List<String> upperCaseEnumNames) {
        PsiClass enumClass = PsiClassGeneratorUtils.generateEnumClass(psiClass, enumClassName, upperCaseEnumNames);
        PsiFile enumFile = psiClass.getContainingFile().getContainingDirectory().createFile(enumClassName.concat(".java"));
        enumFile.add(enumClass);
        fixPackageStatement(enumClass, enumFile);
    }

    private void fixPackageStatement(PsiClass enumClass, PsiFile enumFile) {
        if (psiPackageStatement != null) enumFile.addAfter(psiPackageStatement, null);
        JavaCodeStyleManager.getInstance(enumClass.getProject()).optimizeImports(enumFile);
    }


    private PsiClass generateFactoryClass(List<String> enumNames) {
        PsiClass factoryClass = PsiClassGeneratorUtils.generateClassForProjectWithName(psiClass.getProject(), factoryClassName);
        factoryClass.add(generateFactoryMethod(factoryClass, enumNames));
        new SingletonPatternGenerator(psiClass).generateJavaClass();
        PsiFile factoryClassFile = psiClass.getContainingFile().getContainingDirectory().createFile(factoryClassName.concat(".java"));
        factoryClassFile.add(factoryClass);
        JavaCodeStyleManager.getInstance(factoryClass.getProject()).addImport((PsiJavaFile) factoryClassFile, selectedInterface);
        fixPackageStatement(factoryClass, factoryClassFile);
        return factoryClass;
    }

    private PsiMethod generateFactoryMethod(PsiClass factoryClass, List<String> enumNames) {
        StringBuilder factoryMethodSb = new StringBuilder();
        String argumentName = toFirstLetterLowerCase(enumClassName);
        factoryMethodSb.append("public ").append(selectedInterface.getName()).append(" get").append(selectedInterface.getName()).append("(").append(enumClassName).append(" ").append(argumentName).append("){");
        factoryMethodSb.append("switch (").append(argumentName).append("){");
        enumNames.forEach(eName -> {
                    factoryMethodSb.append("case ").append(FormatUtils.camelCaseToUpperCaseWithUnderScore(eName)).append(": {");
                    factoryMethodSb.append("return new ").append(eName).append("();}");
                }
        );
        factoryMethodSb.append("default: {return null;}}}");
        return JavaPsiFacade.getElementFactory(factoryClass.getProject()).createMethodFromText(factoryMethodSb.toString(), factoryClass);
    }

}
