package JogoPOO;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.imageio.ImageIO;
import java.util.concurrent.ThreadLocalRandom;

public class JogoPandinha extends Game implements KeyListener {

	private Point circle;
	private Point rosquinha;
	private Point speed;
	private Point pedra;
	private int speedPanda;
	private int points;
	private int tempo;
	private int contador;
	private BufferedImage imgBackground;
	private BufferedImage imgBola;
	private BufferedImage imgObjeto;
	private BufferedImage imgPedra;
	HashMap<Integer, Boolean> keyPool;
	ArrayList<Point> rosquinhas;
	ArrayList<AudioClip> songs = new ArrayList<>();
	
	public JogoPandinha() {
		init();
	}
	
	public void init() {
		getMainWindow().addKeyListener(this);
		keyPool = new HashMap<Integer, Boolean>();
		circle = new Point(240, 600);
		speed = new Point(5, 10);
		speedPanda = 50;
		rosquinhas = new ArrayList<>();
		rosquinha = new Point(random(0, this.getWidth() - 65), -32);
		pedra = new Point(random(0, this.getWidth() - 65), -32);
		points = 0;
		tempo = 0;
		contador = 1;
		populateRosquinhas(rosquinhas);
	}

	public void onLoad() {
		try {
			URL imgUrl = getClass().getResource("panda.png");
			if (imgUrl == null) {
				throw new RuntimeException("A imagem /panda.png não foi encontrada.");
			} else {
				imgBola = ImageIO.read(imgUrl);
			}
			imgUrl = getClass().getResource("full-background.png");

			if (imgUrl == null) {
				throw new RuntimeException("A imagem /full-background.png não foi encontrada.");
			} else {
				imgBackground = ImageIO.read(imgUrl);
			}

			imgUrl = getClass().getResource("3.png");

			if (imgUrl == null) {
				throw new RuntimeException("A imagem /leaves.png não foi encontrada.");
			} else {
				imgObjeto = ImageIO.read(imgUrl);
			}
			
			imgUrl = getClass().getResource("4.png");

			if (imgUrl == null) {
				throw new RuntimeException("A imagem /leaves.png não foi encontrada.");
			} else {
				imgPedra = ImageIO.read(imgUrl);
			}
			URL url = getClass().getResource("song.wav");
			songs.add( Applet.newAudioClip(url));
			url = getClass().getResource("happy.wav");
			songs.add(Applet.newAudioClip(url));
			url = getClass().getResource("FX099.wav");
			songs.add(Applet.newAudioClip(url));
			songs.get(1).loop();
		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
	}

	public int random(int min, int max) {
		int range = (max - min) + 1;
		return (int) (Math.random() * range) + min;
	}

	private void populateRosquinhas(ArrayList<Point> rosquinhas) {
		for (int i = 0; i < 3; i++) {
			rosquinhas.add(new Point(random(0, this.getWidth() - 65), -32));
		} 
	}

	public void onUnload() {
	}

	public void onUpdate() {

		if (keyPool.get(KeyEvent.VK_LEFT) != null && circle.x > 10) {
			circle.x -= speedPanda;
		}
		if (keyPool.get(KeyEvent.VK_RIGHT) != null && circle.x <= 440) {
			circle.x += speedPanda;
		}

		
		if (intersectsRosquinha(rosquinhas)) {
			songs.get(2).play();
			points += 10;
		} 
		
		if(intersectsPedra()) {
			songs.get(0).play();
			terminate();
		}
		for (Point point : rosquinhas) {
			if(point.y > 750) {
				songs.get(1).stop();
				songs.get(0).play();
				terminate();
				//rosquinha.x = random(0, this.getWidth() - 65);
				//rosquinha.y = -32;
				//points -= 10;
			}
		}

		cair();
		cairPedra();
		try {
			Thread.sleep(20);
		} catch (InterruptedException ex) {
			Logger.getLogger(JogoPandinha.class.getName()).log(Level.SEVERE, null, ex);
		}
		tempo++;
	}

	public boolean intersectsRosquinha(ArrayList<Point> rosquinhas) {
		Rectangle r1 = new Rectangle(circle.x - 85, circle.y - 85, 85, 85);
		for (Point point : rosquinhas) {
			Rectangle r2 = new Rectangle(point.x - 85, point.y - 85, 85, 85);
			if (r1.intersects(r2)) {

				point.x = random(0, this.getWidth() - 65);
				point.y = -32;
				return true;
			}
		} return false;
	}
	
	public boolean intersectsPedra() {
		Rectangle r1 = new Rectangle(circle.x - 85, circle.y - 85, 85, 85);
		Rectangle r2 = new Rectangle(pedra.x - 85, pedra.y - 85, 85, 85);
			return r1.intersects(r2);
	}

	public void cair() {
		rosquinhas.get(0).y += speed.y - 2;
		//rosquinhas.get(1).y += speed.y + 1;
		//
		//rosquinhas.get(2).y += speed.y + 3;
	}
	
	public void cairPedra() {
		pedra.y += speed.y;
	}

	public void onRender(Graphics2D g) {
		//int x = ThreadLocalRandom.current().nextInt(0, 460 + 1);
		g.drawImage(imgBackground, 0, 0, 640, 740, null);
		
		for (int i = 0; i < 3; i++) {
			g.drawImage(imgObjeto, rosquinhas.get(i).x, rosquinhas.get(i).y, 60, 60, null);
		}
		g.drawImage(imgPedra, pedra.x, pedra.y, 60, 60, null);
		g.drawImage(imgBola, circle.x, circle.y, 85, 64, null);
		g.drawString("SCORE", 40, 40);
		g.drawString(points + "", 55, 55);
		/*if(this.points > 100) {
			g.drawImage(imgObjeto, rosquinha.x, rosquinha.y, 60, 60, null);
		}*/
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));
		g.setComposite(AlphaComposite.SrcOver);
	}

	public void keyTyped(KeyEvent e) {

	}

	public void keyPressed(KeyEvent e) {
		keyPool.put(e.getKeyCode(), true);

	}

	public void keyReleased(KeyEvent e) {
		keyPool.remove(e.getKeyCode());
	}

}