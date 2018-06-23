package design.patterns.strategy;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import design.patterns.base.DesignPatternAction;
import design.patterns.base.MessageBoxDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class StrategyAction extends DesignPatternAction {

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        PsiClass psiClass = getPsiClassFromContext(anActionEvent);
        StrategyNameDialog strategyNameDialog = new StrategyNameDialog(psiClass);
        strategyNameDialog.show();
        if (strategyNameDialog.isOK()) {
            String strategyName = strategyNameDialog.getName().trim();
            if (strategyName.isEmpty()) {
                new MessageBoxDialog(psiClass, "Cannot accept empty name.").show();
                return;
            }
            if (Arrays.stream(psiClass.getContainingFile().getContainingDirectory().getFiles())
                    .filter(psiFile -> psiFile.getName().contains(strategyName))
                    .collect(Collectors.toList()).size() > 0) {
                new MessageBoxDialog(psiClass, "There is already a file with the name: " + strategyName + ".").show();
                return;
            }
            if (psiClass.getMethods().length == 0) generateCode(psiClass, strategyName, new ArrayList<>());
            else {
                StrategyMethodsDialog strategyMethodsDialog = new StrategyMethodsDialog(Objects.requireNonNull(psiClass));
                strategyMethodsDialog.show();
                if (strategyMethodsDialog.isOK()) {
                    generateCode(psiClass, strategyName, strategyMethodsDialog.getSelectedMethods());
                }
            }
        }
    }

    private void generateCode(PsiClass psiClass, String strategyName, List<PsiMethod> selectedMethods) {
        new WriteCommandAction.Simple(psiClass.getProject(), psiClass.getContainingFile()) {
            @Override
            protected void run() {
                new StrategyPatternGenerator(psiClass, strategyName, selectedMethods).generate();
            }
        }.execute();
    }
}
