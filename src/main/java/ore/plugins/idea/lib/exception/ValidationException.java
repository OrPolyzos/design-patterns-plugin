package ore.plugins.idea.lib.exception;

import ore.plugins.idea.lib.exception.base.OrePluginRuntimeException;

public class ValidationException extends OrePluginRuntimeException {

    public ValidationException(String message) {
        super(message);
    }

}
