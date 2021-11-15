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
		int cpt = 0;
		String message = "";
		try {
				ServerSocket sockserv=null;
				sockserv = new ServerSocket (1234); //création socket serveur
			while(true){
				clients[cpt] = sockserv.accept(); //attente requête client
				BufferedReader in = new BufferedReader(new InputStreamReader(clients[cpt].getInputStream()));
				PrintWriter out = new PrintWriter(new OutputStreamWriter(clients[0].getOutputStream()),true);
				message = in.readLine();
				System.out.println("connexion recue : message "+message+"\n");
				if(message.equals("103")) {
					System.out.println("client connecté cpt : "+cpt+"\n");

					if(cpt == 1) { // Lancement partie
						out.println("201");
						System.out.println("Lancement du thread\n");
						AppServ appserv = new AppServ(clients[cpt],clients[cpt-1]);
						Thread th = new Thread(appserv);
						th.start();
						cpt = 0;
					} else { // Attente autre joueur
						out.println("100");
						cpt++;
					}
				} else {
					out.println("400");
					clients[cpt].close();
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
				
				String message1 = in1.readLine();
				out2.println(message1);

				String message2 = in2.readLine();
				out1.println(message2);

				if(message1.equals("stop") || message2.equals("stop")) {
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