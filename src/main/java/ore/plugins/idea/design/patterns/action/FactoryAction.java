package ore.plugins.idea.design.patterns.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.PsiClass;
import com.intellij.psi.search.searches.ClassInheritorsSearch;
import ore.plugins.idea.design.patterns.service.FactoryPatternGenerator;
import ore.plugins.idea.design.patterns.utils.ClassNameValidator;
import ore.plugins.idea.design.patterns.utils.PsiClassGeneratorUtils;
import ore.plugins.idea.lib.action.OrePluginAction;
import ore.plugins.idea.lib.dialog.InputDialog;
import ore.plugins.idea.lib.dialog.SelectStuffDialog;
import ore.plugins.idea.lib.exception.ValidationException;
import ore.plugins.idea.lib.model.ui.NameListCelRenderer;

import javax.swing.*;
import java.util.List;
import java.util.Optional;

public class FactoryAction extends OrePluginAction implements ClassNameValidator {

    private static final String EMPTY_PARENTS_ERROR_MESSAGE = "Does not implement any interface and does not extend any class";
    private static final String EMPTY_IMPLEMENTORS_ERROR_MESSAGE = "You must choose at least one implementor";
    private static final String SINGLE_SELECTION_ERROR_MESSAGE = "Please provide a single selection";

    private static final String FACTORY_DIALOG_TITLE = "Factory";
    private static final String FACTORY_INTERFACE_CHOICE_DIALOG_TEXT = "Based on interface/parent class:";
    private static final String FACTORY_NAME_DIALOG_TEXT = "Give a name for the Factory and the Enum class (\"Factory\" and \"Enum\" suffixes will be automatically added):";
    private static final String FACTORY_IMPLEMENTORS_CHOICE_DIALOG_TEXT = "Implementors to include:";


    private static final String FACTORY_SUFFIX = "Factory";
    private static final String ENUM_SUFFIX = "Enum";


    private Optional<List<PsiClass>> extractParents(PsiClass psiClass) {
        List<PsiClass> parents = PsiClassGeneratorUtils.getInterfacesAndExtends(psiClass);
        return parents.isEmpty() ? Optional.empty() : Optional.of(parents);
    }

    private Optional<PsiClass> extractParent(PsiClass psiClass, List<PsiClass> parents) {
        SelectStuffDialog<PsiClass> selector = new SelectStuffDialog<>(
                psiClass.getProject(),
                FACTORY_DIALOG_TITLE, FACTORY_INTERFACE_CHOICE_DIALOG_TEXT,
                parents, ListSelectionModel.SINGLE_SELECTION, new NameListCelRenderer());
        selector.waitForInput();
        return !selector.getSelectedStuff().isEmpty() ? Optional.of(selector.getSelectedStuff().get(0)) : Optional.empty();
    }


    private Optional<List<PsiClass>> extractSelectedImplementors(PsiClass psiClass, PsiClass selectedParent) {
        SelectStuffDialog<PsiClass> selector = new SelectStuffDialog<>(
                psiClass.getProject(),
                FACTORY_DIALOG_TITLE, FACTORY_IMPLEMENTORS_CHOICE_DIALOG_TEXT,
                ClassInheritorsSearch.search(selectedParent).findAll(), ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        selector.waitForInput();

        return selector.getSelectedStuff().isEmpty() ? Optional.empty() : Optional.of(selector.getSelectedStuff());
    }

    private void generateCode(PsiClass psiClass, PsiClass selectedInterface, String factoryName, String enumName, List<PsiClass> selectedImplementors) {
        WriteCommandAction.runWriteCommandAction(psiClass.getProject(), () -> new FactoryPatternGenerator(psiClass, selectedInterface, factoryName, enumName, selectedImplementors).generateJavaClass());
    }

    @Override
    public void safeActionPerformed(AnActionEvent anActionEvent) {
        PsiClass psiClass = extractPsiClass(anActionEvent);
        List<PsiClass> parents = extractParents(psiClass).orElseThrow(() -> new ValidationException(EMPTY_PARENTS_ERROR_MESSAGE));
        PsiClass selectedParent = extractParent(psiClass, parents).orElseThrow(() -> new ValidationException(SINGLE_SELECTION_ERROR_MESSAGE));

        InputDialog selector = new InputDialog(psiClass, FACTORY_DIALOG_TITLE, FACTORY_NAME_DIALOG_TEXT);
        selector.waitForInput();

        String selectedName = selector.getInput().replace(FACTORY_SUFFIX, "").replace(ENUM_SUFFIX, "");
        String factoryName = validateClassNameOrThrow(psiClass, selectedName.concat(FACTORY_SUFFIX));
        String enumName = validateClassNameOrThrow(psiClass, selectedName.concat(ENUM_SUFFIX));

        List<PsiClass> selectedImplementors = extractSelectedImplementors(psiClass, selectedParent).orElseThrow(() -> new ValidationException(EMPTY_IMPLEMENTORS_ERROR_MESSAGE));
        generateCode(psiClass, selectedParent, factoryName, enumName, selectedImplementors);
    }
}