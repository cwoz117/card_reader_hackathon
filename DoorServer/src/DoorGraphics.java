import java.awt.Dimension;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

class Surface extends JPanel {

	static final long serialVersionUID = 1;
	
    private Image imgLock;
    private Image imgUnlock;
    private boolean locked;

    public boolean getLocked()
    {
    	return locked;
    }

    public void setLocked(boolean b)
    {
    	locked = b;
    }
    
    public Surface()
    {
        loadImage();
        setSurfaceSize();
    }
    
    private void loadImage() 
    {
    	imgLock 	= new ImageIcon("locked.jpg").getImage();
    	imgUnlock 	= new ImageIcon("unlocked.jpg").getImage();
    }
    
    private void doDrawing(Graphics g) 
    {
        Graphics2D g2d = (Graphics2D) g;
        g.setColor(Color.RED);
        g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
        g2d.drawImage(locked ? imgLock : imgUnlock, 0, 0, null);
    }

    private void setSurfaceSize() {
        
        Dimension d = new Dimension();
        d.width = imgLock.getWidth(null);
        d.height = imgUnlock.getHeight(null);
        setPreferredSize(d);        
    }

    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        doDrawing(g);
    }
}
