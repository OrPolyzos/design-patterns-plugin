package ore.plugins.idea.lib.exception;


import ore.plugins.idea.lib.exception.base.OrePluginRuntimeException;

public class InvalidStructureException extends OrePluginRuntimeException {

    public InvalidStructureException(String message) {
        super(message);
    }

    public InvalidStructureException() {
    }
}
