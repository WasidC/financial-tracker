package com.pluralsight;
// declares that this class belongs to the package help organize

import java.time.LocalDate;
// imports LocalDate class to represent dates (year-month-day) without time

import java.time.LocalTime;
// imports LocalTime class to represent times (hour-minute-second) without date

/*
 each transaction includes a date, time, description, vendor, and amount
*/

public class Transaction {
// declare the public class 'Transaction'. Each object represents a single transaction

    private LocalDate date;
    // store the date of the transaction

    private LocalTime time;
    // store the time of the transaction

    private String description;
    // store a short description of the transaction

    private String vendor;
    // store the vendor or payee/payor for the transaction

    private double amount;
    // store the transaction amount. Positive for deposits, negative for payments

    public Transaction(LocalDate date, LocalTime time, String description, String vendor, double amount) {
        // constructor to create a new Transaction object with all fields specified

        this.date = date;
        // assigns the input date to this object's 'date' field

        this.time = time;
        // assigns the input time to this object's 'time' field

        this.description = description;
        // assigns the input description to this object's 'description' field

        this.vendor = vendor;
        // assigns the input vendor to this object's 'vendor' field

        this.amount = amount;
        // assigns the input amount to this object's 'amount' field
    }

    // ------------------- Getter Methods -------------------

    public LocalDate getDate() { return date; }
    // returns the date of this transaction

    public LocalTime getTime() { return time; }
    // returns the time of this transaction

    public String getDescription() { return description; }
    // returns the description of this transaction

    public String getVendor() { return vendor; }
    // returns the vendor of this transaction

    public double getAmount() { return amount; }
    // returns the amount of this transaction

    // ------------------- Utility Methods -------------------

    public String toCSVLine() {
        // converts this Transaction object into string for writing to CSV

        return date + "|" + time + "|" + description + "|" + vendor + "|" + amount;
        //  example for output "2025-10-17|14:30:00|Salary|Company|500.00"
    }
}