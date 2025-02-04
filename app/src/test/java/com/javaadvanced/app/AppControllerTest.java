package com.javaadvanced.app;

import com.javaadvanced.service.KeyValueManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Scanner;

import static org.mockito.Mockito.*;

public class AppControllerTest {

    @Mock
    private KeyValueManager keyValueManager;

    @Mock
    private Scanner scanner;

    private AppController appController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        appController = new AppController(new String[]{"test.txt"}, scanner);
        appController.service = keyValueManager; // Inject the mock
    }

    @Test
    public void testPrintStructure() {
        appController.printStructure();
        verify(keyValueManager, times(1)).print2DStructure();
    }

    @Test
    public void testSaveFile() {
        appController.saveFile();
        verify(keyValueManager, times(1)).saveData();
    }

    @Test
    public void testSearchPatt() {
        when(scanner.next()).thenReturn("key1");
        appController.searchPatt();
        verify(keyValueManager, times(1)).searchPatt("key1");
    }

    @Test
    public void testEditMenu() {
        when(scanner.next())
        .thenReturn("0x0")  // Valid dimension input
        .thenReturn("k")    // Valid edit choice (key)
        .thenReturn("newK");
 
        appController.editMenu();
        verify(keyValueManager, times(1)).editKeyOrValue(0, 0, true, "newK", null);
    }

    @Test
    public void testAddRow() {
        when(scanner.nextInt()).thenReturn(0).thenReturn(0);
        appController.addRow();
        verify(keyValueManager, times(1)).addRow(0, 0);
    }

    @Test
    public void testSortRow() {
        when(scanner.next()).thenReturn("0-asc");
        appController.sortRow();
        verify(keyValueManager, times(1)).sortRow(0, "asc");
    }

    @Test
    public void testResetData() {
        when(scanner.next()).thenReturn("2x2");
        appController.resetData();
        verify(keyValueManager, times(1)).resetData(2, 2);
    }
}