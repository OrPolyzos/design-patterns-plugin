package ore.plugins.idea.lib.model.pom;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PsiBuild {

    private List<PsiPlugin> plugins;

    public List<PsiPlugin> getPlugins() {
        return plugins;
    }

    public void setPlugins(List<PsiPlugin> plugins) {
        this.plugins = plugins;
    }
}
