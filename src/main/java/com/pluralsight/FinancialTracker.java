package com.pluralsight;
// declares that this class belongs to the package help organize

import java.io.File;
// import File class for reading/writing files

import java.io.FileWriter;
// import FileWriter class to write transactions to a file

import java.time.LocalDate;
// import LocalDate to store transaction dates

import java.time.LocalDateTime;
// import LocalDateTime for parsing date + time together

import java.time.LocalTime;
// import LocalTime to store transaction times

import java.time.format.DateTimeFormatter;
// import DateTimeFormatter to parse/format dates and times

import java.time.format.DateTimeParseException;
// import exception class for invalid date/time parsing

import java.util.ArrayList;
// import ArrayList to store multiple transactions in memory

import java.util.Scanner;
// import Scanner for reading user input

/*
 Capstone skeleton – personal finance tracker
 ------------------------------------------------
 Stores transactions in memory and CSV file
 Supports adding deposits/payments, ledger view, and reports
*/
public class FinancialTracker {

    // ------------------- Shared data -------------------

    private static final ArrayList<Transaction> transactions = new ArrayList<>();
    // stores all transactions in memory as Transaction objects

    private static final String FILE_NAME = "transactions.csv";
    // name of the CSV file where transactions are stored

    private static final String DATE_PATTERN = "yyyy-MM-dd";
    // pattern to parse/display dates

    private static final String TIME_PATTERN = "HH:mm:ss";
    // pattern to parse/display times

    private static final String DATETIME_PATTERN = DATE_PATTERN + " " + TIME_PATTERN;
    // pattern to parse/display date + time together

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern(DATE_PATTERN);
    // formatter for LocalDate parsing/formatting

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern(TIME_PATTERN);
    // formatter for LocalTime parsing/formatting

    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern(DATETIME_PATTERN);
    // formatter for LocalDateTime parsing/formatting

    // ------------------- Main Menu -------------------

    public static void main(String[] args) {
        // program starts here

        loadTransactions(FILE_NAME);
        // load existing transactions from CSV file into memory

        Scanner scanner = new Scanner(System.in);
        // create Scanner object for reading user input

        boolean running = true;
        // boolean to control the main menu loop

        while (running) {
            // main menu loop
            System.out.println("Welcome to TransactionApp");
            System.out.println("Choose an option:");
            System.out.println("D) Add Deposit");
            System.out.println("P) Make Payment (Debit)");
            System.out.println("L) Ledger");
            System.out.println("X) Exit");

            String input = scanner.nextLine().trim();
            // read user input and remove leading/trailing spaces

            switch (input.toUpperCase()) {
                case "D" -> addDeposit(scanner);
                // if user selects D, call addDeposit method

                case "P" -> addPayment(scanner);
                // if user selects P, call addPayment method

                case "L" -> ledgerMenu(scanner);
                // if user selects L, go to ledger menu

                case "X" -> running = false;
                // if user selects X, exit program

                default -> System.out.println("Invalid option");
                // any other input is invalid
            }
        }

        scanner.close();
        // close scanner before exiting program
    }

    // ------------------- File I/O -------------------

    /**
     * Load transactions from CSV file.
     * If the file doesn't exist, create an empty file.
     * Each line format: yyyy-MM-dd|HH:mm:ss|description|vendor|amount
     */
    public static void loadTransactions(String fileName) {
        try {
            File file = new File(fileName);
            // create File object for reading

            if (!file.exists()) {
                file.createNewFile();
                // create new empty file if it doesn't exist
                return;
            }

            Scanner fileScanner = new Scanner(file);
            // create Scanner to read the file

            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                // read one line at a time

                String[] parts = line.split("\\|");
                // split line into 5 parts by pipe symbol

                if (parts.length == 5) {
                    // check for correct format
                    LocalDate date = LocalDate.parse(parts[0], DATE_FMT);
                    // parse date from first part

                    LocalTime time = LocalTime.parse(parts[1], TIME_FMT);
                    // parse time from second part

                    String description = parts[2];
                    // store description

                    String vendor = parts[3];
                    // store vendor

                    double amount = Double.parseDouble(parts[4]);
                    // parse amount as double

                    transactions.add(new Transaction(date, time, description, vendor, amount));
                    // create Transaction object and add to memory
                }
            }

            fileScanner.close();
            // close file scanner
        } catch (Exception e) {
            System.out.println("Error reading file: " + e.getMessage());
            // display error if file reading fails
        }
    }

    // ------------------- Add Deposit -------------------

    /**
     * Prompt user to add a deposit transaction.
     * Validate date/time and positive amount.
     * Save transaction to memory and CSV file.
     */
    private static void addDeposit(Scanner scanner) {
        try {
            System.out.print("Enter date and time (yyyy-MM-dd HH:mm:ss): ");
            String dt = scanner.nextLine();
            LocalDateTime dateTime = LocalDateTime.parse(dt, DATETIME_FMT);
            // parse date and time input

            System.out.print("Enter description: ");
            String description = scanner.nextLine();
            // read description

            System.out.print("Enter vendor: ");
            String vendor = scanner.nextLine();
            // read vendor

            System.out.print("Enter deposit amount (positive): ");
            double amount = Double.parseDouble(scanner.nextLine());
            // read amount

            if (amount <= 0) {
                System.out.println("Amount must be positive!");
                return;
                // validate amount
            }

            Transaction t = new Transaction(dateTime.toLocalDate(), dateTime.toLocalTime(), description, vendor, amount);
            // create Transaction object

            transactions.add(t);
            // add to memory

            saveTransactionToFile(t);
            // append to CSV file

            System.out.println("Deposit added successfully.");
            // notify user
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date/time format. Please use yyyy-MM-dd HH:mm:ss");
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount. Please enter a number.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ------------------- Add Payment -------------------

    /**
     * Prompt user to add a payment transaction.
     * Validate date/time and positive amount.
     * Convert amount to negative before saving.
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

            amount = -amount;
            // convert to negative for payments

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

    // ------------------- Save Transaction -------------------

    private static void saveTransactionToFile(Transaction t) {
        // append a single transaction to the CSV file
        try (FileWriter writer = new FileWriter(FILE_NAME, true)) {
            writer.write(t.toCSVLine() + "\n");
            // write transaction as a CSV line
        } catch (Exception e) {
            System.out.println("Error saving transaction: " + e.getMessage());
        }
    }

    // ------------------- Ledger Menu -------------------

    private static void ledgerMenu(Scanner scanner) {
        // show ledger options and handle user input
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

    // ------------------- Display Methods -------------------

    private static void displayLedger() {
        // display all transactions in formatted columns
        System.out.printf("%-12s %-10s %-20s %-15s %10s%n",
                "Date", "Time", "Description", "Vendor", "Amount");

        for (Transaction t : transactions) {
            System.out.printf("%-12s %-10s %-20s %-15s %10.2f%n",
                    t.getDate(), t.getTime(), t.getDescription(), t.getVendor(), t.getAmount());
        }
    }

    private static void displayDeposits() {
        // display only transactions with positive amounts
        for (Transaction t : transactions) {
            if (t.getAmount() > 0) {
                System.out.printf("%-12s %-10s %-20s %-15s %10.2f%n",
                        t.getDate(), t.getTime(), t.getDescription(), t.getVendor(), t.getAmount());
            }
        }
    }

    private static void displayPayments() {
        // display only transactions with negative amounts
        for (Transaction t : transactions) {
            if (t.getAmount() < 0) {
                System.out.printf("%-12s %-10s %-20s %-15s %10.2f%n",
                        t.getDate(), t.getTime(), t.getDescription(), t.getVendor(), t.getAmount());
            }
        }
    }

// ------------------- Reports Menu and Filters -------------------

    private static void reportsMenu(Scanner scanner) {
        // method to show the reports submenu and handle user input
        boolean running = true;
        // boolean to control loop for reports menu

        while (running) {
            // loop until user chooses to go back
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
            // read user input and remove spaces

            switch (input) {
                case "1" -> filterTransactionsByDate(LocalDate.now().withDayOfMonth(1), LocalDate.now());
                // show transactions from the first of this month until today

                case "2" -> {
                    // show transactions from previous month
                    LocalDate firstPrevMonth = LocalDate.now().minusMonths(1).withDayOfMonth(1);
                    LocalDate lastPrevMonth = firstPrevMonth.withDayOfMonth(firstPrevMonth.lengthOfMonth());
                    filterTransactionsByDate(firstPrevMonth, lastPrevMonth);
                }

                case "3" -> filterTransactionsByDate(LocalDate.of(LocalDate.now().getYear(), 1, 1), LocalDate.now());
                // show transactions from January 1st of this year until today

                case "4" -> {
                    // show transactions for previous year
                    int prevYear = LocalDate.now().getYear() - 1;
                    filterTransactionsByDate(LocalDate.of(prevYear, 1, 1), LocalDate.of(prevYear, 12, 31));
                }

                case "5" -> {
                    // filter transactions by vendor
                    System.out.print("Enter vendor name: ");
                    filterTransactionsByVendor(scanner.nextLine());
                }

                case "6" -> customSearch(scanner);
                // perform a custom search with multiple filters

                case "0" -> running = false;
                // go back to ledger menu

                default -> System.out.println("Invalid option");
                // invalid input handling
            }
        }
    }

    // ------------------- Filter Methods -------------------

    private static void filterTransactionsByDate(LocalDate start, LocalDate end) {
        // method to display transactions between start and end dates
        System.out.println("Transactions between " + start + " and " + end + ":");

        for (Transaction t : transactions) {
            if (!t.getDate().isBefore(start) && !t.getDate().isAfter(end)) {
                // if transaction date is within the range, print it
                System.out.printf("%-12s %-10s %-20s %-15s %10.2f%n",
                        t.getDate(), t.getTime(), t.getDescription(), t.getVendor(), t.getAmount());
            }
        }
    }

    private static void filterTransactionsByVendor(String vendor) {
        // method to display all transactions matching a vendor name
        for (Transaction t : transactions) {
            if (t.getVendor().equalsIgnoreCase(vendor)) {
                // case-insensitive match
                System.out.printf("%-12s %-10s %-20s %-15s %10.2f%n",
                        t.getDate(), t.getTime(), t.getDescription(), t.getVendor(), t.getAmount());
            }
        }
    }

    private static void customSearch(Scanner scanner) {
        // method to allow user to filter transactions by multiple optional criteria

        System.out.print("Enter start date (yyyy-MM-dd) or leave blank: ");
        String startInput = scanner.nextLine();
        // read start date, optional

        System.out.print("Enter end date (yyyy-MM-dd) or leave blank: ");
        String endInput = scanner.nextLine();
        // read end date, optional

        System.out.print("Enter description keyword or leave blank: ");
        String descInput = scanner.nextLine();
        // read description keyword, optional

        System.out.print("Enter vendor or leave blank: ");
        String vendorInput = scanner.nextLine();
        // read vendor name, optional

        System.out.print("Enter exact amount or leave blank: ");
        String amountInput = scanner.nextLine();
        // read amount, optional

        LocalDate start = startInput.isEmpty() ? null : parseDate(startInput);
        // parse start date or null if blank

        LocalDate end = endInput.isEmpty() ? null : parseDate(endInput);
        // parse end date or null if blank

        Double amount = amountInput.isEmpty() ? null : parseDouble(amountInput);
        // parse amount or null if blank

        for (Transaction t : transactions) {
            boolean match = true;
            // assume transaction matches until proven otherwise

            if (start != null && t.getDate().isBefore(start)) match = false;
            if (end != null && t.getDate().isAfter(end)) match = false;
            if (!descInput.isEmpty() && !t.getDescription().toLowerCase().contains(descInput.toLowerCase())) match = false;
            if (!vendorInput.isEmpty() && !t.getVendor().equalsIgnoreCase(vendorInput)) match = false;
            if (amount != null && t.getAmount() != amount) match = false;
            // check all criteria, set match to false if any fail

            if (match) {
                // print transaction if it matches all filters
                System.out.printf("%-12s %-10s %-20s %-15s %10.2f%n",
                        t.getDate(), t.getTime(), t.getDescription(), t.getVendor(), t.getAmount());
            }
        }
    }

    // ------------------- Utility Parsers -------------------

    private static LocalDate parseDate(String s) {
        // method to parse a date string into LocalDate
        try {
            return LocalDate.parse(s, DATE_FMT);
        } catch (Exception e) {
            System.out.println("Invalid date format.");
            return null;
            // return null if parsing fails
        }
    }

    private static Double parseDouble(String s) {
        // method to parse a string into a double
        try {
            return Double.parseDouble(s);
        } catch (Exception e) {
            System.out.println("Invalid number.");
            return null;
            // return null if parsing fails
        }
    }
}