package ore.plugins.idea.design.patterns.base.dialog.view;

import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.psi.PsiClass;
import com.intellij.ui.components.JBTextField;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Component for dynamically add fields in one Line
 *
 * @author kostya05983
 */
public class MultiFieldsDialog extends DesignPatternDialog {
    private JComponent jComponent;
    private String title;
    private HashMap<String, LinkedList<JBTextField>> fields;

    protected MultiFieldsDialog(@Nullable PsiClass psiClass, @NotNull HashMap<String, LinkedList<JBTextField>> fields) {
        super(psiClass.getProject());
        setTitle(title);
        this.fields = fields;
    }

    public void setJComponent(JComponent jComponent) {
        this.jComponent = jComponent;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public HashMap<String, LinkedList<JBTextField>> getFields() {
        return fields;
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return jComponent;
    }


    public static class MultiFieldsDialogBuilder {
        private PsiClass psiClass;
        private String title = "Enter classes of interface";
        private JComponent jComponent;
        private HashMap<String, LinkedList<JBTextField>> fields;

        private MultiFieldsDialogBuilder(PsiClass psiClass) {
            this.psiClass = psiClass;
            fields = new HashMap<>();
        }

        public static MultiFieldsDialogBuilder aCreateClassesDialog(PsiClass psiClass) {
            return new MultiFieldsDialogBuilder(psiClass);
        }


        public MultiFieldsDialogBuilder withTitle(String title) {
            this.title = title;
            return this;
        }

        /**
         * Add another textBox in line
         *
         * @param title - label's name
         * @return - builder
         */
        public MultiFieldsDialogBuilder withTextBox(String title) {
            fields.put(title, new LinkedList<>());
            return this;
        }


        private void initFields() {
            JPanel linePanel2 = new JPanel();
            linePanel2.setLayout(new BoxLayout(linePanel2, BoxLayout.X_AXIS));

            for (Map.Entry<String, LinkedList<JBTextField>> entry : fields.entrySet()) {
                JBTextField field = new JBTextField();
                entry.getValue().add(field);
                linePanel2.add(LabeledComponent.create(field, entry.getKey(), BorderLayout.CENTER));
            }
            jComponent.add(linePanel2);
        }

        private void addFieldLine() {
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

            for (Map.Entry<String, LinkedList<JBTextField>> fields : fields.entrySet()) {
                JBTextField field = new JBTextField();
                fields.getValue().add(field);
                panel.add(field);
            }

            jComponent.add(panel, jComponent.getComponentCount() - 1);
            jComponent.revalidate();
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


        public MultiFieldsDialog build() {
            MultiFieldsDialog multiFieldsDialog = new MultiFieldsDialog(psiClass, fields);
            multiFieldsDialog.setTitle(title);
            initMainUI();
            multiFieldsDialog.setJComponent(jComponent);
            return multiFieldsDialog;
        }

        public MultiFieldsDialog buildWithButton() {
            MultiFieldsDialog multiFieldsDialog = new MultiFieldsDialog(psiClass, fields);
            multiFieldsDialog.setTitle(title);
            initUI();
            multiFieldsDialog.setJComponent(jComponent);
            return multiFieldsDialog;
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
