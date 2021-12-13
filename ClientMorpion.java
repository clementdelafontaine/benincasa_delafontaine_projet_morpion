import java.net.*;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.util.Scanner;

public class ClientMorpion {
    public ClientMorpion (String adresse, int port) {
        try {
            //création socket client
            Socket socket = new Socket(adresse, port);

            //création des buffers de communication
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

            //création du scanner pour l'input client
            Scanner sc = new Scanner(System.in);

            //compteur bidon (utile pour l'affichage du plateau de jeu)
            int n = 0;
                
            //demande de connexion
            out.println("103");
            
            //variable de stockage pour les codes reçus par le serveur
            String message = "";

            //tant que je ne reçoit pas de code, attente.
            while((message = in.readLine())==null) ;
                    
            //(104 : partie terminée) tant que la partie n'est pas terminée
            while (!message.equals("104"))
            {
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
                            while (n<9) {
                                System.out.println(in.readLine());
                                n++;
                            }
                            n=0;
                        break;
                        // End of game
                        case "102":
                            System.out.println("Fin de la partie\n");
                            message = "104";
                        break;
                        case "201":
                            System.out.println("Lancement de la partie \n");
                        break;
                        case "202":
                            //nettoyage console
                            System.out.print("\033[H\033[2J");  
                            System.out.println("A votre tour, saisir une ligne puis une colonne");
                            //lecture des lignes représentant le tableau
                            while (n<9) {
                                System.out.println(in.readLine());
                                n++;
                            }
                            n=0;
                            String ligne = sc.nextLine();
                            String colonne = sc.nextLine();
                            out.println(ligne);
                            out.println(colonne);
                        break;
                        case "106":
                            while((message = in.readLine())==null) ;
                            System.out.println(message);
                        break;
                    }
                    while(message != "104" && (message = in.readLine())==null){System.out.println("yes");}

                }
                catch(IOException i)
                {
                    System.out.println(i);
                }
            }

            in.close();
            out.close();
            socket.close();

            
        } catch(IOException e){
            System.out.println("Erreur : " + e);
        }
    }

    public static void main(String[] args) throws Exception {
        ClientMorpion client = new ClientMorpion("localhost", 1234);
    }
}