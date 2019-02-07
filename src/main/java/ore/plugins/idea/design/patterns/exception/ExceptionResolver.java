package ore.plugins.idea.design.patterns.exception;

import com.intellij.openapi.actionSystem.AnActionEvent;
import ore.plugins.idea.design.patterns.exception.validation.ValidationException;
import ore.plugins.idea.design.patterns.util.MessageRenderer;
import org.slf4j.Logger;

public interface ExceptionResolver extends MessageRenderer {

    default void safeExecute(Runnable runnable, AnActionEvent anActionEvent, Logger logger) {
        try {
            runnable.run();
        } catch (InvalidFileException invalidFileException) {
            anActionEvent.getPresentation().setEnabled(false);
        } catch (ValidationException validationException) {
            showAlertMessage(validationException.getPsiClass(), validationException.getMessage());
        } catch (CancelException ignored) {
        } catch (Exception exception) {
            logger.error(exception.getMessage());
        }
    }

}
