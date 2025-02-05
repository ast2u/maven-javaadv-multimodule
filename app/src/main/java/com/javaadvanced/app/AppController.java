package com.javaadvanced.app;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.Scanner;

import com.javaadvanced.model.SearchResult;
import com.javaadvanced.service.IKeyValueService;
import com.javaadvanced.service.KeyValueServiceImpl;

public class AppController {

    private IKeyValueService service;
    private final Scanner sc;
    private String filePath;

    public AppController(String[] args, Scanner scanner) {
        this.sc = scanner;
        this.filePath = args.length > 0 ? args[0] : promptFilePath();
        this.service = new KeyValueServiceImpl(filePath);
        dataCheck();
    }

    public String promptFilePath() {
        System.out.print("Please enter the path to the input file: ");
        String path = sc.nextLine();
        if (!path.endsWith(".txt")) {
            System.out.println("Invalid file type. Please provide a .txt file.");
            return promptFilePath();
        }
        return path;
    }

    public void printStructure() {
        service.print2DStructure();
    }

    public void saveFile() {
        service.saveData();
    }

    public final void dataCheck() {
        while (!filePath.endsWith(".txt")) {
            System.out.println("Invalid file type. Please provide a .txt file.");
            filePath = promptFilePath();
        }
        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("File not found: " + filePath);
            String response = "";
            while (!response.equals("yes") && !response.equals("no")) {
                System.out.print("Do you want to create this file? (yes/no): ");
                response = sc.nextLine().trim().toLowerCase();

                switch (response) {
                    case "yes" ->
                        service.createFileFromResource();
                    case "no" -> {
                        System.out.println("Exiting the program.");
                        System.exit(0);
                    }
                    default ->
                        System.out.println("Must be yes or no.");

                }
            }
        } else {
            try {
                service.loadData();
                service.print2DStructure();
            } catch (IOException e) {
                System.out.println("Error loading data: " + e.getMessage());
            }
        }
    }

    public void searchPatt() {
        System.out.print("Search: ");
        String target = sc.nextLine();

        // Call the search method and store the result
        SearchResult result = service.searchPatt(target);

        // Print the search results
        if (result.getCount() > 0) {
            System.out.println("Search Result: " + result.getPositions());
            System.out.println("Total Matches: " + result.getCount());
        } else {
            System.out.println("No matches found.");
        }
    }


    public void editMenu() {
        String inputChoice = "";
        int rowIndex = 0;
        int colIndex = 0;
        boolean exitEdit = false;
        while (!exitEdit) {
            System.out.print("Enter Dimension: ");
            String input = sc.nextLine().trim();
            Optional<String[]> optionalDimension = Optional.ofNullable(input.split("x"));
            try {
                String[] inputDimension = optionalDimension
                        .filter(arr -> arr.length == 2)
                        .orElseThrow(() -> new IllegalArgumentException("Invalid dimension format"));
                rowIndex = Integer.parseInt(inputDimension[0]);
                colIndex = Integer.parseInt(inputDimension[1]);

                if (rowIndex < 0 || colIndex < 0) {
                    System.out.println("Dimensions must be positive.");
                    continue;
                }

                if (rowIndex >= service.getData().size()) {
                    System.out.println("Row index out of range. Please enter a valid row index.");
                    continue;
                }

                if (colIndex >= service.getData().get(rowIndex).getCells().size()) {
                    System.out.println("Column index out of range for the selected row. Please enter a valid column index.");
                    continue;
                }

                exitEdit = true;
            } catch (ArrayIndexOutOfBoundsException | IllegalArgumentException e) {
                System.out.println("Invalid dimension format. Please enter in 'RowxCol' format (e.g., 1x2).");
            }
        }

        while (!inputChoice.equals("k") && !inputChoice.equals("v") && !inputChoice.equals("b")) {
            System.out.print("Choose to edit Key (k), Value (v), or Both (b): ");
            inputChoice = sc.nextLine().trim().toLowerCase();

            if (!inputChoice.equals("k") && !inputChoice.equals("v") && !inputChoice.equals("b")) {
                System.out.println("Invalid input! Please enter 'k' for Key, 'v' for Value, or 'b' for Both.");
            }
        }

        if (inputChoice.equalsIgnoreCase("b")) {
            boolean kvBoth = false;
            while (!kvBoth) {
                System.out.print("Enter the new key and value (format: key:value): ");
                String[] newValueForBoth = sc.nextLine().split(":");
                if (newValueForBoth.length == 2) {
                    service.editKeyOrValue(rowIndex, colIndex, false, null, newValueForBoth);
                    kvBoth = true;
                } else {
                    System.out.println("Invalid format for Both. Please use the format 'key:value'.");
                }
            }
        } else {
            boolean isKey = inputChoice.equalsIgnoreCase("k");
            System.out.print("Enter the new value: ");
            String newValue = sc.nextLine();
            service.editKeyOrValue(rowIndex, colIndex, isKey, newValue, null);
        }
    }

    public void addRow() {
        boolean addingRow = false;
        while (!addingRow) {
            System.out.print("Enter row number: ");
            int rowIndex = sc.nextInt();

            if (rowIndex < 0 || rowIndex > service.getData().size()) {
                System.out.println("Invalid input. Row input out of bounds.");
                continue;
            }

            System.out.print("Number of cells: ");
            int numCells = sc.nextInt();

            if (numCells < 1 || numCells > 10) {
                System.out.println("Invalid input. Try again only up to 10.");
                continue;
            }

            service.addRow(rowIndex, numCells);
            System.out.println("Row added successfully.");
            addingRow = true;
        }
    }

    public void sortRow() {
        boolean sorting = false;
        while (!sorting) {
            System.out.print("Enter sorting preference (e.g., 0-asc or 1-desc): ");
            String input = sc.nextLine().toLowerCase();

            String[] parts = input.split("-");
            if (parts.length != 2) {
                System.out.println("Invalid input format. Please use '0-asc' or '1-desc'.");
                continue;
            }

            int rowIndex;
            String sortOrder;
            try {
                rowIndex = Integer.parseInt(parts[0]);
                sortOrder = parts[1];
            } catch (NumberFormatException e) {
                System.out.println("Invalid row index. Please provide a numeric value.");
                continue;
            }

            if (!sortOrder.equals("asc") && !sortOrder.equals("desc")) {
                System.out.println("Invalid sort order. Use 'asc' or 'desc'.");
                continue;
            }

            if (rowIndex < 0 || rowIndex >= service.getData().size()) {
                System.out.println("Row index out of range.");
                continue;
            }

            service.sortRow(rowIndex, sortOrder);
            System.out.println("Row " + rowIndex + " sorted in " + sortOrder + " order.");
            sorting = true;
        }
    }

    public void resetData() {
        boolean reset = false;
        while (!reset) {
            System.out.print("Enter Dimension: ");
            String input = sc.nextLine();
            String[] dimensions = input.split("x");
            if (dimensions.length == 2) {
                try {
                    int rows = Integer.parseInt(dimensions[0].trim());
                    int cols = Integer.parseInt(dimensions[1].trim());
                    if (rows < 0 || cols < 0) {
                        System.out.println("Dimensions must be positive.");
                        continue;
                    }
                    service.resetData(rows, cols);
                    System.out.println("Data Reset");
                    service.print2DStructure();
                    reset = true;
                } catch (NumberFormatException e) {
                    System.out.println("Invalid Dimension input. Please use 'RxC' all numbers eg. RxC");
                }
            } else {
                System.out.println("Invalid input format. The format must be 'RxC' eg. 3x3, 4x4");
            }
        }
    }
}
