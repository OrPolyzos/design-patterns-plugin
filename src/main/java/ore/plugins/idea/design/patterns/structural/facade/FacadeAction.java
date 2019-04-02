package ore.plugins.idea.design.patterns.structural.facade;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.PsiClass;
import ore.plugins.idea.design.patterns.base.DesignPatternAction;
import ore.plugins.idea.design.patterns.base.dialog.model.FacadeModel;
import ore.plugins.idea.design.patterns.base.dialog.view.FacadeDialog;
import ore.plugins.idea.design.patterns.util.ClassNameValidator;

public class FacadeAction extends DesignPatternAction implements ClassNameValidator {
    @Override
    public void safeActionPerformed(AnActionEvent anActionEvent) {
        PsiClass psiClass = extractPsiClass(anActionEvent);

        FacadeDialog facadeDialog = new FacadeDialog(psiClass);
        facadeDialog.setResizable(false);
        facadeDialog.showDialog();
        facadeDialog.waitForInput();
        generateCode(psiClass, facadeDialog.getModel());
    }

    private void generateCode(PsiClass psiClass, FacadeModel facadeModel) {
        WriteCommandAction.runWriteCommandAction(psiClass.getProject(),
                () -> new FacadePatternGenerator(psiClass, facadeModel).generate());
    }
}
