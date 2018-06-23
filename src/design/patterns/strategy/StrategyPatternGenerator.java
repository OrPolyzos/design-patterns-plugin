package design.patterns.strategy;

import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import one.util.streamex.Joining;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class StrategyPatternGenerator {

    private PsiClass psiClass;
    private List<PsiMethod> psiMethods;
    private String strategyName;

    public StrategyPatternGenerator(PsiClass psiClass, String strategyName, List<PsiMethod> psiMethods) {
        this.psiClass = psiClass;
        this.psiMethods = psiMethods;
        this.strategyName = strategyName;
    }

    public void generate() {
        PsiClass interfaceClass = generateInterfaceClass();
        PsiFile interfaceFile = psiClass.getContainingFile().getContainingDirectory().createFile(strategyName.concat(".java"));
        interfaceFile.add(interfaceClass);
        prepareImplementationClass();
    }

    private PsiClass generateInterfaceClass() {
        PsiClass interfaceClass = JavaPsiFacade.getElementFactory(psiClass.getProject()).createInterface(strategyName);
        for (PsiMethod psiMethod : psiMethods) {
            StringBuilder methodSb = new StringBuilder();
            methodSb.append(Objects.requireNonNull(psiMethod.getReturnType()).getCanonicalText()).append(" ").append(psiMethod.getName()).append("(");
            String parameters = Arrays.stream(psiMethod.getParameterList().getParameters())
                    .map(psiParameter -> psiParameter.getType().getCanonicalText() + " " + psiParameter.getName())
                    .collect(Joining.with(", "));
            methodSb.append(parameters).append(");");
            PsiMethod newPsiMethod = JavaPsiFacade.getElementFactory(psiClass.getProject()).createMethodFromText(methodSb.toString(), psiClass);
            interfaceClass.add(newPsiMethod);
        }
        return interfaceClass;
    }

    private void prepareImplementationClass() {
        psiClass.getImplementsList();
    }
}
