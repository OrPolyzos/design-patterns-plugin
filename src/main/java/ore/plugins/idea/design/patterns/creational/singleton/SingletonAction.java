package ore.plugins.idea.design.patterns.creational.singleton;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.PsiClass;
import ore.plugins.idea.design.patterns.base.DesignPatternAction;
import org.jetbrains.annotations.NotNull;


public class SingletonAction extends DesignPatternAction {

    private SingletonPatternGenerator singletonPatternGenerator = new SingletonPatternGenerator();

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        PsiClass psiClass = getPsiClassFromContext(anActionEvent);
        generateCode(psiClass);
    }

    @Override
    public void update(@NotNull AnActionEvent anActionEvent) {
        PsiClass psiClass = getPsiClassFromContext(anActionEvent);
        anActionEvent.getPresentation().setEnabled(psiClass != null);
    }

    private void generateCode(PsiClass psiClass) {
        WriteCommandAction.runWriteCommandAction(psiClass.getProject(), () -> singletonPatternGenerator.generate(psiClass));
    }
}
