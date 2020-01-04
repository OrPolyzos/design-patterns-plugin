package ore.plugins.idea.lib.action;

import static java.util.Objects.requireNonNull;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import ore.plugins.idea.lib.exception.InvalidFileException;
import ore.plugins.idea.lib.exception.base.ExceptionResolver;
import ore.plugins.idea.lib.provider.TemplateProvider;
import org.jetbrains.annotations.NotNull;

public abstract class OrePluginAction extends AnAction implements ExceptionResolver, TemplateProvider {

    private static final String JAVA_EXTENSION = "java";

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        safeExecute(() -> safeActionPerformed(anActionEvent), anActionEvent);
    }

    public abstract void safeActionPerformed(AnActionEvent anActionEvent);

    @Override
    public void update(@NotNull AnActionEvent anActionEvent) {
        safeExecute(() -> {
            extractPsiClass(anActionEvent);
            anActionEvent.getPresentation().setEnabled(true);
        }, anActionEvent);
    }

    protected PsiClass extractPsiClass(@NotNull AnActionEvent anActionEvent) {
        PsiFile psiFile = anActionEvent.getData(LangDataKeys.PSI_FILE);
        validatePsiFile(psiFile, anActionEvent);

        Editor editor = anActionEvent.getData(PlatformDataKeys.EDITOR);
        validateForNull(editor, anActionEvent);

        PsiElement elementAt = requireNonNull(psiFile).findElementAt(requireNonNull(editor).getCaretModel().getOffset());
        validateForNull(elementAt, anActionEvent);

        PsiClass psiClass = PsiTreeUtil.getParentOfType(elementAt, PsiClass.class);
        validateForNull(psiClass, anActionEvent);
        return psiClass;
    }

    private void validatePsiFile(PsiFile psiFile, AnActionEvent anActionEvent) {
        validateForNull(psiFile, anActionEvent);
        if (!psiFile.getFileType().getDefaultExtension().equals(JAVA_EXTENSION)) {
            disableContextMenu(anActionEvent);
        }
    }

    private void validateForNull(Object object, AnActionEvent anActionEvent) {
        if (object == null) {
            disableContextMenu(anActionEvent);
        }
    }

    private void disableContextMenu(AnActionEvent anActionEvent) {
        anActionEvent.getPresentation().setEnabled(false);
        throw new InvalidFileException();
    }

}