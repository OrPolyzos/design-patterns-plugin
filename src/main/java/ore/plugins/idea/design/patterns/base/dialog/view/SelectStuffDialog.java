package ore.plugins.idea.design.patterns.base.dialog.view;

import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.psi.PsiClass;
import com.intellij.ui.CollectionListModel;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBList;
import ore.plugins.idea.design.patterns.base.dialog.ui.CustomListCellRenderer;

import javax.swing.JComponent;
import javax.swing.JPanel;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SelectStuffDialog<T> extends DesignPatternDialog {

    private int listSelectionMode;
    private LabeledComponent<JPanel> component;
    private ToolbarDecorator toolbarDecorator;
    private JBList stuffToShow;

    public SelectStuffDialog(PsiClass psiClass, Collection<T> stuffCollection, Predicate<T> stuffPredicate, String title, String componentText, int listSelectionMode) {
        super(psiClass.getProject());
        this.listSelectionMode = listSelectionMode;
        setTitle(title);
        stuffToShow = getStuffBasedOnPredicate(stuffCollection, stuffPredicate);
        createDecorator(componentText);
        component = LabeledComponent.create(toolbarDecorator.createPanel(), componentText);
        showDialog();
    }

    protected JComponent createCenterPanel() {
        return component;
    }

    public List<T> getSelectedStuff() {
        return stuffToShow.getSelectedValuesList();
    }

    private void createDecorator(String componentText) {
        toolbarDecorator = ToolbarDecorator.createDecorator(stuffToShow);
        toolbarDecorator.disableAddAction();
        toolbarDecorator.disableRemoveAction();
        component = LabeledComponent.create(toolbarDecorator.createPanel(), componentText);
    }

    private JBList getStuffBasedOnPredicate(Collection<T> stuffCollection, Predicate<T> predicateForStuffToKeep) {
        Collection<T> filteredStuff = stuffCollection.stream()
                .filter(predicateForStuffToKeep)
                .collect(Collectors.toList());
        CollectionListModel<T> collectionListModel = new CollectionListModel<>(filteredStuff);
        JBList jbStuff = new JBList(collectionListModel);
        jbStuff.setSelectionMode(listSelectionMode);
        jbStuff.setCellRenderer(new CustomListCellRenderer());
        return jbStuff;
    }
}
