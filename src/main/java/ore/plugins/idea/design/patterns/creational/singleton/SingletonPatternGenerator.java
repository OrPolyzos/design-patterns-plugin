package ore.plugins.idea.design.patterns.creational.singleton;

import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMember;
import com.intellij.psi.PsiMethod;
import ore.plugins.idea.design.patterns.base.TemplateReader;
import ore.plugins.idea.design.patterns.base.utilities.PsiMemberModifierField;
import ore.plugins.idea.design.patterns.util.FormatUtils;
import ore.plugins.idea.design.patterns.util.PsiMemberGeneratorUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

public class SingletonPatternGenerator implements TemplateReader {

    private static final String INSTANCE_METHOD_TEMPLATE_PATH = "/templates/creational/singleton/instance-method";

    private static final String INSTANCE_FIELD_SUFFIX = "Instance";
    private static final String INSTANCE_METHOD_NAME = String.format("get%s", INSTANCE_FIELD_SUFFIX);

    private String instanceMethodTemplate = getTemplate(INSTANCE_METHOD_TEMPLATE_PATH);

    public void generate(@NotNull PsiClass psiClass) {
        String instanceFieldName = FormatUtils.toLowerCaseFirstLetterString(Objects.requireNonNull(psiClass.getName()).concat(INSTANCE_FIELD_SUFFIX));
        deleteRelated(psiClass, instanceFieldName);
        psiClass.add(generatePrivateConstructor(psiClass));
        psiClass.add(generateInstanceField(psiClass, instanceFieldName));
        psiClass.add(generateInstanceMethod(psiClass, instanceFieldName));
    }

    private void deleteRelated(PsiClass psiClass, String instanceFieldName) {
        Arrays.stream(psiClass.getConstructors())
                .forEach(PsiMember::delete);
        Arrays.stream(psiClass.getFields())
                .filter(member -> member.getName() != null && member.getName().equals(instanceFieldName))
                .forEach(PsiMember::delete);
        Arrays.stream(psiClass.getMethods())
                .filter(member -> member.getName().equals(INSTANCE_METHOD_NAME))
                .forEach(PsiMember::delete);
    }

    private PsiElement generatePrivateConstructor(PsiClass psiClass) {
        PsiMethod constructor = PsiMemberGeneratorUtils.generateConstructorForClass(psiClass, Collections.emptyList());
        PsiMemberModifierField.PRIVATE.applyModifier(constructor);
        return constructor;
    }

    private PsiField generateInstanceField(PsiClass psiClass, String instanceFieldName) {
        PsiField instanceField = JavaPsiFacade.getElementFactory(psiClass.getProject()).createField(instanceFieldName, JavaPsiFacade.getInstance(psiClass.getProject()).getElementFactory().createType(psiClass));
        PsiMemberModifierField.PRIVATE_STATIC.applyModifier(instanceField);
        return instanceField;
    }

    private PsiMethod generateInstanceMethod(PsiClass psiClass, String instanceFieldName) {
        String instanceMethodContent = String.format(instanceMethodTemplate, psiClass.getName(), INSTANCE_METHOD_NAME, instanceFieldName, instanceFieldName, psiClass.getName(), instanceFieldName);
        PsiMethod instanceMethod = JavaPsiFacade.getElementFactory(psiClass.getProject()).createMethodFromText(instanceMethodContent, psiClass);
        PsiMemberModifierField.PUBLIC_STATIC_SYNCHRONIZED.applyModifier(instanceMethod);
        return instanceMethod;
    }
}
