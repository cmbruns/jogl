package com.jogamp.opengl.test.junit.jogl.util.texture;

import java.awt.Frame;

import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

import com.jogamp.opengl.test.junit.jogl.demos.TextureSequenceDemo01;
import com.jogamp.opengl.test.junit.jogl.demos.es2.TextureSequenceCubeES2;
import com.jogamp.opengl.test.junit.util.MiscUtils;
import com.jogamp.opengl.test.junit.util.QuitAdapter;
import com.jogamp.opengl.test.junit.util.UITestCase;
import com.jogamp.opengl.util.Animator;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestTextureSequence01AWT extends UITestCase {
    static boolean showFPS = false;
    static int width = 510;
    static int height = 300;
    static boolean useBuildInTexLookup = false;
    static long duration = 500; // ms
    static GLProfile glp;
    static GLCapabilities caps;
    
    @BeforeClass
    public static void initClass() {
        glp = GLProfile.getGL2ES2();
        Assert.assertNotNull(glp);
        caps = new GLCapabilities(glp);
        Assert.assertNotNull(caps);
    }

    void testImpl() throws InterruptedException {
        final GLCanvas glc = new GLCanvas(caps);
        final Frame frame = new Frame("TestTextureSequence01AWT");
        Assert.assertNotNull(frame);
        frame.add(glc);
        
        final TextureSequenceDemo01 texSource = new TextureSequenceDemo01(useBuildInTexLookup);
        glc.addGLEventListener(new GLEventListener() {
            @Override
            public void init(GLAutoDrawable drawable) {
                texSource.initGLResources(drawable.getGL());
            }
            @Override
            public void dispose(GLAutoDrawable drawable) { }
            @Override
            public void display(GLAutoDrawable drawable) { }
            @Override
            public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) { }            
        });
        glc.addGLEventListener(new TextureSequenceCubeES2(texSource, false, -2.3f, 0f, 0f));
        final Animator animator = new Animator(glc);
        animator.setUpdateFPSFrames(60, showFPS ? System.err : null);
        QuitAdapter quitAdapter = new QuitAdapter();
        new com.jogamp.newt.event.awt.AWTKeyAdapter(quitAdapter).addTo(glc);
        new com.jogamp.newt.event.awt.AWTWindowAdapter(quitAdapter).addTo(glc);
        try {
            javax.swing.SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    frame.setSize(width, height);
                    frame.setVisible(true);
                }});
        } catch( Throwable throwable ) {
            throwable.printStackTrace();
            Assume.assumeNoException( throwable );
        }                
        animator.start();
        
        while(!quitAdapter.shouldQuit() && animator.isAnimating() && animator.getTotalFPSDuration()<duration) {
            Thread.sleep(100);
        }
        
        animator.stop();
        Assert.assertFalse(animator.isAnimating());
        Assert.assertFalse(animator.isStarted());
        try {
            javax.swing.SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    frame.setVisible(false);
                    frame.remove(glc);
                    frame.dispose();
                }});
        } catch( Throwable throwable ) {
            throwable.printStackTrace();
            Assume.assumeNoException( throwable );
        }                
    }
    
    @Test
    public void test1() throws InterruptedException {
        testImpl();        
    }
    
    public static void main(String[] args) {
        for(int i=0; i<args.length; i++) {
            if(args[i].equals("-time")) {
                i++;
                duration = MiscUtils.atol(args[i], duration);
            } else if(args[i].equals("-width")) {
                i++;
                width = MiscUtils.atoi(args[i], width);
            } else if(args[i].equals("-height")) {
                i++;
                height = MiscUtils.atoi(args[i], height);
            } else if(args[i].equals("-shaderBuildIn")) {
                useBuildInTexLookup = true;
            }
        }
        org.junit.runner.JUnitCore.main(TestTextureSequence01AWT.class.getName());        
    }

}
