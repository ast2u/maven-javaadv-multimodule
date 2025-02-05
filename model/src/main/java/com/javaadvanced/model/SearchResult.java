package com.javaadvanced.model;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class SearchResult {
    private int count;
    private List<String> positions;

    @Override
    public String toString() {
        return "SearchResult{" +
                "count=" + count +
                ", positions=" + positions +
                '}';
    }
}
