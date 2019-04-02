package ore.plugins.idea.design.patterns.base.dialog.model;

import java.util.List;

/**
 * @author kostya05983
 */
public class FacadeModel {
    private String interfaceName;
    private String mainClassName;
    private String mainClassReturnType;
    private List<Arguments> mainArgs;
    private List<Arguments> classArgs;

    public String getMainClassReturnType() {
        return mainClassReturnType;
    }

    public void setMainClassReturnType(String mainClassReturnType) {
        this.mainClassReturnType = mainClassReturnType;
    }

    public String getMainClassName() {
        return mainClassName;
    }

    public void setMainClassName(String mainClassName) {
        this.mainClassName = mainClassName;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public List<Arguments> getMainArgs() {
        return mainArgs;
    }

    public void setMainArgs(List<Arguments> mainArgs) {
        this.mainArgs = mainArgs;
    }

    public List<Arguments> getClassArgs() {
        return classArgs;
    }

    public void setClassArgs(List<Arguments> classArgs) {
        this.classArgs = classArgs;
    }

    public static class Arguments {
        private String name;
        private String type;

        public Arguments(String name, String type) {
            this.name = name;
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}
