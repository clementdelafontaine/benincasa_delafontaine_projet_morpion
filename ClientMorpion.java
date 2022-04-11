import cipher.CryptageMorpion;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.net.*;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.util.Scanner;

/**
 * Client qui permet de se connecter au serveur de jeu et de rencontrer un adversaire pour une partie de morpion dans le terminal
 * Par défaut : java ClientMorpion localhost 1234
 */
public class ClientMorpion {
    CryptageMorpion cipher;

    /**
     * Création d'une instance, prend en paramètres l'adresse et le port du serveur de jeu
     * @param adresse String
     * @param portString String
     */
    public ClientMorpion (String adresse, String portString) {
        int port = Integer.parseInt(portString); 

        try {
            //création socket client
            Socket socket = new Socket(adresse, port);

            //création des buffers de communication
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

            //création du scanner pour l'input client
            Scanner sc = new Scanner(System.in);

            //compteur utilé pour l'affichage du plateau de jeu
            int n = 0;

            cipher = new CryptageMorpion();

            try {
                //creation des clés privés et publiques
                cipher.generateKeyPair();
                System.out.println("génération de la clé publique : " + cipher.getPubliKey());
                //demande de connexion
                out.println("103");

                //Envoi de la clé publique au serveur
                out.println(cipher.getPubliKey());

                //variable de stockage pour les codes reçus par le serveur
                String message = "";
                String encryptedSecretKey = "";
                String encryptedMessage = "";

                //Réception de la clé secrète cryptée
                while((encryptedSecretKey = (in.readLine()))==null) ;

                //Décrypter et stocker la clé secrète
                cipher.decryptSecretKey(encryptedSecretKey);

                //tant que je ne reçoit pas de code, attente.
                while((encryptedMessage = (in.readLine()))==null) ;

                System.out.println("message crypté : " + encryptedMessage);
                message = cipher.decrypt(encryptedMessage);
                System.out.println("message décrypté : " + message);
                //(104 : partie terminée) tant que la partie n'est pas terminée
                while (!message.equals("104"))
                {
                    System.out.println("debut while message : " + message);
                    try
                    {
                        //évaluation des codes reçus par le serveur
                        switch (message) {
                            case "100":
                                System.out.println("En attente d'un opposant ... \n");
                            break;
                            case "101":
                                //nettoyage console
                                System.out.print("\033[H\033[2J");
                                System.out.println("C'est au tour de l'adversaire \n");
                                //lecture des string représentant le tableau
                                printGameBoard(in);
                            break;
                            case "102":
                                //nettoyage console
                                System.out.print("\033[H\033[2J");
                                System.out.println("Fin de la partie\n");
                                message = "104";
                                printGameBoard(in);
                            break;
                            case "105":
                                //nettoyage console
                                System.out.print("\033[H\033[2J");
                                System.out.println("Fin de la partie - match nul\n");
                                message = "104";
                                printGameBoard(in);
                            break;
                            case "201":
                                System.out.println("Lancement de la partie \n");
                            break;
                            case "202":
                                //nettoyage console
                                System.out.print("\033[H\033[2J");
                                System.out.println("A votre tour, saisir une ligne puis une colonne");
                                printGameBoard(in);
                                //saisie des coordonées jouées et envoi au serveur
                                String ligne = sc.nextLine();
                                String colonne = sc.nextLine();
                                cipher.cipherAndSendMessage(ligne,out);
                                cipher.cipherAndSendMessage(colonne,out);
                            break;
                        }
                        while(message != "104" && (message = in.readLine())==null);

                        if (message != "104") {
                            message = cipher.decrypt(message);
                        }
                    }
                    catch(IOException i)
                    {
                        System.out.println(i);
                    }
                    System.out.println("fin while message : " + message);
                }
            } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
                e.printStackTrace();
            } catch (InvalidKeySpecException | IllegalBlockSizeException | BadPaddingException | InvalidKeyException | NoSuchPaddingException e) {
                e.printStackTrace();
            }

            //fermeture socket côté client
            in.close();
            out.close();
            socket.close();

            
        } catch(IOException | NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException e){
            System.out.println("Erreur : " + e);
        }
    }

    /**
     * Récupère, décrypte et affiche les données d'un plateau de jeu
     * @param in
     * @throws IOException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    private void printGameBoard(BufferedReader in) throws IOException, IllegalBlockSizeException, BadPaddingException {
        String board = cipher.decrypt(in.readLine());

        board = board.replaceAll("retourLigne", "\n");

        System.out.println(board);
    }

    public static void main(String[] args) throws Exception {
//        ClientMorpion client = new ClientMorpion(args[0], args[1]);
        ClientMorpion client = new ClientMorpion("localhost", "1234");
    }
}