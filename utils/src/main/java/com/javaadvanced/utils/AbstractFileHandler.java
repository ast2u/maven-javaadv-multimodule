package com.javaadvanced.utils;
import com.javaadvanced.model.Row;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public abstract class AbstractFileHandler {
    protected static final String DELIMITER = "\u001F";
    protected static final String PAIR_DELIMITER = "\u001E";
    protected File file;

    public AbstractFileHandler(String filePath) {
        this.file = new File(filePath);
    }

    
    public abstract List<Row> parseFile() throws IOException;
    public abstract List<Row> parseStream(InputStream inputStream) throws IOException;
    public abstract void saveToFile(String filePath, List<Row> rows);

    
    public boolean fileExists() {
        return file.exists();
    }
}
