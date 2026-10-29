package util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.logging.Logger;

public final class TokenRecuperacao {

    private static final Logger LOG = Logger.getLogger(TokenRecuperacao.class.getName());
    private static final byte[] SEGREDO = carregarSegredo();

    private TokenRecuperacao() {
    }

    // Gera o link assinado: base64(email|expiracao) + "." + base64(assinatura).
    // A assinatura usa o hash da senha atual, então trocar a senha invalida o link.
    public static String gerar(String email, String senhaHashAtual, int minutosDeValidade) {
        long expira = Instant.now().plusSeconds(minutosDeValidade * 60L).getEpochSecond();
        String conteudo = email + "|" + expira;

        return codificar(conteudo.getBytes(StandardCharsets.UTF_8))
                + "." + codificar(assinar(conteudo, senhaHashAtual));
    }

    // Lê o e-mail do token sem confiar nele ainda. Retorna null se estiver malformado ou expirado.
    public static String emailDoToken(String token) {
        String conteudo = conteudoDe(token);
        if (conteudo == null) {
            return null;
        }

        int separador = conteudo.lastIndexOf('|');
        if (separador <= 0) {
            return null;
        }

        try {
            long expira = Long.parseLong(conteudo.substring(separador + 1));
            if (Instant.now().getEpochSecond() > expira) {
                return null;
            }
            return conteudo.substring(0, separador);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // Confere a assinatura usando o hash da senha ATUAL da conta.
    public static boolean conferir(String token, String senhaHashAtual) {
        String conteudo = conteudoDe(token);
        if (conteudo == null) {
            return false;
        }

        try {
            byte[] recebida = Base64.getUrlDecoder().decode(token.split("\\.")[1]);
            byte[] esperada = assinar(conteudo, senhaHashAtual);
            return MessageDigest.isEqual(recebida, esperada);
        } catch (RuntimeException e) {
            return false;
        }
    }

    // Decodifica a primeira parte do token. Retorna null se o formato estiver errado.
    private static String conteudoDe(String token) {
        if (token == null) {
            return null;
        }

        try {
            String[] partes = token.split("\\.");
            if (partes.length != 2) {
                return null;
            }
            return new String(Base64.getUrlDecoder().decode(partes[0]), StandardCharsets.UTF_8);
        } catch (RuntimeException e) {
            return null;
        }
    }

    private static byte[] assinar(String conteudo, String senhaHashAtual) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(SEGREDO, "HmacSHA256"));
            return mac.doFinal((conteudo + "|" + senhaHashAtual).getBytes(StandardCharsets.UTF_8));
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Não foi possível assinar o token.", e);
        }
    }

    private static String codificar(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    // Usa RENOVAI_TOKEN_SECRET (mínimo de 32 caracteres) ou, sem ele, um segredo temporário.
    private static byte[] carregarSegredo() {
        String definido = System.getenv("RENOVAI_TOKEN_SECRET");

        if (definido != null && definido.length() >= 32) {
            return definido.getBytes(StandardCharsets.UTF_8);
        }

        LOG.warning("RENOVAI_TOKEN_SECRET não definido (mínimo de 32 caracteres). "
                + "Usando um segredo temporário: os links não sobrevivem a um reinício do servidor.");
        byte[] aleatorio = new byte[32];
        new SecureRandom().nextBytes(aleatorio);
        return aleatorio;
    }
}