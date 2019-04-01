package ore.plugins.idea.design.patterns.creational.factory;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.PsiClass;
import com.intellij.psi.search.searches.ClassInheritorsSearch;
import ore.plugins.idea.design.patterns.base.DesignPatternAction;
import ore.plugins.idea.design.patterns.base.dialog.view.InputValueDialog;
import ore.plugins.idea.design.patterns.base.dialog.view.SelectStuffDialog;
import ore.plugins.idea.design.patterns.exception.validation.ValidationException;
import ore.plugins.idea.design.patterns.util.ClassNameValidator;
import ore.plugins.idea.design.patterns.util.PsiClassGeneratorUtils;

import javax.swing.ListSelectionModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FactoryAction extends DesignPatternAction implements ClassNameValidator {

    private static final String EMPTY_PARENTS_ERROR_MESSAGE = "Does not implement any interface and does not extend any class";
    private static final String EMPTY_IMPLEMENTORS_ERROR_MESSAGE = "You must choose at least one implementor";
    private static final String SINGLE_SELECTION_ERROR_MESSAGE = "Please provide a single selection";
    private static final String ALREADY_EXISTS_ERROR_TEMPLATE = "There is already a class with the name: %s";
    private static final String INVALID_NAME_ERROR_MESSAGE = "Invalid name";

    private static final String FACTORY_DIALOG_TITLE = "Factory Design Pattern";
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
        SelectStuffDialog<PsiClass> selector = new SelectStuffDialog<>(psiClass, parents, candidateInterface -> true, FACTORY_DIALOG_TITLE, FACTORY_INTERFACE_CHOICE_DIALOG_TEXT, ListSelectionModel.SINGLE_SELECTION);
        selector.waitForInput();
        return !selector.getSelectedStuff().isEmpty() ? Optional.of(selector.getSelectedStuff().get(0)) : Optional.empty();
    }


    private Optional<List<PsiClass>> extractSelectedImplementors(PsiClass psiClass, PsiClass selectedParent) {
        List<PsiClass> candidateImplementors = new ArrayList<>(ClassInheritorsSearch.search(selectedParent).findAll());
        SelectStuffDialog<PsiClass> selector = new SelectStuffDialog<>(psiClass, candidateImplementors, candidateImplementor -> true, FACTORY_DIALOG_TITLE, FACTORY_IMPLEMENTORS_CHOICE_DIALOG_TEXT, ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        List<PsiClass> selectedImplementors = selector.getSelectedStuff();
        selector.waitForInput();
        return selectedImplementors.isEmpty() ? Optional.empty() : Optional.of(selectedImplementors);
    }

    private void generateCode(PsiClass psiClass, PsiClass selectedInterface, String factoryName, String enumName, List<PsiClass> selectedImplementors) {
        WriteCommandAction.runWriteCommandAction(psiClass.getProject(), () -> new FactoryPatternGenerator(psiClass, selectedInterface, factoryName, enumName, selectedImplementors).generate());
    }

    @Override
    public void safeActionPerformed(AnActionEvent anActionEvent) {
        PsiClass psiClass = extractPsiClass(anActionEvent);
        List<PsiClass> parents = extractParents(psiClass).orElseThrow(() -> new ValidationException(psiClass, EMPTY_PARENTS_ERROR_MESSAGE));
        PsiClass selectedParent = extractParent(psiClass, parents).orElseThrow(() -> new ValidationException(psiClass, SINGLE_SELECTION_ERROR_MESSAGE));

        InputValueDialog selector = new InputValueDialog(psiClass, FACTORY_DIALOG_TITLE, FACTORY_NAME_DIALOG_TEXT);
        selector.waitForInput();
        String selectedName = selector.getInput().replace(FACTORY_SUFFIX, "").replace(ENUM_SUFFIX, "");
        String factoryName = validateClassNameOrThrow(psiClass, selectedName.concat(FACTORY_SUFFIX));
        String enumName = validateClassNameOrThrow(psiClass, selectedName.concat(ENUM_SUFFIX));

        List<PsiClass> selectedImplementors = extractSelectedImplementors(psiClass, selectedParent).orElseThrow(() -> new ValidationException(psiClass, EMPTY_IMPLEMENTORS_ERROR_MESSAGE));
        generateCode(psiClass, selectedParent, factoryName, enumName, selectedImplementors);
    }
}