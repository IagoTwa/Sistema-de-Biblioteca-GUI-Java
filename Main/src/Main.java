import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

class Livro {
    String titulo;
    String info;
    String autor;
    boolean disp = true;

    void emprestar() { if (disp) disp = false; }
    void devolver() { disp = true; }

    void info(JTextArea area) {
        String status = disp ? "Disponível" : "Emprestado";
        area.append("Livro: " + titulo + " | Autor: " + autor + " | Status: " + status + " | Info: " + info + "\n");
    }
}

class Usuario {
    String nome;
    Livro livroEmprestado;

    Usuario(String nome) { this.nome = nome; }

    void pegarLivro(Livro l, JTextArea area) {
        if (livroEmprestado != null) {
            area.append(nome + " já tem um livro emprestado!\n");
            return;
        }
        if (l.disp) {
            l.emprestar();
            livroEmprestado = l;
            area.append(nome + " pegou o livro: " + l.titulo + "\n");
        } else {
            area.append("O livro " + l.titulo + " já está emprestado.\n");
        }
    }

    void devolverLivro(JTextArea area, java.util.List<Livro> biblioteca) {
        if (livroEmprestado != null) {
            if (biblioteca.contains(livroEmprestado)) {
                livroEmprestado.devolver();
                area.append(nome + " devolveu o livro: " + livroEmprestado.titulo + "\n");
            } else {
                area.append(nome + " não pode devolver o livro, pois ele foi excluído da biblioteca.\n");
            }
            livroEmprestado = null;
        } else {
            area.append(nome + " não tem nenhum livro para devolver.\n");
        }
    }
}

public class Main extends JFrame {
    private java.util.List<Livro> livros = new ArrayList<>();
    private java.util.List<Usuario> usuarios = new ArrayList<>();
    private JTextArea area;

    public Main() {
        setTitle("Sistema Biblioteca");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        area = new JTextArea();
        area.setEditable(false);
        add(new JScrollPane(area), BorderLayout.CENTER);

        JPanel panel = new JPanel(new GridLayout(2, 3));

        JButton btnListar = new JButton("Listar Livros");
        JButton btnCriarLivro = new JButton("Criar Livro");
        JButton btnExcluirLivro = new JButton("Excluir Livro");
        JButton btnCriarUsuario = new JButton("Criar Usuário");
        JButton btnPegarLivro = new JButton("Pegar Livro");
        JButton btnDevolverLivro = new JButton("Devolver Livro");

        panel.add(btnListar);
        panel.add(btnCriarLivro);
        panel.add(btnExcluirLivro);
        panel.add(btnCriarUsuario);
        panel.add(btnPegarLivro);
        panel.add(btnDevolverLivro);

        add(panel, BorderLayout.SOUTH);

        btnListar.addActionListener(e -> {
            area.setText("");
            if (livros.isEmpty()) {
                area.append("Nenhum livro cadastrado.\n");
            } else {
                for (Livro l : livros) l.info(area);
            }
        });

        btnCriarLivro.addActionListener(e -> {
            ImageIcon iconeLivro = new ImageIcon("assets/livro.png"); // Coloque o caminho do seu ícone
            String titulo = null, autor = null, info = null;

            while (titulo == null || titulo.trim().isEmpty()) {
                titulo = (String) JOptionPane.showInputDialog(
                        this,
                        "Título (obrigatório):",
                        "Criar Livro",
                        JOptionPane.PLAIN_MESSAGE,
                        iconeLivro,
                        null,
                        null
                );
                if (titulo == null) return;
            }
            while (autor == null || autor.trim().isEmpty()) {
                autor = (String) JOptionPane.showInputDialog(
                        this,
                        "Autor (obrigatório):",
                        "Criar Livro",
                        JOptionPane.PLAIN_MESSAGE,
                        iconeLivro,
                        null,
                        null
                );
                if (autor == null) return;
            }

            while (info == null || info.trim().isEmpty()) {
                info = (String) JOptionPane.showInputDialog(
                        this,
                        "Descrição (obrigatório):",
                        "Criar Livro",
                        JOptionPane.PLAIN_MESSAGE,
                        iconeLivro,
                        null,
                        null
                );
                if (info == null) return;
            }

            Livro novo = new Livro();
            novo.titulo = titulo;
            novo.autor = autor;
            novo.info = info;
            livros.add(novo);
            area.append("Livro '" + titulo + "' adicionado!\n");
        });



        btnExcluirLivro.addActionListener(e -> {
            String[] nomes = livros.stream().map(l -> l.titulo).toArray(String[]::new);
            String escolha = (String) JOptionPane.showInputDialog(this, "Escolha o livro:",
                    "Excluir Livro", JOptionPane.PLAIN_MESSAGE, null, nomes, null);
            if (escolha != null) {
                livros.removeIf(l -> l.titulo.equals(escolha));
                area.append("Livro '" + escolha + "' removido!\n");
            }
        });

        btnCriarUsuario.addActionListener(e -> {
            ImageIcon iconeUsuario = new ImageIcon("assets/usuario.png"); // Coloque o caminho do seu ícone
            String nome = (String) JOptionPane.showInputDialog(
                    this,
                    "Nome do usuário:",
                    "Criar Usuário",
                    JOptionPane.PLAIN_MESSAGE,
                    iconeUsuario,
                    null,
                    null
            );

            if (nome != null && !nome.trim().isEmpty()) {
                usuarios.add(new Usuario(nome));
                area.append("Usuário '" + nome + "' criado!\n");
            }
        });


        btnPegarLivro.addActionListener(e -> {
            if (usuarios.isEmpty() || livros.isEmpty()) {
                area.append("Necessário ter usuários e livros cadastrados!\n");
                return;
            }
            String[] nomesUsuarios = usuarios.stream().map(u -> u.nome).toArray(String[]::new);
            String usuarioEscolhido = (String) JOptionPane.showInputDialog(this, "Escolha o usuário:",
                    "Pegar Livro", JOptionPane.PLAIN_MESSAGE, null, nomesUsuarios, null);

            String[] nomesLivros = livros.stream().map(l -> l.titulo).toArray(String[]::new);
            String livroEscolhido = (String) JOptionPane.showInputDialog(this, "Escolha o livro:",
                    "Pegar Livro", JOptionPane.PLAIN_MESSAGE, null, nomesLivros, null);

            if (usuarioEscolhido != null && livroEscolhido != null) {
                Usuario u = usuarios.stream().filter(us -> us.nome.equals(usuarioEscolhido)).findFirst().get();
                Livro l = livros.stream().filter(lb -> lb.titulo.equals(livroEscolhido)).findFirst().get();
                u.pegarLivro(l, area);
            }
        });

        btnDevolverLivro.addActionListener(e -> {
            if (usuarios.isEmpty()) {
                area.append("Nenhum usuário cadastrado!\n");
                return;
            }
            String[] nomesUsuarios = usuarios.stream().map(u -> u.nome).toArray(String[]::new);
            String usuarioEscolhido = (String) JOptionPane.showInputDialog(this, "Escolha o usuário:",
                    "Devolver Livro", JOptionPane.PLAIN_MESSAGE, null, nomesUsuarios, null);

            if (usuarioEscolhido != null) {
                Usuario u = usuarios.stream().filter(us -> us.nome.equals(usuarioEscolhido)).findFirst().get();
                u.devolverLivro(area, livros);

            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }
}

