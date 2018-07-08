package com.design.patterns.creational.factory;

import com.design.patterns.creational.singleton.SingletonPatternGenerator;
import com.design.patterns.util.FormatUtils;
import com.design.patterns.util.GeneratorUtils;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FactoryPatternGenerator {

    private PsiClass psiClass;
    private PsiPackageStatement psiPackageStatement;
    private PsiClass selectedInterface;
    private List<PsiClass> selectedImplementors;
    private String factoryClassName;
    private String enumClassName;

    public FactoryPatternGenerator(PsiClass psiClass, PsiClass selectedInterface, List<PsiClass> selectedImplementors) {
        this.psiClass = psiClass;
        this.selectedInterface = selectedInterface;
        this.selectedImplementors = selectedImplementors;
        this.factoryClassName = selectedInterface.getName() + "Factory";
        this.enumClassName = selectedInterface.getName() + "Enum";
        this.psiPackageStatement = ((PsiJavaFile) psiClass.getContainingFile()).getPackageStatement();
    }

    public void generate() {
        selectedImplementors.forEach(selectedImplementor -> selectedImplementor.add(GeneratorUtils.generateConstructorForClass(selectedImplementor)));
        PsiClass enumClass = generateEnumClass();
        PsiClass factoryClass = generateFactoryClass();
    }

    private PsiClass generateFactoryClass() {
        PsiClass factoryClass = GeneratorUtils.generateClassForProjectWithName(psiClass.getProject(), factoryClassName);
        generateFactoryMethodToClass(factoryClass);
        new SingletonPatternGenerator(factoryClass).generate();
        PsiFile factoryClassFile = psiClass.getContainingFile().getContainingDirectory().createFile(factoryClassName.concat(".java"));
        factoryClassFile.add(factoryClass);
        JavaCodeStyleManager.getInstance(factoryClass.getProject()).addImport((PsiJavaFile) factoryClassFile, selectedInterface);
        factoryClassFile.addAfter(psiPackageStatement, null);
        JavaCodeStyleManager.getInstance(factoryClass.getProject()).optimizeImports(factoryClassFile);
        return factoryClass;
    }

    private PsiMethod generateFactoryMethodToClass(PsiClass factoryClass) {
        List<String> enumNames = selectedImplementors.stream().map(PsiClass::getName).collect(Collectors.toList());
        StringBuilder factoryMethodSb = new StringBuilder();
        String argumentName = FormatUtils.toLowerCaseFirstLetterString(enumClassName);
        factoryMethodSb.append("public " + selectedInterface.getName() + " get" + selectedInterface.getName() + "(" + enumClassName + " " + argumentName + "){\n");
        factoryMethodSb.append("\tswitch (" + argumentName + "){\n");
        enumNames.forEach(eName -> {
                    factoryMethodSb.append("\t\tcase " + FormatUtils.camelCaseToUpperCaseWithUnderScore(eName) + ": {\n");
                    factoryMethodSb.append("\t\t\treturn new " + eName + "();\n");
                    factoryMethodSb.append("\t\t}\n");
                }
        );
        factoryMethodSb.append("\t\tdefault: {\n");
        factoryMethodSb.append("\t\t\treturn null;\n");
        factoryMethodSb.append("\t\t}\n");
        factoryMethodSb.append("\t}\n");
        factoryMethodSb.append("}\n");
        PsiMethod factoryMethod = JavaPsiFacade.getElementFactory(factoryClass.getProject()).createMethodFromText(factoryMethodSb.toString(), factoryClass);
        factoryClass.add(factoryMethod);
        return factoryMethod;
    }

    private PsiClass generateEnumClass() {
        List<String> enumNames = selectedImplementors.stream().map(e -> FormatUtils.camelCaseToUpperCaseWithUnderScore(Objects.requireNonNull(e.getName()))).collect(Collectors.toList());
        PsiClass enumClass = GeneratorUtils.generateEnumClass(psiClass, enumClassName, enumNames);
        PsiFile enumFile = psiClass.getContainingFile().getContainingDirectory().createFile(enumClassName.concat(".java"));
        enumFile.add(enumClass);
        enumFile.addAfter(psiPackageStatement, null);
        JavaCodeStyleManager.getInstance(enumClass.getProject()).optimizeImports(enumFile);
        return enumClass;
    }

}
