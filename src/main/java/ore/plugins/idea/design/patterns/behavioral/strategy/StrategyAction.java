package ore.plugins.idea.design.patterns.behavioral.strategy;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import ore.plugins.idea.design.patterns.base.DesignPatternAction;
import ore.plugins.idea.design.patterns.base.dialog.view.InputValueDialog;
import ore.plugins.idea.design.patterns.base.dialog.view.SelectStuffDialog;
import ore.plugins.idea.design.patterns.util.ClassNameValidator;

import javax.swing.ListSelectionModel;
import java.util.Arrays;
import java.util.List;

public class StrategyAction extends DesignPatternAction implements ClassNameValidator {

    private static final String STRATEGY_DIALOG_TITLE = "Strategy Design Pattern";
    private static final String STRATEGY_METHODS_DIALOG_TEXT = "Methods to include:";
    private static final String STRATEGY_NAME_DIALOG_TEXT = "Give a name for the interface:";


    @Override
    public void safeActionPerformed(AnActionEvent anActionEvent) {
        PsiClass psiClass = extractPsiClass(anActionEvent);
        InputValueDialog strategyNameDialog = new InputValueDialog(psiClass, STRATEGY_DIALOG_TITLE, STRATEGY_NAME_DIALOG_TEXT);
        strategyNameDialog.waitForInput();
        String strategyName = validateClassNameOrThrow(psiClass, strategyNameDialog.getInput());
        SelectStuffDialog<PsiMethod> strategyMethodsDialog = new SelectStuffDialog<>(psiClass, Arrays.asList(psiClass.getMethods()), psiMethod -> !psiMethod.isConstructor(), STRATEGY_DIALOG_TITLE, STRATEGY_METHODS_DIALOG_TEXT, ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        strategyMethodsDialog.waitForInput();
        generateCode(psiClass, strategyName, strategyMethodsDialog.getSelectedStuff());
    }

    private void generateCode(PsiClass psiClass, String strategyName, List<PsiMethod> selectedMethods) {
        WriteCommandAction.runWriteCommandAction(psiClass.getProject(), () -> new StrategyPatternGenerator(psiClass, strategyName, selectedMethods).generate());
    }
}
