package ore.plugins.idea.design.patterns.creational.factory;

import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiPackageStatement;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import ore.plugins.idea.design.patterns.creational.singleton.SingletonPatternGenerator;
import ore.plugins.idea.design.patterns.util.FormatUtils;
import ore.plugins.idea.design.patterns.util.PsiClassGeneratorUtils;
import ore.plugins.idea.design.patterns.util.PsiMemberGeneratorUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

class FactoryPatternGenerator {

    private PsiClass psiClass;
    private PsiPackageStatement psiPackageStatement;
    private PsiClass selectedInterface;
    private List<PsiClass> selectedImplementors;
    private String factoryClassName;
    private String enumClassName;

    FactoryPatternGenerator(PsiClass psiClass, PsiClass selectedInterface, String factoryName, List<PsiClass> selectedImplementors) {
        this.psiClass = psiClass;
        this.selectedInterface = selectedInterface;
        this.selectedImplementors = selectedImplementors;
        this.factoryClassName = factoryName + "Factory";
        this.enumClassName = factoryName + "Enum";
        this.psiPackageStatement = ((PsiJavaFile) psiClass.getContainingFile()).getPackageStatement();
    }

    void generate() {
        selectedImplementors.forEach(selectedImplementor ->
                {
                    PsiMethod selectedImplementorConstructor = PsiMemberGeneratorUtils.generateConstructorForClass(selectedImplementor, new ArrayList<>());
                    Arrays.stream(selectedImplementor.getConstructors())
                            .filter(constructor -> constructor.getParameterList().isEmpty())
                            .forEach(PsiMethod::delete);
                    selectedImplementor.add(selectedImplementorConstructor);
                }
        );
        List<String> enumNames = selectedImplementors.stream().map(PsiClass::getName).collect(Collectors.toList());
        List<String> upperCaseEnumNames = enumNames.stream().map(FormatUtils::camelCaseToUpperCaseWithUnderScore).collect(Collectors.toList());
        generateEnumClass(upperCaseEnumNames);
        generateFactoryClass(enumNames);
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


    private void generateFactoryClass(List<String> enumNames) {
        PsiClass factoryClass = PsiClassGeneratorUtils.generateClassForProjectWithName(psiClass.getProject(), factoryClassName);
        factoryClass.add(generateFactoryMethod(factoryClass, enumNames));
        new SingletonPatternGenerator().generate(factoryClass);
        PsiFile factoryClassFile = psiClass.getContainingFile().getContainingDirectory().createFile(factoryClassName.concat(".java"));
        factoryClassFile.add(factoryClass);
        JavaCodeStyleManager.getInstance(factoryClass.getProject()).addImport((PsiJavaFile) factoryClassFile, selectedInterface);
        fixPackageStatement(factoryClass, factoryClassFile);
    }

    private PsiMethod generateFactoryMethod(PsiClass factoryClass, List<String> enumNames) {
        StringBuilder factoryMethodSb = new StringBuilder();
        String argumentName = FormatUtils.toLowerCaseFirstLetterString(enumClassName);
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
