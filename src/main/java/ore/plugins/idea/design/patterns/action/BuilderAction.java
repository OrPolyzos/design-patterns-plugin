package ore.plugins.idea.design.patterns.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiModifier;
import ore.plugins.idea.design.patterns.service.BuilderPatternGenerator;
import ore.plugins.idea.lib.action.OrePluginAction;
import ore.plugins.idea.lib.dialog.SelectStuffDialog;
import ore.plugins.idea.lib.exception.InvalidFileException;
import ore.plugins.idea.lib.model.ui.NameListCelRenderer;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BuilderAction extends OrePluginAction {

    private static final String BUILDER_DIALOG_TITLE = "Builder";
    private static final String BUILDER_FIELDS_DIALOG_MESSAGE = "Fields to include:";
    private static final String BUILDER_MANDATORY_FIELDS_DIALOG_MESSAGE = "Mandatory fields:";


    @Override
    public void safeActionPerformed(AnActionEvent anActionEvent) {
        PsiClass psiClass = extractPsiClass(anActionEvent);
        List<PsiField> candidateFields = Arrays.stream(psiClass.getFields())
                .filter(this::makeSureIsNotStatic)
                .collect(Collectors.toList());

        SelectStuffDialog<PsiField> includedFieldsDialog = new SelectStuffDialog<>(
                psiClass.getProject(),
                BUILDER_DIALOG_TITLE, BUILDER_FIELDS_DIALOG_MESSAGE,
                candidateFields, ListSelectionModel.MULTIPLE_INTERVAL_SELECTION, new NameListCelRenderer());
        includedFieldsDialog.waitForInput();


        SelectStuffDialog<PsiField> mandatoryFieldsDialog = new SelectStuffDialog<>(
                psiClass.getProject(),
                BUILDER_DIALOG_TITLE, BUILDER_MANDATORY_FIELDS_DIALOG_MESSAGE,
                includedFieldsDialog.getSelectedStuff(), ListSelectionModel.MULTIPLE_INTERVAL_SELECTION, new NameListCelRenderer());
        mandatoryFieldsDialog.waitForInput();
        generateCode(psiClass, includedFieldsDialog.getSelectedStuff(), mandatoryFieldsDialog.getSelectedStuff());
    }


    @Override
    public void update(@NotNull AnActionEvent anActionEvent) {
        safeExecute(() -> {
            super.update(anActionEvent);
            PsiClass psiClass = extractPsiClass(anActionEvent);
            if (psiClass.getModifierList() != null && psiClass.getModifierList().hasModifierProperty(PsiModifier.STATIC)) {
                throw new InvalidFileException();

            }
        }, anActionEvent);
    }

    private void generateCode(PsiClass psiClass, List<PsiField> includedFields, List<PsiField> mandatoryFields) {
        WriteCommandAction.runWriteCommandAction(psiClass.getProject(), () -> new BuilderPatternGenerator(psiClass, includedFields, mandatoryFields).generateJavaClass());
    }

    private boolean makeSureIsNotStatic(PsiField psiField) {
        return psiField.getModifierList() == null || (!psiField.getModifierList().hasModifierProperty(PsiModifier.STATIC) && !psiField.getModifierList().hasModifierProperty(PsiModifier.FINAL));
    }

}
