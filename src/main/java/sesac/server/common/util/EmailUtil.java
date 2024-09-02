package sesac.server.common.util;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Component
@Log4j2
public class EmailUtil {

    private JavaMailSender emailSender;
    private TemplateEngine templateEngine;
    private RetryTemplate retryTemplate;

    @Value("${spring.email.sender.name}")
    private String senderName;

    @Value("${spring.email.sender.address}")
    private String senderAddress;


    @Autowired
    public EmailUtil(JavaMailSender emailSender, TemplateEngine templateEngine) {
        this.emailSender = emailSender;
        this.templateEngine = templateEngine;
        this.retryTemplate = retryTemplate();
    }

    @Bean
    private RetryTemplate retryTemplate() { // 재시도, 최대 3번까지 1초 간격
        RetryTemplate retryTemplate = new RetryTemplate();

        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(3);

        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        backOffPolicy.setBackOffPeriod(1000);

        retryTemplate.setRetryPolicy(retryPolicy);
        retryTemplate.setBackOffPolicy(backOffPolicy);

        return retryTemplate;
    }

    @Async("threadPoolTaskExecutor")
    public void sendTemplateEmail(String to, String subject, String templateName, Context context) {
        retryTemplate.execute(retryContext -> {
            try {
                MimeMessage message = emailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

                helper.setTo(to);                                                   // 수신자
                helper.setSubject(subject);                                         // 제목
                helper.setFrom(new InternetAddress(senderAddress, senderName));     // 발신자 정보

                String htmlContent = templateEngine.process(templateName, context); // html 템플릿
                helper.setText(htmlContent, true);                             // 내용

                emailSender.send(message);
                log.info("성공적으로 이메일 전송이 완료됨: {}", to);
                return null;
            } catch (MessagingException | UnsupportedEncodingException e) {
                log.error("이메일 전송 실패: {}. Attempt: {}. Error: {}",
                        to, retryContext.getRetryCount(), e.getMessage());
                // TODO: 추가적인 error logging
                throw new RuntimeException("이메일 전송 실패", e);
            }
        });
    }
}