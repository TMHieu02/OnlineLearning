package src.service.User.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;

@Service
public class MailService {
    @Autowired
    private JavaMailSender javaMailSender;

    @Value("hieultkrm@gmail.com")
    private String fromMail;

    private String Subject;

    public void sendMessageMail(String mail, MessageDto messageDto)
    {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(fromMail);
        Subject = "Chúng tôi xin được phản hồi thắc mắc của bạn";
        simpleMailMessage.setSubject(Subject);
        simpleMailMessage.setText(messageDto.getMessage());
        simpleMailMessage.setTo(mail);

        javaMailSender.send(simpleMailMessage);
    }

    public void sendOtpEmailForPassword(String email, String otp) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setTo(email);
            mimeMessageHelper.setSubject("Reset Password via OTP");
            String emailContent = String.format(
                    "<html>" +
                            "<body style=\"font-family: Arial, sans-serif;\">" +
                            "<div style=\"background-color: #f4f4f4; padding: 20px; border-radius: 10px;\">" +
                            "<h2 style=\"color: #333;\">Hello,</h2>" +
                            "<p style=\"color: #666;\">Your verification code to reset the password is: <strong>%s</strong></p>" +
                            "<p style=\"color: #666;\">Please use this code to complete the password reset process.</p>" +
                            "</div>" +
                            "<div style=\"margin-top: 20px; text-align: center;\">" +
                            "<p style=\"color: #999; font-size: 12px;\">© 2023 Your Company. All rights reserved.</p>" +
                            "</div>" +

                            "</body>" +
                            "</html>",
                    otp
            );
            mimeMessageHelper.setText(emailContent, true);
            javaMailSender.send(mimeMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }





}
