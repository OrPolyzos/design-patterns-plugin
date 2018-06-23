package design.patterns.builder;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import design.patterns.base.DesignPatternAction;

import java.util.List;
import java.util.Objects;

public class BuilderAction extends DesignPatternAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        PsiClass psiClass = getPsiClassFromContext(e);
        BuilderDialog builderDialog = new BuilderDialog(Objects.requireNonNull(psiClass));
        builderDialog.show();
        if (builderDialog.isOK()) {
            generateCode(psiClass, builderDialog.getSelectedFields());
        }
    }

    private void generateCode(PsiClass psiClass, List<PsiField> selectedFields) {
        new WriteCommandAction.Simple(psiClass.getProject(), psiClass.getContainingFile()) {
            @Override
            protected void run() {
                psiClass.add(new BuilderPatternGenerator(psiClass, selectedFields).generate());
            }
        }.execute();
    }
}
