package ore.plugins.idea.design.patterns.behavioral.strategy;

import com.intellij.psi.*;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class StrategyPatternGenerator {

    private PsiClass psiClass;
    private PsiPackageStatement psiPackageStatement;
    private List<PsiMethod> psiMethods;
    private String strategyName;

    public StrategyPatternGenerator(PsiClass psiClass, String strategyName, List<PsiMethod> psiMethods) {
        this.psiClass = psiClass;
        this.psiMethods = psiMethods;
        this.strategyName = strategyName;
        this.psiPackageStatement = ((PsiJavaFile) psiClass.getContainingFile()).getPackageStatement();
    }

    public void generate() {
        PsiClass interfaceClass = generateInterfaceClass();
        PsiFile interfaceFile = psiClass.getContainingFile().getContainingDirectory().createFile(strategyName.concat(".java"));
        interfaceFile.add(interfaceClass);
        if (psiPackageStatement != null) interfaceFile.addAfter(psiPackageStatement, null);
        JavaCodeStyleManager.getInstance(interfaceClass.getProject()).optimizeImports(interfaceFile);
        prepareImplementationClass();
    }

    private PsiClass generateInterfaceClass() {
        PsiClass interfaceClass = JavaPsiFacade.getElementFactory(psiClass.getProject()).createInterface(strategyName);
        for (PsiMethod psiMethod : psiMethods) {
            StringBuilder methodSb = new StringBuilder();
            methodSb.append(Objects.requireNonNull(psiMethod.getReturnType()).getCanonicalText()).append(" ").append(psiMethod.getName()).append("(");
            String parameters = Arrays.stream(psiMethod.getParameterList().getParameters())
                    .map(psiParameter -> psiParameter.getType().getCanonicalText() + " " + psiParameter.getName())
                    .collect(Collectors.joining(", "));
            methodSb.append(parameters).append(");");
            PsiMethod newPsiMethod = JavaPsiFacade.getElementFactory(psiClass.getProject()).createMethodFromText(methodSb.toString(), psiClass);
            interfaceClass.add(newPsiMethod);
        }
        return interfaceClass;
    }

    private void prepareImplementationClass() {
        PsiJavaCodeReferenceElement psiJavaCodeReferenceElement =
                JavaPsiFacade.getElementFactory(psiClass.getProject()).createReferenceFromText(strategyName, psiClass);
        if (Arrays.stream(Objects.requireNonNull(psiClass.getImplementsList()).getReferencedTypes())
                .filter(imp -> imp.getName().contains(strategyName))
                .collect(Collectors.toList()).size() > 0) return;
        Objects.requireNonNull(psiClass.getImplementsList()).add(psiJavaCodeReferenceElement);
    }
}
