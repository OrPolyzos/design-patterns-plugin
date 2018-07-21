package com.design.patterns.util;

import com.intellij.psi.*;
import com.intellij.psi.util.PsiUtil;
import com.intellij.ui.CollectionListModel;
import com.intellij.ui.components.JBList;
import one.util.streamex.Joining;

import javax.swing.*;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.design.patterns.util.FormatUtils.toLowerCaseFirstLetterString;
import static com.design.patterns.util.FormatUtils.toUpperCaseFirstLetterString;

public class PsiMemberGeneratorUtils {

    public static void modifyPsiMember(PsiMember psiMember, Map<String, Boolean> psiModifierBooleanMap) {
        psiModifierBooleanMap.forEach((psiModifier, value) -> PsiUtil.setModifierProperty(psiMember, psiModifier, value));
    }


    public static JBList<PsiMember> getCandidatePsiMembersBasedOnPredicate(Collection<PsiMember> psiMembersCollection, Predicate<PsiMember> predicateForPsiMembersToKeep) {
        Collection<PsiMember> filteredPsiMembers = psiMembersCollection.stream()
                .filter(predicateForPsiMembersToKeep)
                .collect(Collectors.toList());
        CollectionListModel<PsiMember> collectionListModel = new CollectionListModel<>(filteredPsiMembers);
        JBList<PsiMember> jbPsiMembers = new JBList<>(collectionListModel);
        jbPsiMembers.setCellRenderer(new DefaultListCellRenderer());
        return jbPsiMembers;
    }

    public static JBList<PsiClass> getCandidatePsiClassesBasedOnPredicate(Collection<PsiClass> psiClassesCollection, Predicate<PsiClass> predicateForPsiClassesToKeep) {
        Collection<PsiClass> filteredPsiMembers = psiClassesCollection.stream()
                .filter(predicateForPsiClassesToKeep)
                .collect(Collectors.toList());
        CollectionListModel<PsiClass> collectionListModel = new CollectionListModel<>(filteredPsiMembers);
        JBList<PsiClass> jbPsiClasses = new JBList<>(collectionListModel);
        jbPsiClasses.setCellRenderer(new DefaultListCellRenderer());
        return jbPsiClasses;
    }


    public static PsiMethod generateConstructorForClass(PsiClass psiClass, List<PsiField> constructorArguments) {
        StringBuilder constructorSB = new StringBuilder();
        constructorSB.append("public ").append(psiClass.getName()).append("(");
        String constructorArgumentsString = constructorArguments.stream()
                .map(constructorArgument -> constructorArgument.getType().getCanonicalText() + " " + constructorArgument.getName()).collect(Joining.with(", "));
        constructorSB.append(constructorArgumentsString).append("){");
        String constructorBodyArgumentsString = constructorArguments.stream()
                .map(constructorArgument -> "this." + constructorArgument.getName() + " = " + constructorArgument.getName() + ";").collect(Joining.with(" "));
        constructorSB.append(constructorBodyArgumentsString).append("}");
        return JavaPsiFacade.getElementFactory(psiClass.getProject()).createMethodFromText(constructorSB.toString(), psiClass);
    }

    public static List<PsiMethod> generateGettersAndSettersForClass(List<PsiField> psiFields, PsiClass psiClass) {
        List<PsiMethod> psiMethods = new ArrayList<>();
        for (PsiField psiField : psiFields) {
            psiMethods.add(generateGetterOfFieldInClass(psiField, psiClass));
            psiMethods.add(generateSetterOfFieldInClass(psiField, psiClass));
        }
        return psiMethods;
    }

    public static List<PsiMethod> generateGettersOfFieldsInClass(List<PsiField> psiFields, PsiClass psiClass) {
        List<PsiMethod> getters = new ArrayList<>();
        for (PsiField psiField : psiFields) {
            getters.add(generateGetterOfFieldInClass(psiField, psiClass));
        }
        return getters;
    }

    public static List<PsiMethod> generateSettersOfFieldsInClass(List<PsiField> psiFields, PsiClass psiClass) {
        List<PsiMethod> setters = new ArrayList<>();
        for (PsiField psiField : psiFields) {
            setters.add(generateSetterOfFieldInClass(psiField, psiClass));
        }
        return setters;
    }

    public static PsiMethod generateGetterOfFieldInClass(PsiField psiField, PsiClass psiClass) {
        StringBuilder getterSb = new StringBuilder();
        getterSb.append("public " + psiField.getType().getCanonicalText() + " get" + toUpperCaseFirstLetterString(Objects.requireNonNull(psiField.getName())) + "(){\n");
        getterSb.append("return ").append(toLowerCaseFirstLetterString(psiField.getName())).append(";\n");
        getterSb.append("}");
        PsiMethod getMethod = JavaPsiFacade.getElementFactory(psiClass.getProject()).createMethodFromText(getterSb.toString(), psiClass);
        return getMethod;
    }

    public static PsiMethod generateSetterOfFieldInClass(PsiField psiField, PsiClass psiClass) {
        StringBuilder setterSb = new StringBuilder();
        setterSb.append("public void set").append(toUpperCaseFirstLetterString(psiField.getName()))
                .append("(").append(psiField.getType().getCanonicalText()).append(" ").append(toLowerCaseFirstLetterString(psiField.getName())).append(") {\n");
        setterSb.append("this.").append(toLowerCaseFirstLetterString(psiField.getName())).append(" = ").
                append(toLowerCaseFirstLetterString(psiField.getName())).append(";\n");
        setterSb.append("}");
        return JavaPsiFacade.getElementFactory(psiClass.getProject()).createMethodFromText(setterSb.toString(), psiClass);
    }

    public static PsiMethod generatePrivateNonStaticConstructor(PsiClass psiClass, List<PsiField> arguments) {
        Arrays.stream(psiClass.getConstructors()).forEach(PsiMethod::delete);
        PsiMethod parentClassConstructor = PsiMemberGeneratorUtils.generateConstructorForClass(psiClass, arguments);
        PsiUtil.setModifierProperty(parentClassConstructor, PsiModifier.PRIVATE, true);
        PsiUtil.setModifierProperty(parentClassConstructor, PsiModifier.STATIC, false);
        return parentClassConstructor;
    }

    public static void changeConstructorsToPrivateNonStatic(PsiClass psiClass) {
        Arrays.asList(psiClass.getConstructors()).forEach(constructor -> {
                    PsiUtil.setModifierProperty(constructor, PsiModifier.PRIVATE, true);
                    PsiUtil.setModifierProperty(constructor, PsiModifier.STATIC, false);
                }
        );
    }


}

