package pos;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class QuantityRenderer extends JPanel implements TableCellRenderer {
    JTextField field=new JTextField(3);

    public QuantityRenderer(){
        setOpaque(true);
        add(field);
        field.setHorizontalAlignment(JTextField.CENTER);
        field.setEditable(false);
    }

    public Component getTableCellRendererComponent(
            JTable t,Object v,boolean s,boolean f,int r,int c){
        field.setText(String.valueOf(v));
        return this;
    }
}
