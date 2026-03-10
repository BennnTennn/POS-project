package myProject;

import java.io.*;
import java.util.*;

public class AccountManager {
    private static final String ACCOUNT_FILE = "admin_accounts.txt";

    public Map<String, String> loadAccounts() {
        Map<String, String> accounts = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(ACCOUNT_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    accounts.put(parts[0].trim(), parts[1].trim());
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading accounts: " + e.getMessage());
        }
        return accounts;
    }

    public boolean validateLogin(String username, String password) {
        Map<String, String> accounts = loadAccounts();
        return accounts.containsKey(username) && accounts.get(username).equals(password);
    }
}
