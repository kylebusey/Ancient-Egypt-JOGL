package JOGLScene;


import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLJPanel;
import java.awt.event.*;
import javax.swing.*;
import java.awt.*;


/**
 * Project 2: JOGL Scene written by Kyle Busey
 * February, 2022.
 *
 * This project uses the Java equivalent of OpenGL and was used in the second project of CMSC405: Computer Graphics.
 * My scene consists of "ancient egypt" concepts which include a pyramid, building a cube then using it to build a staircase,
 * gold coins, and pillars on top of a triangular structure (I thought it matched the theme).
 *
 */

public class Scene extends GLJPanel implements GLEventListener, KeyListener {

    //angles for rotation
    private int rotateX = 15;
    private int rotateY = -15;
    private int rotateZ = 0;

    private double translateX = 0, translateY = 0, translateZ = 0;
    private double scaleRate = 1.2;
    private static final double PILLAR_SIZE = 0.25;


    public static void main(String[] args) {
        JFrame window = new JFrame("Ancient Egypt"); //title of JFrame
        Scene panel = new Scene();
        window.setContentPane(panel);
        window.pack();
        window.setLocation(50,50);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setVisible(true);
        panel.requestFocusInWindow();
    }

    public Scene() {
        super(new GLCapabilities(null)); //create new Scene
        setPreferredSize( new Dimension(700, 500) ); //Requirement met
        addGLEventListener(this);
        addKeyListener(this);
    }

    /**
     * Display method for use of JOGL. Allows the use of drawing different shapes.
     * @param glAutoDrawable the needed glAutoDrawable object which allows use of GL2.
     */
    @Override
    public void display(GLAutoDrawable glAutoDrawable) {
        GL2 gl2 = glAutoDrawable.getGL().getGL2();
        gl2.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);


        gl2.glLoadIdentity();
        gl2.glRotated(rotateZ, 0, 0, 1);
        gl2.glRotated(rotateY, 0, 1, 0);
        gl2.glRotated(rotateX, 1, 0, 0);
        gl2.glScaled(scaleRate, scaleRate, scaleRate);
        gl2.glTranslated(translateX, translateY,translateZ);

        drawBackground(gl2);
        drawStairs(gl2, Shapes.cube);

        drawShape(gl2, Shapes.pyramid, 0.25, 0, 1.55, -0.2); //call drawShape to draw the pyramid
        drawShape(gl2, Shapes.coin, 0.15, 1, 2, 1); //call drawShape to draw first gold coin
        drawShape(gl2, Shapes.coin, 0.15, -1, 2, 1); //call drawShape to draw second gold coin


    }

    /**
     * Init method which executes at the beginning of the program. Sets up program with different Matrix modes, glOrtho,
     * and clearColor as well as enabling the depth_test.
     * @param glAutoDrawable Needed glAutoDrawable object for use of GL2.
     */
    public void init(GLAutoDrawable glAutoDrawable) {
        GL2 gl2 = glAutoDrawable.getGL().getGL2();
        gl2.glMatrixMode(GL2.GL_PROJECTION);
        gl2.glOrtho(-1, 1, -1, 1, -1, 1); //the bounds of the program
        gl2.glMatrixMode(GL2.GL_MODELVIEW);
        gl2.glClearColor(0, 0, 0, 1 ); //default clear color
        gl2.glEnable(GL2.GL_DEPTH_TEST);

    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    /**
     * KeyPressed method which allows the user to hit different keys on the keypad to get a different transformation.
     *
     * USER GUIDE:
     * Hitting the left, right, down, and up arrows will rotate the objects along the X, Y, and Z axis.
     * (Left and Right for Y axis, Up and Down for X axis, and Page_UP/Page_DOWN for Z axis)
     *
     * Hitting "1" or "2" on your keyboard will increase the scale of the scene by 10% or 20% respectively.
     *
     * Finally, hitting X, Y, or Z on your keyboard will translate the scene by .1 on whatever respective key you hit.
     *
     * Hitting the "Home" key will reset the whole scene to default.
     *
     * @param keyEvent The KeyEvent object is passed in when a key is hit.
     */
    @Override
    public void keyPressed(KeyEvent keyEvent) {
        int key = keyEvent.getKeyCode();

     if(key == KeyEvent.VK_LEFT)
            rotateY -= 15;
        else if ( key == KeyEvent.VK_RIGHT )
            rotateY += 15;
        else if ( key == KeyEvent.VK_DOWN)
            rotateX += 15;
        else if ( key == KeyEvent.VK_UP )
            rotateX -= 15;
        else if ( key == KeyEvent.VK_PAGE_UP )
            rotateZ += 15;
        else if ( key == KeyEvent.VK_PAGE_DOWN )
            rotateZ -= 15;
        else if(key == KeyEvent.VK_1)
            scaleRate += (scaleRate * .1);
        else if(key == KeyEvent.VK_2)
            scaleRate += (scaleRate * .2);
        else if(key == KeyEvent.VK_X)
            translateX += .1;
        else if(key == KeyEvent.VK_Y)
            translateY += .1;
        else if(key == KeyEvent.VK_Z)
            translateZ += .1;
        else if ( key == KeyEvent.VK_HOME) {
         rotateX = rotateY = rotateZ = 0;
         translateX = translateY = translateZ = 0;
         scaleRate = 1.2;
     }
        repaint();
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {}
    @Override
    public void dispose(GLAutoDrawable glAutoDrawable) {}
    @Override
    public void reshape(GLAutoDrawable glAutoDrawable, int i, int i1, int i2, int i3) {}


    /**
     * The drawShape method takes in various parameters in order to draw a Shapes object that is defined in the
     * Shapes class. It uses a for loop for drawing the vertices and edges instead of writing tons of glVertex3fv
     * statements.
     * @param gl2 The current GL2 object in use.
     * @param currentShape the Shapes object being passed in. Will be defined in the Shapes class in the package.
     * @param size The desired size of the shape, will be scaled.
     * @param x The x coordinate of the shape.
     * @param y  The y coordinate of the shape.
     * @param z The z coordinate of the shape.
     */
    private static void drawShape(GL2 gl2, Shapes currentShape, double size, double x, double y, double z) {
        gl2.glPushMatrix();
        gl2.glScaled(size, size, size);
        gl2.glTranslated(x, y, z);

        //initial for loop which checks the count of faceList
        for (int i = 0; i < currentShape.faces.length; i++) {
            gl2.glPushMatrix();

            gl2.glColor3dv(currentShape.faceColor[i], 0); //get the color from the faceColor array in Shapes

            gl2.glBegin(GL2.GL_TRIANGLE_FAN);
            for (int j = 0; j < currentShape.faces[i].length; j++) {
                int vertexNum = currentShape.faces[i][j];
                float[] vertexCoords = currentShape.vertices[vertexNum];
                gl2.glVertex3fv(vertexCoords, 0);
            }
            gl2.glEnd();

            gl2.glColor3d(0, 0, 0); //set to black due to edges

            gl2.glBegin(GL2.GL_LINE_LOOP); //use line loop for edges
            for (int j = 0; j < currentShape.faces[i].length; j++) {
                int vertexNum = currentShape.faces[i][j];
                gl2.glVertex3fv(currentShape.vertices[vertexNum], 0);
            }
            gl2.glEnd();
            gl2.glPopMatrix();
        }
        gl2.glPopMatrix(); //go back to normal matrix
    }

    /**
     * Draw background method which is used for readability. Draws three background pillars in the scene
     * as well as the triangle platform. Uses drawShape method to accomplish that.
     * @param gl2 The current GL2 object.
     */
    private static void drawBackground(GL2 gl2) {

        drawShape(gl2, Shapes.backgroundPillar, PILLAR_SIZE, 1.75, 1.5, 0.75);
        drawShape(gl2, Shapes.backgroundPillar, PILLAR_SIZE, -0.1, 1.5, -0.9);
        drawShape(gl2, Shapes.backgroundPillar, PILLAR_SIZE, -1.75, 1.5, 0.75);

        gl2.glPushMatrix();
        gl2.glRotated(-90, 1, 0, 0); //rotate the triangle platform so it is not standing
        drawShape(gl2, Shapes.triangle, .5, 0, -0.5, 0);
        gl2.glPopMatrix();

    }

    /**
     * drawStairs method which is used for readability. Calls drawShape for a cube multiple times to build
     * a staircase. Is rotated and translated to go against the pyramid.
     * @param gl2 The current GL2 object.
     * @param cube The defined cube object in Shapes class.
     */
    private static void drawStairs(GL2 gl2, Shapes cube) {
        gl2.glPushMatrix();

        gl2.glTranslated(.15, 0, 0.12); //translate stairs

        drawShape(gl2, cube, 0.2, -0.9, 1.25, 0.5); //first row left
        drawShape(gl2, cube, 0.2, -0.65, 1.25, 0.5); //first row middle
        drawShape(gl2, cube, 0.2, -0.4, 1.25, 0.5); //first row right
        drawShape(gl2, cube, 0.2, -0.9, 1.25, 0.2); //second row left
        drawShape(gl2, cube, 0.2, -0.65, 1.25, 0.2); //second row middle
        drawShape(gl2, cube, 0.2, -0.4, 1.25, 0.2); //second row right
        drawShape(gl2, cube, 0.2, -0.9, 1.45, 0.2); //third row left
        drawShape(gl2, cube, 0.2, -0.65, 1.45, 0.2); //third row middle
        drawShape(gl2, cube, 0.2, -0.4, 1.45, 0.2); //third row right
        drawShape(gl2, cube, 0.2, -0.9, 1.25, -0.1); //fourth row left
        drawShape(gl2, cube, 0.2, -0.65, 1.25, -0.1); //fourth row middle
        drawShape(gl2, cube, 0.2, -0.4, 1.25, -0.1); //fourth row right
        drawShape(gl2, cube, 0.2, -0.9, 1.45, -0.1); //fifth row left
        drawShape(gl2, cube, 0.2, -0.65, 1.45, -0.1); //fifth row middle
        drawShape(gl2, cube, 0.2, -0.4, 1.45, -0.1); //fifth row right
        drawShape(gl2, cube, 0.2, -0.9, 1.65, -0.1); //sixth row left
        drawShape(gl2, cube, 0.2, -0.65, 1.65, -0.1); //sixth row middle
        drawShape(gl2, cube, 0.2, -0.4, 1.65, -0.1); //sixth row right

        gl2.glPopMatrix();


    }
}



