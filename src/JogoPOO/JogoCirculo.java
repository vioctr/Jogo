package JogoPOO;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;

// Esta é a classe que representa nosso jogo. Ela é derivada de "Game", que
// possui o motor do jogo e chama os métodos abaixo quando necessário.
public class JogoCirculo extends Game {

    // Variáveis necessárias para nosso jogo.
    // Elas armazenam a posição do círculo (x,y) e a velocidade que ela anda.
    int x;
    int y;
    int sx;
    int sy;

    public void onLoad() {
        // Este método é chamado quando o jogo é iniciado.
        // Aqui damos os valores iniciais para as variáveis.
        x = 0;
        y = 0;
        sx = 10;
        sy = 10;
    }

    public void onUnload() {
        // Este método é chamado quando o jogo termina.
        // Não é preciso fazer nada para este jogo.
    }

    public void onUpdate() {
        // Este método é chamado cada vez que a lógica do jogo precisa ser
        // atualizada. Aqui mudamos os valores das variáveis para
        // fazer a bola se mover na tela, rebatendo nas bordas.
        x += sx;
        y += sy;
        // Toda vez que a posição chega em um limite da tela,
        // a velocidade naquela direção é invertida.
        if (x < 0 || x > getWidth()) {
            sx *= -1;
        }
        if (y < 0 || y > getHeight()) {
            sy *= -1;
        }
    }

    public void onRender(Graphics2D g) {
        // Este método é chamado cada vez que é preciso atualizar a imagem
        // do jogo na tela. É aqui que desenyhamos abola na posição 
        // armazenada nas variáveis.
    	 Rectangle2D.Float r = new Rectangle2D.Float(0,0, getWidth()-1,getHeight()-1);
    	     Ellipse2D.Float c = new Ellipse2D.Float(0, 0, getWidth()-1,getHeight()-1);
    	     Line2D.Float l = new Line2D.Float(100, 100, getWidth()/2, getHeight()/2);
    	     g.setColor(Color.red);
    	     g.draw(r);
       	     g.setColor(Color.blue);
    	     g.fill(c);
    	     g.setColor(Color.yellow);
    	     g.draw(c);
    	     g.draw(l);
    	     g.translate(100, 100);
    	         g.scale(0.5, 0.5);
    	         g.setColor(Color.red);
    	         g.draw(r);
    	         g.setColor(Color.orange);
    	         g.fill(c);
    	         g.setColor(Color.yellow);
    	         g.draw(c);
    	         g.draw(l);
    	         g.setTransform(AffineTransform.getScaleInstance(1, 1));

    }
    
    // Os métodos acima são chamados automaticamente pelo "motor" do jogo que
    // é herdado da classe Game. Abra o arquivo game.java e veja lá o que ocorre
    // quando o programa roda.
}