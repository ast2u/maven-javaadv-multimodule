package com.javaadvanced.service;

import com.javaadvanced.model.Row;
import com.javaadvanced.model.KeyValuePair;
import com.javaadvanced.model.SearchResult;
import com.javaadvanced.utils.AbstractFileHandler;
import com.javaadvanced.utils.TxtFileHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class KeyValueServiceImplTest {

    @Mock
    private TxtFileHandler fileHandler;

    private KeyValueServiceImpl kvServiceImpl;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        kvServiceImpl = new KeyValueServiceImpl("test.txt");
        kvServiceImpl.fileHandler = fileHandler;
    }

    @Test
    public void testLoadData() throws IOException {
        List<Row> mockData = new ArrayList<>();
        Row row = new Row();
        row.addCell(new KeyValuePair("key1", "value1"));
        mockData.add(row);

        when(fileHandler.parseFile()).thenReturn(mockData);

        kvServiceImpl.loadData();
        assertEquals(1, kvServiceImpl.getData().size());
        assertEquals("key1", kvServiceImpl.getData().get(0).getCells().get(0).getKey());
    }

    @Test
    public void testSaveData() {
        List<Row> data = new ArrayList<>();
        Row row = new Row();
        row.addCell(new KeyValuePair("key1", "value1"));
        data.add(row);

        kvServiceImpl.getData().addAll(data);
        kvServiceImpl.saveData();

        verify(fileHandler, times(1)).saveToFile("test.txt", data);
    }

    @Test
    public void testSearchPatt_Found() {
        List<Row> data = new ArrayList<>();
        Row row = new Row();
        row.addCell(new KeyValuePair("key1", "vapor"));
        row.addCell(new KeyValuePair("pey2", "value2"));
        Row row2 = new Row();
        row2.addCell(new KeyValuePair("pey1", "value"));
        row2.addCell(new KeyValuePair("key2", "pops"));
        data.add(row);
        data.add(row2);

        String[] expectedResult = {"0x0","0x1","1x0","1x1"};

        kvServiceImpl.getData().addAll(data);

        // Act
        SearchResult result = kvServiceImpl.searchPatt("p");

        // Assert
        assertEquals(5, result.getCount(), "Incorrect match count");
        assertEquals(4, result.getPositions().size(), "Incorrect number of positions");
        //assertEquals(expectedResult,result.getPositions()); fix

        System.out.println("Test Search Result: " + result);
    }

    @Test
    public void testSearchPatt_NotFound() {
        List<Row> data = new ArrayList<>();
        Row row = new Row();
        row.addCell(new KeyValuePair("key1", "value1"));
        row.addCell(new KeyValuePair("key2", "value2"));
        data.add(row);
        kvServiceImpl.getData().addAll(data);

        // Act
        SearchResult result = kvServiceImpl.searchPatt("missingKey");

        // Assert
        assertEquals(0, result.getCount(), "Match count should be zero");
        assertTrue(result.getPositions().isEmpty(), "Positions list should be empty");
        System.out.println("Test Search Result: " + result);
    }

    @Test
    public void testEditKeyOrValue() {
        List<Row> data = new ArrayList<>();
        Row row = new Row();
        row.addCell(new KeyValuePair("key1", "value1"));
        data.add(row);

        kvServiceImpl.getData().addAll(data);

        kvServiceImpl.editKeyOrValue(0, 0, true, "newKey", null);
        assertEquals("newKey", kvServiceImpl.getData().get(0).getCells().get(0).getKey());

        kvServiceImpl.editKeyOrValue(0, 0, false, "newValue", null);
        assertEquals("newValue", kvServiceImpl.getData().get(0).getCells().get(0).getValue());
    }

    @Test
    public void testEditBoth(){
        List<Row> data = new ArrayList<>();
        Row row = new Row();
        row.addCell(new KeyValuePair("key1", "value1"));
        row.addCell(new KeyValuePair("key2", "value2"));
        data.add(row);
        String[] testPair = {"newKey","newValue"};

        KeyValuePair expectedPair = data.get(0).getCells().get(0);

        kvServiceImpl.getData().addAll(data);

        kvServiceImpl.editKeyOrValue(0, 0, false, null, testPair);

        KeyValuePair actualPair = kvServiceImpl.getData().get(0).getCells().get(0);
        //System.out.println("Expected: " + expectedPair);
        //System.out.println("Actual: " + actualPair);
    
        // Check if they are equal using the equals method
        //System.out.println("Are they equal? " + expectedPair.equals(actualPair));
    
        assertEquals(expectedPair, actualPair);

    }

    @Test
    public void testAddRow() {
        kvServiceImpl.addRow(0, 2);
        assertEquals(1, kvServiceImpl.getData().size());
        assertEquals(2, kvServiceImpl.getData().get(0).getCells().size());
    }

    @Test
    public void testSortRow() {
        List<Row> data = new ArrayList<>();
        Row row = new Row();
        row.addCell(new KeyValuePair("bAb", "bbb"));
        row.addCell(new KeyValuePair("aBa", "bbb"));
        row.addCell(new KeyValuePair("aab", "bbb"));
        Row row2 = new Row();
        row2.addCell(new KeyValuePair("Q#O", "Ch>"));
        row2.addCell(new KeyValuePair("W!2", "{IA"));
        row2.addCell(new KeyValuePair("]j9", "OM+"));
        data.add(row);
        data.add(row2);

        kvServiceImpl.getData().addAll(data);


        //FIX
        kvServiceImpl.sortRow(0, "asc");
        assertEquals("aab", kvServiceImpl.getData().get(0).getCells().get(0).getKey());

        kvServiceImpl.sortRow(1, "desc");
        assertEquals("Q#O", kvServiceImpl.getData().get(1).getCells().get(1).getKey());
    }

    @Test
    public void testResetData() {
        kvServiceImpl.resetData(2, 2);
        assertEquals(2, kvServiceImpl.getData().size());
        assertEquals(2, kvServiceImpl.getData().get(0).getCells().size());
    }
}