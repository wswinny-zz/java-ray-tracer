package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by Swinny on 10/20/2016.
 */
public class Window implements Runnable
{
    private BufferedImage image;
    private JFrame frame;

    public Window(BufferedImage imageRef)
    {
        this.image = imageRef;

        this.frame = new JFrame("Ray Tracer");
        this.frame.setSize(new Dimension(900, 900));
        this.frame.setLocationRelativeTo(null);
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.frame.setVisible(true);

        new Thread(this).start();
    }

    @Override
    public void run()
    {
        while(true)
        {
            try
            {
                Graphics g = this.frame.getGraphics();
                g.drawImage(this.image.getScaledInstance(900, 900, BufferedImage.SCALE_SMOOTH), 0, 0, null);

                Thread.sleep(1000 / 60);
            }
            catch(Exception e)
            {

            }
        }
    }
}
