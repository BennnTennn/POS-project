package myProject;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/*
 * ============================================================
 * MAIN FRAME - POINT OF SALE SYSTEM
 * ============================================================
 * Handles:
 * - Home screen
 * - Buyer menu
 * - Admin panel
 * - Purchase process
 * - Cart system
 * - Receipt generation
 * ============================================================
 */

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
	private static final String IMAGE_FILE = "src\\image\\bckgrnd.png";

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
		accountBtn.setOpaque(false);
		accountBtn.setFont(new Font("DialogInput", Font.BOLD, 45));

		topPanel.add(accountBtn);
		add(topPanel, BorderLayout.NORTH);

		// ============================================================
		// HOME SCREEN
		// ============================================================
		JPanel homeScreen = new JPanel(new BorderLayout());
		homeScreen.setOpaque(false);

		JButton orderBtn = new JButton("Tap to Order");
		orderBtn.setFont(new Font("DialogInput", Font.BOLD, 55));
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
		JButton clearBtn = new JButton("Clear Cart");

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

		String[] adminCols = {"Fruit", "Price", "Stock"};
		adminTableModel = new DefaultTableModel(adminCols, 0);

		adminTable = new JTable(adminTableModel);
		JScrollPane adminScroll = new JScrollPane(adminTable);

		adminPanel.add(adminScroll, BorderLayout.CENTER);

		// Admin buttons
		JPanel adminButtons = new JPanel(new GridLayout(4, 0, 5, 5));

		JButton addFruitBtn = new JButton("Add");
		JButton removeFruitBtn = new JButton("Remove");
		JButton editFruitBtn = new JButton("Edit");
		JButton backBtn = new JButton("Back");

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

		JDialog loginDialog = new JDialog(this, "Admin Login", true);
		loginDialog.setLayout(new GridLayout(3, 2, 10, 10));

		JTextField userField = new JTextField();
		JPasswordField passField = new JPasswordField();
		JButton loginBtn = new JButton("Login");

		Font fieldFont = new Font("DialogInput", Font.PLAIN, 20);

		userField.setFont(fieldFont);
		passField.setFont(fieldFont);
		loginBtn.setFont(new Font("DialogInput", Font.BOLD, 22));

		loginDialog.add(new JLabel("Username:"));
		loginDialog.add(userField);

		loginDialog.add(new JLabel("Password:"));
		loginDialog.add(passField);

		loginDialog.add(new JLabel());
		loginDialog.add(loginBtn);

		loginDialog.setPreferredSize(new Dimension(400, 200));

		loginBtn.addActionListener(ev -> {

			if (accountManager.validateLogin(
					userField.getText(),
					new String(passField.getPassword()))) {

				JOptionPane.showMessageDialog(this, "Login successful!");

				loginDialog.dispose();
				cardLayout.show(cardPanel, "ADMIN");

			} else {

				JOptionPane.showMessageDialog(
						this,
						"Invalid credentials!",
						"Error",
						JOptionPane.ERROR_MESSAGE
				);
			}
		});

		loginDialog.pack();
		loginDialog.setLocationRelativeTo(this);
		loginDialog.setVisible(true);
	}

	// ============================================================
	// ADMIN ACTIONS
	// ============================================================

	private void handleAddFruit() {

		String fruit = JOptionPane.showInputDialog(this, "Fruit name:");
		int price = Integer.parseInt(JOptionPane.showInputDialog(this, "Price:"));
		int stock = Integer.parseInt(JOptionPane.showInputDialog(this, "Stock:"));

		inventoryManager.addFruit(fruit, price, stock);

		refreshAdminTable();
		refreshMenuButtons();
	}

	private void handleDeleteFruit() {

		int row = adminTable.getSelectedRow();
		if (row == -1) return;

		String fruit = (String) adminTableModel.getValueAt(row, 0);

		inventoryManager.removeFruit(fruit);

		refreshAdminTable();
		refreshMenuButtons();
	}

	private void handleUpdateFruit() {

		int row = adminTable.getSelectedRow();
		if (row == -1) return;

		String fruit = (String) adminTableModel.getValueAt(row, 0);

		String priceStr = JOptionPane.showInputDialog(this, "New Price:");
		if (priceStr == null || priceStr.trim().isEmpty()) return;

		String stockStr = JOptionPane.showInputDialog(this, "New Stock:");
		if (stockStr == null || stockStr.trim().isEmpty()) return;

		try {

			int price = Integer.parseInt(priceStr.trim());
			int stock = Integer.parseInt(stockStr.trim());

			inventoryManager.updateFruit(fruit, price, stock);

			refreshAdminTable();
			refreshMenuButtons();

		} catch (NumberFormatException e) {

			JOptionPane.showMessageDialog(
					this,
					"Invalid number entered!",
					"Error",
					JOptionPane.ERROR_MESSAGE
			);
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

		// Store purchased items before clearing
		List<Object[]> purchasedItems = new ArrayList<>();

		for (int i = 0; i < buyerTableModel.getRowCount(); i++) {

			String fruit = (String) buyerTableModel.getValueAt(i, 0);
			int price = (int) buyerTableModel.getValueAt(i, 1);
			int qty = (int) buyerTableModel.getValueAt(i, 2);

			int stock = inventoryManager.getStock().get(fruit);

			if (qty > stock) {

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

			purchasedItems.add(new Object[]{fruit, price, qty});

			total += price * qty;
		}

		String input = JOptionPane.showInputDialog(
				this,
				"Total: " + total + "\nEnter payment:"
		);

		if (input != null) {

			try {

				int payment = Integer.parseInt(input);

				if (payment >= total) {

					buyerTableModel.setRowCount(0);

					refreshAdminTable();
					refreshMenuButtons();

					showReceipt(purchasedItems, total, payment);

				} else {

					JOptionPane.showMessageDialog(
							this,
							"Insufficient payment!",
							"Error",
							JOptionPane.ERROR_MESSAGE
					);
				}

			} catch (NumberFormatException e) {

				JOptionPane.showMessageDialog(
						this,
						"Invalid input!",
						"Error",
						JOptionPane.ERROR_MESSAGE
				);
			}
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