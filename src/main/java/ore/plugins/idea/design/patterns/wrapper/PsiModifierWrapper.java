package ore.plugins.idea.design.patterns.wrapper;

import com.google.common.collect.ImmutableList;
import com.intellij.psi.PsiMember;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.util.PsiUtil;

import java.util.List;

public enum PsiModifierWrapper {
    PUBLIC(ImmutableList.of(PsiModifier.PUBLIC)),
    PUBLIC_STATIC(ImmutableList.of(PsiModifier.PUBLIC, PsiModifier.STATIC)),
    PUBLIC_STATIC_SYNCHRONIZED(ImmutableList.of(PsiModifier.PUBLIC, PsiModifier.STATIC, PsiModifier.SYNCHRONIZED)),
    PRIVATE(ImmutableList.of(PsiModifier.PRIVATE)),
    STATIC(ImmutableList.of(PsiModifier.STATIC)),
    PRIVATE_STATIC(ImmutableList.of(PsiModifier.PRIVATE, PsiModifier.STATIC));

    private List<String> modifiers;

    PsiModifierWrapper(List<String> modifiers) {
        this.modifiers = modifiers;
    }

    public List<String> getModifiers() {
        return modifiers;
    }

    public void applyModifier(PsiMember psiMember) {
        getModifiers().forEach(psiModifier -> PsiUtil.setModifierProperty(psiMember, psiModifier, true));
    }
}