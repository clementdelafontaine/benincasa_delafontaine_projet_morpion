package cipher;

/**
 * #TODO commenter + document avec architecture + wireshark paquets cryptés
 */

import java.nio.charset.StandardCharsets;
import java.security.*;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class CryptageMorpion {
    KeyGenerator keygen;
    SecretKey key;
    Cipher cipher;
    Cipher decipher;

    public CryptageMorpion() {
        generateDESKey();

    }

    private void generateDESKey() {
        try {
            this.keygen = KeyGenerator.getInstance("DES");
            this.keygen.init(56);
            this.key = this.keygen.generateKey();
            this.cipher = Cipher.getInstance("DES");
            this.cipher.init(1, this.key);
            this.decipher = Cipher.getInstance("DES");
            this.decipher.init(2, this.key);
        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
    }

    private KeyPair generateKeys() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            e.printStackTrace();
        }
    }

    public byte[] encrypt(String message) throws IllegalBlockSizeException, BadPaddingException {
        byte[] bytePhrase = message.getBytes(StandardCharsets.UTF_8);
        return this.cipher.doFinal(bytePhrase);
    }

    public String decrypt(byte[] cryptedMessage) throws IllegalBlockSizeException, BadPaddingException {
        return new String(this.decipher.doFinal(cryptedMessage), StandardCharsets.UTF_8);
    }

    public static void main(String[] args) {
        String phrase = "phrase de test";

        try {
            CryptageMorpion crypto = new CryptageMorpion();
            byte[] cryptedPhrase = crypto.encrypt(phrase);
            String phraseCryptee = new String(cryptedPhrase, StandardCharsets.UTF_8);
            System.out.println(phraseCryptee);
            String decryptedPhrase = crypto.decrypt(cryptedPhrase);
            System.out.println("decryptée : " + decryptedPhrase);
        } catch (BadPaddingException | IllegalBlockSizeException var6) {
            var6.printStackTrace();
        }

    }

    public String getDESKey() {
        return this.key.toString();
    }
}