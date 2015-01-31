package ca.germuth.neural_network.gui;

import java.util.ArrayList;

import javax.media.opengl.DebugGL2;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;

import ca.germuth.neural_network.Cube;

import com.jogamp.opengl.util.FPSAnimator;
/**
 * MyGLCanvas
 * 
 * A Canvas object which translates a OpenGL window into Java Swing
 * Part of JOGL library
 * 
 * Code 
 * @author Aaron Germuth
 */
public class MyGLCanvas extends GLCanvas implements GLEventListener{

	private static final long serialVersionUID = 9193099926662199837L;
	private Cube cube;
	private int width;
	private int height;
	private FPSAnimator animator;
	private GLU glu;
	private ArrayList<GLSquare> allFaces;
	
	public MyGLCanvas(GLCapabilities glcapabilities, int width, int height, Cube cube) {
		super(glcapabilities);
		this.addGLEventListener(this);
		this.cube = cube;
		this.allFaces = RubixCubeDrawer.createPuzzleModel(cube);
		this.width = width;
		this.height = height;
        setSize(width, height);
	}

	//onSurfaceCreated
	@Override
	public void init(GLAutoDrawable drawable) {
		drawable.setGL(new DebugGL2(drawable.getGL().getGL2()));
		GL2 gl2 = drawable.getGL().getGL2();
		//default settings
        gl2.glEnable(GL2.GL_DEPTH_TEST);
        gl2.glDepthFunc(GL2.GL_LEQUAL);
        gl2.glDepthMask(true);
        gl2.glClearColor(0f, 0f, 0f, 1f);
        
        glu = new GLU();

		//run at 60 fps
        animator = new FPSAnimator(this, 60);
        animator.start();
	}
	
	//onDrawFrame
	@Override
	public void display(GLAutoDrawable drawable) {
		GL2 gl2 = drawable.getGL().getGL2();
		//clears the screen
        gl2.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

        //move camera to +70 Z, looking at origin
        gl2.glMatrixMode(GL2.GL_PROJECTION);
        gl2.glLoadIdentity();
        float widthHeightRatio = (float) getWidth() / (float) getHeight();
        glu.gluPerspective(45, widthHeightRatio, 1, 1000);
        glu.gluLookAt(0, 0, 70, 0, 0, 0, 0, 1, 0);
        gl2.glMatrixMode(GL2.GL_MODELVIEW);
        gl2.glLoadIdentity();
        
        //rotate cube 45 degrees
        gl2.glRotatef(-45f, -1f, 0f, 0f);
        
        gl2.glBegin(GL2.GL_TRIANGLES);
        //draw each tile of rubik's cube, one at a time
        for(int i = 0; i < allFaces.size(); i++){
        	GLSquare curr = allFaces.get(i);
        	gl2.glColor3f(curr.getCurrentColor(cube).getRed(), curr.getCurrentColor(cube).getGreen(), curr.getCurrentColor(cube).getBlue());
        	
        	gl2.glVertex3f(curr.getBottomLeft().getX(), curr.getBottomLeft().getY(), curr.getBottomLeft().getZ());
        	gl2.glVertex3f(curr.getBottomRight().getX(), curr.getBottomRight().getY(), curr.getBottomRight().getZ());
        	gl2.glVertex3f(curr.getTopLeft().getX(), curr.getTopLeft().getY(), curr.getTopLeft().getZ());
        	
            gl2.glVertex3f(curr.getBottomRight().getX(), curr.getBottomRight().getY(), curr.getBottomRight().getZ());
            gl2.glVertex3f(curr.getTopRight().getX(), curr.getTopRight().getY(), curr.getTopRight().getZ());
            gl2.glVertex3f(curr.getTopLeft().getX(), curr.getTopLeft().getY(), curr.getTopLeft().getZ());
        }
        
        gl2.glEnd();
	}

	@Override
	public void dispose(GLAutoDrawable arg0) {
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int arg1, int arg2, int arg3, int arg4) {
		GL2 gl2 = drawable.getGL().getGL2();
        gl2.glViewport(0, 0, width, height);
	}
}
