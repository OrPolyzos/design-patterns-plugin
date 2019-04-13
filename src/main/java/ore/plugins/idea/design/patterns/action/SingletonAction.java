package ore.plugins.idea.design.patterns.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.PsiClass;
import ore.plugins.idea.design.patterns.service.SingletonPatternGenerator;
import ore.plugins.idea.lib.action.OrePluginAction;


public class SingletonAction extends OrePluginAction {

    @Override
    public void safeActionPerformed(AnActionEvent anActionEvent) {
        PsiClass psiClass = extractPsiClass(anActionEvent);
        generateCode(psiClass);
    }

    private void generateCode(PsiClass psiClass) {
        WriteCommandAction.runWriteCommandAction(psiClass.getProject(), () -> new SingletonPatternGenerator(psiClass).generateJavaClass());
    }
}
