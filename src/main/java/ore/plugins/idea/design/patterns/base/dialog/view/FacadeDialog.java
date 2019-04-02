package ore.plugins.idea.design.patterns.base.dialog.view;

import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.psi.PsiClass;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.sun.istack.Nullable;
import ore.plugins.idea.design.patterns.base.dialog.model.FacadeModel;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class represents concrete facade dialog
 *
 * @author kostya05983
 */
public class FacadeDialog extends DesignPatternDialog {
    private JComponent jComponent;
    private PsiClass psiClass;

    private InputValueDialog nameOfInterface;
    private MultiFieldsDialog mainClass;
    private MultiFieldsDialog argsMainClass;
    private MultiFieldsDialog namesTypesClasses;

    private FacadeModel model;

    private final static String NAME = "Name";
    private final static String RETURN_TYPE = "Return type";
    private final static String TYPE = "Type";
    private final static String INTERFACE_NAME = "Enter name of interface";

    private final static String MAIN_CLASS_TITLE = "Enter main class";
    private final static String ARGS_OF_MAIN_CLASS = "Main class's args";
    private final static String INHERIBATLES_CLASSES = "Inheribatles classes";

    private final static String TITLE = "Facade Pattern Generator";

    public FacadeDialog(@Nullable PsiClass psiClass) {
        super(psiClass.getProject());
        this.psiClass = psiClass;
        this.model = new FacadeModel();
        setTitle(TITLE);
    }


    @org.jetbrains.annotations.Nullable
    @Override
    protected JComponent createCenterPanel() {
        jComponent = new JPanel();
        jComponent.setLayout(new BoxLayout(jComponent, BoxLayout.Y_AXIS));
        jComponent.add(enterNameInterface());
        jComponent.add(Box.createVerticalStrut(10));
        jComponent.add(enterMainClass());
        jComponent.add(Box.createVerticalStrut(10));
        jComponent.add(addArguments());
        jComponent.add(Box.createVerticalStrut(10));
        jComponent.add(addClasses());
        return jComponent;
    }

    private JComponent enterNameInterface() {
        nameOfInterface = new InputValueDialog(psiClass, INTERFACE_NAME, 32);
        return nameOfInterface.createCenterPanel();
    }

    private JComponent enterMainClass() {
        MultiFieldsDialog.MultiFieldsDialogBuilder dialogBuilder = MultiFieldsDialog.MultiFieldsDialogBuilder.aCreateClassesDialog(psiClass);
        dialogBuilder.withTextBox(NAME);
        dialogBuilder.withTextBox(RETURN_TYPE);
        mainClass = dialogBuilder.build();
        final JComponent panel = mainClass.createCenterPanel();
        return LabeledComponent.create(panel, MAIN_CLASS_TITLE);
    }

    private JComponent addArguments() {
        MultiFieldsDialog.MultiFieldsDialogBuilder dialogBuilder = MultiFieldsDialog.MultiFieldsDialogBuilder.aCreateClassesDialog(psiClass);
        dialogBuilder.withTextBox(NAME);
        dialogBuilder.withTextBox(TYPE);
        argsMainClass = dialogBuilder.buildWithButton();
        final JComponent panel = argsMainClass.createCenterPanel();
        return LabeledComponent.create(panel, ARGS_OF_MAIN_CLASS);
    }

    private JComponent addClasses() {
        MultiFieldsDialog.MultiFieldsDialogBuilder dialogBuilder = MultiFieldsDialog.MultiFieldsDialogBuilder.aCreateClassesDialog(psiClass);
        dialogBuilder.withTextBox(NAME).withTextBox(TYPE);
        namesTypesClasses = dialogBuilder.buildWithButton();
        final JComponent panel = namesTypesClasses.createCenterPanel();
        return LabeledComponent.create(panel, INHERIBATLES_CLASSES);
    }

    /**
     * init models before return this
     */
    private void initModel() {
        model.setInterfaceName(nameOfInterface.getInput());
        String mainClassName = mainClass.getFields().get(NAME).get(0).getText();
        model.setMainClassName(mainClassName);

        String returnType = mainClass.getFields().get(RETURN_TYPE).get(0).getText();
        model.setMainClassReturnType(returnType);

        model.setMainArgs(getArguments(argsMainClass));
        model.setClassArgs(getArguments(namesTypesClasses));
    }

    /**
     * get arguments from multiFiledsDialog
     *
     * @param dialog - dialog for getting arguments
     * @return - arguments in pairs
     */
    private List<FacadeModel.Arguments> getArguments(MultiFieldsDialog dialog) {
        final List<String> names = dialog.getFields().get(NAME).stream()
                .map(JTextComponent::getText).collect(Collectors.toList());
        final List<String> types = dialog.getFields().get(TYPE).stream()
                .map(JTextComponent::getText).collect(Collectors.toList());

        final List<FacadeModel.Arguments> mainArgs = new ArrayList<>();
        for (int i = 0; i < names.size(); i++) {
            mainArgs.add(new FacadeModel.Arguments(names.get(i), types.get(i)));
        }
        return mainArgs;
    }

    /**
     * @return model for generation facade plugin
     */
    public FacadeModel getModel() {
        initModel();
        return model;
    }
}
