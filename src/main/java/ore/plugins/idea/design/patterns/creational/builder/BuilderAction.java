package ore.plugins.idea.design.patterns.creational.builder;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiModifier;
import ore.plugins.idea.design.patterns.base.DesignPatternAction;
import ore.plugins.idea.design.patterns.base.dialog.SelectStuffDialog;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;

public class BuilderAction extends DesignPatternAction {

    private static final String BUILDER_DIALOG_TITLE = "Strategy Design Pattern";
    private static final String BUILDER_FIELDS_DIALOG_TEXT = "Fields to include:";
    private static final String BUILDER_MANDATORY_FIELDS_DIALOG_TEXT = "Mandatory fields:";

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        PsiClass psiClass = getPsiClassFromContext(anActionEvent);
        SelectStuffDialog<PsiField> includedFieldsDialog = new SelectStuffDialog<>(psiClass, Arrays.asList(psiClass.getFields()), psiField -> true, BUILDER_DIALOG_TITLE, BUILDER_FIELDS_DIALOG_TEXT, ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        if (includedFieldsDialog.isOK()) {
            SelectStuffDialog<PsiField> mandatoryFieldsDialog = new SelectStuffDialog<>(psiClass, includedFieldsDialog.getSelectedStuff(), psiField -> true, BUILDER_DIALOG_TITLE, BUILDER_MANDATORY_FIELDS_DIALOG_TEXT, ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            if (mandatoryFieldsDialog.isOK()) {
                generateCode(psiClass, includedFieldsDialog.getSelectedStuff(), mandatoryFieldsDialog.getSelectedStuff());
            }
        }
    }

    @Override
    public void update(@NotNull AnActionEvent anActionEvent) {
        super.update(anActionEvent);
        PsiClass psiClass = getPsiClassFromContext(anActionEvent);
        if (psiClass.getModifierList() != null && psiClass.getModifierList().hasModifierProperty(PsiModifier.STATIC)){
            anActionEvent.getPresentation().setEnabled(false);
        }
    }

    private void generateCode(PsiClass psiClass, List<PsiField> includedFields, List<PsiField> mandatoryFields) {
        WriteCommandAction.runWriteCommandAction(psiClass.getProject(), () -> new BuilderPatternGenerator(psiClass, includedFields, mandatoryFields).generate());
    }

}
