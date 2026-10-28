package util;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;
import java.util.logging.Logger;

public final class EmailService {

    private static final Logger LOG = Logger.getLogger(EmailService.class.getName());

    private EmailService() {
    }

    // Envia o link de redefinição de senha para o e-mail do administrador.
    public static void enviarRecuperacao(String destino, String nome, String link)
            throws MessagingException {

        String host = config("SMTP_HOST", null);
        String usuario = config("SMTP_USER", null);
        String senha = config("SMTP_PASSWORD", null);

        if (host == null || usuario == null || senha == null) {
            LOG.warning("E-mail não configurado (SMTP_HOST, SMTP_USER, SMTP_PASSWORD). "
                    + "Link de redefinição para " + destino + ": " + link);
            return;
        }

        String porta = config("SMTP_PORT", "587");
        String remetente = config("SMTP_FROM", usuario);

        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", porta);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.connectiontimeout", "10000");
        props.put("mail.smtp.timeout", "10000");
        props.put("mail.smtp.writetimeout", "10000");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2 TLSv1.3");

        if ("465".equals(porta)) {
            props.put("mail.smtp.ssl.enable", "true");
        } else {
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.starttls.required", "true");
        }

        Session sessao = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(usuario, senha);
            }
        });

        MimeMessage mensagem = new MimeMessage(sessao);
        mensagem.setFrom(new InternetAddress(remetente));
        mensagem.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destino, false));
        mensagem.setSubject("Redefinição de senha | Renovaí", "UTF-8");
        mensagem.setText(
                "Olá, " + nome + "!\n\n"
                        + "Recebemos um pedido para redefinir a senha da sua conta no Renovaí.\n"
                        + "Para escolher uma nova senha, acesse o link abaixo. "
                        + "Ele vale por 30 minutos e só pode ser usado uma vez:\n\n"
                        + link + "\n\n"
                        + "Se você não fez esse pedido, ignore este e-mail. Sua senha continua a mesma.\n",
                "UTF-8");

        Transport.send(mensagem);
        LOG.info("E-mail de redefinição enviado para " + destino);
    }

    // Lê uma variável de ambiente e devolve o padrão quando ela não existe ou está vazia.
    private static String config(String variavel, String padrao) {
        String valor = System.getenv(variavel);
        if (valor == null || valor.isBlank()) {
            return padrao;
        }
        return valor.trim();
    }
}