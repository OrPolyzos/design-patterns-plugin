package ore.plugins.idea.design.patterns.base.utilities;

import com.intellij.psi.PsiModifier;

import java.util.LinkedHashMap;
import java.util.Map;

public class PsiModifierMapFactory {

    private static PsiModifierMapFactory psiModifierMapFactoryInstance;

    private PsiModifierMapFactory() {
    }

    public static PsiModifierMapFactory getInstance() {
        if (psiModifierMapFactoryInstance == null) {
            psiModifierMapFactoryInstance = new PsiModifierMapFactory();
        }
        return psiModifierMapFactoryInstance;
    }

    public Map<String, Boolean> getPsiModifierMap(PsiModifierMapEnum psiModifierMapEnum) {
        Map<String, Boolean> psiModifierMap = new LinkedHashMap<>();
        switch (psiModifierMapEnum) {
            case PUBLIC:
                psiModifierMap.put(PsiModifier.PUBLIC, true);
                break;
            case PUBLIC_STATIC:
                psiModifierMap.put(PsiModifier.PUBLIC, true);
                psiModifierMap.put(PsiModifier.STATIC, true);
                break;
            case PUBLIC_STATIC_FINAL:
                psiModifierMap.put(PsiModifier.PUBLIC, true);
                psiModifierMap.put(PsiModifier.STATIC, true);
                psiModifierMap.put(PsiModifier.FINAL, true);
                break;
            case PUBLIC_NON_STATIC:
                psiModifierMap.put(PsiModifier.PUBLIC, true);
                psiModifierMap.put(PsiModifier.STATIC, false);
                break;
            case PRIVATE:
                psiModifierMap.put(PsiModifier.PRIVATE, true);
                break;
            case PRIVATE_STATIC:
                psiModifierMap.put(PsiModifier.PRIVATE, true);
                psiModifierMap.put(PsiModifier.STATIC, true);
                break;
            case PRIVATE_NON_STATIC:
                psiModifierMap.put(PsiModifier.PRIVATE, true);
                psiModifierMap.put(PsiModifier.STATIC, false);
                break;
            case STATIC:
                psiModifierMap.put(PsiModifier.STATIC, true);
                break;
        }
        return psiModifierMap;
    }
}
