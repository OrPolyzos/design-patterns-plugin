package ore.plugins.idea.design.patterns.structural.facade;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.PsiClass;
import ore.plugins.idea.design.patterns.base.DesignPatternAction;
import ore.plugins.idea.design.patterns.base.dialog.view.FacadeDialog;
import ore.plugins.idea.design.patterns.util.ClassNameValidator;

public class FacadeAction extends DesignPatternAction implements ClassNameValidator {
    private static final String FACADE_DIALOG_TITLE = "Facade Design Pattern";
    private static final String FACADE_INTERFACE_MESSAGE = "Name of common interface";

    @Override
    public void safeActionPerformed(AnActionEvent anActionEvent) {
        PsiClass psiClass = extractPsiClass(anActionEvent);

        FacadeDialog facadeDialog = new FacadeDialog(psiClass);
        facadeDialog.showDialog();
        facadeDialog.waitForInput();
    }

    private void generateCode(PsiClass psiClass) {
        WriteCommandAction.runWriteCommandAction(psiClass.getProject(), () -> new FacadePatternGenerator().generate());
    }
}
