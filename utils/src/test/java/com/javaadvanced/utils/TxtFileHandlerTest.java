package com.javaadvanced.utils;

import com.javaadvanced.model.Row;
import com.javaadvanced.model.KeyValuePair;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TxtFileHandlerTest {

    private static final String FILE_PATH = "test.txt";

    //@InjectMocks
    private TxtFileHandler txtFileHandler;

    @Mock
    private File file;

    @BeforeEach
    void setUp() {
        txtFileHandler = new TxtFileHandler(FILE_PATH);
    }

    @Test
    void testParseFile() throws Exception {
        List<String> mockLines = Arrays.asList("[key1\u001Fvalue1]\u001E[key2\u001Fvalue2]");
        
        try (var mockedFileUtils = mockStatic(FileUtils.class)) {
            mockedFileUtils.when(() -> FileUtils.readLines(any(File.class), eq(StandardCharsets.UTF_8)))
                    .thenReturn(mockLines);

            List<Row> rows = txtFileHandler.parseFile();

            rows.forEach(row -> {
                row.getCells().forEach(cell -> System.out.print(cell + " "));
                System.out.println();
            });
            assertEquals(1, rows.size());
            assertEquals(2, rows.get(0).getCells().size());
            assertEquals("key1", rows.get(0).getCells().get(0).getKey());
            assertEquals("value1", rows.get(0).getCells().get(0).getValue());
            
        }
    }


    @Test
    void testParseStream() throws Exception {
        
        InputStream mockStream = mock(InputStream.class);
        List<String> mockLines = Arrays.asList("[key1\u001Fvalue1]\u001E[key2\u001Fvalue2]");
        mockStatic(IOUtils.class);
        when(IOUtils.readLines(eq(mockStream), eq(StandardCharsets.UTF_8))).thenReturn(mockLines);

        List<Row> rows = txtFileHandler.parseStream(mockStream);

        rows.forEach(row -> {
            row.getCells().forEach(cell -> System.out.print(cell + " "));
            System.out.println();
        });
        assertEquals(1, rows.size());
        assertEquals(2, rows.get(0).getCells().size());
        assertEquals("key1", rows.get(0).getCells().get(0).getKey());
        assertEquals("value1", rows.get(0).getCells().get(0).getValue());
        
    }

    @Test
    void testSaveToFile() throws Exception {
        
        Row row = new Row();
        row.addCell(new KeyValuePair("key1", "value1"));
        row.addCell(new KeyValuePair("key2", "value2"));
        List<Row> rows = List.of(row);

        try (var mockedFileUtils = mockStatic(FileUtils.class)) {
            
            txtFileHandler.saveToFile(FILE_PATH, rows);

            // Capture the written data
            ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
            mockedFileUtils.verify(() -> FileUtils.writeStringToFile(any(File.class), captor.capture(), eq(StandardCharsets.UTF_8)));

            String expectedOutput = "[key1\u001Fvalue1]\u001E[key2\u001Fvalue2]";
            System.out.println("Test Expected: "+ expectedOutput);
            System.out.println("Test Output: "+ captor.getValue());
            assertEquals(expectedOutput, captor.getValue());
            
        }
    }
    
    @Test
    void testSaveToFileIOException() {
        Row row = new Row();
        row.addCell(new KeyValuePair("key1", "value1"));
        List<Row> rows = List.of(row);

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        
        try (var mockedFileUtils = mockStatic(FileUtils.class)) {
            mockedFileUtils.when(() -> FileUtils.writeStringToFile(any(File.class), anyString(), eq(StandardCharsets.UTF_8)))
                           .thenThrow(new IOException("Mocked IO Error"));

            System.setOut(new PrintStream(outContent));

            txtFileHandler.saveToFile(FILE_PATH, rows);

            // Verify error message is printed
            String expectedMessage = "Error saving to file: Mocked IO Error";
            assertTrue(outContent.toString().contains(expectedMessage));

        } finally {
            //Reset
            System.setOut(originalOut);
        }
    }


}
