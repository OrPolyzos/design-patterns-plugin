package ore.plugins.idea.design.patterns.base.dialog.view;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import ore.plugins.idea.design.patterns.exception.CancelException;
import org.jetbrains.annotations.Nullable;

public abstract class DesignPatternDialog extends DialogWrapper {

    protected DesignPatternDialog(@Nullable Project project) {
        super(project);
    }

    public void showDialog() {
        init();
        show();
    }

    public void waitForInput() {
        if (super.isOK()) {
            return;
        }
        throw new CancelException();
    }
}
