import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.ui.CollectionListModel;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBList;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;

public class BuilderDialog extends DialogWrapper {

    private final LabeledComponent<JPanel> component;
    private JList<PsiField> jFields;

    public BuilderDialog(PsiClass psiClass) {
        super(psiClass.getProject());
        setTitle("Select Fields for Builder Pattern");
        CollectionListModel<PsiField> fields = new CollectionListModel<>(psiClass.getAllFields());
        jFields = new JBList<>(fields);
        jFields.setCellRenderer(new DefaultListCellRenderer());

        ToolbarDecorator listDecorator = ToolbarDecorator.createDecorator(jFields);
        listDecorator.disableAddAction();
        listDecorator.disableRemoveAction();
        component = LabeledComponent.create(listDecorator.createPanel(), "Fields to include in builder pattern");
        init();
    }

    public List<PsiField> getSelectedFields() {
        return jFields.getSelectedValuesList();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return component;
    }
}
