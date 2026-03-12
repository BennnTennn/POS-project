package myProject;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class MainFrame extends JFrame {
	// ============================================================
	// UI COMPONENTS
	// ============================================================
	private CardLayout cardLayout;
	private JPanel cardPanel, menuPanel;

	private JTable buyerTable, adminTable;

	private DefaultTableModel buyerTableModel;
	private DefaultTableModel adminTableModel;

	// ============================================================
	// SYSTEM MANAGERS
	// ============================================================
	static InventoryManager inventoryManager;
	private AccountManager accountManager;

	// Background image path
	private static final String IMAGE_FILE = "C:\\Users\\User\\Downloads\\POS-project-main\\POS-project-main\\myProject\\src\\image\\bckgrnd.png";

	// ============================================================
	// MAIN METHOD
	// ============================================================
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new MainFrame());
	}

	// ============================================================
	// CONSTRUCTOR
	// ============================================================
	public MainFrame() {

		super("POINT OF SALE SYSTEM");

		// Initialize managers
		inventoryManager = new InventoryManager();
		accountManager = new AccountManager();

		// Frame settings
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);

		// Background panel
		setContentPane(new BackgroundPanel(IMAGE_FILE));
		setLayout(new BorderLayout());

		// Card layout (for switching screens)
		cardLayout = new CardLayout();
		cardPanel = new JPanel(cardLayout);

		// ============================================================
		// TOP PANEL (ACCOUNT BUTTON)
		// ============================================================
		JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		topPanel.setOpaque(false);

		JButton accountBtn = new JButton("👥");
		accountBtn.setBackground(Color.decode("#FFA95A"));
		//		accountBtn.setOpaque(false);
		accountBtn.setFocusPainted(false);
		accountBtn.setFont(new Font("DialogInput", Font.BOLD, 45));

		topPanel.add(accountBtn);
		add(topPanel, BorderLayout.NORTH);

		// ============================================================
		// HOME SCREEN
		// ============================================================
		JPanel homeScreen = new JPanel(new BorderLayout());
		homeScreen.setOpaque(false);

		JButton orderBtn = new JButton("Tap to Order");
		orderBtn.setBackground(Color.decode("#A0D585"));
		orderBtn.setFont(new Font("DialogInput", Font.BOLD, 70));
		orderBtn.setFocusPainted(false);

		JPanel centerPanel = new JPanel(new GridBagLayout());
		centerPanel.setOpaque(false);
		centerPanel.add(orderBtn);

		homeScreen.add(centerPanel, BorderLayout.CENTER);

		// ============================================================
		// MENU SCREEN (BUYER SIDE)
		// ============================================================
		JPanel menuScreen = new JPanel(new BorderLayout());
		menuScreen.setOpaque(false);

		// Fruit buttons container
		menuPanel = new JPanel(new GridLayout(0, 3, 5, 5));
		menuPanel.setOpaque(false);

		// Scroll panel for menu
		JScrollPane scrollPane = new JScrollPane(
				menuPanel,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
				);

		scrollPane.setPreferredSize(new Dimension(450, 450));
		scrollPane.setOpaque(false);
		scrollPane.getViewport().setOpaque(false);

		// Faster scroll
		scrollPane.getVerticalScrollBar().setUnitIncrement(50);

		menuScreen.add(scrollPane, BorderLayout.CENTER);

		// ============================================================
		// CART BUTTONS
		// ============================================================
		JButton purchaseBtn = new JButton("Purchase");
		purchaseBtn.setBackground(Color.decode("#A0D585"));
		JButton clearBtn = new JButton("Clear Cart");
		clearBtn.setBackground(Color.decode("#FF5A5A"));

		JPanel cartButtonsPanel = new JPanel(new GridLayout(2, 0, 5, 5));
		cartButtonsPanel.add(purchaseBtn);
		cartButtonsPanel.add(clearBtn);

		menuScreen.add(cartButtonsPanel, BorderLayout.EAST);

		// ============================================================
		// BUYER TABLE (CART)
		// ============================================================
		String[] buyerCols = {"Fruit", "Price", "Quantity", "Add", "Minus"};

		buyerTableModel = new DefaultTableModel(buyerCols, 0) {

			// Allow editing only for Add/Minus buttons
			@Override
			public boolean isCellEditable(int r, int c) {
				return c == 3 || c == 4;
			}
		};

		buyerTable = new JTable(buyerTableModel);

		// Add button column
		buyerTable.getColumn("Add")
		.setCellRenderer(new ButtonRenderer());
		buyerTable.getColumn("Add")
		.setCellEditor(new ButtonEditor(new JCheckBox(), true, buyerTableModel));

		// Minus button column
		buyerTable.getColumn("Minus")
		.setCellRenderer(new ButtonRenderer());
		buyerTable.getColumn("Minus")
		.setCellEditor(new ButtonEditor(new JCheckBox(), false, buyerTableModel));

		// Column sizes
		buyerTable.getColumnModel().getColumn(0).setPreferredWidth(500);
		buyerTable.getColumnModel().getColumn(1).setPreferredWidth(65);
		buyerTable.getColumnModel().getColumn(2).setPreferredWidth(65);
		buyerTable.getColumnModel().getColumn(3).setPreferredWidth(20);
		buyerTable.getColumnModel().getColumn(4).setPreferredWidth(20);

		JScrollPane buyerScroll = new JScrollPane(buyerTable);
		buyerScroll.setOpaque(false);

		menuScreen.add(buyerScroll, BorderLayout.SOUTH);

		// ============================================================
		// ADMIN PANEL
		// ============================================================
		JPanel adminPanel = new JPanel(new BorderLayout());
		adminPanel.setOpaque(false);
		String[] adminCols = {"Fruit", "Price", "Stock"};
		adminTableModel = new DefaultTableModel(adminCols, 0);
		adminTableModel = new DefaultTableModel(adminCols, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false; // no cell editable
			}
		};

		adminTable = new JTable(adminTableModel);
		adminTable.setOpaque(false);
		JScrollPane adminScroll = new JScrollPane(adminTable);
		adminScroll.setOpaque(false);

		adminPanel.add(adminScroll, BorderLayout.CENTER);

		// Admin buttons
		JPanel adminButtons = new JPanel(new GridLayout(4, 0, 5, 5));

		JButton addFruitBtn = new JButton("Add");
		addFruitBtn.setBackground(Color.decode("#7EACB5"));
		JButton removeFruitBtn = new JButton("Remove");
		removeFruitBtn.setBackground(Color.decode("#DC3545"));
		JButton editFruitBtn = new JButton("Edit");
		editFruitBtn.setBackground(Color.decode("#FFC107"));
		JButton backBtn = new JButton("Back");
		backBtn.setBackground(Color.decode("#6C757D"));

		adminButtons.add(addFruitBtn);
		adminButtons.add(removeFruitBtn);
		adminButtons.add(editFruitBtn);
		adminButtons.add(backBtn);

		adminPanel.add(adminButtons, BorderLayout.EAST);


		// ============================================================
		// CARD LAYOUT SCREENS
		// ============================================================
		cardPanel.setOpaque(false);

		cardPanel.add(homeScreen, "HOME");
		cardPanel.add(menuScreen, "BUY");
		cardPanel.add(adminPanel, "ADMIN");

		add(cardPanel, BorderLayout.CENTER);

		// ============================================================
		// BUTTON ACTIONS
		// ============================================================
		orderBtn.addActionListener(e -> cardLayout.show(cardPanel, "BUY"));
		purchaseBtn.addActionListener(e -> handlePurchase());
		clearBtn.addActionListener(e -> buyerTableModel.setRowCount(0));

		accountBtn.addActionListener(e -> showAdminLoginDialog());

		addFruitBtn.addActionListener(e -> handleAddFruit());
		removeFruitBtn.addActionListener(e -> handleDeleteFruit());
		editFruitBtn.addActionListener(e -> handleUpdateFruit());

		backBtn.addActionListener(e -> cardLayout.show(cardPanel, "HOME"));

		// Initial load
		refreshMenuButtons();
		refreshAdminTable();

		setVisible(true);
	}

	// ============================================================
	// REFRESH MENU BUTTONS
	// Rebuilds fruit buttons based on inventory
	// ============================================================
	private void refreshMenuButtons() {

		menuPanel.removeAll();

		for (String fruit : inventoryManager.getStock().keySet()) {

			JButton btn = new JButton(fruit);

			// Transparent button styling
			btn.setOpaque(false);
			btn.setContentAreaFilled(true);
			btn.setBorderPainted(true);
			btn.setFocusPainted(false);

			// Button size
			btn.setPreferredSize(new Dimension(120, 120));
			btn.setMinimumSize(new Dimension(120, 120));

			// Open quantity popup
			btn.addActionListener(e ->
			openQuantityPopup(
					fruit,
					inventoryManager.getPrices().get(fruit),
					inventoryManager.getStock().get(fruit)
					)
					);

			menuPanel.add(btn);
		}

		menuPanel.revalidate();
		menuPanel.repaint();
	}

	// ============================================================
	// REFRESH ADMIN TABLE
	// ============================================================
	private void refreshAdminTable() {

		adminTableModel.setRowCount(0);

		for (String fruit : inventoryManager.getStock().keySet()) {

			adminTableModel.addRow(new Object[]{
					fruit,
					inventoryManager.getPrices().get(fruit),
					inventoryManager.getStock().get(fruit)
			});
		}
	}

	// ============================================================
	// ADMIN LOGIN DIALOG
	// ============================================================
	private void showAdminLoginDialog() {
		JFrame loginFrame = new JFrame("Admin Login");
		loginFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		loginFrame.setLayout(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(10, 10, 10, 10); // padding around components
		gbc.fill = GridBagConstraints.HORIZONTAL;

		JTextField userField = new JTextField(100);
		JPasswordField passField = new JPasswordField(100);
		JButton loginBtn = new JButton("Login");
		loginBtn.setBackground(Color.decode("#A0D585"));
		Font fieldFont = new Font("DialogInput", Font.PLAIN, 20);
		userField.setFont(fieldFont);
		passField.setFont(fieldFont);
		loginBtn.setFont(new Font("DialogInput", Font.BOLD, 22));

		// Username label
		gbc.gridx = 0;
		gbc.gridy = 0;
		loginFrame.add(new JLabel("Username:"), gbc);

		// Username field
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		loginFrame.add(userField, gbc);

		// Password label
		gbc.gridx = 0;
		gbc.gridy = 1;
		loginFrame.add(new JLabel("Password:"), gbc);

		// Password field
		gbc.gridx = 1;
		gbc.gridy = 1;
		loginFrame.add(passField, gbc);

		// Login button (span two columns)
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 2;
		gbc.anchor = GridBagConstraints.CENTER;
		loginFrame.add(loginBtn, gbc);

		loginFrame.setSize(400, 200);
		loginFrame.setLocationRelativeTo(this);
		loginFrame.setVisible(true);

		// Action listener
		loginBtn.addActionListener(ev -> {
			if (accountManager.validateLogin(
					userField.getText(),
					new String(passField.getPassword()))) {

				JOptionPane.showMessageDialog(loginFrame, "Login successful!");
				loginFrame.dispose();
				cardLayout.show(cardPanel, "ADMIN");

			} else {
				JOptionPane.showMessageDialog(
						loginFrame,
						"Invalid credentials!",
						"Error",
						JOptionPane.ERROR_MESSAGE
						);
			}
		});
	}

	// ============================================================
	// ADMIN ACTIONS
	// ============================================================

	private void handleAddFruit() {
		// Create a panel with labels and text fields
		JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));

		JTextField fruitField = new JTextField();
		JTextField priceField = new JTextField();
		JTextField stockField = new JTextField();

		panel.add(new JLabel("Fruit name:"));
		panel.add(fruitField);
		panel.add(new JLabel("Price:"));
		panel.add(priceField);
		panel.add(new JLabel("Stock:"));
		panel.add(stockField);

		int result = JOptionPane.showConfirmDialog(
				this,
				panel,
				"Add New Fruit",
				JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE
				);

		if (result == JOptionPane.OK_OPTION) {
			try {
				String fruit = fruitField.getText().trim();
				int price = Integer.parseInt(priceField.getText().trim());
				int stock = Integer.parseInt(stockField.getText().trim());

				inventoryManager.addFruit(fruit, price, stock);
				refreshAdminTable();
				refreshMenuButtons();
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(this, "Price and Stock must be numbers.", "Input Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}


	private void handleDeleteFruit() {
		int row = adminTable.getSelectedRow();
		if (row == -1) return;

		String fruit = (String) adminTableModel.getValueAt(row, 0);

		int choice = JOptionPane.showConfirmDialog(
				null,
				"Are you sure you want to delete '" + fruit + "'?",
				"Confirm Deletion",
				JOptionPane.YES_NO_OPTION
				);

		if (choice == JOptionPane.YES_OPTION) {
			inventoryManager.removeFruit(fruit);
			refreshAdminTable();
			refreshMenuButtons();
		}
	}

	private void handleUpdateFruit() {
		int row = adminTable.getSelectedRow();
		if (row == -1) return;

		String fruit = (String) adminTableModel.getValueAt(row, 0);

		// Panel with fields
		JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));

		JTextField fruitField = new JTextField(fruit); // pre-filled, read-only
		fruitField.setEditable(false);

		JTextField priceField = new JTextField();
		JTextField stockField = new JTextField();

		panel.add(new JLabel("Fruit:"));
		panel.add(fruitField);
		panel.add(new JLabel("New Price:"));
		panel.add(priceField);
		panel.add(new JLabel("New Stock:"));
		panel.add(stockField);

		int result = JOptionPane.showConfirmDialog(
				this,
				panel,
				"Update Fruit",
				JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE
				);

		if (result == JOptionPane.OK_OPTION) {
			try {
				int price = Integer.parseInt(priceField.getText().trim());
				int stock = Integer.parseInt(stockField.getText().trim());

				inventoryManager.updateFruit(fruit, price, stock);
				refreshAdminTable();
				refreshMenuButtons();

			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(
						this,
						"Price and Stock must be valid numbers.",
						"Input Error",
						JOptionPane.ERROR_MESSAGE
						);
			}
		}
	}

	// ============================================================
	// PURCHASE PROCESS
	// ============================================================
private void handlePurchase() {
    if (buyerTableModel.getRowCount() == 0) {
        JOptionPane.showMessageDialog(this, "Cart is empty!");
        return;
    }

    int total = 0;
    List<Object[]> purchasedItems = new ArrayList<>();

    // Step 1: Calculate total, prepare items
    for (int i = 0; i < buyerTableModel.getRowCount(); i++) {
        String fruit = (String) buyerTableModel.getValueAt(i, 0);
        int price = (int) buyerTableModel.getValueAt(i, 1);
        int qty   = (int) buyerTableModel.getValueAt(i, 2);

        purchasedItems.add(new Object[]{fruit, price, qty});
        total += price * qty;
    }

    // Step 2: Ask for payment
    String input = JOptionPane.showInputDialog(
        this,
        "Total: " + total + "\nEnter payment:"
    );

    if (input == null) return;

    try {
        int payment = Integer.parseInt(input);

        if (payment < total) {
            JOptionPane.showMessageDialog(
                this,
                "Insufficient payment!",
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
            return; // stop here, no stock deduction
        }

        // Step 3: Validate stock only after payment is OK
        for (Object[] item : purchasedItems) {
            String fruit = (String) item[0];
            int price    = (int) item[1];
            int qty      = (int) item[2];

            Integer stock = inventoryManager.getStock().get(fruit);
            if (stock == null || qty > stock) {
                JOptionPane.showMessageDialog(
                    this,
                    "Not enough stock for " + fruit,
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            int newStock = stock - qty;
            inventoryManager.updateFruit(
                fruit,
                inventoryManager.getPrices().get(fruit),
                newStock
            );
        }

        // Step 4: Finalize purchase
        buyerTableModel.setRowCount(0);
        refreshAdminTable();
        refreshMenuButtons();
        showReceipt(purchasedItems, total, payment);

    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(
            this,
            "Invalid input!",
            "Error",
            JOptionPane.ERROR_MESSAGE
        );
    }
}

	// ============================================================
	// RECEIPT GENERATION
	// ============================================================
	private void showReceipt(List<Object[]> purchasedItems, int total, int payment) {

		StringBuilder receipt = new StringBuilder();

		receipt.append(String.format("%-15s %-10s %10s%n",
				"Fruit", "Amount", "Price"));

		receipt.append("---------------------------------------------\n");

		for (Object[] item : purchasedItems) {

			String fruit = (String) item[0];
			int price = (int) item[1];
			int qty = (int) item[2];

			int lineTotal = price * qty;

			receipt.append(
					String.format("%-15s %-10s %10d%n",
							fruit,
							qty + " pcs",
							lineTotal)
					);
		}

		receipt.append("---------------------------------------------\n");

		receipt.append(String.format("%-27s %10d%n", "TOTAL", total));
		receipt.append(String.format("%-27s %10d%n", "PAYMENT", payment));
		receipt.append(String.format("%-27s %10d%n", "CHANGE", payment - total));

		JTextArea textArea = new JTextArea(receipt.toString());

		textArea.setEditable(false);
		textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));

		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setPreferredSize(new Dimension(350, 400));

		JOptionPane.showMessageDialog(
				this,
				scrollPane,
				"Receipt",
				JOptionPane.INFORMATION_MESSAGE
				);
	}

	// ============================================================
	// QUANTITY POPUP (FRUIT SELECTION)
	// ============================================================
	private void openQuantityPopup(String fruit, int price, int stock) {

		JDialog dialog = new JDialog(this, "Select Quantity", true);

		dialog.setLayout(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(10, 100, 10, 100);

		final int[] quantity = {1};

		JLabel nameLabel = new JLabel(fruit);
		nameLabel.setFont(new Font("Monospaced", Font.BOLD, 20));

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 3;

		dialog.add(nameLabel, gbc);

		JLabel priceLabel = new JLabel("Price: " + price);

		gbc.gridy = 1;

		dialog.add(priceLabel, gbc);

		JLabel qtyLabel = new JLabel("" + quantity[0], SwingConstants.CENTER);
		qtyLabel.setFont(new Font("Monospaced", Font.BOLD, 20));

		JButton plus = new JButton("+");
		JButton minus = new JButton("-");

		JLabel totalLabel = new JLabel("Total: " + (price * quantity[0]));
		totalLabel.setFont(new Font("Monospaced", Font.BOLD, 18));

		// Increase quantity
		plus.addActionListener(e -> {

			if (quantity[0] < stock) {

				quantity[0]++;

				qtyLabel.setText("" + quantity[0]);

				totalLabel.setText("Total: " + (price * quantity[0]));

			} else {

				JOptionPane.showMessageDialog(
						dialog,
						"Only " + stock + " available!",
						"Stock Limit",
						JOptionPane.WARNING_MESSAGE
						);
			}
		});

		// Decrease quantity
		minus.addActionListener(e -> {

			if (quantity[0] > 1) {

				quantity[0]--;

				qtyLabel.setText("" + quantity[0]);

				totalLabel.setText("Total: " + (price * quantity[0]));
			}
		});

		gbc.gridwidth = 1;

		gbc.gridy = 2;
		gbc.gridx = 0;
		dialog.add(minus, gbc);

		gbc.gridx = 1;
		dialog.add(qtyLabel, gbc);

		gbc.gridx = 2;
		dialog.add(plus, gbc);

		gbc.gridy = 3;
		gbc.gridx = 0;
		gbc.gridwidth = 3;

		dialog.add(totalLabel, gbc);

		JButton addToCart = new JButton("Add to Cart");

		gbc.gridy = 4;

		dialog.add(addToCart, gbc);

		addToCart.addActionListener(e -> {

			if (quantity[0] > stock) {

				JOptionPane.showMessageDialog(
						dialog,
						"Not enough stock available!",
						"Error",
						JOptionPane.ERROR_MESSAGE
						);
				return;
			}

			buyerTableModel.addRow(
					new Object[]{fruit, price, quantity[0]}
					);

			dialog.dispose();
		});

		dialog.pack();
		dialog.setLocationRelativeTo(this);
		dialog.setVisible(true);
	}
}

/*
 * ============================================================
 * BUTTON RENDERER
 * Used to display buttons inside JTable cells
 * ============================================================
 */
class ButtonRenderer extends JButton implements TableCellRenderer {

	public ButtonRenderer() {
		setOpaque(true);
	}

	@Override
	public Component getTableCellRendererComponent(
			JTable table,
			Object value,
			boolean isSelected,
			boolean hasFocus,
			int row,
			int column) {

		setText((value == null) ? "" : value.toString());
		return this;
	}
}

/*
 * ============================================================
 * BUTTON EDITOR
 * Handles button clicks inside JTable cells
 * ============================================================
 */
class ButtonEditor extends DefaultCellEditor {

	private JButton button;

	private boolean isAdd;

	private int row;

	private DefaultTableModel model;

	public ButtonEditor(JCheckBox checkBox, boolean isAdd, DefaultTableModel model) {

		super(checkBox);

		this.isAdd = isAdd;
		this.model = model;

		button = new JButton(isAdd ? "+" : "-");

		button.addActionListener(e -> fireEditingStopped());
	}

	@Override
	public Component getTableCellEditorComponent(
			JTable table,
			Object value,
			boolean isSelected,
			int row,
			int column) {

		this.row = row;

		return button;
	}

	@Override
	public Object getCellEditorValue() {

		int qty = (int) model.getValueAt(row, 2);

		if (isAdd) {

			String fruit = (String) model.getValueAt(row, 0);

			int stock = MainFrame.inventoryManager.getStock().get(fruit);

			if (qty < stock) {

				model.setValueAt(qty + 1, row, 2);

			} else {

				JOptionPane.showMessageDialog(
						null,
						"Only " + stock + " available!",
						"Stock Limit",
						JOptionPane.WARNING_MESSAGE
						);
			}

		} else if (qty > 1) {

			model.setValueAt(qty - 1, row, 2);
		}

		return button.getText();
	}
}

/*
 * ============================================================
 * BACKGROUND PANEL
 * Draws the background image for the main frame
 * ============================================================
 */
class BackgroundPanel extends JPanel {

	private Image img;

	public BackgroundPanel(String path) {
		img = new ImageIcon(path).getImage();
	}

	@Override
	protected void paintComponent(Graphics g) {

		super.paintComponent(g);

		g.drawImage(
				img,
				0,
				0,
				getWidth(),
				getHeight(),
				this
				);
	}
}