package util;

import jakarta.mail.MessagingException;

public final class EmailService {
    private EmailService() {}

    public static void enviarRecuperacao(String destino, String nome, String link)
            throws MessagingException {
        GmailApiService.send(destino, nome, link);
    }
}
