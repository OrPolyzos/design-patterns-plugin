package ore.plugins.idea.design.patterns.base.dialog.view;

import com.intellij.psi.PsiClass;
import com.intellij.ui.components.JBTextField;
import com.sun.istack.Nullable;
import ore.plugins.idea.design.patterns.base.dialog.model.FacadeModel;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.util.*;
import java.util.stream.Collectors;

public class FacadeDialog extends DesignPatternDialog {
    private JComponent jComponent;
    private String title;
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

    public FacadeDialog(@Nullable PsiClass psiClass) {
        super(psiClass.getProject());
        this.psiClass = psiClass;
        this.model = new FacadeModel();
    }


    @org.jetbrains.annotations.Nullable
    @Override
    protected JComponent createCenterPanel() {
        jComponent = new JPanel();
        jComponent.setLayout(new BoxLayout(jComponent, BoxLayout.Y_AXIS));
        jComponent.add(enterNameInterface());
        jComponent.add(enterMainClass());
        jComponent.add(addArguments());
        jComponent.add(addClasses());
        return jComponent;
    }

    private JComponent enterNameInterface() {
        nameOfInterface = new InputValueDialog(psiClass, INTERFACE_NAME);
        return nameOfInterface.createCenterPanel();
    }

    private JComponent enterMainClass() {
        MultiFieldsDialog.MultiFieldsDialogBuilder dialogBuilder = MultiFieldsDialog.MultiFieldsDialogBuilder.aCreateClassesDialog(psiClass);
        dialogBuilder.withTextBox(NAME);
        dialogBuilder.withTextBox(RETURN_TYPE);
        mainClass = dialogBuilder.build();
        return mainClass.createCenterPanel();
    }

    private JComponent addArguments() {
        MultiFieldsDialog.MultiFieldsDialogBuilder dialogBuilder = MultiFieldsDialog.MultiFieldsDialogBuilder.aCreateClassesDialog(psiClass);
        dialogBuilder.withTextBox(NAME);
        dialogBuilder.withTextBox(TYPE);
        argsMainClass = dialogBuilder.buildWithButton();
        return argsMainClass.createCenterPanel();
    }

    private JComponent addClasses() {
        MultiFieldsDialog.MultiFieldsDialogBuilder dialogBuilder = MultiFieldsDialog.MultiFieldsDialogBuilder.aCreateClassesDialog(psiClass);
        dialogBuilder.withTitle("Testststs");
        dialogBuilder.withTextBox(NAME).withTextBox(TYPE);
        namesTypesClasses = dialogBuilder.buildWithButton();
        return namesTypesClasses.createCenterPanel();
    }

    private void initModel() {
        model.setInterfaceName(nameOfInterface.getInput());
        String mainClassName = mainClass.getFields().get(NAME).get(0).getText();
        model.setMainClassName(mainClassName);

        String returnType = mainClass.getFields().get(RETURN_TYPE).get(0).getText();
        model.setMainClassReturnType(returnType);

        model.setMainArgs(getArguments(argsMainClass));
        model.setClassArgs(getArguments(namesTypesClasses));
    }

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
     * //todo maybe controller?
     *
     * @return
     */
    public FacadeModel getModel() {
        initModel();
        return model;
    }
}
