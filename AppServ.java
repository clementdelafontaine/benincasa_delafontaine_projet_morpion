import java.net.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Scanner;

public class AppServ implements Runnable{
	
	Socket socketClient[] = new Socket[2];

	AppServ(Socket sockcli1,Socket sockcli2){
		this.socketClient[0] = sockcli1;
		this.socketClient[1] = sockcli2;
	}

	public static void main(String[] args){
		Socket[] clients = new Socket[2];
		int compteurAttente = 0;
		String message = "";
		try {
				ServerSocket sockserv=null;
				sockserv = new ServerSocket (1234); //création socket serveur
			while(true){

				clients[compteurAttente] = sockserv.accept(); //attente requête client
				BufferedReader in = new BufferedReader(new InputStreamReader(clients[compteurAttente].getInputStream()));
				PrintWriter out = new PrintWriter(new OutputStreamWriter(clients[compteurAttente].getOutputStream()),true);

				//Tant que je ne reçoit pas de code, attente.
				while((message = in.readLine())==null) ;

				System.out.println("Connexion reçue, message : "+ message);

				if(message.equals("103")) {
					System.out.println("Client connecté, compteurAttente : "+ compteurAttente+"\n");

					if(compteurAttente == 1) { // Lancement partie
						out.println("201");
						System.out.println("Lancement du thread\n");
						PrintWriter out0 = new PrintWriter(new OutputStreamWriter(clients[0].getOutputStream()),true);
						out0.println("201");
						AppServ appserv = new AppServ(clients[compteurAttente-1],clients[compteurAttente]);
						Thread th = new Thread(appserv);
						th.start();
						compteurAttente = 0;
					} else { // Attente autre joueur
						out.println("100");
						compteurAttente++;
					}
				} else {
					out.println("400");
					clients[compteurAttente].close();
				}
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public void run() {
		try {
			// creation des flux de communication
			BufferedReader[] in = new BufferedReader[2];
			PrintWriter[] out = new PrintWriter[2];
			//joueur 1
			in[0] = new BufferedReader(new InputStreamReader(socketClient[0].getInputStream()));
			out[0] = new PrintWriter(new OutputStreamWriter(socketClient[0].getOutputStream()),true);
			//joueur 2
			in[1] = new BufferedReader(new InputStreamReader(socketClient[1].getInputStream()));
			out[1] = new PrintWriter(new OutputStreamWriter(socketClient[1].getOutputStream()),true);

			Morpion jeu = new Morpion();
			String ligne;
			String colonne;
			String status = "";
			String symbole[] = new String[2];
			int tourJoueur = 0;
			int joueurEnAttente = 1;

			String userInput;

			symbole[0] = "Vous jouez avec les X";
			symbole[1] = "Vous jouez avec les O";

			while (status != "102") {
				// Vider buffer du joueur courant
				while ((userInput = in[tourJoueur].readLine()) != null) {
					System.out.println("echo: " + userInput);
				}

				// Envoyer le visuel de la grille de jeu aux joueurs
				// Traitement joueur en cours
				out[tourJoueur].println("202");
				out[tourJoueur].println(symbole[tourJoueur] + "\n" + jeu);
				// Traitement joueur en attente
				out[joueurEnAttente].println("101");
				out[joueurEnAttente].println(symbole[joueurEnAttente] + "\n" + jeu);

				// saisie des données
				ligne = in[tourJoueur].readLine();
				colonne = in[tourJoueur].readLine();
				status = jeu.play(ligne, colonne, tourJoueur + 1);
				System.out.println("status : " + status + " \n" + jeu);
				// Changement de tour si succès du dernier coup
				if (status == "200") {
					tourJoueur = ((tourJoueur == 0) ? 1 : 0);
					joueurEnAttente = ((tourJoueur == 0) ? 1 : 0);
					System.out.println("Tour joueur : " + tourJoueur);
				}
			}

			// Envoi de la grillet du dernier statut
			for (PrintWriter pw : out) {
				pw.println(status);
				pw.println(jeu);
			}

			socketClient[0].close();
			socketClient[1].close();

		} catch (Exception e) {
			System.out.println(e);
		}
	}
}