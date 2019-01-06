package ore.plugins.idea.design.patterns.exception;

import com.intellij.openapi.actionSystem.AnActionEvent;
import org.slf4j.Logger;

public interface ExceptionResolver {

    default void handleException(Exception exception, AnActionEvent anActionEvent, Logger logger) {
        if (exception instanceof InvalidFileException) {
            anActionEvent.getPresentation().setEnabled(false);
        } else {
            logger.error(exception.getMessage());
        }
    }

    default void safeExecute(Runnable runnable, AnActionEvent anActionEvent, Logger logger) {
        try {
            runnable.run();
        } catch (Exception exception) {
            handleException(exception, anActionEvent, logger);
        }
    }
}
