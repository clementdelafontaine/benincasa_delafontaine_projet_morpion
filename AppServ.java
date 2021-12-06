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
	
	Socket sockcli1;
	Socket sockcli2;

	AppServ(Socket sockcli1,Socket sockcli2){
		this.sockcli1 = sockcli1;
		this.sockcli2 = sockcli2;
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
			//joueur 1
			BufferedReader in1 = new BufferedReader(new InputStreamReader(sockcli1.getInputStream()));
			PrintWriter out1 = new PrintWriter(new OutputStreamWriter(sockcli1.getOutputStream()),true);
			//joueur 2
			BufferedReader in2 = new BufferedReader(new InputStreamReader(sockcli2.getInputStream()));
			PrintWriter out2 = new PrintWriter(new OutputStreamWriter(sockcli2.getOutputStream()),true);

			Morpion jeu = new Morpion();
			String result;
			String ligne;
			String colonne;

			while (true) {
		

				out1.println("202"); //tour
				out1.println(jeu.toString());
				out2.println("101"); //attente
				out2.println(jeu.toString());

				ligne = in1.readLine();
				colonne = in1.readLine();
				result = jeu.play(ligne,colonne,1);

				if(result == "102") {
					out1.println("102");
					out2.println("102"); 
					break;
				}

				out1.println("101"); //attente
				out1.println(jeu.toString());
				out2.println("202"); //tour
				out2.println(jeu.toString());

				ligne = in2.readLine();
				colonne = in2.readLine();
				result = jeu.play(ligne,colonne,2);
				
				if(result == "102") {
					out1.println("102");
					out2.println("102"); 
					break;
				}
			}
			
			sockcli1.close();
			sockcli2.close();
			
		} catch (Exception e) {
			System.out.println(e);
		} 
	}
}