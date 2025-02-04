package com.javaadvanced.service;

import com.javaadvanced.model.Row;
import com.javaadvanced.model.KeyValuePair;
import com.javaadvanced.utils.AbstractFileHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class KeyValueManagerTest {

    @Mock
    private AbstractFileHandler fileHandler;

    private KeyValueManager keyValueManager;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        keyValueManager = new KeyValueManager("test.txt");
        keyValueManager.fileHandler = fileHandler; // Inject the mock
    }

    @Test
    public void testLoadData() throws IOException {
        List<Row> mockData = new ArrayList<>();
        Row row = new Row();
        row.addCell(new KeyValuePair("key1", "value1"));
        mockData.add(row);

        when(fileHandler.parseFile()).thenReturn(mockData);

        keyValueManager.loadData();
        assertEquals(1, keyValueManager.getData().size());
        assertEquals("key1", keyValueManager.getData().get(0).getCells().get(0).getKey());
    }

    @Test
    public void testSaveData() {
        List<Row> data = new ArrayList<>();
        Row row = new Row();
        row.addCell(new KeyValuePair("key1", "value1"));
        data.add(row);

        keyValueManager.getData().addAll(data);
        keyValueManager.saveData();

        verify(fileHandler, times(1)).saveToFile("test.txt", data);
    }

    @Test
    public void testSearchPatt() {
        List<Row> data = new ArrayList<>();
        Row row = new Row();
        row.addCell(new KeyValuePair("key1", "value1"));
        row.addCell(new KeyValuePair("key2", "value2"));
        data.add(row);

        keyValueManager.getData().addAll(data);

        keyValueManager.searchPatt("key1");
        // Add assertions based on your expected output
    }

    @Test
    public void testEditKeyOrValue() {
        List<Row> data = new ArrayList<>();
        Row row = new Row();
        row.addCell(new KeyValuePair("key1", "value1"));
        data.add(row);

        keyValueManager.getData().addAll(data);

        keyValueManager.editKeyOrValue(0, 0, true, "newKey", null);
        assertEquals("newKey", keyValueManager.getData().get(0).getCells().get(0).getKey());

        keyValueManager.editKeyOrValue(0, 0, false, "newValue", null);
        assertEquals("newValue", keyValueManager.getData().get(0).getCells().get(0).getValue());
    }

    @Test
    public void testAddRow() {
        keyValueManager.addRow(0, 2);
        assertEquals(1, keyValueManager.getData().size());
        assertEquals(2, keyValueManager.getData().get(0).getCells().size());
    }

    @Test
    public void testSortRow() {
        List<Row> data = new ArrayList<>();
        Row row = new Row();
        row.addCell(new KeyValuePair("key2", "value2"));
        row.addCell(new KeyValuePair("key1", "value1"));
        data.add(row);

        keyValueManager.getData().addAll(data);

        keyValueManager.sortRow(0, "asc");
        assertEquals("key1", keyValueManager.getData().get(0).getCells().get(0).getKey());

        keyValueManager.sortRow(0, "desc");
        assertEquals("key2", keyValueManager.getData().get(0).getCells().get(0).getKey());
    }

    @Test
    public void testResetData() {
        keyValueManager.resetData(2, 2);
        assertEquals(2, keyValueManager.getData().size());
        assertEquals(2, keyValueManager.getData().get(0).getCells().size());
    }
}