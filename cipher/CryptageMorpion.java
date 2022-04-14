package cipher;

/**
 * #TODO commenter + document avec architecture + wireshark paquets cryptés
 */

import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class CryptageMorpion {
    KeyGenerator keygen;
    SecretKey secretKey;
    PublicKey publicKey;
    PrivateKey privateKey;
    KeyPair keyPair;
    Cipher cipherDES;
    Cipher decipherDES;
    Cipher cipherRSA;
    Cipher decipherRSA;

    /**
     * Instancie les Cipher pour le DES et le RSA
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    public CryptageMorpion() throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        this.cipherRSA = Cipher.getInstance("RSA");

        this.decipherRSA = Cipher.getInstance("RSA");

        this.cipherDES = Cipher.getInstance("DES");

        this.decipherDES = Cipher.getInstance("DES");
    }

    /**
     * Génère la clé secrète DES
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws NoSuchPaddingException
     */
    public void generateDESKey() throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException {
        if (secretKey == null) {
            this.keygen = KeyGenerator.getInstance("DES");
            this.keygen.init(56);
            this.secretKey = this.keygen.generateKey();

            initCipherDES();
        }
    }

    /**
     *
     * @return la clé secrète cryptée avec la clé publique du client
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public String getEncryptedSecretKey() throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        return encryptSecretKey();
    }

    /**
     * Génère une paire de clés RSA pour le client
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     */
    public void generateKeyPair() throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException {
        keyPair = null;

        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");

        //Initiallisation
        keyGen.initialize(2048, random);

        //Génération des clés
        keyPair = keyGen.generateKeyPair();
        publicKey = keyPair.getPublic();
        privateKey = keyPair.getPrivate();

        this.cipherRSA.init(Cipher.ENCRYPT_MODE, publicKey);
        this.decipherRSA.init(Cipher.DECRYPT_MODE, privateKey);
    }

    /**
     * Permet de récupérer la clé publique en chaine de caractère afin de l'envoyer au serveur
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public String getPubliKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        if (!(publicKey == null)) {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec spec = keyFactory.getKeySpec(publicKey, X509EncodedKeySpec.class);
            return Base64.getEncoder().encodeToString(spec.getEncoded());
        }
        return null;
    }

    /**
     * Charge la clé publique reçue en String
     * @param publicKeyStringified
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @throws InvalidKeyException
     */
    public void loadPublicKey(String publicKeyStringified) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException {
        byte[] buffer = Base64.getMimeDecoder().decode(publicKeyStringified);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);

        this.publicKey = (RSAPublicKey) keyFactory.generatePublic(keySpec);
        System.out.println("nouvelle clé publique chargée : " + getPubliKey());
    }

    /**
     * Encrypte un message en DES
     * @param message
     * @return
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws InvalidKeyException
     */
    public String encrypt(String message) throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        byte[] bytePhrase = message.getBytes(StandardCharsets.UTF_8);
        return Base64.getMimeEncoder().encodeToString(this.cipherDES.doFinal(bytePhrase));
    }

    /**
     * Encrypte la clé secrète
     * @return la clé secrète cryptée encodée en String
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public String encryptSecretKey() throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        cipherRSA.init(Cipher.ENCRYPT_MODE, publicKey);

        return Base64.getMimeEncoder().encodeToString(cipherRSA.doFinal(secretKey.getEncoded()));
    }

    /**
     * Décode la chaine de caractères et décrypte la clé secrète pour la stocker
     * @param message
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws InvalidKeyException
     */
    public void decryptSecretKey(String message) throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        if (!(privateKey == null)) {
            byte[] decryptedKey = decipherRSA.doFinal(Base64.getMimeDecoder().decode(message));
            this.secretKey = loadSecretKey(decryptedKey);
            initCipherDES();
        }
    }

    /**
     * Initialisation du cipher DES
     * @throws InvalidKeyException
     */
    private void initCipherDES() throws InvalidKeyException {
        this.cipherDES.init(1, this.secretKey);
        this.decipherDES.init(2, this.secretKey);
    }

    /**
     * Chqrge la clé secrète depuis un tableau de byte
     * @param encodedKey
     * @return
     */
    private SecretKey loadSecretKey(byte[] encodedKey) {
        return new SecretKeySpec(encodedKey, 0, encodedKey.length, "DES");
    }

    /**
     * Décrypte le message passé en paramètre avec la clé secrète DES
     * @param cryptedString
     * @return le message décrypté
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public String decrypt(String cryptedString) throws IllegalBlockSizeException, BadPaddingException {
        byte[] cryptedByteMessage = Base64.getMimeDecoder().decode(cryptedString);

        String decryptedMessage = decrypt(cryptedByteMessage);

        return decryptedMessage;
    }

    /**
     * Décrypte le tableau de byte passé en paramètre
     * @param cryptedMessage
     * @return  la chaine de caractère décryptée
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public String decrypt(byte[] cryptedMessage) throws IllegalBlockSizeException, BadPaddingException {
        return new String(this.decipherDES.doFinal(cryptedMessage), StandardCharsets.UTF_8);
    }

    /**
     *
     * @return la clé DES en chaine de caractères
     */
    public String getDESKey() {
        return this.secretKey.toString();
    }

    /**
     * Encrypte et envoie le message via le PrintWriter passés en paramètres
     * @param message
     * @param out
     */
    public void cipherAndSendMessage(String message, PrintWriter out) {
        try {
            message = encrypt(message);
            message = message.replaceAll("(\\r|\\n)", "");
            out.println(message);
        } catch (IllegalBlockSizeException | InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
    }
}