/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package slidingpuzzle;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author renecsc
 */
public class PuzzleGUI extends JFrame implements KeyListener{
    //Atributos
    protected Puzzle puzzle;
    protected JPanel menu;
    protected JButton reiniciar;
    protected JButton cargarImagen;
    protected JLabel labelTamaño;
    protected JComboBox tamaños;
    protected JButton about;
    protected int n;
    protected long timeStart;
    protected long timeEnd;
    protected int clicks;
    protected JLabel labelMiniatura;
    /**
     * Constructor.
     * @param n
     * @throws IOException
     * @throws InterruptedException 
     */
    PuzzleGUI(int n) throws IOException, InterruptedException{
        super("Sliding Puzzle");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());
        this.setResizable(false);
        this.n = n;
        this.clicks = 0;
        construirVentana();
        
    }
    /**
     * se construye la ventana y se inicializan todos sus componentes.
     * @throws IOException
     * @throws InterruptedException 
     */
    private void construirVentana() throws IOException, InterruptedException{
        //Se instancia el puzzle con el tamaño por defecto y la imagen por defecto.
        this.puzzle = new Puzzle(this.n, "cat.jpg");
        //Se agrega el listener para el mouse, junto con la implementacion del metodo mousePresed.
        this.puzzle.addMouseListener(new MouseAdapter() {
            int i;
            int j;
            
            @Override
            public void mousePressed(MouseEvent me){
                //Se toman las coordenadas donde se hace click en la matriz.
                i = me.getX()*n/me.getComponent().getWidth();
                j = me.getY()*n/me.getComponent().getHeight();                
                //se llama al metodo para mover la pieza, siempre y cuando se pueda.
                puzzle.slideSubImage(i, j);
                //Si es el primer movimiento se guarda el tiempo en que se inicia.
                if(clicks == 0){
                    timeStart = System.currentTimeMillis();
                }
                clicks++;//Se aumenta el contador de click o movimientos
                puzzle.repaint();//Se pinta el panel
                //se verifica si el puzzle esta ordenado, por medio del metodo endChecker
                if(puzzle.endChecker()){
                    try {
                        //Se calcula el tiempo empleado y se muestra una ventana emergente con la informacion de la partida
                        timeEnd = System.currentTimeMillis();
                        long time = (timeEnd - timeStart)/1000;
                        //se crea la ventana emergente con la informacion de termino.
                        JOptionPane.showMessageDialog(null, "Felicitciones!\nClicks: "+clicks+"\nTiempo empleado: "+time+"(seg.)", "Terminado", JOptionPane.INFORMATION_MESSAGE);
                        puzzle.shuffle();//se revuelve el puzzle.
                        clicks = 0;//se resetea el contador de movimientos.
                        puzzle.repaint();//se repinta el panel.
                    } catch (InterruptedException ex) {
                        Logger.getLogger(PuzzleGUI.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                }
            }
        });                       
        this.add(this.puzzle, BorderLayout.CENTER);
        //Se inicializan el menu y sus componentes.      
        this.menu = new JPanel();
        this.menu.setPreferredSize(new Dimension(200, 600));
        this.menu.setBackground(Color.WHITE);
        this.menu.setOpaque(true);
        //Se establece un tamaño estandar para los botones
        Dimension dimButton = new Dimension(180, 30);
        //Se agrega el boton para reiniciar
        this.reiniciar = new JButton("Reiniciar");
        this.reiniciar.setPreferredSize(dimButton);
        //se agrega el mouse listener al boton.
        this.reiniciar.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent me){
                try {
                    puzzle.shuffle();//se revuelve el puzzle.
                    clicks = 0;//se resetea el contador de movimientos.
                } catch (InterruptedException ex) {
                    Logger.getLogger(PuzzleGUI.class.getName()).log(Level.SEVERE, null, ex);
                }
                puzzle.repaint();//se repinta el panel.
                requestFocusInWindow();//se cambia el foco a la ventana principal.
            }
        });
        this.menu.add(this.reiniciar);
        //Se agrega el boton para cargar un nueva imagen
        this.cargarImagen = new JButton("Cargar Imagen");
        this.cargarImagen.setPreferredSize(dimButton);
        //se agrega el mouse listener al boton.
        this.cargarImagen.addMouseListener(new MouseAdapter() {
            
            @Override
            public void mousePressed(MouseEvent me){
                //se crea un JFileChooser para cargar una imagen desde un directorio cualquiera del equipo
                //a travez de una ventana emergente.
                JFileChooser chooser = new JFileChooser();
                
                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                chooser.setMultiSelectionEnabled(false);
                //se establece el filtro de extension de los archivos que se pueden seleccionar
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Archivos JPG y PNG", "jpg", "png");
                chooser.setFileFilter(filter);
                
                int returnVal = chooser.showOpenDialog(menu);
                if(returnVal == JFileChooser.APPROVE_OPTION){
                    //se obtiene la ruta del archivo seleccionado.
                    File file = chooser.getSelectedFile();
                    //se setea la nueva ruta de la imagen principal.
                    puzzle.setUrlImg(file.getAbsolutePath());
                    try {
                        puzzle.cagarSubImagenes();//se cargan las subimagenes.
                        clicks = 0;//se resetea el contador de movimientos.
                    } catch (IOException ex) {
                        Logger.getLogger(PuzzleGUI.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(PuzzleGUI.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    puzzle.updateUI();//se actualiza el panel.
                    //se actualiza la miniatura de la imagen principal.
                    ImageIcon icon = new ImageIcon(puzzle.getUrlImg());
                    int width = icon.getIconWidth();  
                    int height = icon.getIconHeight();  
                    BufferedImage buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);  
                    Graphics2D graphics = buffer.createGraphics();  
                    icon.paintIcon(null, graphics, 0, 0);  
                    graphics.dispose();  
                    labelMiniatura.setIcon(new ImageIcon(buffer.getScaledInstance(180, 180, Image.SCALE_DEFAULT)));
                    menu.updateUI();//se actualiza el panel del menu.
                }
                requestFocusInWindow();//se cambia el foco a la ventana principal.
            }
        }); 
        this.menu.add(this.cargarImagen);
        //Se agrega un label junto con un combo box con las opciones del tamaño del puzzle.
        this.labelTamaño = new JLabel("Tamaño puzzle");
        this.menu.add(this.labelTamaño);
        //se crea una lista con los tamaños disponibles
        String[] listaTamaños = {"3x3", "4x4", "5x5", "6x6"};
        //se inicializa el combobox con la lista anterior.
        this.tamaños = new JComboBox(listaTamaños);
        //se setea la opcion seleccionada por defecto.
        this.tamaños.setSelectedIndex(0);
        //se agrega el accion listener al combo box.
        this.tamaños.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                n = tamaños.getSelectedIndex()+3;//se obtiene el tamaño sleccionado y e le asigna n.
                puzzle.setN(n);//se setea el nuevo tamaño.
                puzzle.setSubImagenes(n);//se setea la matriz de subimagenes con el nuevo tamaño.
                try {
                    puzzle.cagarSubImagenes();//se cargan la subimagenes.
                    clicks = 0;//se resetea el contador de movimientos.
                    requestFocusInWindow();//se cambia el foco a la ventana principal.
                } catch (IOException ex) {
                    Logger.getLogger(PuzzleGUI.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InterruptedException ex) {
                    Logger.getLogger(PuzzleGUI.class.getName()).log(Level.SEVERE, null, ex);
                }
                puzzle.updateUI();//se actualiza el panel.
            }
        });
        this.menu.add(this.tamaños);
        //Se inicializa la miniatura referencial de la imagen principal.
        ImageIcon icon = new ImageIcon(puzzle.getUrlImg());
        int width = icon.getIconWidth();  
        int height = icon.getIconHeight();  
        BufferedImage buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);  
        Graphics2D graphics = buffer.createGraphics();  
        icon.paintIcon(null, graphics, 0, 0);  
        graphics.dispose();  
        labelMiniatura = new JLabel(new ImageIcon(buffer.getScaledInstance(180, 180, Image.SCALE_DEFAULT)));
        
        this.menu.add(labelMiniatura);
        //Se agrega el boton de informacion "About".
        this.about = new JButton("About");
        this.about.setPreferredSize(dimButton);
        this.about.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent me){
                //se crea la ventana emergente con la informacion del programa.
                JOptionPane.showMessageDialog(null, "René Suazo Cáceres\n2017\nLicencia GPL", "About", JOptionPane.INFORMATION_MESSAGE);
                requestFocusInWindow();//se cambia el foco a la ventana principal.
            }
        });
        this.menu.add(this.about);
        this.add(this.menu, BorderLayout.EAST);//se agrega el panel de menu al layout de la ventana.        
        this.pack();
        this.addKeyListener(this);
        this.setFocusable(true);
        this.setVisible(true);                
    }

    @Override
    public void keyTyped(KeyEvent e) {
        //No implementado
    }

    @Override
    public void keyPressed(KeyEvent e) {        
        int x = 0;
        int y = 0;
        //Se buscan la coordenadas de la pieza vacia.
        for(int i = 0; i < n; i++){
            for(int j = 0; j < n; j++){
                if(this.puzzle.getSubImagenes()[i][j].getId() == 9){
                    x = i;
                    y = j;
                }
            }
        }
        //dependiendo de la tecla oprimida se llama al metodo para mover una pieza con la coordenadas correspodientes.
        switch (e.getKeyCode()){
            case 37://left
                if(x-1 >= 0){
                    this.puzzle.slideSubImage(x-1, y);
                }
                break;
            case 38://up
                if(y-1 >= 0){
                    this.puzzle.slideSubImage(x, y-1);
                }
                break;
            case 39://right
                if(x+1 < n){
                    this.puzzle.slideSubImage(x+1, y);
                }
                break;
            case 40://down
                if(y+1 < n){
                    this.puzzle.slideSubImage(x, y+1);
                }
                break;
        }
        //Si es el primer movimiento se guarda el tiempo en que se inicia.
        if(clicks == 0){
            timeStart = System.currentTimeMillis();
        }
        clicks++;//Se aumenta el contador de click o movimientos.
        puzzle.repaint();//Se pinta el panel.
        //se verifica si el puzzle esta ordenado, por medio del metodo endChecker.
        if(puzzle.endChecker()){
            try {
                //Se calcula el tiempo empleado y se muestra una ventana emergente con la informacion de la partida.
                timeEnd = System.currentTimeMillis();
                long time = (timeEnd - timeStart)/1000;//se calcula el tiempo de ejecucion.
                //se crea la ventana emergente con la informacion de termino.
                JOptionPane.showMessageDialog(null, "Felicitciones!\nClicks: "+clicks+"\nTiempo empleado: "+time+"(seg.)", "Terminado", JOptionPane.INFORMATION_MESSAGE);
                puzzle.shuffle();//se revuelve el puzzle.
                clicks = 0;//se resetea el contador de movimientos.
                puzzle.repaint();//se repinta el panel.
                } catch (InterruptedException ex) {
                    Logger.getLogger(PuzzleGUI.class.getName()).log(Level.SEVERE, null, ex);
            }                    
        }
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        //No implementado.
    }    
}
