package com.javaadvanced.app;

import java.util.Scanner;

public class MainProgram {

    public final static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        AppController app = new AppController(args);
        boolean exit = false;
        while (!exit) {
            printMenu();
            System.out.print("Action: ");
            String action = sc.next();

            switch (action.toLowerCase()) {
                case "search" -> app.searchPatt();
                case "edit" -> app.editMenu();
                case "add_row" -> app.addRow();
                case "sort" -> app.sortRow();
                case "print" -> app.printStructure();
                case "reset" -> app.resetData();
                case "x" -> {
                    exit = true;
                    app.saveFile();
                    System.out.println("[EXIT PROGRAM]");
                }
                default -> System.out.println("Select only the menu items!");
            }
        }
    }

    static void printMenu() {
        System.out.println("\n-------------");
        System.out.println("[MENU]");
        System.out.println("[ search ] - Search");
        System.out.println("[ edit ] - Edit");
        System.out.println("[ add_row ] - Add Row");
        System.out.println("[ sort ] - Sort");
        System.out.println("[ print ] - Print");
        System.out.println("[ reset ] - Reset");
        System.out.println("[ x ] - Exit");
    }
}
