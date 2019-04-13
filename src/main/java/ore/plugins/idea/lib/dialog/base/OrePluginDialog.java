package ore.plugins.idea.lib.dialog.base;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import ore.plugins.idea.lib.exception.CancelException;
import org.jetbrains.annotations.Nullable;

public abstract class OrePluginDialog extends DialogWrapper {

    protected OrePluginDialog(@Nullable Project project) {
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
