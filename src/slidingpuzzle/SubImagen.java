/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package slidingpuzzle;

import java.awt.Image;
import javax.swing.JPanel;

/**
 *
 * @author renecsc
 */
public class SubImagen extends JPanel{
    //Atributos
    protected  int id;
    protected Image Imagen;
    /**
     * Constructor
     * @param imagen
     * @param id 
     */
    public SubImagen(Image imagen, int id){
        //se inicializan los atributos.
        super();
        this.id = id;
        this.Imagen = imagen;
        
    }
    //metodo para obtener el id.
    public int getId() {
        return id;
    }
    //metodo para setear el id.
    public void setId(int id) {
        this.id = id;
    }
    //metodo para obtener la subimagen.
    public Image getImagen() {
        return Imagen;
    }
    //metodo para setear una subimagen.
    public void setImagen(Image Imagen) {
        this.Imagen = Imagen;
    }        
}
