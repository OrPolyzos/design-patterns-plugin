package com.design.patterns.base.dialog;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.psi.PsiClass;
import com.intellij.ui.components.JBTextField;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class MessageBoxDialog extends DialogWrapper {

    private final JPanel component;

    public MessageBoxDialog(PsiClass psiClass, String message) {
        super(psiClass.getProject());
        setTitle("Attention");
        component = new JPanel();
        JBTextField jbTextField = new JBTextField();
        jbTextField.setText(message);
        jbTextField.setEditable(false);
        component.add(jbTextField);
        init();
        show();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return component;
    }
}
