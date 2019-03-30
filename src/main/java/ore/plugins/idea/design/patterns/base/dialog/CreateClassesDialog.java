package ore.plugins.idea.design.patterns.base.dialog;

import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.psi.PsiClass;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import org.jdesktop.swingx.VerticalLayout;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.LinkedList;

/**
 * Component for dynamically add fields in one Line
 * todo in development
 *
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
        private LinkedList<String> labelsList;

        private CreateClassesDialogBuilder(PsiClass psiClass) {
            this.psiClass = psiClass;
            labelsList = new LinkedList<>();
        }

        public static CreateClassesDialogBuilder aCreateClassesDialog(PsiClass psiClass) {
            return new CreateClassesDialogBuilder(psiClass);
        }


        public CreateClassesDialogBuilder withTitle(String title) {
            this.title = title;
            return this;
        }

        /**
         * Add another textBox in line
         *
         * @param title - label's name
         * @return - builder
         */
        public CreateClassesDialogBuilder withTextBox(String title) {
            labelsList.add(title);
            return this;
        }


        private void initFields() {
            JPanel linePanel2 = new JPanel();
            linePanel2.setLayout(new BoxLayout(linePanel2, BoxLayout.X_AXIS));

            for (String label : labelsList) {
                linePanel2.add(LabeledComponent.create(createTextField(), label, BorderLayout.CENTER));
            }
            jComponent.add(linePanel2);
        }

        private JBTextField createTextField() {
            return new JBTextField();
        }

        private void initMainUI() {
            jComponent = new JPanel();
            jComponent.setLayout(new BoxLayout(jComponent, BoxLayout.Y_AXIS));

            initFields();
        }

        private void initUI() {
            initMainUI();
            JButton button = new JButton();
            button.addMouseListener(new MouseAddClickListener());
            jComponent.add(button);
        }


        private void addFieldLine() {
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

            for (int i = 0; i < labelsList.size(); i++) {
                panel.add(new JBTextField());
            }
            jComponent.add(panel, jComponent.getComponentCount() - 1);
            jComponent.revalidate();
        }

        public CreateClassesDialog build() {
            CreateClassesDialog createClassesDialog = new CreateClassesDialog(psiClass);
            createClassesDialog.setTitle(title);
            initMainUI();
            createClassesDialog.setJComponent(jComponent);
            return createClassesDialog;
        }

        public CreateClassesDialog buildWithButton() {
            CreateClassesDialog createClassesDialog = new CreateClassesDialog(psiClass);
            createClassesDialog.setTitle(title);
            initUI();
            createClassesDialog.setJComponent(jComponent);
            return createClassesDialog;
        }

        /**
         * Click listener for adding new field to enter new classes names and types
         */
        private class MouseAddClickListener implements MouseListener {

            @Override
            public void mouseClicked(MouseEvent e) {
                addFieldLine();
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
