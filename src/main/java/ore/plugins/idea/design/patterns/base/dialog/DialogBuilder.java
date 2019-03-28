package ore.plugins.idea.design.patterns.base.dialog;

import com.intellij.psi.PsiClass;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class DialogBuilder extends DesignPatternDialog {
    private JComponent jComponent;
    private String title;
    private PsiClass psiClass;

    protected DialogBuilder(@Nullable PsiClass psiClass) {
        super(psiClass.getProject());
        this.psiClass = psiClass;
        setTitle(title);
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return jComponent;
    }

    public DialogBuilder withTitle(String title) {
        this.title = title;
        return this;
    }

    public DialogBuilder withClassesDialog() {
        final CreateClassesDialog dialog = CreateClassesDialog.CreateClassesDialogBuilder
                .aCreateClassesDialog(psiClass).build();
        jComponent.add(dialog.getContentPanel());
        return this;
    }

    public DialogBuilder withInputValueDialog() {
        final InputValueDialog inputValueDialog = new InputValueDialog(psiClass);
        jComponent.add(inputValueDialog.getContentPanel());
        return this;
    }
}
