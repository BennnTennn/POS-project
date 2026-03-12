package myProject;

import java.io.*;
import java.util.*;

public class InventoryManager {
    private static final String INVENTORY_FILE = "fruit_data.txt";
    private Map<String, Integer> inventoryStock = new HashMap<>();
    private Map<String, Integer> fruitPrice = new HashMap<>();

    public InventoryManager() {
        loadInventory();
    }

    public Map<String, Integer> getStock() { return inventoryStock; }
    public Map<String, Integer> getPrices() { return fruitPrice; }

    public void loadInventory() {
        inventoryStock.clear();
        fruitPrice.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(INVENTORY_FILE))) {
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
        } catch (IOException e) {
            System.err.println("Error loading inventory: " + e.getMessage());
        }
    }

    public void saveInventory() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(INVENTORY_FILE))) {
            writer.println("Fruit,Price,Stock");
            for (String fruit : inventoryStock.keySet()) {
                writer.println(fruit + "," + fruitPrice.get(fruit) + "," + inventoryStock.get(fruit));
            }
        } catch (IOException e) {
            System.err.println("Error saving inventory: " + e.getMessage());
        }
    }

    public void addFruit(String fruit, int price, int stock) {
        fruitPrice.put(fruit, price);
        inventoryStock.put(fruit, stock);
        saveInventory();
    }

    public void removeFruit(String fruit) {
        fruitPrice.remove(fruit);
        inventoryStock.remove(fruit);
        saveInventory();
    }

    public void updateFruit(String fruit, int price, int stock) {
        if (stock <= 0) {
            
            fruitPrice.remove(fruit);
            inventoryStock.remove(fruit);
        } else {
            fruitPrice.put(fruit, price);
            inventoryStock.put(fruit, stock);
        }
        saveInventory();
    }

}
