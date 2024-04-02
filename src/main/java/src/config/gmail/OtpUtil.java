package src.config.gmail;

import java.util.Random;
import org.springframework.stereotype.Component;

@Component
public class OtpUtil {

    public String generateOtp() {
        Random random = new Random();
        int randomNumber = random.nextInt(9);
        String output = Integer.toString(randomNumber);

        while (output.length() < 1) {
            output = "0" + output;
        }
        return output;
    }
}
