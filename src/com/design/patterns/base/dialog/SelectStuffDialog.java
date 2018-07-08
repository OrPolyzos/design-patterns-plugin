package com.design.patterns.base.dialog;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.psi.PsiClass;
import com.intellij.ui.CollectionListModel;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBList;

import javax.swing.*;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SelectStuffDialog<T> extends DialogWrapper {

    private LabeledComponent<JPanel> component;
    private ToolbarDecorator toolbarDecorator;
    private JBList<T> stuffToShow;

    public SelectStuffDialog(PsiClass psiClass, Collection<T> stuffCollection, Predicate<T> stuffPredicate, String title, String componentText) {
        super(psiClass.getProject());
        setTitle(title);
        stuffToShow = getStuffBasedOnPredicate(stuffCollection, stuffPredicate);
        createDecorator(componentText);
        component = LabeledComponent.create(toolbarDecorator.createPanel(), componentText);
        postConstruct();
    }

    private void postConstruct() {
        init();
        show();
    }

    protected JComponent createCenterPanel() {
        return component;
    }

    public List<T> getSelectedStuff() {
        return stuffToShow.getSelectedValuesList();
    }

    private void createDecorator(String componentText) {
        toolbarDecorator = ToolbarDecorator.createDecorator(stuffToShow);
        toolbarDecorator.disableAddAction();
        toolbarDecorator.disableRemoveAction();
        component = LabeledComponent.create(toolbarDecorator.createPanel(), componentText);
    }

    private JBList<T> getStuffBasedOnPredicate(Collection<T> stuffCollection, Predicate<T> predicateForStuffToKeep) {
        Collection<T> filteredStuff = stuffCollection.stream()
                .filter(predicateForStuffToKeep)
                .collect(Collectors.toList());
        CollectionListModel<T> collectionListModel = new CollectionListModel<>(filteredStuff);
        JBList<T> jbStuff = new JBList<>(collectionListModel);
        jbStuff.setCellRenderer(new DefaultListCellRenderer());
        return jbStuff;
    }
}
