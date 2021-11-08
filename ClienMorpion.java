import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class ClientYlona {

    public static void main(String[] args) throws Exception{
        Socket socketCli = new Socket("localhost", 1234);

        DataInputStream in;
        in = new DataInputStream(socketCli.getInputStream());
        DataOutputStream out;
        out = new DataOutputStream(socketCli.getOutputStream());
        out.flush();
        Scanner sc = new Scanner(System.in);
        String Pseudo = "";
        System.out.println("Choisir Pseudo :");
        Pseudo = sc.next();

        if(socketCli.isConnected()){
            System.out.println("connected");
            String message = sc.next();
            System.out.println("Saisir un message :");

            do {
                String s = Pseudo + " : " + message;
                byte byteMess[] = s.getBytes();
                out.write(byteMess);
                out.flush();
                message = sc.next();
            } while (message.compareTo("!close") != 0);
        }
        socketCli.close();
    }
}