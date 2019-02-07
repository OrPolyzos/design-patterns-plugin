package ore.plugins.idea.design.patterns.util;

import com.intellij.psi.PsiClass;
import ore.plugins.idea.design.patterns.base.dialog.MessageBoxDialog;

public interface MessageRenderer {

    default void showAlertMessage(PsiClass psiClass, String message) {
        MessageBoxDialog.MessageBoxDialogBuilder
                .aMessageBoxDialog(psiClass, message)
                .build()
                .showDialog();
    }
}
