package JogoPOO;

public class Main {

	//<-- p/ esquerda e --> p/ direita para movimentar a mo�a com o carrinho do supermercado
	//O objetivo pincipal � pegar as ma�as, o que aumentar� os pontos
	//Se ela pegar um hamburguer, o jogo termina
	//Se ela pegar uma garrafa d'gua, fica imune e pode encostar em um hamburguer por 2 vezes sem finalizar o jogo
	public static void main(String[] args) {
		Game myGame = new JogoMercado();
		myGame.run();

	}
}
