package com.javaadvanced.app;

import com.javaadvanced.service.KeyValueManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.Scanner;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class AppControllerTest {

    @Mock
    private KeyValueManager service;

    @Mock
    private Scanner sc;

    @InjectMocks
    private AppController appController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testPromptFilePath() {
        when(sc.nextLine()).thenReturn("testfile.txt");
        String filePath = appController.promptFilePath();
        assertEquals("testfile.txt", filePath);
    }

    @Test
    void testDataCheckWithValidFile() throws IOException {
        String[] args = {"testfile.txt"};
        AppController controller = new AppController(args, sc);

        // Mock the void method loadData()
        doNothing().when(service).loadData();

        controller.dataCheck();

        // Verify that loadData() was called
        verify(service, times(1)).loadData();
    }

    @Test
    void testDataCheckWithInvalidFile() {
        String[] args = {"invalidfile.doc"};
        AppController controller = new AppController(args, sc);
        controller.dataCheck();
        verify(service, never()).loadData();
    }

    @Test
    void testSearchPatt() {
        when(sc.next()).thenReturn("searchTerm");
        appController.searchPatt();
        verify(service, times(1)).searchPatt("searchTerm");
    }

    @Test
    void testEditMenu() {
        when(sc.next()).thenReturn("1x2", "k", "newKey");
        appController.editMenu();
        verify(service, times(1)).editKeyOrValue(1, 2, true, "newKey", null);
    }

    @Test
    void testAddRow() {
        when(sc.nextInt()).thenReturn(1, 3);
        appController.addRow();
        verify(service, times(1)).addRow(1, 3);
    }

    @Test
    void testSortRow() {
        when(sc.next()).thenReturn("1-asc");
        appController.sortRow();
        verify(service, times(1)).sortRow(1, "asc");
    }

    @Test
    void testResetData() {
        when(sc.next()).thenReturn("2x2");
        appController.resetData();
        verify(service, times(1)).resetData(2, 2);
    }
}