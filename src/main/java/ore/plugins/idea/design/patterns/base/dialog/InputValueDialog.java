package ore.plugins.idea.design.patterns.base.dialog;

import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.psi.PsiClass;
import com.intellij.ui.components.JBTextField;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;
import javax.swing.JPanel;

public class InputValueDialog extends DesignPatternDialog {

    private final LabeledComponent<JPanel> component;
    private JBTextField jbTextField;

    public InputValueDialog(PsiClass psiClass, String title, String componentText) {
        super(psiClass.getProject());
        setTitle(title);
        JPanel jPanel = new JPanel();
        jbTextField = new JBTextField();
        jbTextField.setEditable(true);
        jbTextField.setColumns(30);
        jPanel.add(jbTextField);
        component = LabeledComponent.create(jPanel, componentText);
        showDialog();
    }

    public InputValueDialog(PsiClass psiClass) {
        super(psiClass.getProject());
        setTitle("");
        JPanel jPanel = new JPanel();
        jbTextField = new JBTextField();
        jbTextField.setEditable(true);
        jbTextField.setColumns(30);
        jPanel.add(jbTextField);
        component = LabeledComponent.create(jPanel, "");
        showDialog();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return component;
    }

    public String getInput() {
        return jbTextField.getText().trim();
    }
}