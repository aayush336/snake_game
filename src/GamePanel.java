import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.*;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {

    static final int SCREEN_WIDTH = 800;
    static final int SCREEN_HEIGHT = 800;

    static final int UNIT_SIZE = 25;
    static final int GAME_UNIT = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE;
    static final int DELAY = 75;
    final int x[] = new int[GAME_UNIT];
    final int y[] = new int[GAME_UNIT];
    int bodyParts = 6;
    int applesEaten;
    int appleX;
    int appleY;
    char direction = 'R';
    boolean running = false;
    Timer timer;
    Random random;

    int boxWidth = 600;
    int boxHeight = 600;
    int boxX = (SCREEN_WIDTH - boxWidth) / 2;
    int boxY = (SCREEN_HEIGHT - boxHeight) / 2;

    Image bg_image, snakeImage;

    public GamePanel() {
        bg_image = new ImageIcon("snake_bg.jpg").getImage();
        
        //snake start from start of the box 
        // box end is boxX + boxWidth same with y
        x[0] = boxX ;
        y[0] = boxY;
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        startGame();
    }

    public void startGame() {
        newApple();
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D graphics2d = (Graphics2D) g;
        int imageWidth = bg_image.getWidth(this);
        int imageHeight = bg_image.getHeight(this);
        double scaleFactor = Math.min((double) SCREEN_WIDTH / imageWidth, (double) SCREEN_HEIGHT / imageHeight);
        int scaledWidth = (int) (imageWidth * scaleFactor);
        int scaledHeight = (int) (imageHeight * scaleFactor);

        // Calculate the position to center the image
        int x = (SCREEN_WIDTH - scaledWidth) / 2;
        int y = (SCREEN_HEIGHT - scaledHeight) / 2;
        graphics2d.drawImage(bg_image, x, y, scaledWidth, scaledHeight, this);
        
        draw(g);
    }

    public void draw(Graphics g) {
    	if(running) {
    	//draw box
    	g.setColor(Color.white);
    	
        g.drawRect(boxX, boxY, boxWidth+UNIT_SIZE, boxHeight+UNIT_SIZE);

        //draw apple
        g.setColor(Color.red);
        g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);
        
        //draw snake
        for (int i = 0; i < bodyParts; i++) {
            if (i == 0) {
                g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
            } else {
                g.setColor(Color.green);
                g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
            }
        }
        
        //Score Display 
    	g.setColor(Color.red);
    	g.setFont(new Font("Ink Free",getFont().BOLD,50));
    	FontMetrics metrics = getFontMetrics(getFont());
    	g.drawString("Score : "+applesEaten,(SCREEN_WIDTH-metrics.stringWidth("Score :"+applesEaten))/2, g.getFont().getSize());
        
        }
    	else {gameOver(g);}
    }

    public void newApple() {
        appleX = boxX + UNIT_SIZE * random.nextInt(boxWidth / UNIT_SIZE);
        appleY = boxY + UNIT_SIZE * random.nextInt(boxHeight / UNIT_SIZE);
    }

    public void move() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }
        switch (direction) {
            case 'U':
                y[0] = y[0] - UNIT_SIZE;
                break;
            case 'D':
                y[0] = y[0] + UNIT_SIZE;
                break;
            case 'L':
                x[0] = x[0] - UNIT_SIZE;
                break;
            case 'R':
                x[0] = x[0] + UNIT_SIZE;
                break;
            default:
                break;
        }
    }

    public void checkApple() {
    	if ((x[0]==appleX) && (y[0]==appleY)) {
    		bodyParts++;
    		applesEaten++;
    		newApple();
    	}

    }

    public void checkCollision() {
    	for (int i =  bodyParts;i>0;i--) {
    		//collision with body detect
    		if((x[0]==x[i])&& (y[0]==y[i])) {
    			running= false;
    		}
    		//collision with left border box detect
    		if (x[0]<boxX) {running=false;}

    		//collision with right border box detect
    		if (x[0]>boxX+boxWidth) {running=false;}
    		
    		//collision with down border box detect
    		if (y[0]>boxY+boxHeight) {running=false;}
    		
    		//collision with up border box detect
    		if (x[0]<boxY) {running=false;}
    	}
    	if(!running) {
    		timer.stop();
    	}
    }

    public void gameOver(Graphics g) {
    	//game over text
    	g.setColor(Color.red);
    	g.setFont(new Font("Ink Free",getFont().BOLD,75));
    	FontMetrics metrics = getFontMetrics(getFont());
    	g.drawString("Game Over",(SCREEN_WIDTH-metrics.stringWidth("Game Over"))/2, (SCREEN_HEIGHT/2));
    	g.setFont(new Font("Ink Free",getFont().BOLD,50));
       	g.drawString("Score : "+applesEaten,(SCREEN_WIDTH-metrics.stringWidth("Score :"+applesEaten))/2, g.getFont().getSize()); 	
    }

    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkApple();
            checkCollision();
        }
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter {
        public void keyPressed(KeyEvent e) {
        	switch (e.getKeyCode()) {
			case KeyEvent.VK_LEFT:
				if (direction != 'R') {
					direction ='L';
				}
				break;
			case KeyEvent.VK_RIGHT:
				if (direction != 'L') {
					direction ='R';
				}
				break;
			case KeyEvent.VK_UP:
				if (direction != 'D') {
					direction ='U';
				}
				break;
			case KeyEvent.VK_DOWN:
				if (direction != 'U') {
					direction ='D';
				}
				break;

			default:
				break;
			}
        }
    }
}
