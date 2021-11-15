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
            Socket socket = new Socket(adresse, port);

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                
            // Demande de connexion
            out.println("103");
            
            // lecture message d'entree
            String message = "";
            while((message = in.readLine())==null) ;
            // Scanner sc = new Scanner(System.in);
                    
            // 104 : partie terminée
            while (!message.equals("104"))
            {
                try
                {
                    switch (message) {
                        case "100":
                        System.out.println("En attente d'un opposant ... \n");
                        break;
                        case "101":
                        // Print tableau de jeu
                        System.out.println("C'est au tour de l'adversaire \n");
                        break;
                        // End of game
                        case "102":
                        System.out.println("En attente d'un opposant ... \n");
                        break;
                        case "201":
                        System.out.println("Lancement de la partie \n");
                        break;
                        case "202":
                        System.out.println("A votre tour, saisir une case : ligne, colonne \n");

                        break;
                    }
                    while((message = in.readLine())==null);

                }
                catch(IOException i)
                {
                    System.out.println(i);
                }
            }
            
     
            // close the connection
            try
            {
                in.close();
                out.close();
                socket.close();
            }
            catch(IOException i)
            {
                System.out.println(i);
            }

            socket.close();
        } catch(IOException e){
            System.out.println("Erreur : " + e);
        }
    }

    public static void main(String[] args) throws Exception {
        ClientMorpion client = new ClientMorpion("localhost", 1234);
    }
}