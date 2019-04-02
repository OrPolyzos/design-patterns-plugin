package ore.plugins.idea.design.patterns.structural.facade;

import com.intellij.psi.*;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import ore.plugins.idea.design.patterns.base.dialog.model.FacadeModel;

import java.util.Objects;

/**
 * @author kostya05983
 * Generates facade pattern
 */
public class FacadePatternGenerator {
    private PsiClass psiClass;
    private FacadeModel facadeModel;
    private PsiPackageStatement psiPackageStatement;

    private final static String TYPE = "type";

    public FacadePatternGenerator(PsiClass psiClass, FacadeModel facadeModel) {
        this.psiClass = psiClass;
        this.facadeModel = facadeModel;
        this.psiPackageStatement = ((PsiJavaFile) psiClass.getContainingFile()).getPackageStatement();
    }

    /**
     * algorithm of generation facade pattern
     */
    public void generate() {
        PsiClass interfaceClass = generateInterfaceClass();
        PsiClass enumClass = generateTypesEnum();
        generateInheritablesClasses(enumClass, interfaceClass);
        generateMethod(enumClass);
    }

    /**
     * Create mainMethod
     */
    private void generateMethod(PsiClass enumClass) {
        final PsiElementFactory factory = JavaPsiFacade.getElementFactory(psiClass.getProject());
        final PsiClassType returnType = factory.createTypeByFQClassName(facadeModel.getMainClassReturnType());
        final PsiMethod mainMethod = factory.createMethod(facadeModel.getMainClassName(), returnType);

        for (FacadeModel.Arguments arg : facadeModel.getMainArgs()) {
            final PsiParameter parameter = factory.createParameter(arg.getName(),
                    factory.createTypeByFQClassName(arg.getType()));
            mainMethod.getParameterList().add(parameter);
        }
        final PsiParameter parameter = factory.createParameter(TYPE,
                factory.createTypeByFQClassName(enumClass.getName()));
        mainMethod.getParameterList().add(parameter);

        JavaCodeStyleManager.getInstance(psiClass.getProject()).shortenClassReferences(mainMethod);
        psiClass.add(mainMethod);
    }

    /**
     * Generate inerface for
     *
     * @return - psiClass of interface representation
     */
    private PsiClass generateInterfaceClass() {
        final PsiClass psiInterface = JavaPsiFacade.getElementFactory(psiClass.getProject())
                .createInterface(facadeModel.getInterfaceName());
        createAndAddToPsiFile(psiInterface);
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
        createAndAddToPsiFile(enumClass);
        return enumClass;
    }


    /**
     * generates inheribitles classes after generation of interface and enum for types
     */
    private void generateInheritablesClasses(PsiClass enumClass, PsiClass interfaceClass) {
        final PsiElementFactory factory = JavaPsiFacade.getElementFactory(psiClass.getProject());

        for (int i = 0; i < facadeModel.getClassArgs().size(); i++) {
            final PsiClass inheritabledClass = factory.
                    createClass(facadeModel.getClassArgs().get(i).getName());

            final PsiClassType interfaceType = factory.createType(interfaceClass);
            Objects.requireNonNull(inheritabledClass.getImplementsList())
                    .add(factory.createReferenceElementByType(interfaceType));

            final PsiClassType type = factory.createType(enumClass);
            final PsiField field = factory.createField(TYPE,
                    type);

            inheritabledClass.add(field);

            createAndAddToPsiFile(inheritabledClass);
        }

    }

    /**
     * Add psi class to file
     *
     * @param aClass - add class
     */
    private void createAndAddToPsiFile(PsiClass aClass) {
        final PsiFile file = psiClass.getContainingFile().getContainingDirectory().
                createFile(aClass.getName().concat(".java"));
        if (psiPackageStatement != null) file.addAfter(psiPackageStatement, null);
        file.add(aClass);
    }
}
