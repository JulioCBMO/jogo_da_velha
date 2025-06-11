## Jogo da Velha em Java

Este repositório contém a implementação do tradicional jogo da velha em Java, com versões utilizando tanto o JOptionPane para interação via diálogos quanto uma interface gráfica completa com Swing.

## Visão Geral
O projeto implementa o jogo da velha com as seguintes abordagens:

## Lógica do Jogo: 
A classe JogoDaVelha contém toda a lógica do jogo, tratando das regras, validação de jogadas, controle do tabuleiro, histórico de movimentos e definição do resultado (vitória, empate, ou jogo em andamento).

## Interface Gráfica Completa:
A classe TelaJogo, que estende JFrame, oferece uma interface gráfica interativa. Nela, o tabuleiro é exibido em um painel com labels que representam células clicáveis. Os controles para início, reinício, exibição do histórico e configuração do jogo (como escolha de símbolos e modo de jogo) são fornecidos através de botões, campos de texto e combo boxes.

## Recursos
Modos de Jogo:

Jogador vs. Jogador: Permitindo que dois jogadores joguem na mesma máquina.

Jogador vs. Máquina: Onde o usuário enfrenta a máquina, com dois níveis de dificuldade:

Fácil: A máquina escolhe jogadas aleatoriamente.

Difícil: A máquina utiliza uma estratégia inteligente para bloquear o jogador e buscar a vitória.

Histórico de Jogadas: Controle e exibição de cada movimento realizado durante o jogo.

Validação de Jogadas: Verifica se as posições escolhidas estão disponíveis e se os dados informados são válidos, evitando jogadas erradas.

Interface Gráfica (TelaJogo):

Células do Tabuleiro: Implementadas com JLabel em um painel com layout GridLayout (3x3), cada célula exibe o símbolo (ou espaço) e reage a cliques do usuário.

Controles e Configurações:

Campos (JTextField) para configurar os símbolos dos jogadores.

JComboBox para seleção do modo de jogo e do nível de dificuldade da máquina.

Botões (JButton) para iniciar, reiniciar o jogo e visualizar o histórico de jogadas.

Painéis Organizados: Utilização de JPanel para distribuir os componentes de forma organizada (tabuleiro, controles e informações).

Eventos de Clique: Cada célula do tabuleiro possui um listener que captura cliques do usuário para efetivar as jogadas quando o jogo está ativo.

## Estrutura do Projeto
JogoDaVelha.java: Gerencia toda a lógica do jogo: controle do tabuleiro, verificação de jogadas válidas, estratégia da máquina e monitoramento do histórico.

TelaJogo.java: Implementa a interface gráfica completa com Swing, possibilitando uma interação mais intuitiva e visual, com as seguintes características:

Inicializa e configura componentes gráficos (labels, botões, campos de texto, combo boxes e painéis).

Configura a disposição dos componentes utilizando diferentes layouts.

Disponibiliza eventos de clique para ações como "jogar", "iniciar" ou "reiniciar" o jogo e "ver histórico".

Permite a escolha entre os modos de jogo e o ajuste do nível de dificuldade para a máquina.

## Requisitos
Java Development Kit (JDK) 8 ou superior
