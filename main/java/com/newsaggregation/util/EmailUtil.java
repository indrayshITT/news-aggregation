package com.newsaggregation.util;

import java.io.IOException;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;

public class EmailUtil {

	private static final String FROM_EMAIL = System.getenv("SENDER_EMAIL");
    private static final String SENDGRID_API_KEY = System.getenv("SENDGRID_API_KEY");

    public static void send(String toEmail, String subject, String body) {
        Email from = new Email(FROM_EMAIL);
        Email to = new Email(toEmail);
        Content content = new Content("text/plain", body);
        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid(SENDGRID_API_KEY);
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);

            System.out.println("[EmailUtil] Status Code: " + response.getStatusCode());
            System.out.println("[EmailUtil] Body: " + response.getBody());
            System.out.println("[EmailUtil] Headers: " + response.getHeaders());
        } catch (IOException ex) {
            System.err.println("[EmailUtil] Failed to send email: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
