/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package slidingpuzzle;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 *
 * @author renecsc
 */
public class Puzzle extends JPanel{
    //Atributos
    private Image imagen;
    private SubImagen subImagenes[][];
    private String urlImg;
    private int Width;
    private int Height;
    private int n;
    /**
     * Constructor
     * @param n tamaño del puzzle
     * @param urlImage ruta de la imagen
     * @throws IOException
     * @throws InterruptedException 
     */
    public Puzzle(int n, String urlImage) throws IOException, InterruptedException{
        //se inicializan los atributos
        super();
        this.Width = 600;
        this.Height = 600;
        this.n = n;
        this.subImagenes = new SubImagen[n][n];
        this.setPreferredSize(new Dimension(this.Width, this.Height));
        this.setBackground(Color.BLACK);
        this.setOpaque(true);
        this.urlImg = urlImage;
        this.cagarSubImagenes();
    }
    /**
     * Metodo para cortar la imagen en piezas pequeñas y cargarlas a una matriz de subimagenes.
     * @throws IOException
     * @throws InterruptedException 
     */
    public void cagarSubImagenes() throws IOException, InterruptedException{
        // se carga la imagen principal mediente la ruta espacificada
        this.imagen = ImageIO.read(new File(this.urlImg));
        //se escala la imagen para adaptarla al tamaño del panel
        this.imagen = this.imagen.getScaledInstance(this.Width, this.Height, Image.SCALE_DEFAULT);
        //se recorre la matriz de subimagenes cortando la imagen principal segun el tamaño espeficicado
        //además se carga el id de cada subimagen
        int id = 1;
        for(int i = 0; i < this.n; i++){
            for(int j = 0; j < this.n; j++){
                if((i != this.n-1) || (j != this.n-1)){ 
                    //se corta la imagen mientras no sea la ultima
                    Image subImagen = createImage(new FilteredImageSource(this.imagen.getSource(), 
                        new CropImageFilter(i * (this.Width/this.n), j * (this.Height/this.n), this.Width/this.n, this.Height/this.n)));
                    SubImagen si = new SubImagen(subImagen, id);
                    this.subImagenes[i][j] = si;
                }
                else{
                    //si es la ultima se dibuja una imagen blanca que representa la pieza vacia
                    BufferedImage subImagen = new BufferedImage(this.Width/this.n, this.Height/this.n, BufferedImage.TYPE_INT_RGB);
                    Graphics2D g = (Graphics2D) subImagen.getGraphics();
                    g.setColor(Color.WHITE);
                    g.fillRect(0, 0, this.Width/this.n, this.Height/this.n);
                    this.subImagenes[i][j] = new SubImagen(subImagen.getScaledInstance(this.Width/this.n, this.Height/this.n, Image.SCALE_DEFAULT), id);
                }
                id++;
            }
        }
        
        this.shuffle();
    }
    /**
     * Sobreescritura del metodo paint.
     * @param g 
     */
    @Override
    public void paintComponent(Graphics g){
        //se dibujan cada una de las subimagenes de la matriz.
        for(int i = 0; i < this.n; i++){
            for(int j = 0; j < this.n; j++){
                g.drawImage(this.subImagenes[i][j].getImagen(), i*(this.Width/this.n), j*(this.Height/this.n), this);
            }
        }
        
    }
    /**
     * metodo para mover un pieza a un espacio vacio dada unas coordenadas.
     * @param i
     * @param j 
     */
    public void slideSubImage(int i, int j){        
        int[] auxI = {-1,1,0,0};
        int[] auxJ = {0,0,-1,1};
        //se verifica las piezas de alrededor de la indicada para comprobar si se puede mover
        for(int h = 0; h < 4; h++){
            //si se puede mover se llama al metodo para cambiar la pieza.
            if((i+auxI[h])>=0 && (j+auxJ[h])>=0 && (i+auxI[h])<n && (j+auxJ[h])<n && this.subImagenes[i+auxI[h]][j+auxJ[h]].getId()==(n*n)){
                this.swap(i, j, i+auxI[h], j+auxJ[h]);
            }
        }
        
    }
    /**
     * metodo para cambiar dos subimagenes de la matriz dada dos coordenandas.
     * @param x1
     * @param y1
     * @param x2
     * @param y2 
     */
    public void swap(int x1, int y1, int x2, int y2){
        //Se realiza un intercambio de dos piezas mediante un auxiliar.
        SubImagen aux = this.subImagenes[x1][y1];
        this.subImagenes[x1][y1] = this.subImagenes[x2][y2];
        this.subImagenes[x2][y2] = aux;
    }
    /**
     * metodo para desordenar la matriz con subimagenes llamando al metodo slideSubImage aleatoriamente.
     * @throws InterruptedException 
     */
    public void shuffle() throws InterruptedException{
        //Se eligen reiteradas veces coordenadas al azar llamando al metodo para mover un pieza.
        for(int i = 0; i<(Math.pow(n, 4)); i++){
            int x = (int) (Math.random()*n);
            int y = (int) (Math.random()*n);
            this.slideSubImage(x, y);
        }
    }
    /**
     * metodo para verificar si el puzzle esta ordenado.
     * @return 
     */
    public boolean endChecker(){
        boolean end = true;
        int id = 1;
        for(int i = 0; i < n; i++){
            for(int j = 0; j < n; j++){
                if(this.subImagenes[i][j].getId() != id){
                    end = false;
                }
                id++;
            }
        }
        return end;
    }
    /**
     * metodo para setear la ruta de la imagen principal
     * @param url 
     */
    public void setUrlImg(String url){
        this.urlImg = url;
    }
    /**
     * metodo para obtener la ruta de la imagen principal
     * @return 
     */
    public String getUrlImg(){
        return this.urlImg;
    }
    /**
     * metodo para setear el tamaño del puzzle
     * @param n 
     */
    public void setN(int n){
        this.n = n;
    }
    /**
     * metodo para setear la matriz de Subimagenes.
     * @param n 
     */
    public void setSubImagenes(int n){
        this.subImagenes = new SubImagen[n][n];
    }
    /**
     * metodo para obtener la matriz de subimagenes.
     * @return 
     */
    public SubImagen[][] getSubImagenes() {
        return subImagenes;
    }        
}
