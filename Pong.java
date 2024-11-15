import javax.swing.*;
import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.*;
import java.awt.event.*;

import javax.swing.JFrame;

// MyPanel extends JPanel, which will eventually be placed in a JFrame

class MyPanel extends JPanel {
    int ball_y_pos;
    int ball_x_pos;
    int y_pos_p1;
    int x_pos_p1;
    int y_pos_p2;
    int x_pos_p2;
    Dimension batDimensions;
    Dimension ballDimension;
    Dimension screenSize;
    Rectangle batP1Bounds;
    Rectangle batP2Bounds;
    Rectangle ballBounds;

    // custom painting is performed by the paintComponent method
    public MyPanel(Dimension screenSize) {
        this.screenSize = screenSize;
        this.initDimensions();
        this.initPositions();
        setBackground(Color.BLACK);
        setPreferredSize(screenSize);
    }

    public void initDimensions() {
        int objectWidth = (int) (0.02 * this.screenSize.width);
        int ballWidth = objectWidth;
        int batWidth = objectWidth;
        int batHeight = (int) (0.15 * this.screenSize.height);
        this.batDimensions = new Dimension(batWidth, batHeight);
        this.ballDimension = new Dimension(ballWidth, ballWidth);
    }

    public void resetBallPositions() {
        this.ball_x_pos = (int) (0.5 * this.screenSize.width) - (int) (0.5 * this.ballDimension.width);
        this.ball_y_pos = (int) (0.5 * this.screenSize.height) - (int) (0.5 * this.ballDimension.height);
    }

    public void initPositions() {
        // keep the player 10% of screenwidth from wall
        int distPlayersFromWallsHorizontal = (int) (0.1 * this.screenSize.width);
        // center player in the middle in height perspective, drawing starts at bottom
        // so need to substract half the bat height.
        int distPlayersFromWallsVertical = (int) (0.5 * this.screenSize.height)
                - (int) (0.5 * this.batDimensions.height);
        this.x_pos_p1 = distPlayersFromWallsHorizontal;
        this.y_pos_p1 = distPlayersFromWallsVertical;
        this.x_pos_p2 = this.screenSize.width - distPlayersFromWallsHorizontal;
        this.y_pos_p2 = distPlayersFromWallsVertical;
        resetBallPositions();
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
        g2.fillRect(this.x_pos_p1, this.y_pos_p1, this.batDimensions.width, this.batDimensions.height);
        g2.fillRect(this.x_pos_p2, this.y_pos_p2, this.batDimensions.width, this.batDimensions.height);
        g2.fillRect(this.ball_x_pos, this.ball_y_pos, this.ballDimension.width, this.ballDimension.width);
        this.ballBounds = new Rectangle(this.ball_x_pos, this.ball_y_pos, this.batDimensions.width,
                this.batDimensions.height);
        this.batP1Bounds = new Rectangle(this.x_pos_p1, this.y_pos_p1, this.batDimensions.width,
                this.batDimensions.height);
        this.batP2Bounds = new Rectangle(this.x_pos_p2, this.y_pos_p2, this.batDimensions.width,
                this.batDimensions.height);
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
    Dimension screenSize;
    double maxRadian = Math.toRadians(70);
    boolean paused = false;

    public Pong() {

        createFrame();
        f.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent evt) {
                moveIt(evt);
            }
        });
    }

    public void createFrame() {
        f = new JFrame();
        // get the content area of Panel.
        Container c = f.getContentPane();
        // set the LayoutManager
        c.setLayout(new BorderLayout());
        c.setBackground(Color.red);
        c.setPreferredSize(new Dimension(1000, 1000));

        // Get the GraphicsConfiguration of the JFrame
        GraphicsConfiguration gc = c.getGraphicsConfiguration();

        // Get the screen device
        GraphicsDevice screen = gc.getDevice();

        // Get the screen size
        screenSize = new Dimension(screen.getDisplayMode().getWidth(), screen.getDisplayMode().getHeight());
        System.out.println(screenSize);
        c.setPreferredSize(screenSize);
        p = new MyPanel(screenSize);
        c.add(p);
        f.pack();
        f.setVisible(true);
        // sets close behavior; EXIT_ON_CLOSE invokes System.exit(0) on closing the
        // JFrame
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public boolean ballHitboundry() {
        if ((p.ball_y_pos >= (screenSize.height - p.ballDimension.width))
                || (p.ball_y_pos <= 0)) {
            return true;
        }
        return false;
    }

    public void checkBallThroughWall() {
        if (p.ball_x_pos <= -p.ballDimension.width) {
            p.resetBallPositions();
            scoreP2++;
            move_y = 0;
            move_x = 5;
        }
        if (p.ball_x_pos >= screenSize.width) {
            p.resetBallPositions();
            scoreP1++;
            move_y = 0;
            move_x = -5;
        }
    }

    public void determineBallMoveAfterBatHit(int y_pos_p, int direction) {
        double middleOfBat = (y_pos_p + 99 + y_pos_p - 19) / 2;
        double distanceFromMiddle = p.ball_y_pos - middleOfBat;
        double scale = y_pos_p + 99 - middleOfBat;
        double radian = (distanceFromMiddle / scale) * maxRadian;
        move_x = (int) (Math.cos(radian) * direction * 10);
        move_y = (int) (Math.sin(radian) * 10);
    }

    public void checkHitBat() {
        if (p.ballBounds.intersects(p.batP1Bounds)) {
            determineBallMoveAfterBatHit(p.y_pos_p1, 1);
        }
        if (p.ballBounds.intersects(p.batP2Bounds)) {
            determineBallMoveAfterBatHit(p.y_pos_p2, -1);
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
        while (running) {
            if (!paused) {
                try {
                    Thread.sleep(25);
                    moveBall();
                    moveCPU();
                    // System.out.println("hallo daar");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            synchronized (this) {
                while (paused) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
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
            case KeyEvent.VK_ESCAPE:
                paused = !paused;
                System.out.println("and now paused is:" + paused);
                if (!paused) {
                    synchronized (this) {
                        this.notify();
                    }
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
