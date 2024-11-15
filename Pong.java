import javax.swing.*;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.*;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.image.BufferStrategy;
import java.awt.event.*;
import java.lang.Math;

// MyPanel extends JPanel, which will eventually be placed in a JFrame

class MyPanel extends JPanel {
    int y_pos_p1 = 450;
    int x_pos_p1 = 100;
    int y_pos_p2 = 450;
    int x_pos_p2 = 880;
    int ball_y_pos = 490;
    int ball_x_pos = 490;

    // custom painting is performed by the paintComponent method
    public MyPanel() {
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(1000, 1000));
    }

    @Override
    public void paintComponent(Graphics g) {
        // clear the previous painting
        // System.out.println("I am being called");
        super.paintComponent(g);
        // cast Graphics to Graphics2D
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.red); // sets Graphics2D color
        // draw the rectangle
        g.setColor(Color.WHITE);
        g2.fillRect(x_pos_p1, y_pos_p1, 20, 100); // drawRect(x-position, y-position, width, height)
        g2.fillRect(x_pos_p2, y_pos_p2, 20, 100);
        g2.fillRect(ball_x_pos, ball_y_pos, 20, 20);
    }
}

public class Pong implements Runnable { // the Class by which we display our rectangle
    JFrame f;
    MyPanel p;
    boolean running = true;
    int move_x = 5;
    int move_y = -5;
    int scoreP1 = 0;
    int scoreP2 = 0;
    double maxRadian = Math.toRadians(70);

    public Pong() {
        f = new JFrame();
        // get the content area of Panel.
        Container c = f.getContentPane();
        // set the LayoutManager
        c.setLayout(new BorderLayout());
        c.setBackground(Color.red);
        c.setPreferredSize(new Dimension(1000, 1000));
        p = new MyPanel();
        // add MyPanel object into container
        // f.setSize(1000, 1000);
        c.add(p);
        f.pack();
        // c.setSize(1200, 1200);
        // set the size of the JFrame
        // make the JFrame visible
        f.setVisible(true);
        // sets close behavior; EXIT_ON_CLOSE invokes System.exit(0) on closing the
        // JFrame
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        f.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent evt) {
                moveIt(evt);
            }
        });
    }

    public boolean ballHitboundry() {
        if (p.ball_y_pos >= 980 || p.ball_y_pos <= 0) {
            return true;
        }
        return false;
    }

    public void checkBallThroughWall() {
        if (p.ball_x_pos <= -20) {
            p.ball_x_pos = 490;
            p.ball_y_pos = 490;
            scoreP2++;
            move_y = 0;
            move_x = 5;
        }
        if (p.ball_x_pos >= 1000) {
            p.ball_x_pos = 490;
            p.ball_y_pos = 490;
            scoreP1++;
            move_y = 0;
            move_x = -5;
        }
    }

    public void checkHitBat() {
        if ((p.ball_y_pos <= (p.y_pos_p1 + 99) && p.ball_y_pos >= p.y_pos_p1 - 19)
                && (p.ball_x_pos <= (p.x_pos_p1 + 20) && p.ball_x_pos >= p.x_pos_p1)) {
            double middleOfBat = (p.y_pos_p1 + 99 + p.y_pos_p1 - 19) / 2;
            // System.out.println("middleOfBat: " + middleOfBat);
            double distanceFromMiddle = p.ball_y_pos - middleOfBat;
            // System.out.println("distanceFromMiddle: " + distanceFromMiddle);
            double scale = p.y_pos_p1 + 99 - middleOfBat;
            // System.out.println("scale: " + scale);
            double radian = (distanceFromMiddle / scale) * maxRadian;
            // System.out.println("radian: " + radian);
            move_x = (int) (Math.cos(radian) * 10);
            move_y = (int) (Math.sin(radian) * 10);
            // System.out.println("Move x: " + move_x);
            // System.out.println("Move y " + move_y);
        }
        if ((p.ball_y_pos <= (p.y_pos_p2 + 99) && p.ball_y_pos >= p.y_pos_p2 - 19)
                && (p.ball_x_pos >= (p.x_pos_p2 - 20) && p.ball_x_pos <= (p.x_pos_p2))) {
            double middleOfBat = (p.y_pos_p2 + 99 + p.y_pos_p2 - 19) / 2;
            double distanceFromMiddle = p.ball_y_pos - middleOfBat;
            double scale = p.y_pos_p2 + 99 - middleOfBat;
            double radian = (distanceFromMiddle / scale) * maxRadian;
            move_x = (int) (Math.cos(radian) * -10);
            move_y = (int) (Math.sin(radian) * 10);
            System.out.println("Move x: " + move_x);
            System.out.println("Move y" + move_y);
        }
    }

    public void moveBall() {
        if (ballHitboundry() == true) {
            move_y = -1 * move_y;
            // p.ball_y_pos = 990;
        }
        checkBallThroughWall();
        checkHitBat();
        p.ball_x_pos += move_x;
        p.ball_y_pos += move_y;
        p.repaint();
    }

    public void moveCPU() {
        double middleOfBat = (p.y_pos_p2 + 99 + p.y_pos_p2 - 19) / 2;
        double distanceFromMiddle = p.ball_y_pos - middleOfBat;
        if (Math.abs(distanceFromMiddle) <= 5) {
            p.y_pos_p2 = p.y_pos_p2;
        } else if (distanceFromMiddle > 5) {
            p.y_pos_p2 += 5;
        } else if (distanceFromMiddle < -5) {
            p.y_pos_p2 -= 5;
        }
        // p.repaint();
    }

    public void run() {
        while (running = true) {
            try {
                Thread.sleep(25);
                moveBall();
                moveCPU();
                // System.out.println("hallo daar");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void refresh() {
        p.repaint();
    }

    public void moveIt(KeyEvent evnt) {
        switch (evnt.getKeyCode()) {
            case KeyEvent.VK_DOWN:
                // System.out.println("Down");
                if (p.y_pos_p1 < 900) {
                    p.y_pos_p1 += 20;
                }
                refresh();
                break;
            case KeyEvent.VK_UP:
                // System.out.println("up");
                if (p.y_pos_p1 > 0) {
                    p.y_pos_p1 -= 20;
                }
                refresh();
                break;
        }
    }

    public static void main(String args[]) {
        Pong t = new Pong();
        t.run();
    }
}
