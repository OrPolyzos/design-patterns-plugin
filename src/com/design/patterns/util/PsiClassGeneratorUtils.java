package com.design.patterns.util;

import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiElementFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PsiClassGeneratorUtils {

    public static PsiClass generateClassForProjectWithName(Project project, String className) {
        return JavaPsiFacade.getElementFactory(project).createClass(className);
    }

    public static PsiClass generateEnumClass(PsiClass psiClass, String enumClassName, List<String> enums) {
        PsiElementFactory psiElementFactory = JavaPsiFacade.getElementFactory(psiClass.getProject());
        PsiClass enumClass = psiElementFactory.createEnum(enumClassName);
        enums.forEach(enumString -> enumClass.add(psiElementFactory.createEnumConstantFromText(enumString, psiClass)));
        return enumClass;
    }


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
}
