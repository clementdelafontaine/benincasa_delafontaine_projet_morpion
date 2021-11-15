import java.net.*;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.PrintWriter;

public class Serveur implements Runnable {
    private BufferedReader in;
    private PrintWriter out;
    private ServerSocket sockserv = null;
    private Socket socketClient = null;
    private String message;
    private InetSocketAddress clientsEnJeu[];
    private InetSocketAddress clientsEnAttente[];
    private int cpt = 0;

    public Serveur(){
        try {
            sockserv = new ServerSocket(1234); // Création du socket serveur
        } catch (IOException e) {}
    }

    public boolean connexion() {
        try {
            socketClient = sockserv.accept(); // Attente  requête client
            return true;
        } catch (Exception e) { return false; }
    }

    public String getSocketClient(){
        return socketClient.toString();
    }

    public void run() {
        try {
            // Création de flux de communication
            in = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socketClient.getOutputStream())), true);
            
            // Création

            while(true){
                message = in.readLine();
                System.out.println("message reçu : "+ message);
                out.println(message);

                if (message.equals("103")) {
                    // Vérifier s'il y a des joueurs dans la liste d'attente
                    if (clientsEnAttente.length != 0 && clientsEnAttente.length% 2 != 0) {
                        // Lancer le thread avec le joueur en attente de la liste
                        
                        clientsEnJeu[cpt] = clientsEnAttente[0];
                        cpt++;
                    } else { // Mettre le nouveau client en liste d'attente
                        clientsEnAttente[0] = (InetSocketAddress)socketClient.getRemoteSocketAddress();
                        // Envoi du message d'attente
                        out.println("100");
                    }

                }

                out.flush();

                if(message.substring(0, 4).equals("stop")){
                    break;
                }
            }

            socketClient.close(); // Fermeture port de communication
        } catch (Exception e) {
            System.out.println("Déconnexion du client : "+e);
        }
    }
}