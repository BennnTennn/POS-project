package pointOfSale;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.text.*;
import java.awt.*;
import java.io.*;
import java.util.*;

public class Main extends JFrame {

    CardLayout cardLayout;
    JPanel cardPanel;

    private Map<String,Integer> inventoryStock = new HashMap<>();
    private Map<String,Integer> fruitPrice = new HashMap<>();
    private Map<String,String> adminAccounts = new HashMap<>();

    private DefaultTableModel buyModel;
    private DefaultTableModel adminModel;

    JTable adminTable;

    public Main() {

        super("POINT OF SALE SYSTEM");
        setSize(1000,700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        /* ================= HOME ================= */
        JPanel home = new JPanel(new GridBagLayout());
        JButton buybtn = new JButton("Buy");
        JButton adminbtn = new JButton("Admin");

        buybtn.setFont(new Font("Monospaced",Font.BOLD,50));
        adminbtn.setFont(new Font("Monospaced",Font.BOLD,50));

        home.add(buybtn);
        home.add(adminbtn);

        /* ================= BUY PANEL ================= */
        JPanel buyPanel = new JPanel(new BorderLayout());

        String[] cols={"Fruit","Price","Quantity"};

        buyModel=new DefaultTableModel(cols,0){
            public boolean isCellEditable(int r,int c){return c==2;}
        };

        JTable buyTable=new JTable(buyModel);
        buyTable.setRowHeight(40);

        buyTable.getColumn("Quantity")
                .setCellRenderer(new QuantityRenderer());
        buyTable.getColumn("Quantity")
                .setCellEditor(new QuantityEditor());

        buyPanel.add(new JScrollPane(buyTable),BorderLayout.CENTER);

        loadInventory();

        JButton purchase=new JButton("Purchase");
        buyPanel.add(purchase,BorderLayout.SOUTH);

        /* ================= ADMIN PANEL ================= */
        JPanel adminPanel=new JPanel(new BorderLayout());

        String[] adminCols={"Fruit","Price","Stock"};

        adminModel=new DefaultTableModel(adminCols,0){
            public boolean isCellEditable(int r,int c){return false;}
        };

        adminTable=new JTable(adminModel);
        adminPanel.add(new JScrollPane(adminTable),BorderLayout.CENTER);

        JPanel adminButtons=new JPanel();

        JButton addFruit=new JButton("Add");
        JButton removeFruit=new JButton("Remove");
        JButton editPrice=new JButton("Edit Price");
        JButton backAdmin=new JButton("Back");

        adminButtons.add(backAdmin);
        adminButtons.add(addFruit);
        adminButtons.add(removeFruit);
        adminButtons.add(editPrice);

        adminPanel.add(adminButtons,BorderLayout.SOUTH);

        /* ================= CARDS ================= */
        cardPanel.add(home,"HOME");
        cardPanel.add(buyPanel,"BUY");
        cardPanel.add(adminPanel,"ADMIN");

        add(cardPanel);

        /* ================= NAVIGATION ================= */
        buybtn.addActionListener(e->cardLayout.show(cardPanel,"BUY"));
        backAdmin.addActionListener(e->cardLayout.show(cardPanel,"HOME"));

        adminbtn.addActionListener(e->adminLogin());

        /* ================= ADD FRUIT ================= */
        addFruit.addActionListener(e->{

            JTextField f=new JTextField();
            JTextField p=new JTextField();
            JTextField s=new JTextField();

            JPanel panel=new JPanel(new GridLayout(3,2,10,10));
            panel.add(new JLabel("Fruit:")); panel.add(f);
            panel.add(new JLabel("Price:")); panel.add(p);
            panel.add(new JLabel("Stock:")); panel.add(s);

            int result=JOptionPane.showConfirmDialog(
                    this,panel,"Add Fruit",
                    JOptionPane.OK_CANCEL_OPTION);

            if(result==JOptionPane.OK_OPTION){
                try{
                    String fruit=f.getText().trim();
                    int price=Integer.parseInt(p.getText());
                    int stock=Integer.parseInt(s.getText());

                    if(inventoryStock.containsKey(fruit)){
                        JOptionPane.showMessageDialog(this,"Fruit exists!");
                        return;
                    }

                    inventoryStock.put(fruit,stock);
                    fruitPrice.put(fruit,price);

                    adminModel.addRow(new Object[]{fruit,price,stock});
                    buyModel.addRow(new Object[]{fruit,price,0});

                    saveInventory();

                }catch(Exception ex){
                    JOptionPane.showMessageDialog(this,"Invalid input!");
                }
            }
        });

        /* ================= REMOVE ================= */
        removeFruit.addActionListener(e->{

            int row=adminTable.getSelectedRow();
            if(row==-1){
                JOptionPane.showMessageDialog(this,"Select a fruit first!");
                return;
            }

            String fruit=adminModel.getValueAt(row,0).toString();

            inventoryStock.remove(fruit);
            fruitPrice.remove(fruit);

            adminModel.removeRow(row);

            // remove from buy table
            for(int i=0;i<buyModel.getRowCount();i++){
                if(buyModel.getValueAt(i,0).equals(fruit)){
                    buyModel.removeRow(i);
                    break;
                }
            }

            saveInventory();
        });

        /* ================= EDIT PRICE ================= */
        editPrice.addActionListener(e->{

            int row=adminTable.getSelectedRow();
            if(row==-1){
                JOptionPane.showMessageDialog(this,"Select a fruit!");
                return;
            }

            String fruit=adminModel.getValueAt(row,0).toString();

            String input=JOptionPane.showInputDialog(
                    this,"New price:");

            try{
                int newPrice=Integer.parseInt(input);

                fruitPrice.put(fruit,newPrice);
                adminModel.setValueAt(newPrice,row,1);

                // update buy table
                for(int i=0;i<buyModel.getRowCount();i++){
                    if(buyModel.getValueAt(i,0).equals(fruit)){
                        buyModel.setValueAt(newPrice,i,1);
                    }
                }

                saveInventory();

            }catch(Exception ex){
                JOptionPane.showMessageDialog(this,"Invalid price!");
            }
        });

        /* ================= PURCHASE ================= */
        purchase.addActionListener(e->{

            int total=0;

            for(int i=0;i<buyModel.getRowCount();i++){
                int qty=(int)buyModel.getValueAt(i,2);
                int price=(int)buyModel.getValueAt(i,1);
                String fruit=buyModel.getValueAt(i,0).toString();

                if(qty>inventoryStock.get(fruit)){
                    JOptionPane.showMessageDialog(this,
                            "Not enough stock for "+fruit);
                    return;
                }

                total+=qty*price;
            }

            JOptionPane.showMessageDialog(this,
                    "Total: "+total);

            // deduct stock
            for(int i=0;i<buyModel.getRowCount();i++){
                String fruit=buyModel.getValueAt(i,0).toString();
                int qty=(int)buyModel.getValueAt(i,2);

                inventoryStock.put(fruit,
                        inventoryStock.get(fruit)-qty);

                buyModel.setValueAt(0,i,2);
            }

            refreshAdminTable();
            saveInventory();
        });
    }

    /* ================= LOGIN ================= */
    private void adminLogin(){

        JTextField u=new JTextField();
        JPasswordField p=new JPasswordField();

        JPanel panel=new JPanel(new GridLayout(2,2));
        panel.add(new JLabel("Username")); panel.add(u);
        panel.add(new JLabel("Password")); panel.add(p);

        int r=JOptionPane.showConfirmDialog(
                this,panel,"Admin Login",
                JOptionPane.OK_CANCEL_OPTION);

        if(r==JOptionPane.OK_OPTION){
            if("admin".equals(u.getText())
                    && "1234".equals(new String(p.getPassword()))){
                cardLayout.show(cardPanel,"ADMIN");
            }else{
                JOptionPane.showMessageDialog(this,"Wrong login");
            }
        }
    }

    /* ================= FILE ================= */
    private void loadInventory(){

        File file=new File("fruit_data.txt");

        if(!file.exists()) return;

        try(Scanner sc=new Scanner(file)){
            while(sc.hasNextLine()){
                String[] d=sc.nextLine().split("\\|");

                String fruit=d[0];
                int price=Integer.parseInt(d[1]);
                int stock=Integer.parseInt(d[2]);

                inventoryStock.put(fruit,stock);
                fruitPrice.put(fruit,price);

                buyModel.addRow(new Object[]{fruit,price,0});
                adminModel.addRow(new Object[]{fruit,price,stock});
            }
        }catch(Exception e){e.printStackTrace();}
    }

    private void saveInventory(){
        try(PrintWriter w=new PrintWriter("fruit_data.txt")){
            for(String f:inventoryStock.keySet()){
                w.println(f+"|"+fruitPrice.get(f)+"|"+inventoryStock.get(f));
            }
        }catch(Exception e){e.printStackTrace();}
    }

    private void refreshAdminTable(){
        for(int i=0;i<adminModel.getRowCount();i++){
            String fruit=adminModel.getValueAt(i,0).toString();
            adminModel.setValueAt(inventoryStock.get(fruit),i,2);
        }
    }

    /* ================= QUANTITY ================= */
    class QuantityRenderer extends JPanel implements TableCellRenderer{
        JTextField field=new JTextField(3);
        public QuantityRenderer(){
            add(field);
        }
        public Component getTableCellRendererComponent(
                JTable t,Object v,boolean s,boolean f,int r,int c){
            field.setText(v.toString());
            return this;
        }
    }

    class QuantityEditor extends AbstractCellEditor implements TableCellEditor{
        JTextField field=new JTextField(3);
        int value;
        public QuantityEditor(){ add(field); }

        public Component getTableCellEditorComponent(
                JTable t,Object v,boolean s,int r,int c){
            value=(int)v;
            field.setText(v.toString());
            return field;
        }

        public Object getCellEditorValue(){
            try{return Integer.parseInt(field.getText());}
            catch(Exception e){return 0;}
        }
    }

    public static void main(String[] args){
        SwingUtilities.invokeLater(()->new Main().setVisible(true));
    }
}