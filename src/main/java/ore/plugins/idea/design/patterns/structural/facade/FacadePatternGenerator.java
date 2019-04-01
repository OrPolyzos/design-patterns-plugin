package ore.plugins.idea.design.patterns.structural.facade;

import com.intellij.psi.*;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import ore.plugins.idea.design.patterns.base.dialog.model.FacadeModel;

import java.util.Objects;

public class FacadePatternGenerator {
    private PsiClass psiClass;
    private FacadeModel facadeModel;
    private PsiPackageStatement psiPackageStatement;

    public FacadePatternGenerator(PsiClass psiClass, FacadeModel facadeModel) {
        this.psiClass = psiClass;
        this.facadeModel = facadeModel;
        this.psiPackageStatement = ((PsiJavaFile) psiClass.getContainingFile()).getPackageStatement();
    }

    public void generate() {
        PsiClass interfaceClass = generateInterfaceClass();
        PsiClass enumClass = generateTypesEnum();
        generateInheritablesClasses(enumClass, interfaceClass);
        generateMethod();
    }

    /**
     * Create mainMethod
     */
    private void generateMethod() {
        final PsiElementFactory factory = JavaPsiFacade.getElementFactory(psiClass.getProject());
        final PsiClassType returnType = factory.createTypeByFQClassName(facadeModel.getMainClassReturnType());
        final PsiMethod mainMethod = factory.createMethod(facadeModel.getMainClassName(), returnType);

        for (FacadeModel.Arguments arg : facadeModel.getMainArgs()) {
            final PsiParameter parameter = factory.createParameter(arg.getName(),
                    factory.createTypeByFQClassName(arg.getType()));
            mainMethod.getParameterList().add(parameter);
        }
        JavaCodeStyleManager.getInstance(psiClass.getProject()).shortenClassReferences(mainMethod);
        psiClass.add(mainMethod);
    }


    private PsiClass generateInterfaceClass() {
        final PsiClass psiInterface = JavaPsiFacade.getElementFactory(psiClass.getProject())
                .createInterface(facadeModel.getInterfaceName());
        final PsiFile file = psiClass.getContainingFile().getContainingDirectory().
                createFile(psiInterface.getName().concat(".java"));
        if (psiPackageStatement != null) file.addAfter(psiPackageStatement, null);
        file.add(psiInterface);
        return psiInterface;
    }

    private PsiClass generateTypesEnum() {
        final PsiElementFactory factory = JavaPsiFacade.getElementFactory(psiClass.getProject());
        final PsiClass enumClass = factory
                .createEnum(facadeModel.getInterfaceName() + "Types");

        facadeModel.getClassArgs().forEach(e -> {
            final PsiEnumConstant constant = factory.createEnumConstantFromText(e.getType(), enumClass);
            enumClass.add(constant);
        });

        final PsiFile file = psiClass.getContainingFile().getContainingDirectory().
                createFile(enumClass.getName().concat(".java"));
        if (psiPackageStatement != null) file.addAfter(psiPackageStatement, null);
        file.add(enumClass);
        return enumClass;
    }

    /**
     * generates inheribitles classes after generation of interface and enum for types
     */
    private void generateInheritablesClasses(PsiClass enumClass, PsiClass interfaceClass) {
        final PsiElementFactory factory = JavaPsiFacade.getElementFactory(psiClass.getProject());
        for (FacadeModel.Arguments arg : facadeModel.getClassArgs()) {
            final PsiClass inheritabledClass = factory.createClass(arg.getName());

            final PsiClassType interfaceType = factory.createType(interfaceClass);
            Objects.requireNonNull(inheritabledClass.getImplementsList())
                    .add(factory.createReferenceElementByType(interfaceType));

            final PsiClassType type = factory.createType(enumClass);
            final PsiField field = factory.createField(arg.getType(),
                    type);
            inheritabledClass.add(field);

            final PsiFile file = psiClass.getContainingFile().getContainingDirectory().
                    createFile(inheritabledClass.getName().concat(".java"));
            if (psiPackageStatement != null) file.addAfter(psiPackageStatement, null);
            file.add(inheritabledClass);
        }
    }
}
