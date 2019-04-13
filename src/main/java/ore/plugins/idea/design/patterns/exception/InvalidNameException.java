package ore.plugins.idea.design.patterns.exception;

import ore.plugins.idea.lib.exception.ValidationException;

public class InvalidNameException extends ValidationException {

    private static final String MESSAGE_TEMPLATE = "Class name '%s' is invalid.";

    public InvalidNameException(String selectedName) {
        super(String.format(MESSAGE_TEMPLATE, selectedName));
    }
}
