import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class TelaJogo extends JFrame {
    private JogoDaVelha jogo;
    private JLabel[] labelsCelulas;
    private JTextField textFieldSimbolo1;
    private JTextField textFieldSimbolo2;
    private JLabel labelInformacoes;
    private JLabel labelResultado;
    private JLabel labelJogadas;
    private JButton buttonIniciar;
    private JButton buttonReiniciar;
    private JButton buttonHistorico;
    private JComboBox<String> comboModoJogo;
    private JComboBox<String> comboNivelMaquina;
    private JPanel panelTabuleiro;
    private JPanel panelControles;
    private JPanel panelInfo;
    
    private boolean jogoAtivo = false;
    private int jogadorAtual = 1;
    
    public TelaJogo() {
        inicializarComponentes();
        configurarLayout();
        configurarEventos();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Jogo da Velha");
        setResizable(false);
        pack();
        setLocationRelativeTo(null);
    }
    
    private void inicializarComponentes() {
        // Células do tabuleiro
        labelsCelulas = new JLabel[9];
        for (int i = 0; i < 9; i++) {
            labelsCelulas[i] = new JLabel(" ", SwingConstants.CENTER);
            labelsCelulas[i].setFont(new Font("Arial", Font.BOLD, 40));
            labelsCelulas[i].setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
            labelsCelulas[i].setOpaque(true);
            labelsCelulas[i].setBackground(Color.WHITE);
            labelsCelulas[i].setPreferredSize(new Dimension(80, 80));
            
            // Adicionar listener para cliques
            final int posicao = i;
            labelsCelulas[i].addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    if (jogoAtivo) {
                        realizarJogada(posicao);
                    }
                }
            });
        }
        
        // Controles
        textFieldSimbolo1 = new JTextField("X", 3);
        textFieldSimbolo2 = new JTextField("O", 3);
        
        String[] modos = {"Jogador vs Jogador", "Jogador vs Máquina"};
        comboModoJogo = new JComboBox<>(modos);
        
        String[] niveis = {"Fácil", "Difícil"};
        comboNivelMaquina = new JComboBox<>(niveis);
        
        buttonIniciar = new JButton("Iniciar Jogo");
        buttonReiniciar = new JButton("Reiniciar");
        buttonReiniciar.setEnabled(false);
        buttonHistorico = new JButton("Ver Histórico");
        buttonHistorico.setEnabled(false);
        
        // Informações
        labelInformacoes = new JLabel("Configure o jogo e clique em Iniciar");
        labelResultado = new JLabel(" ");
        labelJogadas = new JLabel("Jogadas: 0");
        
        // Painéis
        panelTabuleiro = new JPanel(new GridLayout(3, 3, 2, 2));
        panelTabuleiro.setBackground(Color.BLACK);
        
        panelControles = new JPanel();
        panelInfo = new JPanel();
    }
    
    private void configurarLayout() {
        setLayout(new BorderLayout());
        
        // Tabuleiro
        for (JLabel label : labelsCelulas) {
            panelTabuleiro.add(label);
        }
        
        // Controles
        panelControles.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        gbc.gridx = 0; gbc.gridy = 0;
        panelControles.add(new JLabel("Modo:"), gbc);
        gbc.gridx = 1;
        panelControles.add(comboModoJogo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panelControles.add(new JLabel("Símbolo Jogador 1:"), gbc);
        gbc.gridx = 1;
        panelControles.add(textFieldSimbolo1, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        panelControles.add(new JLabel("Símbolo Jogador 2:"), gbc);
        gbc.gridx = 1;
        panelControles.add(textFieldSimbolo2, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        panelControles.add(new JLabel("Nível Máquina:"), gbc);
        gbc.gridx = 1;
        panelControles.add(comboNivelMaquina, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        panelControles.add(buttonIniciar, gbc);
        
        gbc.gridy = 5;
        panelControles.add(buttonReiniciar, gbc);
        
        gbc.gridy = 6;
        panelControles.add(buttonHistorico, gbc);
        
        // Informações
        panelInfo.setLayout(new BoxLayout(panelInfo, BoxLayout.Y_AXIS));
        panelInfo.add(labelInformacoes);
        panelInfo.add(labelJogadas);
        panelInfo.add(labelResultado);
        
        // Layout principal
        add(panelTabuleiro, BorderLayout.CENTER);
        add(panelControles, BorderLayout.WEST);
        add(panelInfo, BorderLayout.SOUTH);
        
        // Atualizar visibilidade dos controles
        atualizarVisibilidadeControles();
    }
    
    private void configurarEventos() {
        buttonIniciar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                iniciarJogo();
            }
        });
        
        buttonReiniciar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reiniciarJogo();
            }
        });
        
        buttonHistorico.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostrarHistorico();
            }
        });
        
        comboModoJogo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                atualizarVisibilidadeControles();
            }
        });
    }
    
    private void atualizarVisibilidadeControles() {
        boolean isVsMaquina = comboModoJogo.getSelectedIndex() == 1;
        textFieldSimbolo2.setEnabled(!isVsMaquina);
        comboNivelMaquina.setEnabled(isVsMaquina);
        
        if (isVsMaquina) {
            textFieldSimbolo2.setText("m");
        } else {
            textFieldSimbolo2.setText("O");
        }
    }
    
    private void iniciarJogo() {
        try {
            String simbolo1 = textFieldSimbolo1.getText().trim();
            if (simbolo1.isEmpty()) simbolo1 = "X";
            
            if (comboModoJogo.getSelectedIndex() == 0) {
                // Jogador vs Jogador
                String simbolo2 = textFieldSimbolo2.getText().trim();
                if (simbolo2.isEmpty()) simbolo2 = "O";
                jogo = new JogoDaVelha(simbolo1, simbolo2);
            } else {
                // Jogador vs Máquina
                int nivel = comboNivelMaquina.getSelectedIndex() + 1;
                jogo = new JogoDaVelha(simbolo1, nivel);
            }
            
            jogoAtivo = true;
            jogadorAtual = 1;
            
            // Atualizar interface
            limparTabuleiro();
            atualizarInformacoes();
            
            buttonIniciar.setEnabled(false);
            buttonReiniciar.setEnabled(true);
            
            // Desabilitar controles durante o jogo
            comboModoJogo.setEnabled(false);
            textFieldSimbolo1.setEnabled(false);
            textFieldSimbolo2.setEnabled(false);
            comboNivelMaquina.setEnabled(false);
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao iniciar o jogo: " + ex.getMessage());
        }
    }
    
    private void reiniciarJogo() {
        jogoAtivo = false;
        jogo = null;
        jogadorAtual = 1;
        
        limparTabuleiro();
        
        buttonIniciar.setEnabled(true);
        buttonReiniciar.setEnabled(false);
        buttonHistorico.setEnabled(false);
        
        // Reabilitar controles
        comboModoJogo.setEnabled(true);
        textFieldSimbolo1.setEnabled(true);
        atualizarVisibilidadeControles();
        
        labelInformacoes.setText("Configure o jogo e clique em Iniciar");
        labelResultado.setText(" ");
        labelJogadas.setText("Jogadas: 0");
    }
    
    private void realizarJogada(int posicao) {
        if (!jogoAtivo || jogo == null) return;
        
        try {
            // Jogada do jogador humano
            jogo.jogaJogador(jogadorAtual, posicao);
            atualizarTabuleiro();
            atualizarInformacoes();
            
            // Verificar se o jogo terminou
            if (jogo.terminou()) {
                finalizarJogo();
                return;
            }
            
            // Se for modo vs máquina e for a vez da máquina
            if (jogo.isJogadorVsMaquina() && jogadorAtual == 1) {
                jogadorAtual = 2;
                
                // Pequeno delay para melhor experiência
                Timer timer = new Timer(500, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        jogo.jogaMaquina();
                        atualizarTabuleiro();
                        atualizarInformacoes();
                        
                        if (jogo.terminou()) {
                            finalizarJogo();
                        } else {
                            jogadorAtual = 1;
                        }
                    }
                });
                timer.setRepeats(false);
                timer.start();
            } else {
                // Alternar jogador (modo jogador vs jogador)
                jogadorAtual = (jogadorAtual == 1) ? 2 : 1;
            }
            
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }
    
    private void atualizarTabuleiro() {
        for (int i = 0; i < 9; i++) {
            String conteudo = jogo.getCelula(i);
            labelsCelulas[i].setText(conteudo.equals(" ") ? " " : conteudo);
            
            // Colorir células baseado no símbolo
            if (!conteudo.equals(" ")) {
                if (conteudo.equals(jogo.getSimbolo(1))) {
                    labelsCelulas[i].setBackground(new Color(173, 216, 230)); // Azul claro
                } else {
                    labelsCelulas[i].setBackground(new Color(255, 182, 193)); // Rosa claro
                }
            } else {
                labelsCelulas[i].setBackground(Color.WHITE);
            }
        }
    }
    
    private void limparTabuleiro() {
        for (JLabel label : labelsCelulas) {
            label.setText(" ");
            label.setBackground(Color.WHITE);
        }
    }
    
    private void atualizarInformacoes() {
        if (jogo == null) return;
        
        labelJogadas.setText("Jogadas: " + jogo.getQuantidadeJogadas());
        
        if (jogoAtivo) {
            if (jogo.isJogadorVsMaquina()) {
                if (jogadorAtual == 1) {
                    labelInformacoes.setText("Sua vez! Símbolo: " + jogo.getSimbolo(1));
                } else {
                    labelInformacoes.setText("Vez da máquina...");
                }
            } else {
                labelInformacoes.setText("Vez do Jogador " + jogadorAtual + 
                                       " (Símbolo: " + jogo.getSimbolo(jogadorAtual) + ")");
            }
        }
    }
    
    private void finalizarJogo() {
        jogoAtivo = false;
        int resultado = jogo.getResultado();
        
        String mensagem;
        switch (resultado) {
            case 0:
                mensagem = "Empate!";
                break;
            case 1:
                mensagem = jogo.isJogadorVsMaquina() ? "Você ganhou!" : 
                          "Jogador 1 (" + jogo.getSimbolo(1) + ") ganhou!";
                break;
            case 2:
                mensagem = jogo.isJogadorVsMaquina() ? "A máquina ganhou!" : 
                          "Jogador 2 (" + jogo.getSimbolo(2) + ") ganhou!";
                break;
            default:
                mensagem = "Jogo finalizado";
        }
        
        labelResultado.setText(mensagem);
        labelInformacoes.setText("Jogo finalizado! Clique em 'Ver Histórico' para revisar as jogadas.");
        
        // Habilitar botão de histórico
        buttonHistorico.setEnabled(true);
        
        // Destacar células vencedoras se houver vitória
        if (resultado == 1 || resultado == 2) {
            destacarSequenciaVencedora();
        }
    }
    
    private void destacarSequenciaVencedora() {
        String simboloVencedor = (jogo.getResultado() == 1) ? jogo.getSimbolo(1) : jogo.getSimbolo(2);
        
        // Verificar linhas
        for (int i = 0; i < 3; i++) {
            int linha = i * 3;
            if (jogo.getCelula(linha).equals(simboloVencedor) && 
                jogo.getCelula(linha + 1).equals(simboloVencedor) && 
                jogo.getCelula(linha + 2).equals(simboloVencedor)) {
                
                labelsCelulas[linha].setBackground(Color.GREEN);
                labelsCelulas[linha + 1].setBackground(Color.GREEN);
                labelsCelulas[linha + 2].setBackground(Color.GREEN);
                return;
            }
        }
        
        // Verificar colunas
        for (int i = 0; i < 3; i++) {
            if (jogo.getCelula(i).equals(simboloVencedor) && 
                jogo.getCelula(i + 3).equals(simboloVencedor) && 
                jogo.getCelula(i + 6).equals(simboloVencedor)) {
                
                labelsCelulas[i].setBackground(Color.GREEN);
                labelsCelulas[i + 3].setBackground(Color.GREEN);
                labelsCelulas[i + 6].setBackground(Color.GREEN);
                return;
            }
        }
        
        // Verificar diagonais
        if (jogo.getCelula(0).equals(simboloVencedor) && 
            jogo.getCelula(4).equals(simboloVencedor) && 
            jogo.getCelula(8).equals(simboloVencedor)) {
            
            labelsCelulas[0].setBackground(Color.GREEN);
            labelsCelulas[4].setBackground(Color.GREEN);
            labelsCelulas[8].setBackground(Color.GREEN);
            return;
        }
        
        if (jogo.getCelula(2).equals(simboloVencedor) && 
            jogo.getCelula(4).equals(simboloVencedor) && 
            jogo.getCelula(6).equals(simboloVencedor)) {
            
            labelsCelulas[2].setBackground(Color.GREEN);
            labelsCelulas[4].setBackground(Color.GREEN);
            labelsCelulas[6].setBackground(Color.GREEN);
        }
    }
    
    private void mostrarHistorico() {
        if (jogo == null) return;
        
        // Criar janela do histórico
        JDialog dialogHistorico = new JDialog(this, "Histórico do Jogo", true);
        dialogHistorico.setSize(500, 400);
        dialogHistorico.setLocationRelativeTo(this);
        
        // Criar componentes
        JTextArea textAreaHistorico = new JTextArea();
        textAreaHistorico.setEditable(false);
        textAreaHistorico.setFont(new Font("Courier New", Font.PLAIN, 12));
        textAreaHistorico.setMargin(new Insets(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(textAreaHistorico);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        // Construir texto do histórico
        StringBuilder sb = new StringBuilder();
        String textoHistorico = construirTextoHistorico();
        
        textAreaHistorico.setText(textoHistorico);
        textAreaHistorico.setCaretPosition(0); // Rolar para o topo
        
        // Botões
        JButton buttonFechar = new JButton("Fechar");
        buttonFechar.addActionListener(e -> dialogHistorico.dispose());
        
        JButton buttonSalvar = new JButton("Salvar Histórico");
        buttonSalvar.addActionListener(e -> salvarHistorico(textoHistorico));
        
        JPanel panelBotoes = new JPanel(new FlowLayout());
        panelBotoes.add(buttonSalvar);
        panelBotoes.add(buttonFechar);
        
        // Layout da janela
        dialogHistorico.setLayout(new BorderLayout());
        dialogHistorico.add(scrollPane, BorderLayout.CENTER);
        dialogHistorico.add(panelBotoes, BorderLayout.SOUTH);
        
        dialogHistorico.setVisible(true);
    }
    
    private String construirTextoHistorico() {
        StringBuilder sb = new StringBuilder();
        
        // Data e hora
        LocalDateTime agora = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        sb.append("========== HISTÓRICO DO JOGO ==========\n");
        sb.append("Data/Hora: ").append(agora.format(formatter)).append("\n\n");
        
        // Informações gerais
        sb.append("Modo de Jogo: ");
        if (jogo.isJogadorVsMaquina()) {
            sb.append("Jogador vs Máquina\n");
            sb.append("Nível da Máquina: ").append(comboNivelMaquina.getSelectedItem()).append("\n");
        } else {
            sb.append("Jogador vs Jogador\n");
        }
        
        sb.append("Símbolo Jogador 1: ").append(jogo.getSimbolo(1)).append("\n");
        sb.append("Símbolo Jogador 2").append(jogo.isJogadorVsMaquina() ? "/Máquina" : "")
          .append(": ").append(jogo.getSimbolo(2)).append("\n");
        
        sb.append("Total de Jogadas: ").append(jogo.getQuantidadeJogadas()).append("\n");
        
        // Resultado
        int resultado = jogo.getResultado();
        sb.append("Resultado: ");
        switch (resultado) {
            case 0:
                sb.append("Empate");
                break;
            case 1:
                sb.append("Vitória do Jogador 1 (").append(jogo.getSimbolo(1)).append(")");
                break;
            case 2:
                if (jogo.isJogadorVsMaquina()) {
                    sb.append("Vitória da Máquina (").append(jogo.getSimbolo(2)).append(")");
                } else {
                    sb.append("Vitória do Jogador 2 (").append(jogo.getSimbolo(2)).append(")");
                }
                break;
        }
        sb.append("\n\n");
        
        // Histórico de jogadas
        sb.append("========== SEQUÊNCIA DE JOGADAS ==========\n\n");
        
        LinkedHashMap<Integer, String> historico = jogo.getHistorico();
        int jogadaNumero = 1;
        
        for (Map.Entry<Integer, String> entrada : historico.entrySet()) {
            int posicao = entrada.getKey();
            String simbolo = entrada.getValue();
            
            // Determinar qual jogador fez a jogada
            String jogador;
            if (simbolo.equals(jogo.getSimbolo(1))) {
                jogador = "Jogador 1";
            } else {
                jogador = jogo.isJogadorVsMaquina() ? "Máquina" : "Jogador 2";
            }
            
            // Converter posição para coordenadas (linha, coluna)
            int linha = posicao / 3 + 1;
            int coluna = posicao % 3 + 1;
            
            sb.append(String.format("Jogada %2d: %s colocou '%s' na posição %d (linha %d, coluna %d)\n", 
                     jogadaNumero, jogador, simbolo, posicao, linha, coluna));
            
            jogadaNumero++;
        }
        
        // Estado final do tabuleiro
        sb.append("\n========== TABULEIRO FINAL ==========\n\n");
        sb.append(formatarTabuleiro());
        
        // Posições que estavam disponíveis (caso o jogo não tenha terminado em empate)
        ArrayList<Integer> posicoesDisponiveis = jogo.getPosicoesDisponiveis();
        if (!posicoesDisponiveis.isEmpty()) {
            sb.append("\nPosições ainda disponíveis: ");
            for (int i = 0; i < posicoesDisponiveis.size(); i++) {
                sb.append(posicoesDisponiveis.get(i));
                if (i < posicoesDisponiveis.size() - 1) {
                    sb.append(", ");
                }
            }
            sb.append("\n");
        }
        
        return sb.toString();
    }
    
    private void salvarHistorico(String textoHistorico) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Salvar Histórico do Jogo");
        
        // Sugerir nome do arquivo baseado na data/hora
        LocalDateTime agora = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        String nomeArquivo = "historico_jogo_velha_" + agora.format(formatter) + ".txt";
        fileChooser.setSelectedFile(new java.io.File(nomeArquivo));
        
        int resultado = fileChooser.showSaveDialog(this);
        
        if (resultado == JFileChooser.APPROVE_OPTION) {
            try {
                FileWriter writer = new FileWriter(fileChooser.getSelectedFile());
                writer.write(textoHistorico);
                writer.close();
                
                JOptionPane.showMessageDialog(this, 
                    "Histórico salvo com sucesso!\nArquivo: " + fileChooser.getSelectedFile().getName(),
                    "Salvo", JOptionPane.INFORMATION_MESSAGE);
                    
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, 
                    "Erro ao salvar o arquivo: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private String formatarTabuleiro() {
        StringBuilder sb = new StringBuilder();
        sb.append("   0   1   2\n");
        sb.append(" ┌───┬───┬───┐\n");
        
        for (int linha = 0; linha < 3; linha++) {
            sb.append(linha).append("│");
            for (int coluna = 0; coluna < 3; coluna++) {
                int posicao = linha * 3 + coluna;
                String conteudo = jogo.getCelula(posicao);
                if (conteudo.equals(" ")) {
                    conteudo = " ";
                }
                sb.append(" ").append(conteudo).append(" │");
            }
            sb.append("\n");
            
            if (linha < 2) {
                sb.append(" ├───┼───┼───┤\n");
            }
        }
        
        sb.append(" └───┴───┴───┘\n");
        return sb.toString();
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                new TelaJogo().setVisible(true);
            }
        });
    }
}