package ore.plugins.idea.design.patterns.base.dialog;

import com.intellij.psi.PsiClass;
import com.sun.istack.Nullable;

import javax.swing.*;

public class FacadeDialog extends DesignPatternDialog {
    private JComponent jComponent;
    private String title;
    private PsiClass psiClass;


    public FacadeDialog(@Nullable PsiClass psiClass) {
        super(psiClass.getProject());
        this.psiClass = psiClass;
    }


    @org.jetbrains.annotations.Nullable
    @Override
    protected JComponent createCenterPanel() {
        jComponent = new JPanel();
        jComponent.setLayout(new BoxLayout(jComponent, BoxLayout.Y_AXIS));
        jComponent.add(enterNameInterface());
        jComponent.add(enterMainClass());
        jComponent.add(addArguments());
        CreateClassesDialog.CreateClassesDialogBuilder dialogBuilder = CreateClassesDialog.CreateClassesDialogBuilder.aCreateClassesDialog(psiClass);
        dialogBuilder.withTitle("Testststs");
        dialogBuilder.withTextBox("ClassName").withTextBox("TypeName");
        CreateClassesDialog dialog = dialogBuilder.buildWithButton();
        jComponent.add(dialog.createCenterPanel());
        return jComponent;
    }

    private JComponent enterNameInterface() {
        InputValueDialog inputValueDialog = new InputValueDialog(psiClass, "Tetsts");
        return inputValueDialog.createCenterPanel();
    }

    private JComponent enterMainClass() {
        CreateClassesDialog.CreateClassesDialogBuilder dialogBuilder = CreateClassesDialog.CreateClassesDialogBuilder.aCreateClassesDialog(psiClass);
        dialogBuilder.withTextBox("name");
        dialogBuilder.withTextBox("return type");
        CreateClassesDialog dialog = dialogBuilder.build();
        return dialog.createCenterPanel();
    }

    private JComponent addArguments() {
        CreateClassesDialog.CreateClassesDialogBuilder dialogBuilder = CreateClassesDialog.CreateClassesDialogBuilder.aCreateClassesDialog(psiClass);
        dialogBuilder.withTextBox("name");
        dialogBuilder.withTextBox("type");
        CreateClassesDialog dialog = dialogBuilder.buildWithButton();
        return dialog.createCenterPanel();
    }
}
