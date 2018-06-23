package design.patterns.strategy;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.psi.PsiClass;
import com.intellij.ui.components.JBTextField;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class StrategyNameDialog extends DialogWrapper {

    private final LabeledComponent<JPanel> component;
    private JBTextField jbTextField;

    StrategyNameDialog(PsiClass psiClass) {
        super(psiClass.getProject());
        setTitle("Strategy Pattern");
        JPanel jPanel = new JPanel();
        jbTextField = new JBTextField();
        jbTextField.setEditable(true);
        jbTextField.setColumns(30);
        jPanel.add(jbTextField);
        component = LabeledComponent.create(jPanel, "Give a name for the interface:");
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return component;
    }

    String getName() {
        return jbTextField.getText();
    }
}