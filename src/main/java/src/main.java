package src;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableWebMvc
@EnableAsync
public class main {


    public static void main(String[] args) {
        SpringApplication.run(main.class, args);
        System.out.println("""
                --------------------------------------------------------------------------------------------------------------------------------------------------------
                """);
        System.out.println("""
                🚀 Server ready at http://localhost:8080
                """);
        System.out.println("""
                🚀 Server ready at http://localhost:8080/api/v1
                """);
        System.out.println("""
                🚀 Api doc ready at http://localhost:8080/swagger-ui/index.html
                """);
    }
    @Bean
    public Cloudinary cloudinary(){
        Cloudinary cloud = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dqptxftlv",
                "api_key", "268148952558612",
                "api_secret", "8gzmcO9n4yChRpHfXAr-8-T6ZXQ",
                "secure", true
                ));

        return cloud;
    }

}
