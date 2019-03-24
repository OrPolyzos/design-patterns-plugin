package ore.plugins.idea.design.patterns.base.dialog;

import com.intellij.psi.PsiClass;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Component for enter names of new Classes with their types
 * todo in development
 * @author kostya05983
 */
public class CreateClassesDialog extends DesignPatternDialog {
    private JComponent jComponent;
    private String title;

    protected CreateClassesDialog(@Nullable PsiClass psiClass) {
        super(psiClass.getProject());
        setTitle(title);
    }

    public void setJComponent(JComponent jComponent) {
        this.jComponent = jComponent;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return jComponent;
    }


    public static class CreateClassesDialogBuilder {
        private PsiClass psiClass;
        private String title = "Enter classes of interface";
        private JComponent jComponent;

        private CreateClassesDialogBuilder(PsiClass psiClass) {
            this.psiClass = psiClass;
        }

        public static CreateClassesDialogBuilder aCreateClassesDialog(PsiClass psiClass) {
            return new CreateClassesDialogBuilder(psiClass);
        }

        private void createDefault() {
            jComponent = new JPanel();

            BoxLayout verticalLayout = new BoxLayout(jComponent, BoxLayout.Y_AXIS);
            jComponent.setLayout(verticalLayout);

            addTextLabels();
            addFieldInput();

            JButton button = new JButton();
            button.addMouseListener(new MouseAddClickListener());
            jComponent.add(button);
        }

        public CreateClassesDialogBuilder withTitle(String title) {
            this.title = title;
            return this;
        }

        public CreateClassesDialog build() {
            CreateClassesDialog createClassesDialog = new CreateClassesDialog(psiClass);
            createClassesDialog.setTitle(title);

            createDefault();

            createClassesDialog.setJComponent(jComponent);
            return createClassesDialog;
        }

        private void addTextLabels() {
            JPanel panel = new JPanel();
            BoxLayout horizontalLayout = new BoxLayout(panel, BoxLayout.X_AXIS);
            panel.setLayout(horizontalLayout);

            JBLabel classNameLabel = new JBLabel();
            classNameLabel.setText("ClassName");
            panel.add(classNameLabel);

            JBLabel typeLabel = new JBLabel();
            typeLabel.setText("Type");
            panel.add(typeLabel);


            jComponent.add(panel, jComponent.getComponents().length - 1);
            jComponent.revalidate();
        }

        private void addFieldInput() {
            JPanel panel = new JPanel();
            BoxLayout horizontalLayout = new BoxLayout(panel, BoxLayout.X_AXIS);
            panel.setLayout(horizontalLayout);

            JBTextField nameClass = new JBTextField();
            nameClass.setEditable(true);
            panel.add(nameClass);

            JBTextField typeTextField = new JBTextField();
            typeTextField.setEditable(true);
            panel.add(typeTextField);

            jComponent.add(panel);
            jComponent.revalidate();
        }


        /**
         * Click listener for adding new field to enter new classes names and types
         */
        private class MouseAddClickListener implements MouseListener {

            @Override
            public void mouseClicked(MouseEvent e) {
                addFieldInput();
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        }
    }
}
