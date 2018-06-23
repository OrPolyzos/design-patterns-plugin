package design.patterns.strategy;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import design.patterns.base.DesignPatternAction;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class StrategyAction extends DesignPatternAction {

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        PsiClass psiClass = getPsiClassFromContext(anActionEvent);
        if (psiClass.getMethods().length == 0) {
            generateCode(psiClass, new ArrayList<>());
        } else {
            StrategyDialog strategyDialog = new StrategyDialog(Objects.requireNonNull(psiClass));
            strategyDialog.show();
            if (strategyDialog.isOK()) {
                generateCode(psiClass, strategyDialog.getSelectedMethods());
            }
        }

    }

    private void generateCode(PsiClass psiClass, List<PsiMethod> selectedMethods) {
        new WriteCommandAction.Simple(psiClass.getProject(), psiClass.getContainingFile()) {
            @Override
            protected void run() {
                new StrategyPatternGenerator(psiClass, selectedMethods).generate();
            }
        }.execute();
    }
}
