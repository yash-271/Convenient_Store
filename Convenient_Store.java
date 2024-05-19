import java.io.*;
import java.util.*;

public class Testing3 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        String userName;
        do {
            System.out.println();
            System.out.println(
                    "-------------------------Inventory Management and Billing System--------------------------");
            System.out.println();
            System.out.print("Enter Your Name: ");
            userName = scanner.nextLine();

            if (userName.equalsIgnoreCase("logout")) {
                System.out.println("Logged out successfully!");
                break; 
            }

            if (userName.equalsIgnoreCase("inventory")) {
                manageInventory();
            } else {
                shoppingFlow(scanner, userName);
            }
        } while (true);
    }

    private static void shoppingFlow(Scanner scanner, String userName) {
        Map<String, Integer[]> itemDict = loadInventory();

        System.out.println("\nWelcome To Our Shop " + userName.toUpperCase());
        System.out.println("\nAvailable Items \n");
        displayAvailableItems(itemDict);

        Map<String, Integer[]> cart = new HashMap<>();
        int amount = 0;

        String choice = "";
        do {
            System.out.print("\nDo you want to shop? (Yes/No): ");
            choice = scanner.nextLine();

            if (choice.equalsIgnoreCase("yes") || choice.equalsIgnoreCase("y")) {
                System.out.print("\nAdd item: ");
                String add = scanner.nextLine().toLowerCase();

                if (!itemDict.containsKey(add)) {
                    System.out.println("\nItem not in store\n");
                } else {
                    System.out.print("Enter quantity: ");
                    int quantity = Integer.parseInt(scanner.nextLine());
                    if (itemDict.get(add)[1] - quantity < 0) {
                        System.out.println("\nSorry! We only have " + itemDict.get(add)[1] + " in stock.");
                    } else {
                        itemDict.get(add)[1] -= quantity;

                        if (!cart.containsKey(add)) {
                            cart.put(add, new Integer[] { quantity * itemDict.get(add)[0], quantity });
                        } else {
                            cart.get(add)[1] += quantity;
                            cart.get(add)[0] += (quantity * itemDict.get(add)[0]);
                        }

                        amount += quantity * itemDict.get(add)[0];
                        System.out.println("Your Subtotal: " + amount);
                    }
                }
            }
        } while (!choice.equalsIgnoreCase("no") && !choice.equalsIgnoreCase("n"));

        if (!cart.isEmpty()) {
            generateBill(cart, itemDict, amount, userName);
        } else {
            System.out.println("\nThank you for the visit!");
            System.out.println("\nPlease come again.");
        }

        saveInventory(itemDict);
    }

    private static void manageInventory() {
        Map<String, Integer[]> itemDict = loadInventory();
        Scanner scanner = new Scanner(System.in);

        int option;
        boolean tmp = true;

        while (tmp) {

            System.out.println("\nCurrent Inventory:\n");
            displayAvailableItems(itemDict);

            System.out.println(
                    "\nPress 1 to remove an item.\nPress 2 to add an item.\nPress 3 to update unit price.\nPress 4 to update available quantity.\nPress 5 to exit.");
            option = Integer.parseInt(scanner.nextLine());

            switch (option) {
                case 1:
                    System.out.print("Enter the item name to remove: ");
                    String itemToRemove = scanner.nextLine().toLowerCase();
                    if (itemDict.containsKey(itemToRemove)) {
                        itemDict.remove(itemToRemove);
                        System.out.println("Item removed successfully.");
                    } else {
                        System.out.println("Item not found in inventory.");
                    }
                    break;
                case 2:
                    System.out.print("Enter the item name: ");
                    String newItemName = scanner.nextLine().toLowerCase();
                    System.out.print("Enter the unit price: ");
                    int unitPrice = Integer.parseInt(scanner.nextLine());
                    System.out.print("Enter the available quantity: ");
                    int quantity = Integer.parseInt(scanner.nextLine());
                    itemDict.put(newItemName, new Integer[] { unitPrice, quantity });
                    System.out.println("Item added successfully.");
                    break;
                case 3:
                    System.out.print("Enter the item name to update unit price: ");
                    String itemNameToUpdatePrice = scanner.nextLine().toLowerCase();
                    if (itemDict.containsKey(itemNameToUpdatePrice)) {
                        System.out.print("Enter the new unit price: ");
                        int newUnitPrice = Integer.parseInt(scanner.nextLine());
                        itemDict.get(itemNameToUpdatePrice)[0] = newUnitPrice;
                        System.out.println("Unit price updated successfully.");
                    } else {
                        System.out.println("Item not found in inventory.");
                    }
                    break;
                case 4:
                    System.out.print("Enter the item name to update available quantity: ");
                    String itemNameToUpdateQuantity = scanner.nextLine().toLowerCase();
                    if (itemDict.containsKey(itemNameToUpdateQuantity)) {
                        System.out.print("Enter the new available quantity: ");
                        int newQuantity = Integer.parseInt(scanner.nextLine());
                        itemDict.get(itemNameToUpdateQuantity)[1] = newQuantity;
                        System.out.println("Available quantity updated successfully.");
                    } else {
                        System.out.println("Item not found in inventory.");
                    }
                    break;
                case 5:
                    System.out.println("Exiting inventory management.");
                    tmp = false;
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }

        saveInventory(itemDict);
    }

    private static Map<String, Integer[]> loadInventory() {
        Map<String, Integer[]> itemDict = new HashMap<>();
        try {
            File file = new File("item1.txt");
            Scanner fileScanner = new Scanner(file);
            while (fileScanner.hasNextLine()) {
                String[] split = fileScanner.nextLine().split(" ");
                if (!split[0].equals("ITEMS")) {
                    itemDict.put(split[0].toLowerCase(),
                            new Integer[] { Integer.parseInt(split[2]), Integer.parseInt(split[1]) });
                }
            }
            fileScanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return itemDict;
    }

    private static void displayAvailableItems(Map<String, Integer[]> itemDict) {
        System.out.printf("%-15s %-15s %-15s\n", "ITEMS", "UNITPRICE", "AVAILABLE");
        for (Map.Entry<String, Integer[]> entry : itemDict.entrySet()) {
            System.out.printf("%-15s Rs.%-14d %-15d\n", entry.getKey(), entry.getValue()[0], entry.getValue()[1]);
        }
    }

    private static void generateBill(Map<String, Integer[]> cart, Map<String, Integer[]> itemDict, int amount,
            String userName) {
        System.out.print("Enter anything to make the bill: ");
        new Scanner(System.in).nextLine();

        System.out.println("\nYOUR BILL: \n");
        System.out.printf("%-15s%-16s%-15s%-15s\n", "ITEMS", "QUANTITY", "RATE", "TOTAL");

        for (Map.Entry<String, Integer[]> entry : cart.entrySet()) {
            String item = entry.getKey();
            Integer[] quantity = entry.getValue();

            System.out.printf("%-17s%-14d Rs.%-14d Rs.%d\n", item, quantity[1], itemDict.get(item)[0], quantity[0]);
        }

        System.out.printf("\nTotal amount to be paid: Rs.%d\n", amount);
        System.out.println("\nThank you for shopping at our store, " + userName.substring(0, 1).toUpperCase()
                + userName.substring(1));
    }

    private static void saveInventory(Map<String, Integer[]> itemDict) {
        try {
            FileWriter writer = new FileWriter("item1.txt");
            writer.write("ITEMS AVAILABLE UNITPRICE\n");
            for (Map.Entry<String, Integer[]> entry : itemDict.entrySet()) {
                String key = entry.getKey();
                Integer[] value = entry.getValue();
                writer.write(key + " " + value[1] + " " + value[0] + "\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
