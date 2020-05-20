/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package travail_pratique_3;

import java.io.File;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javafx.application.Application;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Programme qui fait un  jeu à la candy crush
 * 2020-mai-20
 * @author carm + daniela
 */

/** Classe de jeu, définition des objets et fonctions de celle-ci
 *   
 */
public class Travail_Pratique_3 extends Application {
    /**déclaration des variables globales */
    private static final int W = 6; 
    private static final int H = 6;
    private static final int SIZE = 100;
    private static final int WIDTH = 1000;
    
    /** déclaration d'un tableau de couleur contenant 5 couleurs*/
    private Color[] colors = new Color[] {
            Color.BLACK, Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW
    };

    private Boule selected = null;
    private List<Boule> Tab_Boules; /** Table contenant des boules */
    private IntegerProperty score = new SimpleIntegerProperty();
    
    /** Fonction qui créait un pane et le return 
     * gridPane est le pane contenant la table de boules
     * le pane root contient le gridPane,la barre de  menu et le texte affichant le score
     * @return root
     */
    private Parent createContent() {
        Pane root = new Pane();
        VBox maBoite = new VBox();
        Pane gridPane = new Pane();
        root.setPrefSize(W * SIZE + 400, H * SIZE + 50);
        gridPane.relocate(0, 30);
        
        
        /** Tansfère l'objet Boule à Tab_Boules*/
        Tab_Boules = IntStream.range(0, W * H)
                .mapToObj(i -> new Point2D(i % W, i / W))
                .map(Boule::new)
                .collect(Collectors.toList());
        
        gridPane.getChildren().addAll(Tab_Boules);
        root.getChildren().add(gridPane);
        
        Text textScore = new Text();
        textScore.setTranslateX(W * SIZE);
        textScore.setTranslateY(100);
        textScore.setFont(Font.font(68));
        textScore.textProperty().bind(score.asString("Score: [%d]"));
        /** ajoute les éléments sur la scène*/
        root.getChildren().add(textScore);
        root.getChildren().add(maBoite);
        maBoite.getChildren().add(menu());
        return root;  
    }
    
    /**
     * Fonction checkState() qui vérifie l'état des boules dans la table.
     */
    private void checkState() {
        Map<Integer, List<Boule>> rows = Tab_Boules.stream().collect(Collectors.groupingBy(Boule::getRow));
        Map<Integer, List<Boule>> columns = Tab_Boules.stream().collect(Collectors.groupingBy(Boule::getColumn));

        rows.values().forEach(this::checkCombo);
        columns.values().forEach(this::checkCombo);
    }
    /**
     * Fonction CheckCombo qui vérifie si les boules alignées, sont toutes de même couleur.
     * si oui ajoute 1000 au score et fait jouer un petit son
     * AudioClip sert à aller récupérer le fichier son dans son répertoire
     * pathname donne le chemin d'accès vers le répertoire
     * count 
     * @param Tab_BoulesLine tableau de boule
     */
    private void checkCombo(List<Boule> Tab_BoulesLine) {
       
        Boule jewel = Tab_BoulesLine.get(0);
        long count = Tab_BoulesLine.stream().filter(j -> j.getColor() != jewel.getColor()).count();
       
        if (count == 0) { 
            String pathname = "src\\repertoire\\son.mp3";
            AudioClip song = new AudioClip(new File(pathname).toURI().toString());
            score.set(score.get() + 1000);
            song.play(); 
            Tab_BoulesLine.forEach(Boule::randomize);/** rajoute une nouvelle ligne boule au hasard */
           
        }
    }
    /**
     * Fonction qui permet de faire un échange de couleur entre deux boule a et b.
     * @param a transfère sa couleur à b
     * @param b transfère sa couleur à a
     */
    private void swap(Boule a, Boule b) {
        Paint color = a.getColor();
        a.setColor(b.getColor());
        b.setColor(color);
    }

    /**
     * Fonction met la scène en place dès le lancement du programme
     * @param primaryStage fenêtre principale
     * @throws Exception utilisé pour lever l'exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setScene(new Scene(createContent()));
        primaryStage.show();
    }
    
    /**
     * Class Boule qui défini les propriétés des boules, leur taille et leur position.
     * Constructeur Boule qui utilise un mouse event pour détecter le clic de la souris
     * selectionne les boules et interchanger leur couleur selon leur position dans le tableau colors.
     */
    private class Boule extends Parent {
        private Circle circle = new Circle(SIZE / 2);
       /** Constructeur de la classe boule 
        * @param point position de la boule
        */
        public Boule(Point2D point) {
            /** définition des tailles des boules*/
            circle.setCenterX(SIZE / 2);
            circle.setCenterY(SIZE / 2);
            circle.setFill(colors[new Random().nextInt(colors.length)]);
            
            setTranslateX(point.getX() * SIZE);
            setTranslateY(point.getY() * SIZE);
            getChildren().add(circle);

            setOnMouseClicked(event -> {
                if (selected == null) {
                    selected = this;
                }
                else {
                    swap(selected, this);
                    checkState();
                    selected = null;
                }
            });
        }
        
        /** Fonction qui réinitialise les boules avec des couleurs au hasard
         */
        public void randomize() {
       
          circle.setFill(colors[new Random().nextInt(colors.length)]);
        }
        
        /** 
         * 
         * retourne la valeur de la colonne
         *@return int)getTranslateX() / SIZE
         */
        public int getColumn() {
            return (int)getTranslateX() / SIZE;
        }
        
        /**
         * retourne la valeur de ligne 
         * @return (int)getTranslateY() / SIZE
         */
        public int getRow() {
            return (int)getTranslateY() / SIZE;
        }
        
        /**
         * Transfère la nouvelle couleur à l'objet 
         * @param  color la couleur de l'objet boule
         */
        public void setColor(Paint color) {
            circle.setFill(color);
        }
        
        /**  retourne l'objet avec sa nouvelle couleur
         *@return circle.getFill()
         */
        public Paint getColor() {
            return circle.getFill();
        }
    }
    /** 
     * Fonction qui créait un menu pour le jeu
     * maBarre qui contient les menus et sous-menu
     * Sortie: gestionnaire d'évèment pour quitter le jeu
     * Partie: gestionnaire d'évènemnt pour recommencer la partie
     * Detail: gestionnaire d'évènement pour afficher les informations supplémentaires
     * @return maBarre
     */
    public Parent menu(){
        
        MenuBar maBarre = new MenuBar();
        maBarre.setMinWidth(WIDTH);
        Menu option = new Menu("Option");
        Menu info = new Menu("?");
        MenuItem redemarrer = new MenuItem("Redemarrez");
        MenuItem quittez = new MenuItem("Quittez");
        MenuItem smInfo = new MenuItem(" À propos");
        /** Ajout des  menus et sous menus à la barre de menu*/
        option.getItems().add(redemarrer);
        option.getItems().add(quittez);
        info.getItems().add(smInfo);
        maBarre.getMenus().add(option);
        maBarre.getMenus().add(info);
        /** déclaration de gestionnaire d'évènement*/
        quittez.setOnAction(Sortie);
        redemarrer.setOnAction(Partie);
        smInfo.setOnAction(Detail);
        return maBarre;
    }
    /**
     * Evènement qui permet de sortie de l'application en faisant un clique sur le sous menu @Quittez
    */
    EventHandler<ActionEvent> Sortie = (ActionEvent Event) -> {
        System.exit(0); 
    };
    /**
     * Evènement qui réinitialise la partie en mettant le score à 0 après avoir pesé sur le sous menu @Redemarrer
     */
    EventHandler<ActionEvent> Partie = (ActionEvent Event)->{
        score.set(0);    
    };
    
    /**
     * Evènement qui affiche le lien vers le répertoire github et mon nom après avoir pesé sur le menu @A propos
     * lien variable utilisée pour insérer un lien internet
     * getHostServices().showDocument permet d'avoir accès au site
     */
    EventHandler<ActionEvent> Detail = (ActionEvent Event) -> {
        Pane pane = new Pane();
        Scene nouvelFenetre = new Scene(pane,585,150);
        Text nom = new Text(40,50,"Auteur : Daniela Ameni");
        nom.setFont(Font.font("Verdana",15));
        
        Hyperlink lien = new Hyperlink("Lien web: "
                + "https://github.com/daniela-rose/Projet-Java-Daniela-Ameni2");
        lien.relocate(40,80);
        lien.setFont(Font.font("Verdana",15));
        
        lien.setOnAction((ActionEvent e) -> {
            getHostServices().showDocument(""
                    + "https://github.com/daniela-rose/Projet-Java-Daniela-Ameni2");
        });
        /** Création d'une liste observable*/
        ObservableList maListe = pane.getChildren();
        maListe.add(nom);
        maListe.add(lien);
        
        /** création de la nouvelle fenètre*/
        Stage fenetre = new Stage();
        fenetre.setTitle("About");
        fenetre.setScene(nouvelFenetre);
        fenetre.show(); 
    };
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
      launch(args);
    }
}
