package ore.plugins.idea.lib.model.ui;

import com.intellij.navigation.NavigationItem;

import javax.swing.*;
import java.awt.*;

public class NameListCelRenderer extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        if (value instanceof NavigationItem) {
            value = ((NavigationItem) value).getName();
        }
        return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
    }
}
