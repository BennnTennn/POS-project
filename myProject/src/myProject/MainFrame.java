package myProject;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.io.*;
import java.util.*;

public class MainFrame extends JFrame {
    CardLayout cardLayout;
    JPanel cardPanel;
    private JPanel menuPanel; // field so we can refresh dynamically

    private Map<String, Integer> inventoryStock = new HashMap<>();
    private Map<String, Integer> fruitPrice = new HashMap<>();

    private JTable buyerTable;
    private DefaultTableModel buyerTableModel;

    private JTable adminTable;           // now a field
    private DefaultTableModel adminTableModel; // now a field

    private static final String INVENTORY_FILE = "fruit_data.txt";
    private static final String IMAGE_FILE = "src\\image\\sample.png";

    private JButton account, order;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame());
    }

    public MainFrame() {
        super("POINT OF SALE SYSTEM");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setContentPane(new BackgroundPanel(IMAGE_FILE));
        setLayout(new BorderLayout());

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setOpaque(false);

        // ===== TOP PANEL =====
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.setOpaque(false);
        account = new JButton("👥");
        account.setFont(new Font("DialogInput", Font.BOLD, 45));
        account.setFocusPainted(false);
        topPanel.add(account);
        add(topPanel, BorderLayout.NORTH);

        // ===== HOME SCREEN =====
        JPanel homeScreen = new JPanel(new BorderLayout());
        homeScreen.setOpaque(false);

        order = new JButton("Tap to Order");
        order.setFont(new Font("DialogInput", Font.BOLD, 75));
        order.setFocusPainted(false);

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(order);
        homeScreen.add(centerPanel, BorderLayout.CENTER);

        // ===== MENU SCREEN =====
        JPanel menuScreen = new JPanel(new BorderLayout());
        menuScreen.setOpaque(false);

        // Menu panel with buttons
        menuPanel = new JPanel(new GridLayout(0, 4, 15, 15));
        menuPanel.setOpaque(false);
        JScrollPane scrollPane = new JScrollPane(menuPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setPreferredSize(new Dimension(1200, 600));
        menuScreen.add(scrollPane, BorderLayout.CENTER);

        JPanel cartButtonsPanel = new JPanel(new GridLayout(2,0,5,5));
        cartButtonsPanel.setOpaque(false);

        JButton purchaseBtn = new JButton("Purchase");
        JButton clearBtn = new JButton("Clear Cart");
        purchaseBtn.setFont(new Font("DialogInput", Font.BOLD, 40));
        clearBtn.setFont(new Font("DialogInput", Font.BOLD, 40));
        cartButtonsPanel.add(purchaseBtn);
        cartButtonsPanel.add(clearBtn);

        menuScreen.add(cartButtonsPanel, BorderLayout.EAST);

        // Load inventory and create buttons
        loadInventoryFromFile(INVENTORY_FILE);
        createMenuButtons(menuPanel);

        // Cart table
        String[] columns = {"Fruit", "Price", "Quantity"};
        buyerTableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int r, int c) {
                return c == 2; // only quantity editable
            }
        };
        buyerTable = new JTable(buyerTableModel);
        buyerTable.setRowHeight(40);
        buyerTable.setShowHorizontalLines(false);
        buyerTable.setShowVerticalLines(false);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < buyerTable.getColumnCount(); i++) {
            buyerTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        JScrollPane tableScroll = new JScrollPane(buyerTable);
        tableScroll.setPreferredSize(new Dimension(800, 250));
        menuScreen.add(tableScroll, BorderLayout.SOUTH);

        // ===== ADMIN PANEL =====
        JPanel adminPanel = new JPanel(new BorderLayout());
        adminPanel.setOpaque(false);

        // Admin table
        String[] adminColumns = {"Fruit", "Price", "Stock"};
        adminTableModel = new DefaultTableModel(adminColumns, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        adminTable = new JTable(adminTableModel);
        JScrollPane adminScroll = new JScrollPane(adminTable);
        adminPanel.add(adminScroll, BorderLayout.CENTER);

        // Load inventory into admin table
        for (String fruit : inventoryStock.keySet()) {
            adminTableModel.addRow(new Object[]{fruit, fruitPrice.get(fruit), inventoryStock.get(fruit)});
        }

        JPanel adminButtons = new JPanel(new GridLayout(4,0,5,5));
        JButton addFruit = new JButton("Add");
        JButton removeFruit = new JButton("Remove");
        JButton editPrice = new JButton("Edit Price");
        JButton back = new JButton("Back");
        adminButtons.add(addFruit);
        adminButtons.add(removeFruit);
        adminButtons.add(editPrice);
        adminButtons.add(back);
        adminPanel.add(adminButtons, BorderLayout.EAST);

        // ===== CARDS =====
        cardPanel.add(homeScreen, "HOME");
        cardPanel.add(menuScreen, "BUY");
        cardPanel.add(adminPanel, "ADMIN");
        add(cardPanel, BorderLayout.CENTER);

        // ===== ACTIONS =====
        order.addActionListener(e -> cardLayout.show(cardPanel, "BUY"));
        purchaseBtn.addActionListener(e -> handlePurchase());
        clearBtn.addActionListener(e -> handleClearCart());

        account.addActionListener(e -> showAdminLoginDialog());

        addFruit.addActionListener(e -> handleAddFruit());
        removeFruit.addActionListener(e -> handleDeleteFruit());
        editPrice.addActionListener(e -> handleUpdateFruit());
        back.addActionListener(e -> handleExitAdmin());

        setVisible(true);
    }

    // ===== METHODS =====
    private void showAdminLoginDialog() {
        JDialog loginDialog = new JDialog(this, "Admin Login", true);
        loginDialog.setLayout(new GridBagLayout());
        loginDialog.setSize(400, 250);
        loginDialog.setLocationRelativeTo(this);

        Map<String, String> accounts = loadAccountsFromFile();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel userLabel = new JLabel("Username:");
        gbc.gridx = 0; gbc.gridy = 0;
        loginDialog.add(userLabel, gbc);

        JTextField usernameField = new JTextField();
        gbc.gridx = 1; gbc.gridy = 0;
        loginDialog.add(usernameField, gbc);

        JLabel passLabel = new JLabel("Password:");
        gbc.gridx = 0; gbc.gridy = 1;
        loginDialog.add(passLabel, gbc);

        JPasswordField passwordField = new JPasswordField();
        gbc.gridx = 1; gbc.gridy = 1;
        loginDialog.add(passwordField, gbc);

        JButton loginBtn = new JButton("Login");
        JButton cancelBtn = new JButton("Cancel");
        JPanel btnPanel = new JPanel(new FlowLayout());
        btnPanel.add(loginBtn);
        btnPanel.add(cancelBtn);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        loginDialog.add(btnPanel, gbc);

        loginBtn.addActionListener(ev -> {
            String user = usernameField.getText().trim();
            String pass = new String(passwordField.getPassword());

            if (accounts.containsKey(user) && accounts.get(user).equals(pass)) {
                JOptionPane.showMessageDialog(this, "Login successful!");
                loginDialog.dispose();
                cardLayout.show(cardPanel, "ADMIN");
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelBtn.addActionListener(ev -> loginDialog.dispose());
        loginDialog.setVisible(true);
    }

    private Map<String, String> loadAccountsFromFile() {
        Map<String, String> accounts = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("admin_accounts.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2) accounts.put(parts[0].trim(), parts[1].trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Cannot read accounts file!", "Error", JOptionPane.ERROR_MESSAGE);
        }
        return accounts;
    }

    private void loadInventoryFromFile(String filePath) {
        fruitPrice.clear();
        inventoryStock.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            reader.readLine(); // skip header
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    String fruit = parts[0];
                    int price = Integer.parseInt(parts[1]);
                    int stock = Integer.parseInt(parts[2]);
                    fruitPrice.put(fruit, price);
                    inventoryStock.put(fruit, stock);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void createMenuButtons(JPanel panel) {
        panel.removeAll();
        for (String fruit : inventoryStock.keySet()) {
            JButton btn = new JButton(fruit);
            btn.setPreferredSize(new Dimension(120, 120));
            btn.setFont(new Font("Monospaced", Font.BOLD, 18));
            btn.addActionListener(e -> {
                int price = fruitPrice.get(fruit);
                int stock = inventoryStock.get(fruit);
                openQuantityPopup(fruit, price, stock);
            });
            panel.add(btn);
        }
        panel.revalidate();
        panel.repaint();
    }

    private void saveInventoryToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(INVENTORY_FILE))) {
            writer.println("Fruit,Price,Stock");
            for (String fruit : inventoryStock.keySet()) {
                writer.println(fruit + "," + fruitPrice.get(fruit) + "," + inventoryStock.get(fruit));
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void decrementStockAfterPurchase() {
        for (int i = 0; i < buyerTableModel.getRowCount(); i++) {
            String fruit = (String) buyerTableModel.getValueAt(i, 0);
            int qty = (int) buyerTableModel.getValueAt(i, 2);
            if (inventoryStock.containsKey(fruit)) {
                inventoryStock.put(fruit, inventoryStock.get(fruit) - qty);
            }
        }
        createMenuButtons(menuPanel);
    }

    private void handleExitAdmin() { cardLayout.show(cardPanel, "HOME"); }

    private void handlePurchase() {
        if (buyerTableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Your cart is empty!", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int total = 0;
        for (int i = 0; i < buyerTableModel.getRowCount(); i++) {
            int price = (int) buyerTableModel.getValueAt(i, 1);
            int qty = (int) buyerTableModel.getValueAt(i, 2);
            total += price * qty;
        }
        String input = JOptionPane.showInputDialog(this, "Total Amount: " + total + "\nEnter payment:");
        if (input != null) {
            try {
                int payment = Integer.parseInt(input);
                if (payment >= total) {
                    int change = payment - total;
                    JOptionPane.showMessageDialog(this, "Payment accepted! Change: " + change);
                    decrementStockAfterPurchase();
                    saveInventoryToFile();
                    buyerTableModel.setRowCount(0);
                } else {
                    JOptionPane.showMessageDialog(this, "Insufficient payment!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid input!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleClearCart() { buyerTableModel.setRowCount(0); }

    private void handleAddFruit() {
        JDialog dialog = new JDialog(this, "Add Fruit", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,10,10,10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel nameLabel = new JLabel("Fruit Name:");
        JTextField nameField = new JTextField(15);
        gbc.gridx = 0; gbc.gridy = 0; dialog.add(nameLabel, gbc);
        gbc.gridx = 1; dialog.add(nameField, gbc);

        JLabel priceLabel = new JLabel("Price:");
        JTextField priceField = new JTextField(10);
        gbc.gridx = 0; gbc.gridy = 1; dialog.add(priceLabel, gbc);
        gbc.gridx = 1; dialog.add(priceField, gbc);

        JLabel stockLabel = new JLabel("Stock:");
        JTextField stockField = new JTextField(10);
        gbc.gridx = 0; gbc.gridy = 2; dialog.add(stockLabel, gbc);
        gbc.gridx = 1; dialog.add(stockField, gbc);

        JButton addBtn = new JButton("Add");
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        dialog.add(addBtn, gbc);

        addBtn.addActionListener(e -> {
            String fruit = nameField.getText().trim();
            try {
                int price = Integer.parseInt(priceField.getText().trim());
                int stock = Integer.parseInt(stockField.getText().trim());
                if (fruit.isEmpty()) { JOptionPane.showMessageDialog(dialog, "Fruit name cannot be empty!"); return; }
                fruitPrice.put(fruit, price);
                inventoryStock.put(fruit, stock);
                adminTableModel.addRow(new Object[]{fruit, price, stock});
                saveInventoryToFile();
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Price and Stock must be numbers!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void handleDeleteFruit() {
        int selectedRow = adminTable.getSelectedRow();
        if (selectedRow == -1) { JOptionPane.showMessageDialog(this, "Select a fruit to delete!"); return; }
        String fruit = (String) adminTableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete " + fruit + "?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            inventoryStock.remove(fruit);
            fruitPrice.remove(fruit);
            adminTableModel.removeRow(selectedRow);
            saveInventoryToFile();
        }
    }

    private void handleUpdateFruit() {
        int selectedRow = adminTable.getSelectedRow();
        if (selectedRow == -1) { JOptionPane.showMessageDialog(this, "Select a fruit to update!"); return; }

        String fruit = (String) adminTableModel.getValueAt(selectedRow, 0);
        int currentPrice = (int) adminTableModel.getValueAt(selectedRow, 1);
        int currentStock = (int) adminTableModel.getValueAt(selectedRow, 2);

        JDialog dialog = new JDialog(this, "Update Fruit", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,10,10,10); gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel priceLabel = new JLabel("Price:"); JTextField priceField = new JTextField(""+currentPrice,10);
        gbc.gridx=0; gbc.gridy=0; dialog.add(priceLabel, gbc); gbc.gridx=1; dialog.add(priceField, gbc);
        JLabel stockLabel = new JLabel("Stock:"); JTextField stockField = new JTextField(""+currentStock,10);
        gbc.gridx=0; gbc.gridy=1; dialog.add(stockLabel, gbc); gbc.gridx=1; dialog.add(stockField, gbc);

        JButton updateBtn = new JButton("Update");
        gbc.gridx=0; gbc.gridy=2; gbc.gridwidth=2; dialog.add(updateBtn, gbc);

        updateBtn.addActionListener(e -> {
            try {
                int price = Integer.parseInt(priceField.getText().trim());
                int stock = Integer.parseInt(stockField.getText().trim());
                fruitPrice.put(fruit, price);
                inventoryStock.put(fruit, stock);
                adminTableModel.setValueAt(price, selectedRow, 1);
                adminTableModel.setValueAt(stock, selectedRow, 2);
                saveInventoryToFile();
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Price and Stock must be numbers!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void openQuantityPopup(String fruit, int price, int stock) {
    		JDialog dialog = new JDialog(this, "Select Quantity", true);
    		dialog.setLayout(new GridBagLayout());
    		GridBagConstraints gbc = new GridBagConstraints();
    		gbc.insets = new Insets(10, 75, 10, 75);

    		int[] quantity = {1};

    		JLabel nameLabel = new JLabel(fruit);
    		nameLabel.setFont(new Font("Monospaced", Font.BOLD, 20));
    		gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 3;
    		dialog.add(nameLabel, gbc);

    		JLabel priceLabel = new JLabel("Price: " + price);
    		gbc.gridy = 1;
    		dialog.add(priceLabel, gbc);

    		JLabel qtyLabel = new JLabel("" + quantity[0], SwingConstants.CENTER);
    		qtyLabel.setFont(new Font("Monospaced", Font.BOLD, 20));

    		JButton plus = new JButton("+");
    		JButton minus = new JButton("-");
    		plus.setFocusPainted(false);
    		minus.setFocusPainted(false);

    		JLabel totalLabel = new JLabel("Total: " + (price * quantity[0]));
    		totalLabel.setFont(new Font("Monospaced", Font.BOLD, 18));

    		plus.addActionListener(e -> {
    			quantity[0]++;
    			qtyLabel.setText("" + quantity[0]);
    			totalLabel.setText("Total: " + (price * quantity[0]));
    		});
    		minus.addActionListener(e -> {
    			if (quantity[0] > 1) {
    				quantity[0]--;
    				qtyLabel.setText("" + quantity[0]);
    				totalLabel.setText("Total: " + (price * quantity[0]));
    			}
    		});

    		gbc.gridwidth = 1;
    		gbc.gridy = 2; gbc.gridx = 0; dialog.add(minus, gbc);
    		gbc.gridx = 1; dialog.add(qtyLabel, gbc);
    		gbc.gridx = 2; dialog.add(plus, gbc);

    		gbc.gridy = 3; gbc.gridx = 0; gbc.gridwidth = 3;
    		dialog.add(totalLabel, gbc);

    		JButton addToCart = new JButton("Add to Cart");
    		addToCart.setFocusPainted(false);
    		gbc.gridy = 4;
    		dialog.add(addToCart, gbc);

    		addToCart.addActionListener(e -> {
    			buyerTableModel.addRow(new Object[]{fruit, price, quantity[0]});
    			dialog.dispose();
    		});

    		dialog.pack();
    		dialog.setLocationRelativeTo(this);
    		dialog.setVisible(true);
    	}

    	class BackgroundPanel extends JPanel {
    		Image img;

    		public BackgroundPanel(String path) {
    			img = new ImageIcon(path).getImage();
    		}

    		protected void paintComponent(Graphics g) {
    			super.paintComponent(g);
    			g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
    		}
    	}
    }