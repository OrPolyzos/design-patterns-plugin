package ore.plugins.idea.lib.dialog;

import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.psi.PsiClass;
import com.intellij.ui.components.JBTextField;
import ore.plugins.idea.lib.dialog.base.OrePluginDialog;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class InputDialog extends OrePluginDialog {

    private final LabeledComponent<JPanel> component;
    private JBTextField jbTextField;

    public InputDialog(PsiClass psiClass, String title, String componentText) {
        super(psiClass.getProject());
        setTitle(title);
        JPanel jPanel = new JPanel();
        jbTextField = new JBTextField();
        jbTextField.setEditable(true);
        jbTextField.setFocusable(true);
        jbTextField.setColumns(40);
        jPanel.add(jbTextField);
        component = LabeledComponent.create(jPanel, componentText);
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