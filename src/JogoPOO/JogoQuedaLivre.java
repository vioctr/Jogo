package JogoPOO;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Random;
import javax.imageio.ImageIO;

public class JogoQuedaLivre extends Game implements KeyListener {
    // Estados do jogo.
    // Indica que o jogador n�o saltou (est� agarrado pelo pterod�tilo).

    static final int EST_VOANDO = 0;
    // Indica que o jogador saltou, est� caindo de paraquedas.
    static final int EST_CAINDO = 1;
    // Indica que o jogador caiu sobre o alvo (acertou).
    static final int EST_ACERTO = 2;
    // Indica que o jogador caiu fora do alvo (errou).
    static final int EST_ERRO = 3;
    // Indica que o jogo est� no finalizado, mostrando uma mensagem.
    static final int EST_FINAL = 4;
    // Modelo do jogo.
    // Representa o estado atual do jogo.
    int estado;
    // Representa a posi��o do pterod�tilo na tela.
    Point posPtero;
    // Representa a velocidade horizntal do pterod�tilo.
    int velPtero;
    // Representa a posi��o do paraquedista (jogador).
    // � utilizado ponto flutuante para conseguir um controle exato da velocidade.
    Point2D.Float posJogador;
    // Representa as velocidades (horizontal e vertical) do paraquedista.
    Point2D.Float velJogador;
    // Representa a posi��o do alvo.
    Point posAlvo;
    // Representa a alrgura do alvo (a altura � fixa no c�digo).
    int largAlvo;
    // Armazena a quantidade de pontos que o jogador fez.
    int pontos;
    // Armazena a quantidade de pontos que o jogador ainda tem.
    int tentativas;
    // Imagens, sons e fontes.
    BufferedImage imgCaindo;
    BufferedImage imgAcerto;
    BufferedImage imgErro;
    BufferedImage imgCenario;
    BufferedImage imgPterodatilo;
    AudioClip sndPterodatilo;
    AudioClip sndCaindo;
    AudioClip sndAcerto;
    AudioClip sndErro;
    Font fontCrimewave;
    // Utilit�rios.
    // Objeto para gera��o de n�meros aleatoriamente.
    Random rnd;
    // Num�ro arbitr�rio de milisegundos que se o jogo vai parar a cada
    // volta do game loop. Serve para reduzir a velocidade do jogo.
    int delay;
    // Implementa��o do Key Pooling para o teclado.
    HashMap<Integer, Boolean> keyPool;

    public JogoQuedaLivre() {
        // Adiciona esta classe como ouvinte do teclado.
        getMainWindow().addKeyListener(this);
        // Cria��o dos objetos.
        keyPool = new HashMap<Integer, Boolean>();
        rnd = new Random();
        posJogador = new Point2D.Float(0, 0);
        // A posi��o do pterod�tilo � criada na altura 150px, n�mero
        // que n�o vai mudar.
        posPtero = new Point(0, 50);
        velJogador = new Point2D.Float();
        velPtero = 0;
        // A posi��o do alvo � criada na altura 575px, n�mero que n�o vai mudar.
        posAlvo = new Point(0, 575);
        largAlvo = 150;
        estado = EST_VOANDO;
        pontos = 0;
        tentativas = 5;
        delay = 25;
    }

    @Override
    public void onLoad() {
        try {
            // Carrega imagens, son e fontes.
           imgCenario = loadImage("/cenario.png");
            imgPterodatilo = loadImage("/pterodatilo.png");
            imgCaindo = loadImage("/caindo.png");
            imgAcerto = loadImage("/acerto.png");
            imgErro = loadImage("/erro.png");
            sndPterodatilo = Applet.newAudioClip(getClass().getResource(
                    "/aviao.wav"));
            sndCaindo = Applet.newAudioClip(getClass().getResource(
                    "/caindo.wav"));
            sndAcerto = Applet.newAudioClip(getClass().getResource(
                    "/acerto.wav"));
            sndErro = Applet.newAudioClip(
                    getClass().getResource("/erro.wav"));
            File tmp = new File(getClass().getResource("/CrimewaveBB.ttf").
                    toURI());
            fontCrimewave = Font.createFont(Font.TRUETYPE_FONT, tmp);
            // Determina a quantidade de tentativas.
            tentativas = 5;
            // Zera a pontua��o.
            pontos = 0;
            // Executa a rotina que inicia a a��o.
            runReinicio();
        } catch (FontFormatException ex) {
            throw new RuntimeException(ex);
        } catch (URISyntaxException ex) {
            throw new RuntimeException(ex);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    protected BufferedImage loadImage(String fileName) throws IOException {
        // Rotina auxiliar para carga das imagens.
        BufferedImage img = null;
        URL url = getClass().getResource(fileName);
        if (url == null) {
            throw new RuntimeException(
                    "A imagem " + fileName + " n�o foi encontrada.");
        } else {
            img = ImageIO.read(url);
        }
        return img;
    }

    @Override
    public void onUnload() {
        // Interrompe o som do pterod�tilo
        // (que pode estar ocorrendo ao sair do jogo).
        sndPterodatilo.stop();
    }

    @Override
    public void onUpdate() {
        // Como sabemos est� rotina � chamada a cada volta do game loop.
        // Como o c�digo � extenso, ela foi dividida em rotinas menores.
        // Temos uma rotina para executar a l�gica de cada estado do jogo.

        // A rotina abaixo � executada em todas voltas e controla a sa�da do jogo
        // (tecla ESC) e outras coisas gerais.
        runControleDoJogo();

        // Aqui come�amos a testar os testados.
        if (estado == EST_FINAL) {
            // Se o estado � FINAL, executa a rotina abaixo e nenhuma outra.
            runEstadoFinal();
        } else {
            // Se n�o est� no estado final, move o pterod�tilo.
            runMoveAviao();
            // E verifica se est� voando ou caindo.
            if (estado == EST_VOANDO) {
                runEstadoVoando();
            } else if (estado == EST_CAINDO) {
                runEstadoCaindo();
            }

        }
    }

    protected void runReinicio() {
        // Esta rotina d� os valores iniciais para os atributos, iniciando a a��o.
        // Muda o estado para VOANDO.
        estado = EST_VOANDO;
        // Posiciona o pterod�tilo 100px � esquerda (fora) da tela.
        // A posi��o vertical nunca muda.
        posPtero.x = -100;
        // Sorteia uma velocidade para o pterod�tilo, entre 2px e 12px.
        velPtero = 2 + rnd.nextInt(10);
        // Sorteia a posi��o do alvo.
        // A posi��o vertical nunca muda.
        posAlvo.x = rnd.nextInt(600);
        // P�ra o som atual do pterod�tilo.
        sndPterodatilo.stop();
        // Inicia novamente o som do pterod�tilo em loop.
        sndPterodatilo.loop();
    }

    protected void runControleDoJogo() {
        if (keyPool.get(KeyEvent.VK_ESCAPE) != null) {
            // Se a tecla ESC est� pressionada, termina o jogo.
            terminate();
        }
        if (keyPool.get(KeyEvent.VK_UP) != null) {
            // Se a seta para cima est� pressionada, diminui o delay.
            if (delay > 1) {
                // S� diminui se for maior que 1.
                delay--;
            }
        }
        if (keyPool.get(KeyEvent.VK_DOWN) != null) {
            // Se a seta para baixo est� pressionada, aumenta o delay.
            if (delay < 100) {
                // S� aumenta se for menor que 100.
                delay++;
            }
        }
        try {
            // Faz o programa parar de executar durante delay milisegundos.
            // Isto serve para reduzir a velocidade em m�quinas muito r�pidas.
            Thread.sleep(delay);
        } catch (InterruptedException ex) {
        }
    }

    protected void runMoveAviao() {
        // Atualiza a posi��o do pterod�tilo conforme a velocidade.
        posPtero.x += velPtero;
        if (posPtero.x > getWidth() + 100) {
            // Se a posi��o do pterod�tilo ultrapassa a largura da tela mais 100px,
            // chama a rotina para reiniciar a a��o.
            runReinicio();
        }
    }

    protected void runEstadoFinal() {
        if (keyPool.get(KeyEvent.VK_ENTER) != null) {
            // Se a tecla ENTER est� pressionada, recarrega o jogo.
            onLoad();
        }
        // Apesar deste estado ficar observando apenas a tecla ENTER, temos a
        // rotina runControleDoJogo, que aguarda ESC e executa o tempo todo.
        // Isso permite que, ao inv�s de reiniciar o jogo, a pessoa saia do mesmo.
    }

    protected void runEstadoVoando() {
        // Posiciona o jogador relativo � posi��o do pterod�tilo.
        // Os valores abaixo foram escolhidos de forma a parecer que o jogador
        // � carregado pelo pterod�tilo.
        posJogador.x = posPtero.x + 50;
        posJogador.y = posPtero.y + 90;
        if (keyPool.get(KeyEvent.VK_SPACE) != null) {
            // Se tecla ESPACO est� pressionada, inicia o salto.
            // A velocidade horizontal do jogador � 10% maiopr que a velocidade
            // atual do pterod�tilo. Quanto mais r�pido o pterod�tilo, mais
            // r�pido o jogador.
            velJogador.x = velPtero * 1.1f;
            // A velocidade vertical (queda) do jogador � iniciada com 5px.
            velJogador.y = 5;
            // Muda o estado para CAINDO.
            estado = EST_CAINDO;
            // Inicia o som de caindo.
            sndCaindo.play();
        }
    }

    protected void runEstadoCaindo() {
        // Atualiza a posi��o do jogador com base em sua velocidade.
        posJogador.x += velJogador.x;
        posJogador.y += velJogador.y;
        if (velJogador.x > 0) {
            // Se a velocidade do jogador � maior do que zero,
            // diminui ela um pouco. Valor arbitr�rio.
            // Isso faz com que a valocidade horizontal seja freada durante a queda,
            // por�m evitando de ficar negativa (caso em que o jogador
            // voaria para tr�s)
            velJogador.x -= 0.2f;
        }
        // A velocidade vertical � sempre aumentada um pouco. Valor arbitr�rio.
        velJogador.y += 0.2f;
        if (posJogador.y > posAlvo.y) {
            // Se apois��o vertical do jogador pasou da altura do alvo na tela,
            // ent�o o jogador chegou ao ch�o.
            // Interrompe o som de caindo.
            sndCaindo.stop();
            // Ajuda a posi��o vertical para fica exatamente na linha do alvo.
            posJogador.y = posAlvo.y;
            // Verifica��o se o jogador est� dentro do alvo.
            // Para isso sua posi��o horizontal deve estar entre a esquerda e a
            // direita do mesmo. A posi��o direita do alvo � obtida somando sua
            // largura � posi��o esquerda.
            if (posAlvo.x < posJogador.x && posJogador.x < posAlvo.x
                    + largAlvo) {
                // Se est� sobre o alvo, muda o estado para acerto.
                estado = EST_ACERTO;
                // Executa o som de acerto.
                sndAcerto.play();
                // Adiciona 10 pontos.
                pontos += 10;
            } else {
                // Se est� fora do alvo, muda o estado para erro.
                estado = EST_ERRO;
                // Executa o som de erro.
                sndErro.play();
                // Diminui uma tentativa.
                tentativas--;
                if (tentativas == 0) {
                    // Se as tentativas chegaram a zero, muda o estado para final.
                    estado = EST_FINAL;
                    // Interrompe o som do pterod�tilo.
                    sndPterodatilo.stop();
                    // Posiciona o avim�o fora da tela, para n�o fica aparecendo
                    // na tela de final;
                    posPtero.x = getWidth();
                }
            }
        }
    }

    @Override
    public void onRender(Graphics2D g) {
        // Esta rotina � chamada a cada volta do game loop.
        // Foi dividida em rotinas conforme a parte a ser desenhada.
        // Desenha o cen�rio, pterod�tilo, jogador e alvo.
        renderJogo(g);
        // Desenha o Heads Up Display, que s�o as informa��es do jogo na tela.
        renderHUD(g);
        if (estado == EST_FINAL) {
            // Se est� no estado final, desenha a mensagem de final.
            renderMensagemFinal(g);
        }
    }

    protected void renderJogo(Graphics2D g) {
        // Desenha a imagem de fundo.
        g.drawImage(imgCenario, 0, 0, null);
        // Muda a cor para branco.
        g.setColor(Color.white);
        // Especifica uma composi��o com alpha de 30% (ou seja, 70% de transpar�ncia)
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                0.3f));
        // Preenche uma elipse na posi��o do alvo, observando sua largura e com
        // altura fixa de 10px. Ser� preenchida com a transpar�ncia acima.
        g.fillOval(posAlvo.x, posAlvo.y - 2, largAlvo, 10);
        // Especifica a composi��o normal (sem transpar�ncia).
        g.setPaintMode();
        // Desenha uma elipse na posi��o do alvo. Agorta � apenas o contorno.
        g.drawOval(posAlvo.x, posAlvo.y - 2, largAlvo, 10);
        // Declara um objeto para apontar para uma das imagens do jogador.
        BufferedImage img = null;
        switch (estado) {
            case EST_VOANDO:
                // Se esta no estado voando, n�o aponta para imagem alguma.
                img = imgCaindo;
                break;
            case EST_CAINDO:
                // Se est� no estado caindo, aponta para a imagem do paraquedas.
                img = imgCaindo;
                break;
            case EST_ACERTO:
                // Se est� no estado acerto, aponta para a imagem corespondente.
                img = imgAcerto;
                break;
            case EST_ERRO:
                // Se est� no estado erro, aponta para a imagem corespondente.
                img = imgErro;
                break;
        }
        if (img != null) {
            // Se img est� apontando para alguma imagem, desenha ela na posi��o
            // do jogador. � calculada a posi��o de forma a centralizar
            // horizontalmente a imagem na posi��o.
            g.drawImage(img, (int) posJogador.x - img.getWidth() / 2,
                    (int) posJogador.y - img.getHeight(), null);
        }
        // Desenha a imagem do pterod�tilo na sua posi��o.
        g.drawImage(imgPterodatilo, posPtero.x, posPtero.y, null);
    }

    protected void renderMensagemFinal(Graphics2D g) {
        // Muda a cor para vermelho.
        g.setColor(Color.red);
        // Muda a fonte para uma derivada de tamanho 44.
        g.setFont(fontCrimewave.deriveFont(Font.BOLD, 44f));
        // Escreve as mensagens na tela, em posi��es determinadas.
        g.drawString("Terminaram suas tentativas!", 120, 200);
        g.drawString("Voce fez " + pontos + " pontos.", 200, 250);
        // Muda a cor para branco.
        g.setColor(Color.white);
        // Muda a fonte para uma derivada de tamanho 24.
        g.setFont(fontCrimewave.deriveFont(Font.BOLD, 24f));
        // Escreve a mensagem sobre continuar o jogo.
        g.drawString("PRESSIONE [ENTER] PARA JOGAR NOVAMENTE", 150, 300);
    }

    protected void renderHUD(Graphics2D g) {
        // Muda a cor para amarelo.
        g.setColor(Color.yellow);
        // Muda a fonte para uma derivada de tamanho 44.
        g.setFont(fontCrimewave.deriveFont(Font.BOLD, 44f));
        // Escreve as informa��es na tela (pontos e tentativas).
        g.drawString(" PONTOS", getWidth() - 180, 40);
        g.drawString("" + pontos, getWidth() - 240, 40);
        g.drawString(tentativas + " TENTATIVAS", 20, 40);
    }

    public void keyTyped(KeyEvent e) {
        // Rotina n�o utilizada. Evento de tecla teclada.
    }

    public void keyPressed(KeyEvent e) {
        // Quando uma tecla � pressionada, adiciona ela no pool.
        keyPool.put(e.getKeyCode(), true);
    }

    public void keyReleased(KeyEvent e) {
        // Quando uma tecla � liberada, remove ela do pool.
        keyPool.remove(e.getKeyCode());
    }
}