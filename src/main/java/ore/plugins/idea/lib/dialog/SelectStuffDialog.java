package ore.plugins.idea.lib.dialog;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.ui.CollectionListModel;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBList;
import ore.plugins.idea.lib.dialog.base.OrePluginDialog;

import javax.swing.*;
import java.util.Collection;
import java.util.List;

public class SelectStuffDialog<T> extends OrePluginDialog {

    private String title;
    private String message;
    private Collection<T> optionsList;
    private int listSelectionModel;
    private ListCellRenderer<? super T> listCellRenderer;

    private LabeledComponent<JPanel> labeledComponent;
    private JBList<T> optionsJbList;

    public SelectStuffDialog(Project project, String title, String message, Collection<T> optionsList, int listSelectionModel) {
        super(project);
        setCommonFields(title, message, optionsList, listSelectionModel);
        setup();
        showDialog();
    }

    private void setCommonFields(String title, String message, Collection<T> optionsList, int listSelectionModel) {
        this.title = title;
        this.message = message;
        this.optionsList = optionsList;
        this.listSelectionModel = listSelectionModel;
    }

    public SelectStuffDialog(Project project, String title, String message, Collection<T> optionsList, int listSelectionModel, ListCellRenderer<? super T> listCellRenderer) {
        super(project);
        setCommonFields(title, message, optionsList, listSelectionModel);
        this.listCellRenderer = listCellRenderer;
        setup();
        showDialog();
    }


    private void setup() {
        setTitle(title);
        setupOptionsJbList();
        setupLabeledComponent();
    }

    private void setupOptionsJbList() {
        optionsJbList = new JBList<>(new CollectionListModel<>(optionsList));
        optionsJbList.setSelectionMode(listSelectionModel);
        if (listCellRenderer != null) {
            optionsJbList.setCellRenderer(listCellRenderer);
        }
    }

    @Override
    protected JComponent createCenterPanel() {
        return labeledComponent;
    }


    private void setupLabeledComponent() {
        ToolbarDecorator toolbarDecorator = ToolbarDecorator.createDecorator(optionsJbList);
        toolbarDecorator.disableAddAction();
        toolbarDecorator.disableRemoveAction();
        labeledComponent = LabeledComponent.create(toolbarDecorator.createPanel(), message);
    }

    public List<T> getSelectedStuff() {
        return optionsJbList.getSelectedValuesList();
    }

}
