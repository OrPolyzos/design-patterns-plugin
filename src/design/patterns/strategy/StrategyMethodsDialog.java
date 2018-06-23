package design.patterns.strategy;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.ui.CollectionListModel;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBList;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class StrategyMethodsDialog extends DialogWrapper {

    private final LabeledComponent<JPanel> component;
    private JList<PsiMethod> jMethods;

    StrategyMethodsDialog(PsiClass psiClass) {
        super(psiClass.getProject());
        setTitle("Strategy Pattern");
        CollectionListModel<PsiMethod> fields = new CollectionListModel<>(psiClass.getMethods());
        jMethods = new JBList<>(fields);
        jMethods.setCellRenderer(new DefaultListCellRenderer());
        ToolbarDecorator listDecorator = ToolbarDecorator.createDecorator(jMethods);
        listDecorator.disableAddAction();
        listDecorator.disableRemoveAction();
        component = LabeledComponent.create(listDecorator.createPanel(), "Methods to include in Strategy Pattern:");
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return component;
    }

    List<PsiMethod> getSelectedMethods() {
        return jMethods.getSelectedValuesList();
    }
}
