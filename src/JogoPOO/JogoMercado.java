package JogoPOO;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.AlphaComposite;
import java.awt.Color;
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

public class JogoMercado extends Game implements KeyListener {

	private Point jogador;
	private Point speed;
	private Point agua;
	private Point menina;
	private BufferedImage imgBackground;
	private BufferedImage imgBola;
	private BufferedImage imgMaca;
	private BufferedImage imgHamburger;
	private BufferedImage imgHud;
	private BufferedImage imgAgua;
	private BufferedImage imgMenina;
	HashMap<Integer, Boolean> keyPool;
	boolean faceRight = true;
	boolean imune;
	private ArrayList<Point> alimentosBons;
	private int fase;
	private Point alimentoRuim;
	private int quantidade;
	private int speedJogador;
	private int points;
	private int tempo;
	private int contImunidade;
	ArrayList<AudioClip> songs;

	
	public JogoMercado() {
		init();
	}
	
	public void init() {
		getMainWindow().addKeyListener(this);
		keyPool = new HashMap<Integer, Boolean>();
		jogador = new Point(0, 400);
		speed = new Point(5, 10);
		agua = new Point(random(100, this.getWidth() - 65), 5);
		menina = new Point(agua.x, agua.y);
		speedJogador = 20;
		alimentosBons = new ArrayList<>();
		alimentoRuim = new Point(random(100, this.getWidth() - 65), 5);
		points = 0;
		fase = 1;
		quantidade = 1;
		imune = false;
		contImunidade = 0;
		populateAlimentos(alimentosBons, quantidade);
		songs = new ArrayList<AudioClip>();
	}

	public void onLoad() {
		try {
			URL imgUrl = getClass().getResource("shopping.png");
			if (imgUrl == null) {
				throw new RuntimeException("A imagem /shopping.png não foi encontrada.");
			} else {
				imgBola = ImageIO.read(imgUrl);
			}
			imgUrl = getClass().getResource("1.jpg");

			if (imgUrl == null) {
				throw new RuntimeException("A imagem /1.png não foi encontrada.");
			} else {
				imgBackground = ImageIO.read(imgUrl);
			}

			imgUrl = getClass().getResource("apple.png");

			if (imgUrl == null) {
				throw new RuntimeException("A imagem /apple.png não foi encontrada.");
			} else {
				imgMaca = ImageIO.read(imgUrl);
			}
	
			imgUrl = getClass().getResource("hamburger.png");

			if (imgUrl == null) {
				throw new RuntimeException("A imagem /hamburger.png não foi encontrada.");
			} else {
				imgHamburger = ImageIO.read(imgUrl);
			}
			
			imgUrl = getClass().getResource("HUDA.png");

			if (imgUrl == null) {
				throw new RuntimeException("A imagem /HUDA.png não foi encontrada.");
			} else {
				imgHud = ImageIO.read(imgUrl);
			}
			
			imgUrl = getClass().getResource("girl.png");

			if (imgUrl == null) {
				throw new RuntimeException("A imagem /girl.png não foi encontrada.");
			} else {
				imgMenina = ImageIO.read(imgUrl);
			}
			
			imgUrl = getClass().getResource("water.png");

			if (imgUrl == null) {
				throw new RuntimeException("A imagem /water.png não foi encontrada.");
			} else {
				imgAgua = ImageIO.read(imgUrl);
			}
			
			URL url = getClass().getResource("song.wav");
			songs.add (Applet.newAudioClip(url));
			url = getClass().getResource("FX099.wav");
			songs.add(Applet.newAudioClip(url));

		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
	}

	public void onUnload() {
	}

	public void onUpdate() {
		//troca de fase
		switch (fase) {
		case 1:
			speed.y  = 8;
			break;

		case 2:
			speed.y = 12;
			break;
		}

		//condição para mudança de fase
		if(points == 25) {
			fase = 2;
		}
		
		if(imune) {
			tempo = 0;
		}
		
		//movimenta a personagem
		if (keyPool.get(KeyEvent.VK_LEFT) != null && jogador.x > 0) {
			faceRight = false;
			jogador.x -= speedJogador;
		}
		if (keyPool.get(KeyEvent.VK_RIGHT) != null && jogador.x <= 580) {
			faceRight = true;
			jogador.x += speedJogador;
		}
		
		//quando encontra uma maçã, aumenta 1 ponto
		if (intersectsAlimentosBons(alimentosBons)) {
			points += 1;
			songs.get(1).play();
		} 
		
		//quando encontra um hamburger, termina o jogo (se o personagem não estiver com imunidade)
		if (intersectsAlimentoRuim()) {
			if(!imune) {
				terminate();	
			}
			else {
				contImunidade++;
			}
			songs.get(0).play();
		} 
		
		//mudança de estado do personagem, se colidir com uma garrafa d'agua, ele fica imune
		// e não poderá morrer por 2 rodadas
		if (intersectsAgua()) {
			imune = true;
			songs.get(1).play();
		}
		
		if(contImunidade == 2) {
			imune = false;
			contImunidade = 0;
		}
		
		//reposiciona os objetos após eles caírem
		for (Point point : alimentosBons) {
			if(point.y > 800) {
				point.x = random(0, this.getWidth() - 65);
				point.y = -(random(1,500));
			}
		}

		if(alimentoRuim.y > 800) {
				alimentoRuim.x = random(0, this.getWidth() - 65);
				alimentoRuim.y = -random(1,100);
		}

		
		//faz cair os objetos
		cair();
		cair2();
		
		if(tempo >= 1000) {
			cair3();
		}
		
		if(agua.y > 800) {
			agua.x = random(100, this.getWidth() - 65);
			agua.y = 5;
			menina.x = agua.x;
			menina.y = agua.y;
			tempo = -1;
		}
		
		if (alimentosBons.size() == 0) {
			quantidade += 1;
			populateAlimentos(alimentosBons, quantidade);
		}
		
		if(quantidade == 3) { 
			quantidade = 0;
		}
		
		try {
			Thread.sleep(20);
		} catch (InterruptedException ex) {
			Logger.getLogger(JogoMercado.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	public int random(int min, int max) {
		int range = (max - min) + 1;
		return (int) (Math.random() * range) + min;
	}

	private void populateAlimentos(ArrayList<Point> alimentosBons, int quantidade) {
		for (int i = 0; i < quantidade; i++) {
			if (i==0) {
				alimentosBons.add(new Point(random(0, this.getWidth() - 65), -random(101,200)));
			} else if (i==1) {
				alimentosBons.add(new Point(random(0, this.getWidth() - 65), -random(201,600)));
			} else {
				alimentosBons.add(new Point(random(0, this.getWidth() - 65), -random(601,1000)));
				
			}
		} 
	}
	
	public boolean intersectsAlimentosBons(ArrayList<Point> alimentos) {
		Rectangle r1 = null;
		if (faceRight) {
			r1 = new Rectangle(jogador.x + 90, jogador.y + 110, 105, 119);
		} else {
			r1 = new Rectangle(jogador.x - 20, jogador.y + 80, 105, 119);
		}
		for (Point point : alimentosBons) {
			Rectangle r2 = new Rectangle(point.x, point.y, 60, 60);
			if (r1.intersects(r2)) {
				point.x = random(0, this.getWidth() - 65);
				point.y = -32;
				alimentosBons.remove(point);
				return true;
			}
		} return false;
	}
	
	public boolean intersectsAlimentoRuim() {
		Rectangle r1 = null;
		if (faceRight) {
			r1 = new Rectangle(jogador.x + 90, jogador.y + 110, 105, 119);
		} else {
			r1 = new Rectangle(jogador.x - 20, jogador.y + 80, 105, 119);
		}
			Rectangle r2 = new Rectangle(alimentoRuim.x, alimentoRuim.y, 60, 60);
			if (r1.intersects(r2)) {
				alimentoRuim.x = random(0, this.getWidth() - 65);
				alimentoRuim.y = -32;
				return true;
			}
	 return false;
	}
	
	public boolean intersectsAgua() {
		Rectangle r1 = null;
		if (faceRight) {
			r1 = new Rectangle(jogador.x + 90, jogador.y + 110, 105, 119);
		} else {
			r1 = new Rectangle(jogador.x - 20, jogador.y + 80, 105, 119);
		}
			Rectangle r2 = new Rectangle(agua.x, agua.y, 70, 70);
			if (r1.intersects(r2)) {
				agua.x = random(100, this.getWidth() - 65);
				agua.y = 5;
				menina.y = agua.y;
				menina.x = agua.x;
				tempo = -1;
				return true;
			}
		 return false;
	}
	
	public void cair() {
		for (Point point : alimentosBons) {
			point.y += speed.y - 2;
		}
	}
	
	public void cair2() {
		alimentoRuim.y += speed.y - 2;
	}
	
	public void cair3() {
		agua.y += (speed.y - 3) ;
	}

	public void onRender(Graphics2D g) {
		g.drawImage(imgBackground, 0, 0, 800, 600, null);
		
		if(tempo >= 990) {
			g.drawImage(imgMenina, menina.x, menina.y, 85, 85, null);
			g.drawImage(imgAgua, agua.x + 15, agua.y + 90 , 60, 60, null);
		}
		
		for (Point point : alimentosBons) {
				g.drawImage(imgMaca, point.x, point.y, 60, 60, null);
		}
		
		g.drawImage(imgHamburger, alimentoRuim.x, alimentoRuim.y, 60, 60, null);
		
		if (faceRight) {
			g.drawImage(imgBola, jogador.x, jogador.y, 200, 200, null);
		} else {
			g.drawImage(imgBola, jogador.x + 200, jogador.y, -200, 200, null);
		}
		g.drawImage(imgHud, 50, 50, null);
		g.drawString("FASE " + fase, 78, 80);
		g.drawString("ALIMENTOS", 60, 95);
		g.drawString("COLETADOS", 60, 110);
		g.drawString(points + "", 93, 125);
		//g.drawString("TEMPO " + tempo + "", 60, 140);
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));
		g.setComposite(AlphaComposite.SrcOver);
		
		tempo++;
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