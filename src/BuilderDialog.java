import com.intellij.ide.util.DefaultPsiElementCellRenderer;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.ui.CollectionListModel;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBList;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class BuilderDialog extends DialogWrapper {

    private final LabeledComponent<JPanel> component;
    private CollectionListModel<PsiField> fields;

    public LabeledComponent<JPanel> getComponent() {
        return component;
    }

    public CollectionListModel<PsiField> getFields() {
        return fields;
    }

    public BuilderDialog(PsiClass psiClass) {
        super(psiClass.getProject());
        init();
        setTitle("Select Fields for Builder Pattern.");

        fields = new CollectionListModel<>(psiClass.getAllFields());
        JBList<PsiField> jbFields = new JBList<>(fields);
        jbFields.setCellRenderer(new DefaultPsiElementCellRenderer());
        ToolbarDecorator decorator = ToolbarDecorator.createDecorator(jbFields);
        decorator.disableAddAction();

        JPanel jPanel = decorator.createPanel();
        component = LabeledComponent.create(jPanel, "Fields to include in builder pattern.");

    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return component;
    }
}
