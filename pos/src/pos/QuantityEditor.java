package pos;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;

public class QuantityEditor extends AbstractCellEditor implements TableCellEditor {
    JTextField field=new JTextField(3);
    int value;

    public QuantityEditor(){
        field.setHorizontalAlignment(JTextField.CENTER);
    }

    public Component getTableCellEditorComponent(
            JTable t,Object v,boolean s,int r,int c){
        value=(int)v;
        field.setText(String.valueOf(value));
        return field;
    }

    public Object getCellEditorValue(){
        return Integer.parseInt(field.getText());
    }
}
