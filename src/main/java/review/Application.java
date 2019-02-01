package review;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        setAPIKey();
        SpringApplication.run(Application.class, args);
    }

    public static String getAPIKey() {
        return APIKey;
    }

    private static String APIKey;

    private static void setAPIKey(){
        try {
            BufferedReader in = new BufferedReader(new FileReader("apiKey.env"));
            APIKey = in.readLine();
            System.out.println("API Key = " + APIKey);
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}