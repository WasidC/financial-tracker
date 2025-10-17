package com.pluralsight;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;


/*
 * Capstone skeleton – personal finance tracker.
 * ------------------------------------------------
 * File format  (pipe-delimited)
 *     yyyy-MM-dd|HH:mm:ss|description|vendor|amount
 * A deposit has a positive amount; a payment is stored
 * as a negative amount.
 */
public class FinancialTracker {

    /* ------------------------------------------------------------------
       Shared data and formatters
       ------------------------------------------------------------------ */
    private static final ArrayList<Transaction> transactions = new ArrayList<>();
    private static final String FILE_NAME = "transactions.csv";

    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private static final String TIME_PATTERN = "HH:mm:ss";
    private static final String DATETIME_PATTERN = DATE_PATTERN + " " + TIME_PATTERN;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern(DATE_PATTERN);
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern(TIME_PATTERN);
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern(DATETIME_PATTERN);

    /* ------------------------------------------------------------------
       Main menu
       ------------------------------------------------------------------ */
    public static void main(String[] args) {
        loadTransactions(FILE_NAME);

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("Welcome to TransactionApp");
            System.out.println("Choose an option:");
            System.out.println("D) Add Deposit");
            System.out.println("P) Make Payment (Debit)");
            System.out.println("L) Ledger");
            System.out.println("X) Exit");

            String input = scanner.nextLine().trim();

            switch (input.toUpperCase()) {
                case "D" -> addDeposit(scanner);
                case "P" -> addPayment(scanner);
                case "L" -> ledgerMenu(scanner);
                case "X" -> running = false;
                default -> System.out.println("Invalid option");
            }
        }
        scanner.close();
    }

    /* ------------------------------------------------------------------
       File I/O
       ------------------------------------------------------------------ */

    /**
     * Load transactions from FILE_NAME.
     * • If the file doesn’t exist, create an empty one so that future writes succeed.
     * • Each line looks like: date|time|description|vendor|amount
     */
    public static void loadTransactions(String fileName) {
        try {
            File file = new File(fileName);

            // If file doesn’t exist, create an empty one so writing later doesn’t fail
            if (!file.exists()) {
                file.createNewFile();
                return;
            }

            // Read each line and split fields by "|"
            Scanner fileScanner = new Scanner(file);
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                String[] parts = line.split("\\|");

                // Each transaction has 5 parts
                if (parts.length == 5) {
                    LocalDate date = LocalDate.parse(parts[0], DATE_FMT);
                    LocalTime time = LocalTime.parse(parts[1], TIME_FMT);
                    String description = parts[2];
                    String vendor = parts[3];
                    double amount = Double.parseDouble(parts[4]);

                    // Create Transaction object and add it to the list
                    transactions.add(new Transaction(date, time, description, vendor, amount));
                }
            }
            fileScanner.close();

        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }

    /* ------------------------------------------------------------------
       Add new transactions
       ------------------------------------------------------------------ */

    /**
     * Prompt for ONE date+time string in the format
     * "yyyy-MM-dd HH:mm:ss", plus description, vendor, amount.
     * Validate that the amount entered is positive.
     * Store the amount as-is (positive) and append to the file.
     */
    private static void addDeposit(Scanner scanner) {
        try {
            System.out.print("Enter date and time (yyyy-MM-dd HH:mm:ss): ");
            String dt = scanner.nextLine();
            LocalDateTime dateTime = LocalDateTime.parse(dt, DATETIME_FMT);

            System.out.print("Enter description: ");
            String description = scanner.nextLine();

            System.out.print("Enter vendor: ");
            String vendor = scanner.nextLine();

            System.out.print("Enter deposit amount (positive): ");
            double amount = Double.parseDouble(scanner.nextLine());

            if (amount <= 0) {
                System.out.println("Amount must be positive!");
                return;
            }

            // Create a new Transaction object
            Transaction t = new Transaction(dateTime.toLocalDate(), dateTime.toLocalTime(), description, vendor, amount);
            transactions.add(t);

            // Append transaction to file
            saveTransactionToFile(t);
            System.out.println("Deposit added successfully.");

        } catch (DateTimeParseException e) {
            System.out.println("Invalid date/time format. Please use yyyy-MM-dd HH:mm:ss");
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount. Please enter a number.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Same prompts as addDeposit.
     * Amount must be entered as a positive number,
     * then converted to a negative amount before storing.
     */
    private static void addPayment(Scanner scanner) {
        try {
            System.out.print("Enter date and time (yyyy-MM-dd HH:mm:ss): ");
            String dt = scanner.nextLine();
            LocalDateTime dateTime = LocalDateTime.parse(dt, DATETIME_FMT);

            System.out.print("Enter description: ");
            String description = scanner.nextLine();

            System.out.print("Enter vendor: ");
            String vendor = scanner.nextLine();

            System.out.print("Enter payment amount (positive): ");
            double amount = Double.parseDouble(scanner.nextLine());

            if (amount <= 0) {
                System.out.println("Amount must be positive!");
                return;
            }
            // Store as negative for payment
            amount = -amount;

            Transaction t = new Transaction(dateTime.toLocalDate(), dateTime.toLocalTime(), description, vendor, amount);
            transactions.add(t);
            saveTransactionToFile(t);
            System.out.println("Payment recorded successfully.");

        } catch (DateTimeParseException e) {
            System.out.println("Invalid date/time format. Please use yyyy-MM-dd HH:mm:ss");
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount. Please enter a number.");
        }
    }

    /* Save a single transaction to the CSV file. */

    private static void saveTransactionToFile(Transaction t) {
        try (FileWriter writer = new FileWriter(FILE_NAME, true)) {
            writer.write(t.toCSVLine() + "\n");
        } catch (IOException e) {
            System.out.println("Error saving transaction: " + e.getMessage());
        }
    }

    /* ------------------------------------------------------------------
       Ledger menu
       ------------------------------------------------------------------ */
    private static void ledgerMenu(Scanner scanner) {
        boolean running = true;
        while (running) {
            System.out.println("Ledger");
            System.out.println("Choose an option:");
            System.out.println("A) All");
            System.out.println("D) Deposits");
            System.out.println("P) Payments");
            System.out.println("R) Reports");
            System.out.println("H) Home");

            String input = scanner.nextLine().trim();

            switch (input.toUpperCase()) {
                case "A" -> displayLedger();
                case "D" -> displayDeposits();
                case "P" -> displayPayments();
                case "R" -> reportsMenu(scanner);
                case "H" -> running = false;
                default -> System.out.println("Invalid option");
            }
        }
    }

    /* ------------------------------------------------------------------
       Display helpers: show data in neat columns
       ------------------------------------------------------------------ */
    private static void displayLedger() {
        System.out.printf("%-12s %-10s %-20s %-15s %10s%n",
                "Date", "Time", "Description", "Vendor", "Amount");
        for (Transaction t : transactions) {
            System.out.printf("%-12s %-10s %-20s %-15s %10.2f%n",
                    t.getDate(), t.getTime(), t.getDescription(), t.getVendor(), t.getAmount());
        }
    }

    private static void displayDeposits() {
        for (Transaction t : transactions) {
            if (t.getAmount() > 0) {
                System.out.printf("%-12s %-10s %-20s %-15s %10.2f%n",
                        t.getDate(), t.getTime(), t.getDescription(), t.getVendor(), t.getAmount());
            }
        }
    }

    private static void displayPayments() {
        for (Transaction t : transactions) {
            if (t.getAmount() < 0) {
                System.out.printf("%-12s %-10s %-20s %-15s %10.2f%n",
                        t.getDate(), t.getTime(), t.getDescription(), t.getVendor(), t.getAmount());
            }
        }
    }

    /* ------------------------------------------------------------------
       Reports menu
       ------------------------------------------------------------------ */
    private static void reportsMenu(Scanner scanner) {
        boolean running = true;
        while (running) {
            System.out.println("Reports");
            System.out.println("Choose an option:");
            System.out.println("1) Month To Date");
            System.out.println("2) Previous Month");
            System.out.println("3) Year To Date");
            System.out.println("4) Previous Year");
            System.out.println("5) Search by Vendor");
            System.out.println("6) Custom Search");
            System.out.println("0) Back");

            String input = scanner.nextLine().trim();

            switch (input) {
                case "1" -> filterTransactionsByDate(LocalDate.now().withDayOfMonth(1), LocalDate.now());
                case "2" -> {
                    LocalDate firstPrevMonth = LocalDate.now().minusMonths(1).withDayOfMonth(1);
                    LocalDate lastPrevMonth = firstPrevMonth.withDayOfMonth(firstPrevMonth.lengthOfMonth());
                    filterTransactionsByDate(firstPrevMonth, lastPrevMonth);
                }
                case "3" -> filterTransactionsByDate(LocalDate.of(LocalDate.now().getYear(), 1, 1), LocalDate.now());
                case "4" -> {
                    int prevYear = LocalDate.now().getYear() - 1;
                    filterTransactionsByDate(LocalDate.of(prevYear, 1, 1), LocalDate.of(prevYear, 12, 31));
                }
                case "5" -> {
                    System.out.print("Enter vendor name: ");
                    filterTransactionsByVendor(scanner.nextLine());
                }
                case "6" -> customSearch(scanner);
                case "0" -> running = false;
                default -> System.out.println("Invalid option");
            }
        }
    }

    /* ------------------------------------------------------------------
       Reporting helpers
       ------------------------------------------------------------------ */
    private static void filterTransactionsByDate(LocalDate start, LocalDate end) {
        System.out.println("Transactions between " + start + " and " + end + ":");
        for (Transaction t : transactions) {
            if (!t.getDate().isBefore(start) && !t.getDate().isAfter(end)) {
                System.out.printf("%-12s %-10s %-20s %-15s %10.2f%n",
                        t.getDate(), t.getTime(), t.getDescription(), t.getVendor(), t.getAmount());
            }
        }
    }

    private static void filterTransactionsByVendor(String vendor) {
        for (Transaction t : transactions) {
            if (t.getVendor().equalsIgnoreCase(vendor)) {
                System.out.printf("%-12s %-10s %-20s %-15s %10.2f%n",
                        t.getDate(), t.getTime(), t.getDescription(), t.getVendor(), t.getAmount());
            }
        }
    }

    private static void customSearch(Scanner scanner) {
        System.out.print("Enter start date (yyyy-MM-dd) or leave blank: ");
        String startInput = scanner.nextLine();
        System.out.print("Enter end date (yyyy-MM-dd) or leave blank: ");
        String endInput = scanner.nextLine();
        System.out.print("Enter description keyword or leave blank: ");
        String descInput = scanner.nextLine();
        System.out.print("Enter vendor or leave blank: ");
        String vendorInput = scanner.nextLine();
        System.out.print("Enter exact amount or leave blank: ");
        String amountInput = scanner.nextLine();

        LocalDate start = startInput.isEmpty() ? null : parseDate(startInput);
        LocalDate end = endInput.isEmpty() ? null : parseDate(endInput);
        Double amount = amountInput.isEmpty() ? null : parseDouble(amountInput);

        for (Transaction t : transactions) {
            boolean match = true;
            if (start != null && t.getDate().isBefore(start)) match = false;
            if (end != null && t.getDate().isAfter(end)) match = false;
            if (!descInput.isEmpty() && !t.getDescription().toLowerCase().contains(descInput.toLowerCase())) match = false;
            if (!vendorInput.isEmpty() && !t.getVendor().equalsIgnoreCase(vendorInput)) match = false;
            if (amount != null && t.getAmount() != amount) match = false;

            if (match) {
                System.out.printf("%-12s %-10s %-20s %-15s %10.2f%n",
                        t.getDate(), t.getTime(), t.getDescription(), t.getVendor(), t.getAmount());
            }
        }
    }

    /* ------------------------------------------------------------------
       Utility parsers (you can reuse in many places)
       ------------------------------------------------------------------ */
    private static LocalDate parseDate(String s) {
        try {
            return LocalDate.parse(s, DATE_FMT);
        } catch (Exception e) {
            System.out.println("Invalid date format.");
            return null;
        }
    }
        private static Double parseDouble(String s) {
            try {
                return Double.parseDouble(s);
            } catch (Exception e) {
                System.out.println("Invalid number.");
                return null;
            }
        }
    }