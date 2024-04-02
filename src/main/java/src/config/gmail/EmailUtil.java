package src.config.gmail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeMessage;

@Component
public class EmailUtil {

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendOtpEmail(String email, String otp) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setTo(email);
            mimeMessage.setSubject("Mã kích hoạt khóa học");
            mimeMessageHelper.setSubject("Verify OTP");

            String emailContent = String.format("""
                    <div>
                      <a href="http://localhost:8080/verify-account?email=%s&otp=%s" target="_blank">Click this link to verify</a>
                    </div>
                    """, email, otp);

            mimeMessageHelper.setText(emailContent, true);

            javaMailSender.send(mimeMessage);
        } catch (Exception e) {
            // Handle exception appropriately (e.g., log it)
            e.printStackTrace();
        }
    }

    public void guiMaKichHoat(String email, String otp) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setTo(email);
            mimeMessage.setSubject("Mã kích hoạt khóa học");
            mimeMessageHelper.setSubject("Verify OTP");

            String emailContent = String.format("""
                    <div>
                      <a href="http://localhost:8080/verify-account?email=%s&otp=%s" target="_blank">Click this link to verify</a>
                    </div>
                    """, email, otp);

            mimeMessageHelper.setText(emailContent, true);

            javaMailSender.send(mimeMessage);
        } catch (Exception e) {
            // Handle exception appropriately (e.g., log it)
            e.printStackTrace();
        }
    }

    public void guiMakichHoatKhoahoc(String email, String otp, String courseName) {

        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setTo(email);
            mimeMessage.setSubject("Mã kích hoạt khóa học");
            mimeMessageHelper.setSubject("Mã kích hoạt khóa khọc");

            String emailContent = "Mã kích hoạt khóa học:" +courseName  +"là: " + otp;

            mimeMessageHelper.setText(emailContent, true);

            javaMailSender.send(mimeMessage);
        } catch (Exception e) {
            // Handle exception appropriately (e.g., log it)
            e.printStackTrace();
        }
    }

}
