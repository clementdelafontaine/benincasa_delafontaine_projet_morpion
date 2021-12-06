import java.util.Arrays;

import java.util.Scanner;

public class Morpion {
    int[][] plateau;

    public Morpion() {
        plateau = new int[3][3];

        for (int[] row : plateau) {
            Arrays.fill(row, 0);
        }
        System.out.println(this);
    }

    public String play(String testLigne, String testColonne, int noJoueur){
        int ligne, colonne;
        try {   
			ligne = Integer.parseInt(testLigne); 
            colonne = Integer.parseInt(testColonne); 

            // Vérification notOutOfBounds et case non jouée
            if (ligne < 1 || ligne > 3
                || colonne < 1 || colonne > 3
                || plateau[ligne-1][colonne-1] != 0) {
                return "406";
            } else {
                plateau[ligne-1][colonne-1] = noJoueur;
                if (aGagne(noJoueur))
                    return "102";
                else 
                    return "200";
            }
		} catch (NumberFormatException e) { 
			return "406";
		} 
    }

    public boolean aGagne(int noJoueur){
        // Vérification lignes
        for (int[] row : plateau) {
            if (row[0] == noJoueur && row[1] == noJoueur && row[2] == noJoueur) return true;
        }

        // Vérification colonnes
        for (int i = 0; i<3; i++) {
            if (plateau[0][i] == noJoueur && plateau[1][i] == noJoueur && plateau[2][i] == noJoueur) return true;
        }

        // Vérification diagonales
        if ((plateau[0][0] == noJoueur && plateau[1][1] == noJoueur && plateau[2][2] == noJoueur)) return true;
        if (plateau[2][0] == noJoueur && plateau[1][1]  ==noJoueur && plateau[0][2] == noJoueur) return true;
        
        return false;
    }

    public String toString() {
        String res = "-------------\n";
        for (int[] row : plateau) {
            for (int box : row) {
                if (box == 0) res += "|   ";
                else if (box == 1 ) res += "| X ";
                else if (box == 2 ) res += "| O ";
            }
            res += "|\n-------------\n";
        }

        return res;
    }



    public static void main(String[] args) {
        Morpion m1 = new Morpion();
        String status = "";
        String ligne, colonne;
        Scanner sc = new Scanner(System.in);
        int tourJoueur = 1;
        while (status != "102") {
                System.out.println("A votre tour joueur "+tourJoueur+", saisir une case : \"ligne colonne\"");
                ligne = sc.nextLine();
                colonne = sc.nextLine();
                status = m1.play(ligne, colonne, tourJoueur);
                System.out.println("status : "+status+" \n"+m1);
                if (status == "200") {
                    tourJoueur = ((tourJoueur == 1) ? 2 : 1);
                    System.out.println("Tour joueur : "+tourJoueur);
                }
            
        }
    }
}


