// Pesquisa Swing GUI Assist ---> Recomendação do Matheus para parar de sofrer com muitas linhas de código >:)
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class Main {
    private JFrame frame;
    private JTextField nomeField;
    private JTextField telefoneField;
    private JTextField emailField;
    private JComboBox<String> statusCombo;
    private JComboBox<String> tipoTelefoneCombo;
    private JCheckBox telefoneCheckBox;
    private JCheckBox emailCheckBox;
    private JButton salvarButton;
    private JButton excluirButton;
    private JButton listarOrdemAdicaoButton;
    private JButton listarOrdemAlfabeticaButton;
    private JButton abrirBlocoNotasButton;
    private JButton abrirCalendarioButton;
    private JTextArea outputArea;

    private static final String ARQUIVO_CSV = "contatos.csv";

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                Main window = new Main();
                window.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public Main() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Agenda de Contatos");
        frame.setBounds(100, 100, 750, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout(0, 0));

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(10, 2, 5, 5));
        frame.getContentPane().add(inputPanel, BorderLayout.NORTH);

        // Campos do formulário
        inputPanel.add(new JLabel("Nome:"));
        nomeField = new JTextField();
        inputPanel.add(nomeField);

        inputPanel.add(new JLabel("Telefone:"));
        JPanel telefonePanel = new JPanel(new BorderLayout(5, 5));
        telefoneCheckBox = new JCheckBox();
        telefoneCheckBox.setSelected(true);
        telefoneCheckBox.addActionListener(e -> toggleTelefoneField());

        JPanel telefoneSubPanel = new JPanel(new BorderLayout(5, 5));
        tipoTelefoneCombo = new JComboBox<>(new String[] {"Celular", "Fixo"});
        tipoTelefoneCombo.setPreferredSize(new Dimension(80, 20));
        telefoneField = new JTextField();

        telefoneSubPanel.add(tipoTelefoneCombo, BorderLayout.WEST);
        telefoneSubPanel.add(telefoneField, BorderLayout.CENTER);

        telefonePanel.add(telefoneCheckBox, BorderLayout.WEST);
        telefonePanel.add(telefoneSubPanel, BorderLayout.CENTER);
        inputPanel.add(telefonePanel);

        inputPanel.add(new JLabel("E-mail:"));
        JPanel emailPanel = new JPanel(new BorderLayout(5, 5));
        emailCheckBox = new JCheckBox();
        emailCheckBox.setSelected(true);
        emailCheckBox.addActionListener(e -> toggleEmailField());
        emailField = new JTextField();

        emailPanel.add(emailCheckBox, BorderLayout.WEST);
        emailPanel.add(emailField, BorderLayout.CENTER);
        inputPanel.add(emailPanel);

        inputPanel.add(new JLabel("Status:"));
        statusCombo = new JComboBox<>(new String[] {"Estudante", "Docente", "Direção", "Instituição"});
        inputPanel.add(statusCombo);

        // Botões
        salvarButton = new JButton("Salvar Contato");
        salvarButton.addActionListener(e -> salvarContato());
        inputPanel.add(salvarButton);

        excluirButton = new JButton("Excluir Contato");
        excluirButton.addActionListener(e -> excluirContato());
        inputPanel.add(excluirButton);

        listarOrdemAdicaoButton = new JButton("Listar por Ordem de Adição");
        listarOrdemAdicaoButton.addActionListener(e -> listarContatos(false));
        inputPanel.add(listarOrdemAdicaoButton);

        listarOrdemAlfabeticaButton = new JButton("Listar por Ordem Alfabética");
        listarOrdemAlfabeticaButton.addActionListener(e -> listarContatos(true));
        inputPanel.add(listarOrdemAlfabeticaButton);

        abrirBlocoNotasButton = new JButton("Abrir Bloco de Notas");
        abrirBlocoNotasButton.addActionListener(e -> abrirBlocoDeNotas());
        inputPanel.add(abrirBlocoNotasButton);

        // Botão do Calendário
        abrirCalendarioButton = new JButton("Abrir Calendário");
        abrirCalendarioButton.addActionListener(e -> abrirCalendario());
        inputPanel.add(abrirCalendarioButton);

        // Área de saída
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);

        verificarArquivoCSV();
    }

    private void abrirCalendario() {
        JFrame calendarioFrame = new JFrame("Calendário");
        calendarioFrame.setSize(400, 400);
        calendarioFrame.setLocationRelativeTo(frame);

        JPanel panel = new JPanel(new BorderLayout());
        JLabel monthLabel = new JLabel("", SwingConstants.CENTER);
        JButton prevButton = new JButton("<");
        JButton nextButton = new JButton(">");

        Calendar calendar = Calendar.getInstance();
        updateMonthLabel(monthLabel, calendar);

        prevButton.addActionListener(evt -> {
            calendar.add(Calendar.MONTH, -1);
            updateMonthLabel(monthLabel, calendar);
            updateDaysPanel(panel, calendar, monthLabel);
        });

        nextButton.addActionListener(evt -> {
            calendar.add(Calendar.MONTH, 1);
            updateMonthLabel(monthLabel, calendar);
            updateDaysPanel(panel, calendar, monthLabel);
        });

        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.add(prevButton, BorderLayout.WEST);
        buttonPanel.add(nextButton, BorderLayout.EAST);

        panel.add(monthLabel, BorderLayout.NORTH);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        updateDaysPanel(panel, calendar, monthLabel);
        calendarioFrame.add(panel);
        calendarioFrame.setVisible(true);
    }

    private void updateDaysPanel(JPanel mainPanel, Calendar calendar, JLabel monthLabel) {
        // Remove o painel de dias antigo se existir
        Component oldDaysPanel = null;
        for (Component c : mainPanel.getComponents()) {
            if (c instanceof JPanel && "daysPanel".equals(c.getName())) {
                oldDaysPanel = c;
                break;
            }
        }
        if (oldDaysPanel != null) {
            mainPanel.remove(oldDaysPanel);
        }

        // Cria novo painel de dias
        JPanel daysPanel = new JPanel(new GridLayout(0, 7));
        daysPanel.setName("daysPanel");

        String[] daysOfWeek = {"Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sáb"};
        for (String day : daysOfWeek) {
            JLabel label = new JLabel(day, SwingConstants.CENTER);
            label.setFont(label.getFont().deriveFont(Font.BOLD));
            daysPanel.add(label);
        }

        Calendar tempCalendar = (Calendar) calendar.clone();
        tempCalendar.set(Calendar.DAY_OF_MONTH, 1);
        int firstDayOfWeek = tempCalendar.get(Calendar.DAY_OF_WEEK);
        int daysInMonth = tempCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        // Espaços vazios para os dias antes do primeiro dia do mês
        for (int i = 1; i < firstDayOfWeek; i++) {
            daysPanel.add(new JLabel(""));
        }

        // Adiciona os dias do mês
        for (int i = 1; i <= daysInMonth; i++) {
            final int day = i;
            JButton dayButton = new JButton(String.valueOf(i));
            dayButton.addActionListener(evt -> {
                String dataSelecionada = String.format("%02d/%02d/%04d",
                        day, tempCalendar.get(Calendar.MONTH) + 1, tempCalendar.get(Calendar.YEAR));
                JOptionPane.showMessageDialog(null,
                        "Data selecionada: " + dataSelecionada,
                        "Data", JOptionPane.INFORMATION_MESSAGE);
            });
            daysPanel.add(dayButton);
        }

        mainPanel.add(daysPanel, BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private void updateMonthLabel(JLabel label, Calendar calendar) {
        String[] months = {"Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
                "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"};
        label.setText(months[calendar.get(Calendar.MONTH)] + " " + calendar.get(Calendar.YEAR));
        label.setFont(label.getFont().deriveFont(Font.BOLD, 16));
    }

    private void abrirBlocoDeNotas() {
        SwingUtilities.invokeLater(() -> {
            JFrame blocoFrame = new JFrame("Bloco de Notas");
            blocoFrame.setSize(500, 400);

            JTextArea textArea = new JTextArea();
            JScrollPane scrollPane = new JScrollPane(textArea);

            JMenuBar menuBar = new JMenuBar();
            JMenu fileMenu = new JMenu("Arquivo");

            JMenuItem saveItem = new JMenuItem("Salvar");
            saveItem.addActionListener(e -> salvarNotas(textArea));

            JMenuItem openItem = new JMenuItem("Abrir");
            openItem.addActionListener(e -> abrirNotas(textArea));

            fileMenu.add(saveItem);
            fileMenu.add(openItem);
            menuBar.add(fileMenu);

            blocoFrame.setJMenuBar(menuBar);
            blocoFrame.add(scrollPane);
            blocoFrame.setVisible(true);
        });
    }

    private void salvarNotas(JTextArea textArea) {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            try (PrintWriter writer = new PrintWriter(fileChooser.getSelectedFile())) {
                writer.write(textArea.getText());
                JOptionPane.showMessageDialog(null, "Arquivo salvo com sucesso!");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Erro ao salvar arquivo: " + e.getMessage(),
                        "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void abrirNotas(JTextArea textArea) {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            try (BufferedReader reader = new BufferedReader(new FileReader(fileChooser.getSelectedFile()))) {
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
                textArea.setText(content.toString());
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Erro ao abrir arquivo: " + e.getMessage(),
                        "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void toggleTelefoneField() {
        telefoneField.setEnabled(telefoneCheckBox.isSelected());
        tipoTelefoneCombo.setEnabled(telefoneCheckBox.isSelected());
        if (!telefoneCheckBox.isSelected()) {
            telefoneField.setText("");
        }
    }

    private void toggleEmailField() {
        emailField.setEnabled(emailCheckBox.isSelected());
        if (!emailCheckBox.isSelected()) {
            emailField.setText("");
        }
    }

    private void verificarArquivoCSV() {
        File arquivo = new File(ARQUIVO_CSV);
        if (!arquivo.exists()) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(ARQUIVO_CSV))) {
                writer.println("Nome,TipoTelefone,Telefone,Email,Status");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(frame, "Erro ao criar arquivo CSV: " + e.getMessage(),
                        "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void salvarContato() {
        String nome = nomeField.getText().trim();
        String tipoTelefone = telefoneCheckBox.isSelected() ? (String) tipoTelefoneCombo.getSelectedItem() : "";
        String telefone = telefoneCheckBox.isSelected() ? telefoneField.getText().trim() : "";
        String email = emailCheckBox.isSelected() ? emailField.getText().trim() : "";
        String status = (String) statusCombo.getSelectedItem();

        if (nome.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "O nome é obrigatório!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (telefone.isEmpty() && email.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Pelo menos um telefone ou e-mail deve ser fornecido!",
                    "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (telefoneCheckBox.isSelected() && !telefone.isEmpty()) {
            String regex = tipoTelefone.equals("Celular") ? "^\\(?\\d{2}\\)?[\\s-]?\\d{5}[\\s-]?\\d{4}$" : "^\\(?\\d{2}\\)?[\\s-]?\\d{4}[\\s-]?\\d{4}$";
            if (!telefone.matches(regex)) {
                JOptionPane.showMessageDialog(frame,
                        tipoTelefone.equals("Celular") ? "Formato de celular inválido! Use (XX) XXXXX-XXXX" : "Formato de telefone fixo inválido! Use (XX) XXXX-XXXX",
                        "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        if (emailCheckBox.isSelected() && !email.isEmpty()) {
            String regex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
            if (!email.matches(regex)) {
                JOptionPane.showMessageDialog(frame, "Formato de e-mail inválido!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        if (telefoneCheckBox.isSelected() && !telefone.isEmpty()) {
            telefone = telefone.replaceAll("[^0-9]", "");
            if (tipoTelefone.equals("Celular")) {
                telefone = String.format("(%s) %s-%s",
                        telefone.substring(0, 2),
                        telefone.substring(2, 7),
                        telefone.substring(7));
            } else {
                telefone = String.format("(%s) %s-%s",
                        telefone.substring(0, 2),
                        telefone.substring(2, 6),
                        telefone.substring(6));
            }
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(ARQUIVO_CSV, true))) {
            writer.println(String.format("%s,%s,%s,%s,%s", nome, tipoTelefone, telefone, email, status));
            JOptionPane.showMessageDialog(frame, "Contato salvo com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            limparCampos();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Erro ao salvar contato: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void listarContatos(boolean ordemAlfabetica) {
        List<String[]> contatos = lerContatosDoArquivo();

        if (contatos.isEmpty()) {
            outputArea.setText("Nenhum contato cadastrado.");
            return;
        }

        if (ordemAlfabetica) {
            Collections.sort(contatos, (a, b) -> a[0].compareToIgnoreCase(b[0]));
            listarPorOrdemAlfabetica(contatos);
        } else {
            listarPorOrdemAdicao(contatos);
        }
    }

    private List<String[]> lerContatosDoArquivo() {
        List<String[]> contatos = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(ARQUIVO_CSV))) {
            String linha;
            boolean primeiraLinha = true;
            while ((linha = reader.readLine()) != null) {
                if (primeiraLinha) {
                    primeiraLinha = false;
                    continue; // Pula o cabeçalho
                }
                contatos.add(linha.split(","));
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Erro ao ler contatos: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
        return contatos;
    }

    private void listarPorOrdemAdicao(List<String[]> contatos) {
        StringBuilder sb = new StringBuilder();
        sb.append("Contatos por Ordem de Adição:\n\n");

        for (String[] dados : contatos) {
            sb.append(String.format("Nome: %s\n", dados[0]));
            if (!dados[2].isEmpty()) {
                sb.append(String.format("Telefone (%s): %s\n", dados[1], dados[2]));
            }
            if (!dados[3].isEmpty()) sb.append(String.format("E-mail: %s\n", dados[3]));
            sb.append(String.format("Status: %s\n", dados[4]));
            sb.append("----------------\n");
        }

        outputArea.setText(sb.toString());
    }

    private void listarPorOrdemAlfabetica(List<String[]> contatos) {
        StringBuilder sb = new StringBuilder();
        sb.append("Contatos por Ordem Alfabética:\n\n");

        Map<Character, List<String[]>> contatosPorLetra = new TreeMap<>();

        for (String[] contato : contatos) {
            char letraInicial = Character.toUpperCase(contato[0].charAt(0));
            contatosPorLetra.putIfAbsent(letraInicial, new ArrayList<>());
            contatosPorLetra.get(letraInicial).add(contato);
        }

        for (Map.Entry<Character, List<String[]>> entry : contatosPorLetra.entrySet()) {
            char letra = entry.getKey();
            List<String[]> contatosDaLetra = entry.getValue();

            sb.append(String.format("=== %c ===\n\n", letra));

            for (String[] dados : contatosDaLetra) {
                sb.append(String.format("Nome: %s\n", dados[0]));
                if (!dados[2].isEmpty()) {
                    sb.append(String.format("Telefone (%s): %s\n", dados[1], dados[2]));
                }
                if (!dados[3].isEmpty()) sb.append(String.format("E-mail: %s\n", dados[3]));
                sb.append(String.format("Status: %s\n", dados[4]));
                sb.append("----------------\n");
            }
            sb.append("\n");
        }

        outputArea.setText(sb.toString());
    }

    private void excluirContato() {
        List<String[]> contatos = lerContatosDoArquivo();

        if (contatos.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Não há contatos para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String[] nomes = new String[contatos.size()];
        for (int i = 0; i < contatos.size(); i++) {
            nomes[i] = contatos.get(i)[0];
        }

        String nomeSelecionado = (String) JOptionPane.showInputDialog(
                frame,
                "Selecione o contato a ser excluído:",
                "Excluir Contato",
                JOptionPane.PLAIN_MESSAGE,
                null,
                nomes,
                nomes[0]);

        if (nomeSelecionado == null) {
            return; // Usuário cancelou
        }

        contatos.removeIf(contato -> contato[0].equals(nomeSelecionado));

        try (PrintWriter writer = new PrintWriter(new FileWriter(ARQUIVO_CSV))) {
            writer.println("Nome,TipoTelefone,Telefone,Email,Status"); // Cabeçalho
            for (String[] contato : contatos) {
                writer.println(String.join(",", contato));
            }
            JOptionPane.showMessageDialog(frame, "Contato excluído com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Erro ao excluir contato: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limparCampos() {
        nomeField.setText("");
        telefoneField.setText("");
        emailField.setText("");
        telefoneCheckBox.setSelected(true);
        emailCheckBox.setSelected(true);
        telefoneField.setEnabled(true);
        tipoTelefoneCombo.setEnabled(true);
        emailField.setEnabled(true);
        statusCombo.setSelectedIndex(0);
        tipoTelefoneCombo.setSelectedIndex(0);
    }
}