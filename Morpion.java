import java.util.Arrays;
import java.util.Scanner;

import javax.sound.midi.Soundbank;

/**
 * Morpion est la classe qui permet d'instancier et de gérer une partie
 * 
 * @author Julien Benincasa Clément Delafontaine
 */

 
public class Morpion {
    int[][] plateau;

    /**
     * Instancie un tableau d'entiers initialisés à 0
     * de dimensions 3 par 3
     * 
     */
    public Morpion() {
        plateau = new int[3][3];

        for (int[] row : plateau) {
            Arrays.fill(row, 0);
        }
        System.out.println(this);
    }

    /**
     * play est joué à chaque tour de jeu
     * la méthode vérifie en premier lieu si les coordonnées de jeu passées en paramètres
     * sont bien des entier et vérifie également que les coordonnées fassent partie des limites de la grille de jeu
     * si non elle retourne le code 406
     * la case jouée prend le numéro du joueur
     * si tout se passe bien la méthode retourne le code 200
     * si le joueur gagne la méthode retourne le code 102
     * si le match est nul retourne le code 105
     * 
     * @param testLigne String
     * @param testColonne String
     * @param noJoueur int
     * @return string 
     */
    public String play (String testLigne, String testColonne, int noJoueur){
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

                return (aGagne(noJoueur) ? "102" : (partieTerminee() ? "105" : "200"));
            }
		} catch (NumberFormatException e) { 
			return "406";
		} 
    }

    /**
     * Vérifie si un coup est gagnant ou non
     * 
     * @param noJoueur int
     * @return boolean
     */
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

    /**
     * Vérifie si le match est nul
     * @return boolean
     */
    public boolean partieTerminee() {
        int nbCasesVides = 0;
        for (int i=0; i<3; i++) {
            for (int j=0; j<3; j++) {
                nbCasesVides += (plateau[i][j] == 0) ? 1 : 0;
            }
        }
        System.out.println("nbCasesVides : "+nbCasesVides);
        return nbCasesVides == 0;
    }

    /**
     * Affiche la grille de jeu dans l'état de la partie où la méthode est appelée
     * Le client remplace les chaines de caractères "retourLigne" par des "\n"
     * "retourLigne" est utilisé afin de pouvoir utiliser les BufferedReaders et le cryptage
     */
    public String toString() {
        int i = 1;
        String res = "retourLigne    1    2    3retourLigne   -------------retourLigne";
        for (int[] row : plateau) {
            res += i+"  ";
            for (int box : row) {
                if (box == 0) res += "|   ";
                else if (box == 1 ) res += "| X ";
                else if (box == 2 ) res += "| O ";
            }
            res += "|retourLigne   -------------retourLigne";
            i++;
        }

        return res;
    }


    /**
     * Permet de tester une partie en local dans la même console
     */
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
                System.out.println("status : "+status+" retourLigne"+m1);
                if (status == "200") {
                    tourJoueur = ((tourJoueur == 1) ? 2 : 1);
                    System.out.println("Tour joueur : "+tourJoueur);
                }
            
        }
    }
}


