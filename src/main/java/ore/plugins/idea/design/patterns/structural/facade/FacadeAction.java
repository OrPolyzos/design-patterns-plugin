package ore.plugins.idea.design.patterns.structural.facade;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.psi.PsiClass;
import ore.plugins.idea.design.patterns.base.DesignPatternAction;
import ore.plugins.idea.design.patterns.base.dialog.CreateClassesDialog;
import ore.plugins.idea.design.patterns.base.dialog.FacadeDialog;
import ore.plugins.idea.design.patterns.base.dialog.InputValueDialog;
import ore.plugins.idea.design.patterns.util.ClassNameValidator;

public class FacadeAction extends DesignPatternAction implements ClassNameValidator {
    private static final String FACADE_DIALOG_TITLE = "Facade Design Pattern";
    private static final String FACADE_INTERFACE_MESSAGE = "Name of common interface";

    @Override
    public void safeActionPerformed(AnActionEvent anActionEvent) {
        PsiClass psiClass = extractPsiClass(anActionEvent);
//        InputValueDialog facadeInterfaceDialog = new InputValueDialog(psiClass, FACADE_DIALOG_TITLE, FACADE_INTERFACE_MESSAGE);
//        facadeInterfaceDialog.waitForInput();
//        String interfaceName = validateClassNameOrThrow(psiClass, facadeInterfaceDialog.getInput());
//
//
//        dialog.showDialog();
//        dialog.waitForInput();
        FacadeDialog facadeDialog = new FacadeDialog(psiClass);
        facadeDialog.showDialog();
        facadeDialog.waitForInput();
    }
}
