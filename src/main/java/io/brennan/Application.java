package io.brennan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

@SpringBootApplication
public class Application {
    public static void main(String[] args) throws IOException{
        setAPIKey();
        SpringApplication.run(Application.class, args);
    }

    public static String getAPIKey() {
        return APIKey;
    }

    private static String APIKey;

    private static void setAPIKey() throws IOException{
        String key = System.getenv("EXTERNAL_API_KEY");
        System.out.println(key);
        if (key == null){
            BufferedReader in = new BufferedReader(new FileReader("apiKey.env"));
            key = in.lines().findFirst().get();
        }

        APIKey = key;
        System.out.println("API Key = " + APIKey);
    }
}