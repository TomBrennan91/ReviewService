package io.brennan.review;

import io.brennan.Application;
import io.brennan.Utilities;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;

class ReviewTest {
    @Test
    void testApi() throws IOException {
        BufferedReader reader = Utilities.fromFile("/movieBigList.txt", ReviewTest.class);
        reader.lines().limit(1).forEach(line -> printLine(line));
    }

    private void printLine(String line){
        String[] splitLine = line.split("\t");
        String[] splitTitle = splitLine[1].split(" \\(");
        String title = splitTitle[0].replace("\"","");
        if (title.contains(",")){
            String[] commaTitle = title.split(",");
            title = commaTitle[1].substring(1) + " " + commaTitle[0];
        }

        if (splitTitle.length > 1) {
            String yearStr = splitTitle[splitTitle.length - 1].replace(")","").replace("\"","");
            try {
                Integer year = Integer.parseInt(yearStr);

            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println(title);
        }

    }

}