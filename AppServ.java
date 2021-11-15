import java.net.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

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

				while((message = in.readLine())==null) ;
				System.out.println("connexion recue : message "+message+"\n");
				if(message.equals("103")) {
					System.out.println("client connecté compteurAttente : "+compteurAttente+"\n");

					if(compteurAttente == 1) { // Lancement partie
						out.println("201");
						System.out.println("Lancement du thread\n");
						PrintWriter out0 = new PrintWriter(new OutputStreamWriter(clients[0].getOutputStream()),true);
						out0.println("201");
						AppServ appserv = new AppServ(clients[compteurAttente],clients[compteurAttente-1]);
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
			int[] = new int[9];
			// creation des flux de communication
			//joueur 1
			BufferedReader in1;
			PrintWriter out1;
			//joueur 2
			BufferedReader in2;
			PrintWriter out2;
			while (true) {
				in1 = new BufferedReader(new InputStreamReader(sockcli1.getInputStream()));
				out1 = new PrintWriter(new OutputStreamWriter(sockcli1.getOutputStream()),true);
				in2 = new BufferedReader(new InputStreamReader(sockcli2.getInputStream()));
				out2 = new PrintWriter(new OutputStreamWriter(sockcli2.getOutputStream()),true);
				
				
				out1.println("202");
				//ne pas oubblier le break 
			}
			sockcli1.close();
			sockcli2.close();
			
		} catch (Exception e) {
			System.out.println(e);
		} 
	}
}