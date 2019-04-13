package ore.plugins.idea.lib.service;

import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import ore.plugins.idea.lib.exception.base.OrePluginRuntimeException;
import ore.plugins.idea.lib.provider.ConstructorProvider;
import ore.plugins.idea.lib.service.base.OrePluginGenerator;

import java.io.File;
import java.util.Objects;

public abstract class JavaCodeGenerator extends OrePluginGenerator implements ConstructorProvider {

    protected static final String DEFAULT_JAVA_SRC_PATH = "/src/main/java/";

    public JavaCodeGenerator(PsiClass psiClass) {
        super(psiClass);
    }

    public abstract void generateJavaClass();

    protected VirtualFile createFolderIfNotExists(String path) {
        File packageFile = new File(path);
        if (!packageFile.exists() && !packageFile.mkdirs()) {
            throw new OrePluginRuntimeException(String.format("Failed to generateJavaClass package at '%s'", packageFile));
        }
        return LocalFileSystem.getInstance().refreshAndFindFileByIoFile(packageFile);
    }

    protected PsiJavaFile createJavaFileInDirectory(PsiDirectory psiDirectory, String resourceRepositoryName) {
        return (PsiJavaFile) psiDirectory.createFile(resourceRepositoryName.concat(".java"));
    }

    protected PsiJavaFile createJavaFileInDirectoryWithPackage(PsiDirectory psiDirectory, String resourceServiceName, String fullPackagePath) {
        PsiJavaFile resourceServiceFile = createJavaFileInDirectory(psiDirectory, resourceServiceName);

        if (fullPackagePath.length() > 0) {
            PsiPackageStatement packageStatement = getElementFactory().createPackageStatement(fullPackagePath);
            resourceServiceFile.addAfter(packageStatement, null);
        }
        return resourceServiceFile;
    }

    protected void addQualifiedAnnotationNameTo(String qualifiedAnnotationName, PsiMember psiMember) {
        Objects.requireNonNull(psiMember.getModifierList()).addAnnotation(qualifiedAnnotationName);
    }

    protected void addOverrideTo(PsiMethod resourceFieldGetter) {
        addQualifiedAnnotationNameTo("java.lang.Override", resourceFieldGetter);
    }

    protected void addQualifiedExtendsToClass(String qualifiedExtendsName, PsiClass psiClass) {
        PsiJavaCodeReferenceElement psiJavaCodeReferenceElement = getElementFactory().createReferenceFromText(qualifiedExtendsName, psiClass);
        PsiReferenceList extendsList = psiClass.getExtendsList();
        Objects.requireNonNull(extendsList).add(psiJavaCodeReferenceElement);
    }

    protected void addQualifiedImplementsToClass(String qualifiedImplementsName, PsiClass psiClass) {
        PsiJavaCodeReferenceElement psiJavaCodeReferenceElement = getElementFactory().createReferenceFromText(qualifiedImplementsName, psiClass);
        PsiReferenceList implementsList = psiClass.getImplementsList();
        Objects.requireNonNull(implementsList).add(psiJavaCodeReferenceElement);
    }

}
