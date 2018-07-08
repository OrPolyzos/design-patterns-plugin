package com.design.patterns.util;

import com.intellij.codeInsight.completion.AllClassesGetter;
import com.intellij.codeInsight.completion.PlainPrefixMatcher;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiUtil;
import com.intellij.ui.CollectionListModel;
import com.intellij.ui.components.JBList;

import javax.swing.*;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.design.patterns.util.FormatUtils.toLowerCaseFirstLetterString;
import static com.design.patterns.util.FormatUtils.toUpperCaseFirstLetterString;

public class GeneratorUtils {

    public static List<PsiClass> getInterfacesAndExtends(PsiClass psiClass) {
        List<PsiClass> interfacesAndExtends = new ArrayList<>();
        interfacesAndExtends.addAll(getInterfaces(psiClass));
        interfacesAndExtends.addAll(getExtends(psiClass));
        return interfacesAndExtends;
    }

    public static List<PsiClass> getInterfaces(PsiClass psiClass) {
        if (psiClass.getImplementsList() == null) {
            return new ArrayList<>();
        }
        if (psiClass.getImplementsList() != null && psiClass.getImplementsList().getReferencedTypes().length == 0) {
            return new ArrayList<>();
        }
        return Arrays.stream(psiClass.getImplementsList().getReferencedTypes())
                .map(PsiClassType::resolve)
                .collect(Collectors.toList());
    }

    public static List<PsiClass> getExtends(PsiClass psiClass) {
        if (psiClass.getExtendsList() == null) {
            return new ArrayList<>();
        }
        if (psiClass.getExtendsList() != null && psiClass.getExtendsList().getReferencedTypes().length == 0) {
            return new ArrayList<>();
        }
        return Arrays.stream(psiClass.getExtendsList().getReferencedTypes())
                .map(PsiClassType::resolve)
                .collect(Collectors.toList());
    }

    public static List<PsiClass> getAllClassesStartingWith(Project project, String prefix) {
        List<PsiClass> filteredClasses = new ArrayList<>();
        AllClassesGetter.processJavaClasses(
                new PlainPrefixMatcher(prefix),
                project,
                GlobalSearchScope.projectScope(project),
                psiClass -> {
                    filteredClasses.add(psiClass);
                    return true;
                }
        );
        return filteredClasses;
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

    public static PsiClass generateClassForProjectWithName(Project project, String className) {
        return JavaPsiFacade.getElementFactory(project).createClass(className);
    }

    public static PsiMethod generateConstructorForClass(PsiClass psiClass) {
        return JavaPsiFacade.getElementFactory(psiClass.getProject()).createConstructor(Objects.requireNonNull(psiClass.getName()));
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

    public static PsiMethod generatePrivateNonStaticConstructor(PsiClass psiClass) {
        Arrays.stream(psiClass.getConstructors())
                .filter(c -> c.getParameterList().isEmpty())
                .forEach(PsiMethod::delete);
        PsiMethod parentClassConstructor = GeneratorUtils.generateConstructorForClass(psiClass);
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

    public static PsiClass generateEnumClass(PsiClass psiClass, String enumClassName, List<String> enums) {
        PsiElementFactory psiElementFactory = JavaPsiFacade.getElementFactory(psiClass.getProject());
        PsiClass enumClass = psiElementFactory.createEnum(enumClassName);
        enums.forEach(enumString -> enumClass.add(psiElementFactory.createEnumConstantFromText(enumString, psiClass)));
        return enumClass;
    }

}

