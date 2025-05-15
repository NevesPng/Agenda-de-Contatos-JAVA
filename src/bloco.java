import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.undo.UndoManager;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.filechooser.FileNameExtensionFilter;

public class bloco extends JFrame implements ActionListener {

    private JTextArea textArea;
    private JMenuBar menuBar;
    private JMenu menuArquivo, menuEditar;
    private JMenuItem itemNovo, itemAbrir, itemSalvar, itemSair;
    private JMenuItem itemCopiar, itemColar, itemRecortar, itemDesfazer, itemRefazer;
    private JFileChooser fileChooser;
    private File arquivoAtual;
    private UndoManager undoManager;

    public bloco() {
        setTitle("Bloco de Notas");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centraliza a janela

        textArea = new JTextArea();
        textArea.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(textArea);
        add(scrollPane, BorderLayout.CENTER);

        menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        // Menu Arquivo
        menuArquivo = new JMenu("Arquivo");
        menuBar.add(menuArquivo);

        itemNovo = new JMenuItem("Novo");
        itemAbrir = new JMenuItem("Abrir...");
        itemSalvar = new JMenuItem("Salvar");
        itemSair = new JMenuItem("Sair");

        itemNovo.addActionListener(this);
        itemAbrir.addActionListener(this);
        itemSalvar.addActionListener(this);
        itemSair.addActionListener(this);

        menuArquivo.add(itemNovo);
        menuArquivo.add(itemAbrir);
        menuArquivo.add(itemSalvar);
        menuArquivo.addSeparator(); // Adiciona uma linha separadora
        menuArquivo.add(itemSair);

        // Menu Editar
        menuEditar = new JMenu("Editar");
        menuBar.add(menuEditar);

        itemCopiar = new JMenuItem("Copiar");
        itemColar = new JMenuItem("Colar");
        itemRecortar = new JMenuItem("Recortar");
        itemDesfazer = new JMenuItem("Desfazer");
        itemRefazer = new JMenuItem("Refazer");

        itemCopiar.addActionListener(this);
        itemColar.addActionListener(this);
        itemRecortar.addActionListener(this);
        itemDesfazer.addActionListener(this);
        itemRefazer.addActionListener(this);

        menuEditar.add(itemCopiar);
        menuEditar.add(itemColar);
        menuEditar.add(itemRecortar);
        menuEditar.addSeparator();
        menuEditar.add(itemDesfazer);
        menuEditar.add(itemRefazer);

        fileChooser = new JFileChooser();
        fileChooser.setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter txtFilter = new FileNameExtensionFilter("Arquivos de Texto (*.txt)", "txt");
        fileChooser.addChoosableFileFilter(txtFilter);

        undoManager = new UndoManager();
        textArea.getDocument().addUndoableEditListener(new UndoableEditListener() {
            public void undoableEditHappened(UndoableEditEvent e) {
                undoManager.addEdit(e.getEdit());
            }
        });

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(bloco::new);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == itemNovo) {
            novoArquivo();
        } else if (e.getSource() == itemAbrir) {
            abrirArquivo();
        } else if (e.getSource() == itemSalvar) {
            salvarArquivo();
        } else if (e.getSource() == itemSair) {
            System.exit(0);
        } else if (e.getSource() == itemCopiar) {
            textArea.copy();
        } else if (e.getSource() == itemColar) {
            textArea.paste();
        } else if (e.getSource() == itemRecortar) {
            textArea.cut();
        } else if (e.getSource() == itemDesfazer) {
            if (undoManager.canUndo()) {
                undoManager.undo();
            }
        } else if (e.getSource() == itemRefazer) {
            if (undoManager.canRedo()) {
                undoManager.redo();
            }
        }
    }

    private void novoArquivo() {
        textArea.setText("");
        arquivoAtual = null;
        setTitle("Bloco de Notas");
    }

    private void abrirArquivo() {
        int returnVal = fileChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            arquivoAtual = fileChooser.getSelectedFile();
            try (BufferedReader reader = new BufferedReader(new FileReader(arquivoAtual))) {
                textArea.setText("");
                String linha;
                while ((linha = reader.readLine()) != null) {
                    textArea.append(linha + "\n");
                }
                setTitle("Bloco de Notas - " + arquivoAtual.getName());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Erro ao abrir o arquivo.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void salvarArquivo() {
        if (arquivoAtual == null) {
            salvarComoArquivo();
        } else {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(arquivoAtual))) {
                writer.write(textArea.getText());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Erro ao salvar o arquivo.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void salvarComoArquivo() {
        int returnVal = fileChooser.showSaveDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            arquivoAtual = fileChooser.getSelectedFile();
            if (!arquivoAtual.getName().toLowerCase().endsWith(".txt")) {
                arquivoAtual = new File(arquivoAtual.getAbsolutePath() + ".txt");
            }
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(arquivoAtual))) {
                writer.write(textArea.getText());
                setTitle("Bloco de Notas - " + arquivoAtual.getName());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Erro ao salvar o arquivo.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}