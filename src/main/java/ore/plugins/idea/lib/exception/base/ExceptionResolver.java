package ore.plugins.idea.lib.exception.base;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;
import ore.plugins.idea.lib.exception.CancelException;
import ore.plugins.idea.lib.exception.InvalidFileException;
import ore.plugins.idea.lib.exception.ValidationException;

public interface ExceptionResolver {

    default void safeExecute(Runnable runnable, AnActionEvent anActionEvent) {
        try {
            runnable.run();
        } catch (InvalidFileException invalidFileException) {
            anActionEvent.getPresentation().setEnabled(false);
        } catch (ValidationException validationException) {
            Messages.showErrorDialog(validationException.getMessage(), "Validation Error");
        } catch (CancelException ignored) {
        } catch (OrePluginRuntimeException exception) {
            Messages.showErrorDialog(exception.getMessage(), "Error");
        }
    }

}
