package util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public final class SenhaAPI {

    private static final int ITERACOES = 210_000;

    private SenhaAPI() {
    }
    fdsa

    // Formato salvo no banco: iterações:sal:hash
    public static String gerarHash(String senha) {
        byte[] sal = new byte[16];
        new SecureRandom().nextBytes(sal);

        byte[] hash = derivar(senha, sal, ITERACOES);

        return ITERACOES + ":"
                + Base64.getEncoder().encodeToString(sal) + ":"
                + Base64.getEncoder().encodeToString(hash);
    }

    public static boolean conferir(String senha, String armazenado) {
        try {
            String[] partes = armazenado.split(":");
            int iteracoes = Integer.parseInt(partes[0]);
            byte[] sal = Base64.getDecoder().decode(partes[1]);
            byte[] esperado = Base64.getDecoder().decode(partes[2]);

            byte[] calculado = derivar(senha, sal, iteracoes);
            return MessageDigest.isEqual(esperado, calculado);

        } catch (RuntimeException e) {
            return false;
        }
    }

    private static byte[] derivar(String senha, byte[] sal, int iteracoes) {
        try {
            PBEKeySpec spec = new PBEKeySpec(senha.toCharArray(), sal, iteracoes, 256);
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(spec)
                    .getEncoded();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Não foi possível processar a senha.", e);
        }
    }
}