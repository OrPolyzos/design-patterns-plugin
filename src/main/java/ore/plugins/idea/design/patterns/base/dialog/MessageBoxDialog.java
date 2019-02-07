package ore.plugins.idea.design.patterns.base.dialog;

import com.intellij.psi.PsiClass;
import com.intellij.ui.components.JBTextField;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;
import javax.swing.JPanel;

public class MessageBoxDialog extends DesignPatternDialog {

    private String myTitle;
    private JComponent jComponent;

    private MessageBoxDialog(PsiClass psiClass) {
        super(psiClass.getProject());
        setTitle(myTitle);
        showDialog();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return jComponent;
    }

    public void setMyTitle(String title) {
        this.myTitle = title;
    }

    public void setJComponent(JComponent jComponent) {
        this.jComponent = jComponent;
    }

    public static class MessageBoxDialogBuilder {
        private PsiClass psiClass;
        private String title = "Attention";
        private String message;
        private JComponent jComponent;

        private MessageBoxDialogBuilder(PsiClass psiClass, String message) {
            this.psiClass = psiClass;
            this.message = message;
        }

        public static MessageBoxDialogBuilder aMessageBoxDialog(PsiClass psiClass, String message) {
            return new MessageBoxDialogBuilder(psiClass, message);
        }

        public MessageBoxDialogBuilder withTitle(String title) {
            this.title = title;
            return this;
        }

        public MessageBoxDialogBuilder withJComponent(JComponent jComponent) {
            this.jComponent = jComponent;
            return this;
        }

        private JComponent constructDefaultJComponent() {
            JComponent jComponent = new JPanel();
            JBTextField jbTextField = new JBTextField();
            jbTextField.setText(message);
            jbTextField.setEditable(false);
            jComponent.add(jbTextField);
            return jComponent;
        }

        public MessageBoxDialog build() {
            MessageBoxDialog messageBoxDialog = new MessageBoxDialog(psiClass);
            messageBoxDialog.setMyTitle(title);
            if (jComponent == null) {
                jComponent = constructDefaultJComponent();
            }
            messageBoxDialog.setJComponent(jComponent);
            return messageBoxDialog;
        }
    }
}
