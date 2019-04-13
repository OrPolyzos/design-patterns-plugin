package ore.plugins.idea.lib.model.pom;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "project")
@JsonIgnoreProperties(ignoreUnknown = true)
public class PsiPom {

    private PsiParent parent;
    private List<PsiDependency> dependencies;
    private PsiBuild build;

    public PsiParent getParent() {
        return parent;
    }

    public void setParent(PsiParent parent) {
        this.parent = parent;
    }

    public List<PsiDependency> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<PsiDependency> dependencies) {
        this.dependencies = dependencies;
    }

    public PsiBuild getBuild() {
        return build;
    }

    public void setBuild(PsiBuild build) {
        this.build = build;
    }
}
