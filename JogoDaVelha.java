import java.util.*;

public class JogoDaVelha {
    // Atributos encapsulados
    private String[] celulas;
    private String[] simbolos;
    private LinkedHashMap<Integer, String> historico;
    private int quantidadeJogadas;
    private int nivelEsperteza;
    private boolean jogadorVsMaquina;
    
    // Construtor para 2 jogadores
    public JogoDaVelha(String simbolo1, String simbolo2) {
        this.celulas = new String[9];
        this.simbolos = new String[2];
        this.simbolos[0] = simbolo1;
        this.simbolos[1] = simbolo2;
        this.historico = new LinkedHashMap<>();
        this.quantidadeJogadas = 0;
        this.nivelEsperteza = 0;
        this.jogadorVsMaquina = false;
        inicializarCelulas();
    }
    
    // Construtor para jogador vs máquina
    public JogoDaVelha(String simboloJogador, int nivel) {
        this.celulas = new String[9];
        this.simbolos = new String[2];
        this.simbolos[0] = simboloJogador;
        this.simbolos[1] = "m"; // máquina sempre usa "m"
        this.historico = new LinkedHashMap<>();
        this.quantidadeJogadas = 0;
        this.nivelEsperteza = (nivel == 1 || nivel == 2) ? nivel : 1;
        this.jogadorVsMaquina = true;
        inicializarCelulas();
    }
    
    private void inicializarCelulas() {
        for (int i = 0; i < 9; i++) {
            celulas[i] = " ";
        }
    }
    
    public void jogaJogador(int numeroJogador, int posicao) {
        // Validação do número do jogador
        if (numeroJogador < 1 || numeroJogador > 2) {
            throw new IllegalArgumentException("Número do jogador deve ser 1 ou 2");
        }
        
        // Validação da posição
        if (posicao < 0 || posicao > 8) {
            throw new IllegalArgumentException("Posição deve estar entre 0 e 8");
        }
        
        // Verificar se a posição está livre
        if (!celulas[posicao].equals(" ")) {
            throw new IllegalArgumentException("Posição já ocupada");
        }
        
        // Efetuar a jogada
        String simbolo = simbolos[numeroJogador - 1];
        celulas[posicao] = simbolo;
        historico.put(posicao, simbolo);
        quantidadeJogadas++;
    }
    
    // Método responsável pela jogada da máquina (retorna a posição onde jogou)
    public int jogaMaquina() {
        if (!jogadorVsMaquina) {
            throw new IllegalStateException("Jogo não está configurado para jogador vs máquina");
        }
        
        int posicaoEscolhida;
        
        if (nivelEsperteza == 1) {
            // Nível baixo: escolha aleatória
            posicaoEscolhida = escolhaPosicaoAleatoria();
        } else {
            // Nível alto: estratégia inteligente
            posicaoEscolhida = escolhaPosicaoInteligente();
        }
        
        if (posicaoEscolhida != -1) {
            celulas[posicaoEscolhida] = simbolos[1]; // "m"
            historico.put(posicaoEscolhida, simbolos[1]);
            quantidadeJogadas++;
        }
        
        return posicaoEscolhida;
    }
    
    private int escolhaPosicaoAleatoria() {
        ArrayList<Integer> posicoesLivres = getPosicoesDisponiveis();
        if (posicoesLivres.isEmpty()) {
            return -1;
        }
        Random random = new Random();
        return posicoesLivres.get(random.nextInt(posicoesLivres.size()));
    }
    
    private int escolhaPosicaoInteligente() {
        // 1. Verificar se pode ganhar
        int posicaoVitoria = verificarPossibilidadeVitoria(simbolos[1]);
        if (posicaoVitoria != -1) {
            return posicaoVitoria;
        }
        
        // 2. Verificar se precisa bloquear o adversário
        int posicaoBloqueio = verificarPossibilidadeVitoria(simbolos[0]);
        if (posicaoBloqueio != -1) {
            return posicaoBloqueio;
        }
        
        // 3. Priorizar centro
        if (celulas[4].equals(" ")) {
            return 4;
        }
        
        // 4. Priorizar cantos
        int[] cantos = {0, 2, 6, 8};
        for (int canto : cantos) {
            if (celulas[canto].equals(" ")) {
                return canto;
            }
        }
        
        // 5. Escolher qualquer posição livre
        return escolhaPosicaoAleatoria();
    }
    
    private int verificarPossibilidadeVitoria(String simbolo) {
        // Linhas
        for (int i = 0; i < 3; i++) {
            int linha = i * 3;
            if (verificarSequencia(linha, linha + 1, linha + 2, simbolo)) {
                return obterPosicaoVazia(linha, linha + 1, linha + 2);
            }
        }
        
        // Colunas
        for (int i = 0; i < 3; i++) {
            if (verificarSequencia(i, i + 3, i + 6, simbolo)) {
                return obterPosicaoVazia(i, i + 3, i + 6);
            }
        }
        
        // Diagonais
        if (verificarSequencia(0, 4, 8, simbolo)) {
            return obterPosicaoVazia(0, 4, 8);
        }
        if (verificarSequencia(2, 4, 6, simbolo)) {
            return obterPosicaoVazia(2, 4, 6);
        }
        
        return -1;
    }
    
    private boolean verificarSequencia(int pos1, int pos2, int pos3, String simbolo) {
        int contador = 0;
        int vazias = 0;
        
        if (celulas[pos1].equals(simbolo)) contador++;
        else if (celulas[pos1].equals(" ")) vazias++;
        
        if (celulas[pos2].equals(simbolo)) contador++;
        else if (celulas[pos2].equals(" ")) vazias++;
        
        if (celulas[pos3].equals(simbolo)) contador++;
        else if (celulas[pos3].equals(" ")) vazias++;
        
        return contador == 2 && vazias == 1;
    }
    
    private int obterPosicaoVazia(int pos1, int pos2, int pos3) {
        if (celulas[pos1].equals(" ")) return pos1;
        if (celulas[pos2].equals(" ")) return pos2;
        if (celulas[pos3].equals(" ")) return pos3;
        return -1;
    }
    
    public boolean terminou() {
        return getResultado() != -1;
    }
    
    public int getResultado() {
        // Verificar vitória do jogador 1
        if (verificarVitoria(simbolos[0])) {
            return 1;
        }
        
        // Verificar vitória do jogador 2/máquina
        if (verificarVitoria(simbolos[1])) {
            return 2;
        }
        
        // Verificar empate (todas as posições preenchidas)
        if (quantidadeJogadas == 9) {
            return 0;
        }
        
        // Jogo ainda em andamento
        return -1;
    }
    
    private boolean verificarVitoria(String simbolo) {
        // Verificar linhas
        for (int i = 0; i < 3; i++) {
            int linha = i * 3;
            if (celulas[linha].equals(simbolo) && 
                celulas[linha + 1].equals(simbolo) && 
                celulas[linha + 2].equals(simbolo)) {
                return true;
            }
        }
        
        // Verificar colunas
        for (int i = 0; i < 3; i++) {
            if (celulas[i].equals(simbolo) && 
                celulas[i + 3].equals(simbolo) && 
                celulas[i + 6].equals(simbolo)) {
                return true;
            }
        }
        
        // Verificar diagonais
        if (celulas[0].equals(simbolo) && celulas[4].equals(simbolo) && celulas[8].equals(simbolo)) {
            return true;
        }
        if (celulas[2].equals(simbolo) && celulas[4].equals(simbolo) && celulas[6].equals(simbolo)) {
            return true;
        }
        
        return false;
    }
    
    public String getSimbolo(int numeroJogador) {
        if (numeroJogador < 1 || numeroJogador > 2) {
            throw new IllegalArgumentException("Número do jogador deve ser 1 ou 2");
        }
        return simbolos[numeroJogador - 1];
    }
    
    public String getFoto() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 9; i++) {
            sb.append(celulas[i]);
            if ((i + 1) % 3 == 0) {
                sb.append("\n");
            } else {
                sb.append("|");
            }
        }
        return sb.toString();
    }
    
    public ArrayList<Integer> getPosicoesDisponiveis() {
        ArrayList<Integer> posicoesLivres = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            if (celulas[i].equals(" ")) {
                posicoesLivres.add(i);
            }
        }
        return posicoesLivres;
    }
    
    public LinkedHashMap<Integer, String> getHistorico() {
        return new LinkedHashMap<>(historico);
    }
    
    // Métodos auxiliares para a interface
    public int getQuantidadeJogadas() {
        return quantidadeJogadas;
    }
    
    public boolean isJogadorVsMaquina() {
        return jogadorVsMaquina;
    }
    
    public String getCelula(int posicao) {
        if (posicao < 0 || posicao > 8) {
            throw new IllegalArgumentException("Posição deve estar entre 0 e 8");
        }
        return celulas[posicao];
    }
}
