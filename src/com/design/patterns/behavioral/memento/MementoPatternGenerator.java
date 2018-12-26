package com.design.patterns.behavioral.memento;

import com.design.patterns.base.utilities.PsiModifierMapEnum;
import com.design.patterns.base.utilities.PsiModifierMapFactory;
import com.design.patterns.util.PsiClassGeneratorUtils;
import com.design.patterns.util.PsiMemberGeneratorUtils;
import com.intellij.psi.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class MementoPatternGenerator {

    private static final Map<String, Boolean> publicNonStaticMap = PsiModifierMapFactory.getInstance().getPsiModifierMap(PsiModifierMapEnum.PUBLIC_NON_STATIC);
    private static final Map<String, Boolean> privateNonStaticMap = PsiModifierMapFactory.getInstance().getPsiModifierMap(PsiModifierMapEnum.PRIVATE_NON_STATIC);


    private PsiClass parentClass;
    private List<PsiField> selectedFields;
    private static final String MEMENTO_CLASS_NAME = "Memento";
    private String caretakerClassName;
    private String JAVA_FILE_SUFFIX = ".java";

    public MementoPatternGenerator(PsiClass psiClass, String caretakerClassName, List<PsiField> selectedFields) {
        this.parentClass = psiClass;
        this.caretakerClassName = caretakerClassName;
        this.selectedFields = selectedFields;
    }

    public void generate() {
        prepareParentClass();
        generateCaretakerClass();
    }


    private void prepareParentClass() {
        deletePreviousInstanceOfMementoClass();
        PsiClass mementoClass = generateStuffForMementoClass();
        parentClass.add(mementoClass);
        parentClass.add(generateSaveMethod());
        parentClass.add(generateUndoToLastSaveMethod());
    }

    private void deletePreviousInstanceOfMementoClass() {
        Arrays.stream(parentClass.getInnerClasses())
                .filter(innerClass -> Objects.equals(innerClass.getName(), MEMENTO_CLASS_NAME))
                .forEach(PsiMember::delete);
    }

    private PsiClass generateStuffForMementoClass() {
        PsiClass mementoClass = generateMementoClass();
        List<PsiField> mementoFields = generateMementoFields();
        mementoFields.forEach(mementoClass::add);
        PsiMethod mementoConstructor = generateMementoConstructor(mementoClass);
        mementoClass.add(mementoConstructor);
        return mementoClass;
    }

    private PsiClass generateMementoClass() {
        PsiClass mementoClass = PsiClassGeneratorUtils.generateClassForProjectWithName(parentClass.getProject(), MEMENTO_CLASS_NAME);
        PsiMemberGeneratorUtils.modifyPsiMember(mementoClass, privateNonStaticMap);
        return mementoClass;
    }

    private PsiMethod generateMementoConstructor(PsiClass mementoClass) {
        PsiMethod mementoConstructor = PsiMemberGeneratorUtils.generateConstructorForClass(mementoClass, selectedFields);
        PsiMemberGeneratorUtils.modifyPsiMember(mementoConstructor, publicNonStaticMap);
        return mementoConstructor;
    }

    private List<PsiField> generateMementoFields() {
        List<PsiField> mementoFields = selectedFields.stream()
                .map(includedField -> JavaPsiFacade.getElementFactory(parentClass.getProject()).createField(Objects.requireNonNull(includedField.getName()), includedField.getType()))
                .collect(Collectors.toList());
        mementoFields.forEach(mementoField -> PsiMemberGeneratorUtils.modifyPsiMember(mementoField, privateNonStaticMap));
        return mementoFields;
    }

    private PsiMethod generateSaveMethod() {
        StringBuilder saveMethoSb = new StringBuilder();
        saveMethoSb.append("public ").append(MEMENTO_CLASS_NAME).append(" save(){");
        saveMethoSb.append("return new Memento(");
        String fieldsString = selectedFields.stream()
                .map(field -> "this." + field.getName())
                .collect(Collectors.joining(","));
        saveMethoSb.append(fieldsString).append(");}");
        return JavaPsiFacade.getElementFactory(parentClass.getProject()).createMethodFromText(saveMethoSb.toString(), parentClass);
    }


    private PsiMethod generateUndoToLastSaveMethod() {
        StringBuilder undoToLastSaveSb = new StringBuilder();
        undoToLastSaveSb.append("public void undoToLastSave(Object obj){");
        undoToLastSaveSb.append("Memento memento = (Memento) obj;");
        String fieldsSettingString = selectedFields.stream()
                .map(field -> "this." + field.getName() + " = memento." + field.getName() + ";")
                .collect(Collectors.joining());
        undoToLastSaveSb.append(fieldsSettingString);
        undoToLastSaveSb.append("}");
        return JavaPsiFacade.getElementFactory(parentClass.getProject()).createMethodFromText(undoToLastSaveSb.toString(), parentClass);
    }

    private void generateCaretakerClass() {
    }
}
