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
            Socket socketServeur = new Socket(adresse, port);

            BufferedReader in = new BufferedReader(new InputStreamReader(socketServeur.getinStream()));
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socketServeur.getOutputStream())), true);
                
            // Demande de connexion
            out.println("103");
            
            // lecture message d'entree
            String message = "";            

            Scanner sc = new Scanner(System.in);
                    
            // 102 : partie terminée
            while (!message.equals("102"))
            {
                try
                {
                    message = in.readLine();
                    out.println(message);

                    switch (message) {
                        case "100":
                        System.out.println("En attente d'un opposant ... \n");
                        break;
                        case "101":
                        // Print tableau de jeu
                        System.out.println("C'est au tour de l'adversaire \n");
                        break;
                    }

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

            socketServeur.close();
        } catch(IOException e){
            System.out.println("Erreur : " + e);
        }
    }

    public static void main(String[] args) throws Exception {
        ClientMorpion client = new ClientMorpion("localhost", 1234);
    }
}