package com.javaadvanced.service;

import com.javaadvanced.model.Row;
import com.javaadvanced.model.KeyValuePair;
import com.javaadvanced.model.SearchResult;
import com.javaadvanced.utils.TxtFileHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.stream.IntStream;
import java.io.IOException;
import java.io.InputStream;
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
        List<Row> mockData = List.of(
                new Row(List.of(new KeyValuePair("key1", "vapor"), new KeyValuePair("pey2", "value2"))),
                new Row(List.of(new KeyValuePair("pey1", "value"), new KeyValuePair("key2", "pops")))
        );

        when(fileHandler.parseFile()).thenReturn(mockData);

        kvServiceImpl.loadData();
        assertEquals(2, kvServiceImpl.getData().size());
    }

    @Test
    public void testSaveData() {
        Row row = new Row();
        row.addCell(new KeyValuePair("key1", "value1"));
        List<Row> data = List.of(row);

        kvServiceImpl.getData().addAll(data);
        kvServiceImpl.saveData();

        verify(fileHandler, times(1)).saveToFile("test.txt", data);
    }

    @Test
    public void testCreateFileFromResource() throws IOException {
        try (InputStream resourceStream = getClass().getClassLoader().getResourceAsStream("default_data.txt")) {
            
            // Ensure that the resource stream is not null (the resource file should exist in src/test/resources)
            assertNotNull(resourceStream, "Default resource file not found in the module's resources.");

            List<Row> mockData = List.of(
                    new Row(List.of(new KeyValuePair("key1", "vapor"), new KeyValuePair("key2", "value"))),
                    new Row(List.of(new KeyValuePair("key3", "test")))
            );

            when(fileHandler.parseStream(any(InputStream.class))).thenReturn(mockData);

            kvServiceImpl.createFileFromResource();

            // Verify that fileHandler.saveToFile was called to save the loaded data
            verify(fileHandler, times(1)).saveToFile(eq("test.txt"), eq(mockData));
            kvServiceImpl.print2DStructure();
            
            assertEquals(mockData, kvServiceImpl.getData(), "Data loaded from resource should be set correctly.");

        } catch (IOException e) {
            fail("Error occurred while reading the resource file: " + e.getMessage());
        }
    }


    @Test
    public void testSearchPatt_Found() {
        List<Row> data = List.of(
                new Row(List.of(new KeyValuePair("key1", "vapor"), new KeyValuePair("pey2", "value2"))),
                new Row(List.of(new KeyValuePair("pey1", "value"), new KeyValuePair("key2", "pops")))
        );

        kvServiceImpl.getData().addAll(data);
        SearchResult result = kvServiceImpl.searchPatt("p");

        assertEquals(5, result.getCount());
        assertEquals(4, result.getPositions().size());
        assertArrayEquals(new String[]{"0x0", "0x1", "1x0", "1x1"}, result.getPositions().toArray());
    }

    @Test
    public void testSearchPatt_NotFound() {
        List<Row> data = List.of(
                new Row(List.of(new KeyValuePair("key1", "vapor"), new KeyValuePair("pey2", "value2"))),
                new Row(List.of(new KeyValuePair("pey1", "value"), new KeyValuePair("key2", "pops")))
        );

        kvServiceImpl.getData().addAll(data);
        SearchResult result = kvServiceImpl.searchPatt("missingKey");

        assertEquals(0, result.getCount());
        assertTrue(result.getPositions().isEmpty());
    }

    @Test
    public void testEditKeyOrValue() {
        Row row = new Row(List.of(new KeyValuePair("key1", "value1")));
        kvServiceImpl.getData().add(row);

        kvServiceImpl.editKeyOrValue(0, 0, true, "newKey", null);
        kvServiceImpl.editKeyOrValue(0, 0, false, "newValue", null);

        KeyValuePair editedPair = kvServiceImpl.getData().get(0).getCells().get(0);
        assertEquals("newKey", editedPair.getKey());
        assertEquals("newValue", editedPair.getValue());
    }

    @Test
    public void testEditBoth() {
        Row row = new Row(List.of(new KeyValuePair("key1", "value1")));
        kvServiceImpl.getData().add(row);
        kvServiceImpl.editKeyOrValue(0, 0, false, null, new String[]{"newKey", "newValue"});
        assertEquals(new KeyValuePair("newKey", "newValue"), kvServiceImpl.getData()
            .get(0)
            .getCells()
            .get(0));
    }

    @Test
    public void testAddRow() {
        kvServiceImpl.addRow(0, 2);
        assertEquals(1, kvServiceImpl.getData().size());
        assertEquals(2, kvServiceImpl.getData().get(0).getCells().size());
    }

    @Test
    public void testSearchPatt_EmptyString() {
        kvServiceImpl.getData().add(new Row(List.of(new KeyValuePair("test", "value"))));
        SearchResult result = kvServiceImpl.searchPatt("");
        assertEquals(0, result.getCount());
    }

    @Test
    public void testEditOutOfBounds() {
        assertThrows(IndexOutOfBoundsException.class, () -> {
            kvServiceImpl.editKeyOrValue(99, 99, true, "newKey", null);
        });
    }

    @Test
    public void testSortRow_Empty() {
        kvServiceImpl.getData().add(new Row());
        kvServiceImpl.sortRow(0, "asc");
        assertEquals(0, kvServiceImpl.getData().get(0).getCells().size());
    }


    @Test
    public void testSortRow() {
        List<Row> data = List.of(
                new Row(List.of(new KeyValuePair("bAb", "bbb"), new KeyValuePair("aBa", "bbb"), new KeyValuePair("aab", "bbb"))),
                new Row(List.of(new KeyValuePair("Q#O", "Ch>"), new KeyValuePair("W!2", "{IA"), new KeyValuePair("]j9", "OM+")))
        );
        kvServiceImpl.getData().addAll(data);
        System.out.println("Before 2D Table Structure");
        kvServiceImpl.print2DStructure();
        System.out.println();

        kvServiceImpl.sortRow(0, "asc");

        assertArrayEquals(new String[]{"aab", "aBa", "bAb"}, 
            kvServiceImpl.getData().get(0).getCells()
            .stream()
            .map(KeyValuePair::getKey)
            .toArray());

        kvServiceImpl.sortRow(1, "desc");
        assertArrayEquals(new String[]{"W!2", "Q#O", "]j9"}, kvServiceImpl.getData().get(1).getCells()
            .stream()
            .map(KeyValuePair::getKey)
            .toArray());

        System.out.println("After 2D Table Structure");
        kvServiceImpl.print2DStructure();
    }

    @Test
    public void testResetData() {
        kvServiceImpl.resetData(2, 2);
        assertEquals(2, kvServiceImpl.getData().size());
        assertEquals(2, kvServiceImpl.getData().get(0).getCells().size());
    }
}
