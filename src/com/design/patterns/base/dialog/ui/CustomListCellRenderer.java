package com.design.patterns.base.dialog.ui;

import com.intellij.navigation.NavigationItem;

import javax.swing.*;
import java.awt.*;

public class CustomListCellRenderer extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        if (value instanceof NavigationItem) {
            value = ((NavigationItem) value).getName();
        }
        return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
    }
}
