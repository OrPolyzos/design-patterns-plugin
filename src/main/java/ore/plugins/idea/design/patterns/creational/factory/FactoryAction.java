package ore.plugins.idea.design.patterns.creational.factory;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.PsiClass;
import com.intellij.psi.search.searches.ClassInheritorsSearch;
import ore.plugins.idea.design.patterns.base.DesignPatternAction;
import ore.plugins.idea.design.patterns.base.dialog.InputValueDialog;
import ore.plugins.idea.design.patterns.base.dialog.MessageBoxDialog;
import ore.plugins.idea.design.patterns.base.dialog.SelectStuffDialog;
import ore.plugins.idea.design.patterns.util.PsiClassGeneratorUtils;
import ore.plugins.idea.design.patterns.util.ValidationUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class FactoryAction extends DesignPatternAction {

    private static final String FACTORY_DIALOG_TITLE = "Factory Design Pattern";
    private static final String FACTORY_INTERFACE_CHOICE_DIALOG_TEXT = "Based on interface/parent class:";
    private static final String FACTORY_NAME_DIALOG_TEXT = "Give a name for the Factory and the Enum class (\"Factory\" and \"Enum\" suffixes will be automatically added):";
    private static final String FACTORY_IMPLEMENTORS_CHOICE_DIALOG_TEXT = "Implementors to include:";
    private static final String EMPTY_NAME_ERROR_MESSAGE = "Cannot accept empty name";
    private static final String DUPLICATE_NAME_ERROR_MESSAGE = "There is already a file with the name: ";
    private static final String EMPTY_INTERFACES_LIST_ERROR_MESSAGE = "The class does not implement any interfaces or extends and classes";
    private static final String EXACT_SIZE_INTERFACES_LIST_ERROR_MESSAGE = "You must choose exactly one interface or one parent class";
    private static final String EMPTY_IMPLEMENTORS_LIST_ERROR_MESSAGE = "You must choose at least one implementor";
    private static final String FACTORY_SUFFIX = "Factory";
    private static final String ENUM_SUFFIX = "Enum";
    private static final String JAVA_FILE_EXTENSION = ".java";

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        PsiClass psiClass = getPsiClassFromContext(anActionEvent);
        List<PsiClass> interfacesAndExtendsList = PsiClassGeneratorUtils.getInterfacesAndExtends(psiClass);
        if (ValidationUtils.validateClassesListIfNonEmpty(psiClass, interfacesAndExtendsList, EMPTY_INTERFACES_LIST_ERROR_MESSAGE)) {
            SelectStuffDialog<PsiClass> factoryInterfaceChoiceDialog = new SelectStuffDialog<>(psiClass, interfacesAndExtendsList, candidateInterface -> true, FACTORY_DIALOG_TITLE, FACTORY_INTERFACE_CHOICE_DIALOG_TEXT, ListSelectionModel.SINGLE_SELECTION);
            if (factoryInterfaceChoiceDialog.isOK()) {
                List<PsiClass> selectedInterfaces = factoryInterfaceChoiceDialog.getSelectedStuff();
                if (ValidationUtils.validateClassesListForExactSize(psiClass, selectedInterfaces, 1, EXACT_SIZE_INTERFACES_LIST_ERROR_MESSAGE)) {
                    PsiClass selectedInterface = selectedInterfaces.get(0);
                    InputValueDialog factoryNameDialog = new InputValueDialog(psiClass, FACTORY_DIALOG_TITLE, FACTORY_NAME_DIALOG_TEXT);
                    if (factoryNameDialog.isOK()) {
                        String factoryName = factoryNameDialog.getInput();
                        factoryName = factoryName.replace(FACTORY_SUFFIX, "");
                        factoryName = factoryName.replace(ENUM_SUFFIX, "");
                        if (factoryName.isEmpty()) {
                            new MessageBoxDialog(psiClass, EMPTY_NAME_ERROR_MESSAGE);
                        } else if (ValidationUtils.validateClassNameForDuplicate(psiClass, factoryName + FACTORY_SUFFIX + JAVA_FILE_EXTENSION, DUPLICATE_NAME_ERROR_MESSAGE + factoryName + FACTORY_SUFFIX + JAVA_FILE_EXTENSION)
                                && ValidationUtils.validateClassNameForDuplicate(psiClass, factoryName + ENUM_SUFFIX + JAVA_FILE_EXTENSION, DUPLICATE_NAME_ERROR_MESSAGE + factoryName + ENUM_SUFFIX + JAVA_FILE_EXTENSION)) {
                            List<PsiClass> candidateImplementors = new ArrayList<>(ClassInheritorsSearch.search(selectedInterface).findAll());
                            SelectStuffDialog<PsiClass> factoryImplementorsChoiceDialog = new SelectStuffDialog<>(psiClass, candidateImplementors, candidateImplementor -> true, FACTORY_DIALOG_TITLE, FACTORY_IMPLEMENTORS_CHOICE_DIALOG_TEXT, ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                            if (factoryImplementorsChoiceDialog.isOK() && ValidationUtils.validateClassesListIfNonEmpty(psiClass, factoryImplementorsChoiceDialog.getSelectedStuff(), EMPTY_IMPLEMENTORS_LIST_ERROR_MESSAGE)) {
                                generateCode(psiClass, selectedInterface, factoryName, factoryImplementorsChoiceDialog.getSelectedStuff());
                            }
                        }
                    }
                }
            }
        }
    }

    private void generateCode(PsiClass psiClass, PsiClass selectedInterface, String factoryName, List<PsiClass> selectedImplementors) {
        WriteCommandAction.runWriteCommandAction(psiClass.getProject(), () -> new FactoryPatternGenerator(psiClass, selectedInterface, factoryName, selectedImplementors).generate());
    }
}