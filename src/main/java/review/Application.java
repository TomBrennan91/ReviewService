package review;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {

        try{
            System.out.println( Review.getHTML("http://www.omdbapi.com/?apikey=714ddca5&t=" + "tom"));
        } catch (Exception e){
            e.printStackTrace();
        }

        SpringApplication.run(Application.class, args);
    }
}