package pointOfSale;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.io.*;
import java.util.*;

public class Main extends JFrame {

    CardLayout cardLayout;
    JPanel cardPanel;

    // ================= DATA =================
    private Map<String, Integer> inventoryStock = new HashMap<>();
    private Map<String, Integer> fruitPrice = new HashMap<>();
    private Map<String, String> adminAccounts = new HashMap<>();

    private DefaultTableModel buyModel;
    private DefaultTableModel adminModel;

    public Main() {

        super("POINT OF SALE SYSTEM");
        setSize(1000,700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setContentPane(new BackgroundPanel("C:\\Users\\Student\\eclipse-workspace\\POS-project-main\\pointOfSale\\src\\image\\sample.png"));
        setLayout(new BorderLayout());

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setOpaque(false);

        /* ================= HOME ================= */

        JPanel whole = new JPanel(new GridLayout(2,1));
        whole.setOpaque(false);

        JPanel upper = new JPanel(new GridBagLayout());
        upper.setOpaque(false);

        JPanel lower = new JPanel(new GridBagLayout());
        lower.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JButton buybtn = new JButton("Buy");
        buybtn.setFont(new Font("Monospaced",Font.BOLD,50));
        buybtn.setOpaque(false);
        buybtn.setFocusPainted(false);
        gbc.gridx=0; gbc.gridy=0;
        lower.add(buybtn,gbc);

        JButton adminbtn = new JButton("Admin");
        adminbtn.setFont(new Font("Monospaced",Font.BOLD,50));
        adminbtn.setOpaque(false);
        adminbtn.setFocusPainted(false);
        gbc.gridy=1;
        gbc.insets=new Insets(10,0,10,0);
        lower.add(adminbtn,gbc);

        whole.add(upper);
        whole.add(lower);

        /* ================= BUY PANEL ================= */

        JPanel buyPanel = new JPanel(new BorderLayout());
        buyPanel.setOpaque(false);

        String[] columns={"Fruit","Price","Quantity"};

        buyModel = new DefaultTableModel(columns,0){
            public boolean isCellEditable(int r,int c){
                return c==2;
            }
        };

        JTable table  =new JTable(buyModel);
        table.setRowHeight(40);
        table.setShowHorizontalLines(false);
        table.setShowVerticalLines(false);

        DefaultTableCellRenderer center=new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);

        table.getColumnModel().getColumn(0).setCellRenderer(center);
        table.getColumnModel().getColumn(1).setCellRenderer(center);

        table.getColumn("Quantity")
                .setCellRenderer(new QuantityRenderer());
        table.getColumn("Quantity")
                .setCellEditor(new QuantityEditor());

        JScrollPane scroll=new JScrollPane(table);

        // ONLY container transparent
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);

        scroll.setBorder(
                BorderFactory.createEmptyBorder(80,80,0,80));

        buyPanel.add(scroll,BorderLayout.CENTER);

        loadInventory(buyModel);
        loadAdminAccounts();

        JPanel buttons=new JPanel();
        buttons.setOpaque(false);

        JButton backFromBuy=new JButton("Back");
        JButton purchase=new JButton("Purchase");

        buttons.add(backFromBuy);
        buttons.add(purchase);

        buyPanel.add(buttons,BorderLayout.SOUTH);

        /* ================= ADMIN PANEL ================= */

        JPanel adminPanel=new JPanel(new BorderLayout());
        adminPanel.setOpaque(false);

        String[] adminColumns={"Fruit","Price","Stock"};

        adminModel=new DefaultTableModel(adminColumns,0){
            public boolean isCellEditable(int r,int c){
                return false;
            }
        };

        JTable adminTable=new JTable(adminModel);

        for(String fruit:inventoryStock.keySet()){
            adminModel.addRow(new Object[]{
                    fruit,
                    fruitPrice.get(fruit),
                    inventoryStock.get(fruit)
            });
        }

        DefaultTableCellRenderer center2=new DefaultTableCellRenderer();
        center2.setHorizontalAlignment(JLabel.CENTER);

        for(int i=0;i<adminTable.getColumnCount();i++){
            adminTable.getColumnModel()
                    .getColumn(i)
                    .setCellRenderer(center2);
        }

        JScrollPane adminScroll = new JScrollPane(adminTable);

        adminScroll.setOpaque(false);
        adminScroll.getViewport().setOpaque(false);

        adminScroll.setBorder(
                BorderFactory.createEmptyBorder(10,20,10,20));

        adminPanel.add(adminScroll,BorderLayout.CENTER);

        JPanel adminButtons=new JPanel();
        adminButtons.setOpaque(false);

        JButton backFromAdmin=new JButton("Back");
        JButton addFruit=new JButton("Add");
        JButton removeFruit=new JButton("Remove");
        JButton editPrice=new JButton("Edit Price");

        adminButtons.add(backFromAdmin);
        adminButtons.add(addFruit);
        adminButtons.add(removeFruit);
        adminButtons.add(editPrice);

        adminPanel.add(adminButtons,BorderLayout.SOUTH);

        /* ================= CARDS ================= */

        cardPanel.add(whole,"HOME");
        cardPanel.add(buyPanel,"BUY");
        cardPanel.add(adminPanel,"ADMIN");

        add(cardPanel,BorderLayout.CENTER);

        /* ================= ACTIONS ================= */

        buybtn.addActionListener(e->
                cardLayout.show(cardPanel,"BUY"));

        backFromBuy.addActionListener(e->
                cardLayout.show(cardPanel,"HOME"));

        adminbtn.addActionListener(e->adminLogin());

        backFromAdmin.addActionListener(e->
                cardLayout.show(cardPanel,"HOME"));

        /* --- ADD / REMOVE / EDIT unchanged --- */
        // (kept exactly as your logic)

        addFruit.addActionListener(e -> {

            JTextField fruitField = new JTextField();
            JTextField priceField = new JTextField();
            JTextField stockField = new JTextField();

            JPanel panel = new JPanel(new GridLayout(3,2,5,5));
            panel.add(new JLabel("Fruit Name:"));
            panel.add(fruitField);
            panel.add(new JLabel("Price:"));
            panel.add(priceField);
            panel.add(new JLabel("Stock:"));
            panel.add(stockField);

            int result = JOptionPane.showConfirmDialog(
                    this,panel,"Add New Fruit",
                    JOptionPane.OK_CANCEL_OPTION);

            if(result == JOptionPane.OK_OPTION){
                try{
                    String fruit = fruitField.getText().trim();
                    int price = Integer.parseInt(priceField.getText());
                    int stock = Integer.parseInt(stockField.getText());

                    if(fruit.isEmpty()){
                        JOptionPane.showMessageDialog(this,"Fruit name required!");
                        return;
                    }

                    fruitPrice.put(fruit, price);
                    inventoryStock.put(fruit, stock);

                    adminModel.addRow(new Object[]{fruit, price, stock});
                    buyModel.addRow(new Object[]{fruit, price, 0});

                    saveInventory();

                }catch(Exception ex){
                    JOptionPane.showMessageDialog(this,"Invalid input!");
                }
            }
        });

        removeFruit.addActionListener(e -> {

            int row = adminTable.getSelectedRow();

            if(row == -1){
                JOptionPane.showMessageDialog(this,"Select a fruit first.");
                return;
            }

            String fruit = adminModel.getValueAt(row,0).toString();

            inventoryStock.remove(fruit);
            fruitPrice.remove(fruit);

            adminModel.removeRow(row);

            for(int i=0;i<buyModel.getRowCount();i++){
                if(buyModel.getValueAt(i,0).equals(fruit)){
                    buyModel.removeRow(i);
                    break;
                }
            }

            saveInventory();
        });

        editPrice.addActionListener(e -> {

            int row = adminTable.getSelectedRow();

            if(row == -1){
                JOptionPane.showMessageDialog(this,"Select a fruit.");
                return;
            }

            String fruit = adminModel.getValueAt(row,0).toString();

            String newPriceStr = JOptionPane.showInputDialog(
                    this,"New price for " + fruit);

            if(newPriceStr == null) return;

            try{
                int newPrice = Integer.parseInt(newPriceStr);

                fruitPrice.put(fruit,newPrice);
                adminModel.setValueAt(newPrice,row,1);

                for(int i=0;i<buyModel.getRowCount();i++){
                    if(buyModel.getValueAt(i,0).equals(fruit)){
                        buyModel.setValueAt(newPrice,i,1);
                        break;
                    }
                }

                saveInventory();

            }catch(Exception ex){
                JOptionPane.showMessageDialog(this,"Invalid number!");
            }
        });
    }

    /* ================= BACKGROUND ================= */

    class BackgroundPanel extends JPanel{
        Image img;
        BackgroundPanel(String path){
            img=new ImageIcon(path).getImage();
        }
        protected void paintComponent(Graphics g){
            super.paintComponent(g);
            g.drawImage(img,0,0,getWidth(),getHeight(),this);
        }
    }

    /* ================= QUANTITY ================= */

    class QuantityRenderer extends JPanel implements TableCellRenderer{

        JTextField field=new JTextField(3);

        QuantityRenderer(){
            setOpaque(true); // keep cells solid
            add(field);
            field.setHorizontalAlignment(JTextField.CENTER);
            field.setEditable(false);
        }

        public Component getTableCellRendererComponent(
                JTable t,Object v,boolean s,
                boolean f,int r,int c){

            field.setText(String.valueOf(v));
            return this;
        }
    }

    class QuantityEditor extends AbstractCellEditor implements TableCellEditor{

        JTextField field=new JTextField(3);
        int value;

        QuantityEditor(){
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

    /* ================= FILE ================= */

    private void loadInventory(DefaultTableModel model){

        File file=new File("C:\\Users\\Student\\eclipse-workspace\\POS-project-main\\pointOfSale\\fruit_data.txt");
        model.setRowCount(0);

        if(!file.exists()) return;

        try(Scanner sc=new Scanner(file)){
            while(sc.hasNextLine()){
                String[] p=sc.nextLine().split("\\|");

                String fruit=p[0];
                int price=Integer.parseInt(p[1]);
                int stock=Integer.parseInt(p[2]);

                inventoryStock.put(fruit,stock);
                fruitPrice.put(fruit,price);

                model.addRow(new Object[]{fruit,price,0});
            }
        }catch(Exception e){
            JOptionPane.showMessageDialog(this,"Load error");
        }
    }

    private void saveInventory(){
        try(PrintWriter w=
                    new PrintWriter(
                            new FileWriter("C:\\Users\\Student\\eclipse-workspace\\POS-project-main\\fruit_data.txt"))){

            for(String f:inventoryStock.keySet()){
                w.println(f+"|"+
                        fruitPrice.get(f)+"|"+
                        inventoryStock.get(f));
            }
        }catch(Exception e){
            JOptionPane.showMessageDialog(this,"Save error");
        }
    }

    private void adminLogin() {
        JTextField u = new JTextField();
        JPasswordField p = new JPasswordField();

        JPanel panel = new JPanel(new GridLayout(2, 2));
        panel.add(new JLabel("Username")); panel.add(u);
        panel.add(new JLabel("Password")); panel.add(p);

        int r = JOptionPane.showConfirmDialog(
                this, panel, "Admin Login",
                JOptionPane.OK_CANCEL_OPTION);

        if (r == JOptionPane.OK_OPTION) {
            String username = u.getText().trim();
            String password = new String(p.getPassword());

            if (checkCredentials(username, password)) {
                cardLayout.show(cardPanel, "ADMIN");
            } else {
                JOptionPane.showMessageDialog(this, "Wrong login");
            }
        }
    }

    private boolean checkCredentials(String username, String password) {
        File file = new File("C:\\Users\\Student\\eclipse-workspace\\POS-project-main\\pointOfSale\\admin_accounts.txt"); // format: user|password per line
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 2) {
                    String fileUser = parts[0].trim();
                    String filePass = parts[1].trim();
                    if (username.equals(fileUser) && password.equals(filePass)) {
                        return true;
                    }
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading users file");
        }
        return false;
    }


    private void loadAdminAccounts(){
        File file=new File("C:\\Users\\Student\\eclipse-workspace\\POS-project-main\\pointOfSale\\admin_accounts.txt");
        if(!file.exists()) return;

        try(Scanner sc=new Scanner(file)){
            while(sc.hasNextLine()){
                String[] p=sc.nextLine().split("\\|");
                adminAccounts.put(p[0],p[1]);
            }
        }catch(Exception e){
            JOptionPane.showMessageDialog(this,"Admin load error");
        }
    }

    public static void main(String[] args){
        SwingUtilities.invokeLater(
                ()->new Main().setVisible(true));
    }
}
