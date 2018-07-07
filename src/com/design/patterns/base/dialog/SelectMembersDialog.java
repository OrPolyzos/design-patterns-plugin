package com.design.patterns.base.dialog;

import com.design.patterns.util.GeneratorUtils;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMember;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBList;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

public class SelectMembersDialog extends DialogWrapper {

    private JBList<PsiMember> psiMembersToShow;
    protected LabeledComponent<JPanel> component;
    protected ToolbarDecorator toolbarDecorator;

    public SelectMembersDialog(PsiClass psiClass, Collection<PsiMember> psiMembersCollection, Predicate<PsiMember> psiMemberPredicate, String title, String componentText) {
        super(psiClass.getProject());
        setTitle(title);
        psiMembersToShow = GeneratorUtils.getCandidatePsiMembersOfClassBasedOnPredicate(psiMembersCollection, psiMemberPredicate);
        createDecorator(componentText);
        component = LabeledComponent.create(toolbarDecorator.createPanel(), componentText);
        postConstruct();
    }

    protected void postConstruct(){
        init();
    }

    protected JComponent createCenterPanel() {
        return component;
    }

    public List<PsiMember> getSelectedPsiMembers() {
        return psiMembersToShow.getSelectedValuesList();
    }

    private void createDecorator(String componentText) {
        toolbarDecorator = ToolbarDecorator.createDecorator(psiMembersToShow);
        toolbarDecorator.disableAddAction();
        toolbarDecorator.disableRemoveAction();
        component = LabeledComponent.create(toolbarDecorator.createPanel(), componentText);
    }

}
