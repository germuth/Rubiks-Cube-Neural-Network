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
        gl2.glShadeModel(GL2.GL_SMOOTH);
        gl2.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);
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

        // Set camera.
        setCamera(gl2, glu, 100);
		
//        // Write triangle.
//        gl2.glColor3f(0.9f, 0.5f, 0.2f);
//        gl2.glBegin(GL2.GL_TRIANGLE_FAN);
//        gl2.glVertex3f(-20, -20, 0);
//        gl2.glVertex3f(+20, -20, 0);
//        gl2.glVertex3f(0, 20, 0);
//        gl2.glEnd();
        
        
      // Write Square
        gl2.glBegin(GL2.GL_TRIANGLES);
        for(int i = 0; i < allFaces.size(); i++){
        	GLSquare curr = allFaces.get(i);
        	gl2.glColor3f(curr.getColor().getRed(), curr.getColor().getGreen(), curr.getColor().getBlue());
        	
            gl2.glVertex3f(curr.getBottomLeft().getX(), curr.getBottomLeft().getY(), curr.getBottomLeft().getZ());
            gl2.glVertex3f(+20, -20, 0);
            gl2.glVertex3f(-20, 20, 0);
            
            gl2.glVertex3f(+20, -20, 0);
            gl2.glVertex3f(+20, +20, 0);
            gl2.glVertex3f(-20, 20, 0);
        }
        
        gl2.glEnd();
        
        
//      gl2.glColor3f(0.9f, 0.5f, 0.2f);
//      gl2.glVertex3f(-20, -20, 0);
//      gl2.glVertex3f(+20, -20, 0);
//      gl2.glVertex3f(-20, 20, 0);
//      
//      gl2.glVertex3f(+20, -20, 0);
//      gl2.glVertex3f(+20, +20, 0);
//      gl2.glVertex3f(-20, 20, 0);
//      
//      gl2.glEnd();
        
        
//		 // draw a triangle filling the window
//        gl2.glLoadIdentity();
//        gl2.glBegin( GL2.GL_TRIANGLES );
//        gl2.glColor3f( 1, 0, 0 );
//        gl2.glVertex2f( 0, 0 );
//        gl2.glColor3f( 0, 1, 0 );
//        gl2.glVertex2f( width, 0 );
//        gl2.glColor3f( 0, 0, 1 );
//        gl2.glVertex2f( width / 2, height );
//        gl2.glEnd();
        
////		for(int i = 0; i < this.allFaces.size(); i++){
////			GLShape curr = this.allFaces.get(i);
//			GLShape curr = new GLShape();
//			GLVertex one = new GLVertex(-20, -20, 0);
//			GLVertex two = new GLVertex(+20, -20, 0);
//			GLVertex three = new GLVertex(0, 20, 0);
//			ArrayList<GLVertex> verts = new ArrayList<GLVertex>();
//			verts.add(one);
//			verts.add(two);
//			verts.add(three);
//			curr.setVerticies(verts);
//			
//			final short drawOrder[] = { 0, 1, 2, 0, 2, 3 };
//			
//			ByteBuffer bb = ByteBuffer.allocateDirect(
//			// (# of coordinate values * 4 bytes per float)
//			// verticies * coordinates * bytes per float
//					curr.getVerticies().size() * 3 * 4);
//			bb.order(ByteOrder.nativeOrder());
//			FloatBuffer vertexBuffer = bb.asFloatBuffer();
//			vertexBuffer.put(curr.getCoords());
//			vertexBuffer.position(0);
//
//			// initialize byte buffer for the draw list
//			ByteBuffer dlb = ByteBuffer.allocateDirect(
//			// (# of coordinate values * 2 bytes per short)
//					drawOrder.length * 2);
//			dlb.order(ByteOrder.nativeOrder());
//			ShortBuffer drawListBuffer = dlb.asShortBuffer();
//			drawListBuffer.put(drawOrder);
//			drawListBuffer.position(0);
//
//			gl2.glColor3f(0.9f, 0.5f, 0.2f);
//			gl2.glDrawElements(GL2.GL_TRIANGLES, drawOrder.length, GL2.GL_UNSIGNED_SHORT, drawListBuffer);
//			gl2.glEnd();
//		}
	}

	@Override
	public void dispose(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void reshape(GLAutoDrawable drawable, int arg1, int arg2, int arg3, int arg4) {
		GL2 gl2 = drawable.getGL().getGL2();
        gl2.glViewport(0, 0, width, height);
		
//		gl2.glMatrixMode( GL2.GL_PROJECTION );
//        gl2.glLoadIdentity();
//
//        // coordinate system origin at lower left with width and height same as the window
//        GLU glu = new GLU();
//        glu.gluOrtho2D( 0.0f, width, 0.0f, height );
//
//        gl2.glMatrixMode( GL2.GL_MODELVIEW );
//        gl2.glLoadIdentity();
//
//        gl2.glViewport( 0, 0, width, height );
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
