import java.awt.EventQueue;

import javax.swing.JFrame;

public class BasicEx extends JFrame {

	private Surface surface;
	
	public Surface getSurface()
	{
		return surface;
	}
	
    public BasicEx() {

        initUI();
    }

    private void initUI() {
    	surface = new Surface();
        add(surface);

        setTitle("Door Server");
        setSize(500, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

}
