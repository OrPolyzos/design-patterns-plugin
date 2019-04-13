package ore.plugins.idea.design.patterns.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import ore.plugins.idea.design.patterns.service.StrategyPatternGenerator;
import ore.plugins.idea.design.patterns.utils.ClassNameValidator;
import ore.plugins.idea.lib.action.OrePluginAction;
import ore.plugins.idea.lib.dialog.InputDialog;
import ore.plugins.idea.lib.dialog.SelectStuffDialog;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StrategyAction extends OrePluginAction implements ClassNameValidator {

    private static final String STRATEGY_DIALOG_TITLE = "Strategy";
    private static final String STRATEGY_METHODS_DIALOG_TEXT = "Methods to include:";
    private static final String STRATEGY_NAME_DIALOG_TEXT = "Give a name for the interface:";


    @Override
    public void safeActionPerformed(AnActionEvent anActionEvent) {
        PsiClass psiClass = extractPsiClass(anActionEvent);
        InputDialog strategyNameDialog = new InputDialog(psiClass, STRATEGY_DIALOG_TITLE, STRATEGY_NAME_DIALOG_TEXT);
        strategyNameDialog.waitForInput();
        String strategyName = validateClassNameOrThrow(psiClass, strategyNameDialog.getInput());

        List<PsiMethod> allExceptConstructor = Arrays.stream(psiClass.getMethods()).filter(method -> !method.isConstructor()).collect(Collectors.toList());
        SelectStuffDialog<PsiMethod> strategyMethodsDialog = new SelectStuffDialog<>(
                psiClass.getProject(),
                STRATEGY_DIALOG_TITLE, STRATEGY_METHODS_DIALOG_TEXT,
                allExceptConstructor, ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        strategyMethodsDialog.waitForInput();
        generateCode(psiClass, strategyName, strategyMethodsDialog.getSelectedStuff());
    }

    private void generateCode(PsiClass psiClass, String strategyName, List<PsiMethod> selectedMethods) {
        WriteCommandAction.runWriteCommandAction(psiClass.getProject(), () -> new StrategyPatternGenerator(psiClass, strategyName, selectedMethods).generateJavaClass());
    }
}
