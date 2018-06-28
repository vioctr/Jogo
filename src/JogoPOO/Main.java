package JogoPOO;

public class Main {

	//<-- p/ esquerda e --> p/ direita para movimentar a moça com o carrinho do supermercado
	//O objetivo pincipal é pegar as maças, o que aumentará os pontos
	//Se ela pegar um hamburguer, o jogo termina
	//Se ela pegar uma garrafa d'gua, fica imune e pode encostar em um hamburguer por 2 vezes sem finalizar o jogo
	public static void main(String[] args) {
		Game myGame = new JogoMercado();
		myGame.run();

	}
}
