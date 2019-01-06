package ore.plugins.idea.design.patterns.behavioral.strategy;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import ore.plugins.idea.design.patterns.base.DesignPatternAction;
import ore.plugins.idea.design.patterns.base.dialog.InputValueDialog;
import ore.plugins.idea.design.patterns.base.dialog.MessageBoxDialog;
import ore.plugins.idea.design.patterns.base.dialog.SelectStuffDialog;
import ore.plugins.idea.design.patterns.util.ValidationUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;

public class StrategyAction extends DesignPatternAction {

    private static final String STRATEGY_DIALOG_TITLE = "Strategy Design Pattern";
    private static final String STRATEGY_METHODS_DIALOG_TEXT = "Methods to include:";
    private static final String STRATEGY_NAME_DIALOG_TEXT = "Give a name for the interface:";
    private static final String EMPTY_NAME_ERROR_MESSAGE = "Cannot accept empty name.";
    private static final String DUPLICATE_NAME_ERROR_MESSAGE = "There is already a file with the name: ";

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        safeExecute(() -> {
            PsiClass psiClass = extractPsiClass(anActionEvent);
            InputValueDialog strategyNameDialog = new InputValueDialog(psiClass, STRATEGY_DIALOG_TITLE, STRATEGY_NAME_DIALOG_TEXT);
            if (strategyNameDialog.isOK()) {
                String strategyName = strategyNameDialog.getInput();
                if (strategyName.isEmpty()) {
                    new MessageBoxDialog(psiClass, EMPTY_NAME_ERROR_MESSAGE);
                } else if (ValidationUtils.validateClassNameForDuplicate(psiClass, strategyName, DUPLICATE_NAME_ERROR_MESSAGE + strategyName + ".")) {
                    SelectStuffDialog<PsiMethod> strategyMethodsDialog = new SelectStuffDialog<>(psiClass, Arrays.asList(psiClass.getMethods()), psiMethod -> !psiMethod.isConstructor(), STRATEGY_DIALOG_TITLE, STRATEGY_METHODS_DIALOG_TEXT, ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                    if (strategyMethodsDialog.isOK()) {
                        generateCode(psiClass, strategyName, strategyMethodsDialog.getSelectedStuff());
                    }
                }
            }
        }, anActionEvent, LOGGER);

    }

    private void generateCode(PsiClass psiClass, String strategyName, List<PsiMethod> selectedMethods) {
        WriteCommandAction.runWriteCommandAction(psiClass.getProject(), () -> new StrategyPatternGenerator(psiClass, strategyName, selectedMethods).generate());
    }
}
