import java.util.*;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

class Expense {
    private String date;
    private String category;
    private double amount;
    private String note;

    public Expense(String date, String category, double amount, String note) {
        this.date = date;
        this.category = category;
        this.amount = amount;
        this.note = note;
    }

    public String getDate() { return date; }
    public String getCategory() { return category; }
    public double getAmount() { return amount; }
    public String getNote() { return note; }

    public void setDate(String date) { this.date = date; }
    public void setCategory(String category) { this.category = category; }
    public void setAmount(double amount) { this.amount = amount; }
    public void setNote(String note) { this.note = note; }

    @Override
    public String toString() {
        return date + "," + category + "," + amount + "," + note;
    }

    public static Expense fromString(String line) {
        String[] parts = line.split(",", 4);
        return new Expense(parts[0], parts[1], Double.parseDouble(parts[2]), parts[3]);
    }
}

class ExpenseManager {
    private List<Expense> expenses = new ArrayList<>();
    private final String filePath = "expenses.txt";

    public ExpenseManager() {
        loadExpenses();
    }

    public void addExpense(Expense expense) {
        expenses.add(expense);
        saveExpenses();
    }

    public void editExpense(int index, Expense newExpense) {
        if (index >= 0 && index < expenses.size()) {
            expenses.set(index, newExpense);
            saveExpenses();
        }
    }

    public void deleteExpense(int index) {
        if (index >= 0 && index < expenses.size()) {
            expenses.remove(index);
            saveExpenses();
        }
    }

    public void showExpenses() {
        if (expenses.isEmpty()) {
            System.out.println("No expenses recorded.");
        } else {
            for (int i = 0; i < expenses.size(); i++) {
                System.out.println((i + 1) + ". " + expenses.get(i));
            }
        }
    }

    public void filterExpensesByCategory(String category) {
        boolean found = false;
        for (Expense e : expenses) {
            if (e.getCategory().equalsIgnoreCase(category)) {
                System.out.println(e);
                found = true;
            }
        }
        if (!found) System.out.println("No expenses found for category: " + category);
    }

    public void filterExpensesByDateRange(String from, String to) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate fromDate = LocalDate.parse(from, formatter);
        LocalDate toDate = LocalDate.parse(to, formatter);

        boolean found = false;
        for (Expense e : expenses) {
            LocalDate expenseDate = LocalDate.parse(e.getDate(), formatter);
            if ((expenseDate.isEqual(fromDate) || expenseDate.isAfter(fromDate)) &&
                (expenseDate.isEqual(toDate) || expenseDate.isBefore(toDate))) {
                System.out.println(e);
                found = true;
            }
        }
        if (!found) System.out.println("No expenses found in this date range.");
    }

    public double getTotalExpenses() {
        return expenses.stream().mapToDouble(Expense::getAmount).sum();
    }

    public double getMonthlyTotal(String month) {
        return expenses.stream()
            .filter(e -> e.getDate().startsWith(month))
            .mapToDouble(Expense::getAmount)
            .sum();
    }

    private void loadExpenses() {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                expenses.add(Expense.fromString(line));
            }
        } catch (IOException e) {
            System.out.println("No previous data found.");
        }
    }

    private void saveExpenses() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Expense e : expenses) {
                writer.write(e.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving expenses.");
        }
    }
}

public class ExpenseTracker {
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        ExpenseManager manager = new ExpenseManager();

        while (true) {
            System.out.println("\nExpense Tracker");
            System.out.println("1. Add Expense");
            System.out.println("2. Edit Expense");
            System.out.println("3. Delete Expense");
            System.out.println("4. Show All Expenses");
            System.out.println("5. View Total");
            System.out.println("6. Monthly Summary");
            System.out.println("7. Filter by Category");
            System.out.println("8. Filter by Date Range");
            System.out.println("9. Exit");
            System.out.print("Choose: ");
            
            int choice = readIntInput();

            switch (choice) {
                case 1:
                    manager.addExpense(readExpenseInput());
                    System.out.println("Expense added.");
                    break;
                case 2:
                    manager.showExpenses();
                    System.out.print("Enter expense number to edit: ");
                    int editIndex = readIntInput() - 1;
                    manager.editExpense(editIndex, readExpenseInput());
                    System.out.println("Expense updated.");
                    break;
                case 3:
                    manager.showExpenses();
                    System.out.print("Enter expense number to delete: ");
                    int delIndex = readIntInput() - 1;
                    manager.deleteExpense(delIndex);
                    System.out.println("Expense deleted.");
                    break;
                case 4:
                    manager.showExpenses();
                    break;
                case 5:
                    System.out.println("Total Expenses: ₹" + manager.getTotalExpenses());
                    break;
                case 6:
                    System.out.print("Enter month (YYYY-MM): ");
                    String month = scanner.nextLine();
                    System.out.println("Total for " + month + ": ₹" + manager.getMonthlyTotal(month));
                    break;
                case 7:
                    System.out.print("Enter category to filter: ");
                    String filterCat = scanner.nextLine();
                    manager.filterExpensesByCategory(filterCat);
                    break;
                case 8:
                    System.out.print("From date (YYYY-MM-DD): ");
                    String from = scanner.nextLine();
                    System.out.print("To date (YYYY-MM-DD): ");
                    String to = scanner.nextLine();
                    manager.filterExpensesByDateRange(from, to);
                    break;
                case 9:
                    System.out.println("Goodbye!");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private static Expense readExpenseInput() {
        System.out.print("Date (YYYY-MM-DD): ");
        String date = scanner.nextLine();
        System.out.print("Category: ");
        String category = scanner.nextLine();
        System.out.print("Amount: ");
        double amount = readDoubleInput();
        System.out.print("Note: ");
        String note = scanner.nextLine();
        return new Expense(date, category, amount, note);
    }

    private static int readIntInput() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (Exception e) {
                System.out.print("Invalid number. Try again: ");
            }
        }
    }

    private static double readDoubleInput() {
        while (true) {
            try {
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (Exception e) {
                System.out.print("Invalid amount. Try again: ");
            }
        }
    }
}
