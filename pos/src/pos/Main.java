package pos;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.io.*;
import java.util.*;

public class Main extends JFrame {

	CardLayout cardLayout;
	JPanel cardPanel;

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

		setContentPane(new BackgroundPanel("C:\\Users\\Student\\eclipse-workspace\\pos\\src\\image\\sample.png"));
		setLayout(new BorderLayout());

		cardLayout = new CardLayout();
		cardPanel = new JPanel(cardLayout);
		cardPanel.setOpaque(false);

		// HOME PANEL
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

		// BUY PANEL
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

		table.getColumn("Quantity").setCellRenderer(new QuantityRenderer());
		table.getColumn("Quantity").setCellEditor(new QuantityEditor());

		JScrollPane scroll=new JScrollPane(table);
		scroll.setOpaque(false);
		scroll.getViewport().setOpaque(false);
		scroll.setBorder(BorderFactory.createEmptyBorder(80,80,0,80));
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

		// ADMIN PANEL
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
			adminTable.getColumnModel().getColumn(i).setCellRenderer(center2);
		}

		JScrollPane adminScroll = new JScrollPane(adminTable);
		adminScroll.setOpaque(false);
		adminScroll.getViewport().setOpaque(false);
		adminScroll.setBorder(BorderFactory.createEmptyBorder(10,20,10,20));
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

		// CARDS
		cardPanel.add(whole,"HOME");
		cardPanel.add(buyPanel,"BUY");
		cardPanel.add(adminPanel,"ADMIN");
		add(cardPanel,BorderLayout.CENTER);

		// ACTIONS
		buybtn.addActionListener(e-> cardLayout.show(cardPanel,"BUY"));
		backFromBuy.addActionListener(e-> cardLayout.show(cardPanel,"HOME"));
		adminbtn.addActionListener(e-> adminLogin());
		backFromAdmin.addActionListener(e-> cardLayout.show(cardPanel,"HOME"));

		// Purchase, Add, Remove, Edit logic unchanged...
		// (keep same as your original code)
	}

	// FILE METHODS (unchanged)
	private void loadInventory(DefaultTableModel model){

		File file=new File("C:\\Users\\Student\\eclipse-workspace\\pos\\src\\fruit_data.txt");
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
		}}

	private void saveInventory(){
		try(PrintWriter w=
				new PrintWriter(
						new FileWriter("C:\\Users\\Student\\eclipse-workspace\\pos\\src\\fruit_data.txt"))){

			for(String f:inventoryStock.keySet()){
				w.println(f+"|"+
						fruitPrice.get(f)+"|"+
						inventoryStock.get(f));
			}
		}catch(Exception e){
			JOptionPane.showMessageDialog(this,"Save error");
		}}
	
	private void adminLogin(){
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
		}}
	
	private boolean checkCredentials(String username, String password){File file = new File("C:\\Users\\Student\\eclipse-workspace\\pos\\src\\admin_accounts.txt"); // format: user|password per line
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
	return false;}
	private void loadAdminAccounts(){
        File file=new File("C:\\Users\\Student\\eclipse-workspace\\pos\\src\\admin_accounts.txt");
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
		SwingUtilities.invokeLater(() -> new Main().setVisible(true));
	}
}
