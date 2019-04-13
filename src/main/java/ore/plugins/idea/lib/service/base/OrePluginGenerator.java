package ore.plugins.idea.lib.service.base;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiManager;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.psi.search.GlobalSearchScope;
import ore.plugins.idea.lib.provider.TemplateProvider;

public abstract class OrePluginGenerator implements TemplateProvider {

    protected PsiClass psiClass;
    protected Project project;

    public OrePluginGenerator(PsiClass psiClass) {
        this.psiClass = psiClass;
        this.project = psiClass.getProject();
    }

    public PsiClass getPsiClass() {
        return psiClass;
    }

    public Project getProject() {
        return project;
    }

    protected ProjectRootManager getProjectRootManager() {
        return ProjectRootManager.getInstance(project);
    }

    protected PsiManager getPsiManager() {
        return PsiManager.getInstance(project);
    }

    protected JavaCodeStyleManager getJavaCodeStyleManager() {
        return JavaCodeStyleManager.getInstance(project);
    }

    protected JavaPsiFacade getJavaPsiFacade() {
        return JavaPsiFacade.getInstance(project);
    }

    protected PsiElementFactory getElementFactory() {
        return JavaPsiFacade.getInstance(project).getElementFactory();
    }

    protected PsiClass getClassFromQualifiedName(String qualifiedName) {
        return JavaPsiFacade.getInstance(project).findClass(qualifiedName, GlobalSearchScope.allScope(project));
    }
}
