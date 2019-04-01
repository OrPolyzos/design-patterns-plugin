package ore.plugins.idea.design.patterns.structural.facade;

import com.intellij.psi.PsiClass;
import ore.plugins.idea.design.patterns.base.dialog.model.FacadeModel;

public class FacadePatternGenerator {
    private PsiClass psiClass;
    private FacadeModel facadeModel;

    public FacadePatternGenerator(PsiClass psiClass, FacadeModel facadeModel) {
        this.psiClass = psiClass;
        this.facadeModel = facadeModel;
    }

    public void generate() {

    }
}
