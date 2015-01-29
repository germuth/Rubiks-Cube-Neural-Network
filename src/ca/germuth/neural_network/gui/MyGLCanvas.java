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
import ca.germuth.neural_network.gui.shapes.GLSquare;

import com.jogamp.opengl.util.FPSAnimator;

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
		
		// Global settings.
        gl2.glEnable(GL2.GL_DEPTH_TEST);
        gl2.glDepthFunc(GL2.GL_LEQUAL);
        gl2.glDepthMask(true);
        gl2.glClearColor(0f, 0f, 0f, 1f);
        
        glu = new GLU();

		 // Start animator (which should be a field).
        animator = new FPSAnimator(this, 60);
        animator.start();
	}
	
	//onDrawFrame
	@Override
	public void display(GLAutoDrawable drawable) {
		GL2 gl2 = drawable.getGL().getGL2();
	
		// Clear screen.
        gl2.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

        // Set camera 70 positions away down Z axis looking at origin
        setCamera(gl2, glu, 70);
        
        //rotate cube 45 degrees
        gl2.glRotatef(-45f, -1f, 0f, 0f);
        
        gl2.glBegin(GL2.GL_TRIANGLES);
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int arg1, int arg2, int arg3, int arg4) {
		GL2 gl2 = drawable.getGL().getGL2();
        gl2.glViewport(0, 0, width, height);
	}

	private void setCamera(GL2 gl2, GLU glu, float distance) {
        // Change to projection matrix.
        gl2.glMatrixMode(GL2.GL_PROJECTION);
        gl2.glLoadIdentity();

        // Perspective.
        float widthHeightRatio = (float) getWidth() / (float) getHeight();
        glu.gluPerspective(45, widthHeightRatio, 1, 1000);
        glu.gluLookAt(0, 0, distance, 0, 0, 0, 0, 1, 0);

        // Change back to model view matrix.
        gl2.glMatrixMode(GL2.GL_MODELVIEW);
        gl2.glLoadIdentity();
    }
}
