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
    SecretKey secretKey;
    KeyPair keyPair;
    Cipher cipher;
    Cipher decipher;

    public CryptageMorpion() {
    }

    private void generateDESKey() {
        try {
            this.keygen = KeyGenerator.getInstance("DES");
            this.keygen.init(56);
            this.secretKey = this.keygen.generateKey();
            this.cipher = Cipher.getInstance("DES");
            this.cipher.init(1, this.secretKey);
            this.decipher = Cipher.getInstance("DES");
            this.decipher.init(2, this.secretKey);
        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
    }

    /**
     * TODO
     * @return
     */
    public String getEncryptedSecretKey() {
        return "";
    }

    private void generateKeys() {
        keyPair = null;

        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");

            //Initiallisation
            keyGen.initialize(2048, random);

            //Génération des clés
            keyPair = keyGen.generateKeyPair();
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            e.printStackTrace();
        }
    }

    public String getPubliKey() {
        return keyPair.getPublic().toString();
    }

    /**
     * TODO
     * @param publicKeyStringified
     * @return
     */
    public PublicKey loadPublicKey(String publicKeyStringified) {
        try {

        } catch () {

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
        return this.secretKey.toString();
    }
}