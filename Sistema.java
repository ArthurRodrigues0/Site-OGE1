
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sistemastickets.config.Database;

/**
 * Sistema de Gerenciamento de Tickets de Suporte Classe principal que gerencia
 * todos os aspectos do sistema
 */
public class Sistema {

    // Enums para definir tipos e status
    public enum StatusTicket {
        ABERTO("Aberto", "#ff6b6b"),
        EM_ANDAMENTO("Em Andamento", "#4ecdc4"),
        RESOLVIDO("Resolvido", "#45b7d1"),
        FECHADO("Fechado", "#96ceb4");

        private final String nome;
        private final String cor;

        StatusTicket(String nome, String cor) {
            this.nome = nome;
            this.cor = cor;
        }

        public String getNome() {
            return nome;
        }

        public String getCor() {
            return cor;
        }
    }

    public enum PrioridadeTicket {
        BAIXA("Baixa", 1, "#96ceb4"),
        MEDIA("Média", 2, "#feca57"),
        ALTA("Alta", 3, "#ff9ff3"),
        CRITICA("Crítica", 4, "#ff6b6b");

        private final String nome;
        private final int nivel;
        private final String cor;

        PrioridadeTicket(String nome, int nivel, String cor) {
            this.nome = nome;
            this.nivel = nivel;
            this.cor = cor;
        }

        public String getNome() {
            return nome;
        }

        public int getNivel() {
            return nivel;
        }

        public String getCor() {
            return cor;
        }
    }

    public enum PerfilUsuario {
        USUARIO("Usuário", 1),
        TECNICO("Técnico", 2),
        ADMIN("Administrador", 3);

        private final String nome;
        private final int nivel;

        PerfilUsuario(String nome, int nivel) {
            this.nome = nome;
            this.nivel = nivel;
        }

        public String getNome() {
            return nome;
        }

        public int getNivel() {
            return nivel;
        }
    }

    public enum TipoComentario {
        COMENTARIO("Comentário"),
        RESOLUCAO("Resolução"),
        INTERNO("Interno");

        private final String nome;

        TipoComentario(String nome) {
            this.nome = nome;
        }

        public String getNome() {
            return nome;
        }
    }

    // Classes internas para modelar os dados
    public static class Ticket {

        private int id;
        private String codigo;
        private String titulo;
        private String descricao;
        private StatusTicket status;
        private PrioridadeTicket prioridade;
        private int categoriaId;
        private int solicitanteId;
        private Integer responsavelId;
        private LocalDateTime dataCriacao;
        private LocalDateTime dataAtualizacao;
        private LocalDateTime dataResolucao;
        private List<String> anexos;
        private List<String> tags;
        private List<Comentario> comentarios;

        public Ticket(int id, String codigo, String titulo, String descricao, StatusTicket status, PrioridadeTicket prioridade, int categoriaId, int solicitanteId, Integer responsavelId, Timestamp dataCriacao, Timestamp dataAtualizacao, Timestamp dataResolucao) {
            this.id = id;
            this.codigo = codigo;
            this.titulo = titulo;
            this.descricao = descricao;
            this.status = status;
            this.prioridade = prioridade;
            this.categoriaId = categoriaId;
            this.solicitanteId = solicitanteId;
            this.responsavelId = responsavelId;
            this.dataCriacao = dataCriacao.toLocalDateTime();
            this.dataAtualizacao = dataAtualizacao.toLocalDateTime();
            this.dataResolucao = (dataResolucao != null) ? dataResolucao.toLocalDateTime() : null;
            this.anexos = new ArrayList<>();
            this.tags = new ArrayList<>();
            this.comentarios = new ArrayList<>();
        }

        // Construtor removido, pois a criação será gerenciada pelo banco de dados.
        // Getters e Setters
        public int getId() {
            return id;
        }

        public String getCodigo() {
            return codigo;
        }

        public String getTitulo() {
            return titulo;
        }

        public void setTitulo(String titulo) {
            this.titulo = titulo;
            this.dataAtualizacao = LocalDateTime.now();
        }

        public String getDescricao() {
            return descricao;
        }

        public void setDescricao(String descricao) {
            this.descricao = descricao;
            this.dataAtualizacao = LocalDateTime.now();
        }

        public StatusTicket getStatus() {
            return status;
        }

        public void setStatus(StatusTicket status) {
            this.status = status;
            this.dataAtualizacao = LocalDateTime.now();
            if (status == StatusTicket.RESOLVIDO || status == StatusTicket.FECHADO) {
                this.dataResolucao = LocalDateTime.now();
            }
        }

        public PrioridadeTicket getPrioridade() {
            return prioridade;
        }

        public void setPrioridade(PrioridadeTicket prioridade) {
            this.prioridade = prioridade;
            this.dataAtualizacao = LocalDateTime.now();
        }

        public int getCategoriaId() {
            return categoriaId;
        }

        public void setCategoriaId(int categoriaId) {
            this.categoriaId = categoriaId;
            this.dataAtualizacao = LocalDateTime.now();
        }

        public int getSolicitanteId() {
            return solicitanteId;
        }

        public Integer getResponsavelId() {
            return responsavelId;
        }

        public void setResponsavelId(Integer responsavelId) {
            this.responsavelId = responsavelId;
            this.dataAtualizacao = LocalDateTime.now();
        }

        public LocalDateTime getDataCriacao() {
            return dataCriacao;
        }

        public LocalDateTime getDataAtualizacao() {
            return dataAtualizacao;
        }

        public LocalDateTime getDataResolucao() {
            return dataResolucao;
        }

        public List<String> getAnexos() {
            return anexos;
        }

        public void adicionarAnexo(String anexo) {
            this.anexos.add(anexo);
            this.dataAtualizacao = LocalDateTime.now();
        }

        public List<String> getTags() {
            return tags;
        }

        public void adicionarTag(String tag) {
            if (!this.tags.contains(tag)) {
                this.tags.add(tag);
                this.dataAtualizacao = LocalDateTime.now();
            }
        }

        public List<Comentario> getComentarios() {
            return comentarios;
        }

        public void adicionarComentario(Comentario comentario) {
            this.comentarios.add(comentario);
            this.dataAtualizacao = LocalDateTime.now();
        }

        public String getDataCriacaoFormatada() {
            return dataCriacao.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        }

        public String getDataAtualizacaoFormatada() {
            return dataAtualizacao.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        }

        public long getTempoAberto() {
            LocalDateTime fim = (dataResolucao != null) ? dataResolucao : LocalDateTime.now();
            return java.time.Duration.between(dataCriacao, fim).toHours();
        }
    }

    public static class Usuario {

        private int id;
        private String nome;
        private String email;
        private PerfilUsuario perfil;
        private int departamentoId;
        private boolean ativo;
        private LocalDateTime dataCriacao;

        public Usuario(int id, String nome, String email, PerfilUsuario perfil, int departamentoId, boolean ativo, Timestamp dataCriacao) {
            this.id = id;
            this.nome = nome;
            this.email = email;
            this.perfil = perfil;
            this.departamentoId = departamentoId;
            this.ativo = ativo;
            this.dataCriacao = dataCriacao.toLocalDateTime();
        }

        // Getters e Setters
        public int getId() {
            return id;
        }

        public String getNome() {
            return nome;
        }

        public void setNome(String nome) {
            this.nome = nome;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public PerfilUsuario getPerfil() {
            return perfil;
        }

        public void setPerfil(PerfilUsuario perfil) {
            this.perfil = perfil;
        }

        public int getDepartamentoId() {
            return departamentoId;
        }

        public void setDepartamentoId(int departamentoId) {
            this.departamentoId = departamentoId;
        }

        public boolean isAtivo() {
            return ativo;
        }

        public void setAtivo(boolean ativo) {
            this.ativo = ativo;
        }

        public LocalDateTime getDataCriacao() {
            return dataCriacao;
        }

        public boolean podeEditarTicket(Ticket ticket) {
            return perfil == PerfilUsuario.ADMIN
                    || perfil == PerfilUsuario.TECNICO
                    || ticket.getSolicitanteId() == this.id;
        }

        public boolean podeAssumirTicket() {
            return perfil == PerfilUsuario.ADMIN || perfil == PerfilUsuario.TECNICO;
        }
    }

    public static class Comentario {

        private int id;
        private int ticketId;
        private int usuarioId;
        private String conteudo;
        private TipoComentario tipo;
        private LocalDateTime data;

        public Comentario(int id, int ticketId, int usuarioId, String conteudo, TipoComentario tipo, Timestamp data) {
            this.id = id;
            this.ticketId = ticketId;
            this.usuarioId = usuarioId;
            this.conteudo = conteudo;
            this.tipo = tipo;
            this.data = data.toLocalDateTime();
        }

        // Getters
        public int getId() {
            return id;
        }

        public int getTicketId() {
            return ticketId;
        }

        public int getUsuarioId() {
            return usuarioId;
        }

        public String getConteudo() {
            return conteudo;
        }

        public TipoComentario getTipo() {
            return tipo;
        }

        public LocalDateTime getData() {
            return data;
        }

        public String getDataFormatada() {
            return data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        }
    }

    public static class Categoria {

        private int id;
        private String nome;
        private String descricao;
        private String cor;
        private boolean ativa;

        public Categoria(int id, String nome, String descricao, String cor, boolean ativa) {
            this.id = id;
            this.nome = nome;
            this.descricao = descricao;
            this.cor = cor;
            this.ativa = ativa;
        }

        // Getters e Setters
        public int getId() {
            return id;
        }

        public String getNome() {
            return nome;
        }

        public void setNome(String nome) {
            this.nome = nome;
        }

        public String getDescricao() {
            return descricao;
        }

        public void setDescricao(String descricao) {
            this.descricao = descricao;
        }

        public String getCor() {
            return cor;
        }

        public void setCor(String cor) {
            this.cor = cor;
        }

        public boolean isAtiva() {
            return ativa;
        }

        public void setAtiva(boolean ativa) {
            this.ativa = ativa;
        }
    }

    // Atributos principais do sistema
    private Connection conexao;
    private Usuario usuarioLogado;

    // Construtor
    public Sistema() {
        this.conexao = Database.getConnection();
        // Define um usuário logado padrão para fins de teste.
        // Em um sistema real, isso viria de uma tela de login.
        this.usuarioLogado = buscarUsuarioPorId("1"); // admin
    }

    // Métodos principais do sistema
    public String criarTicket(String titulo, String descricao, String categoriaId) {
        if (usuarioLogado == null) {
            return "Erro: Usuário não autenticado";
        }
        if (titulo == null || titulo.trim().isEmpty() || descricao == null || descricao.trim().isEmpty()) {
            return "Erro: Título e descrição são obrigatórios";
        }
        try (PreparedStatement stmt = conexao.prepareStatement(
                "INSERT INTO tickets (titulo, descricao, categoria_id, solicitante_id, prioridade) VALUES (?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, titulo);
            stmt.setString(2, descricao);
            stmt.setInt(3, Integer.parseInt(categoriaId));
            stmt.setInt(4, usuarioLogado.getId());
            stmt.setString(5, PrioridadeTicket.MEDIA.name());
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return "Ticket criado com sucesso! ID: " + generatedKeys.getLong(1);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Erro ao criar ticket: " + e.getMessage();
        }
        return "Erro: Não foi possível criar o ticket";
    }

    private Ticket mapearResultSetParaTicket(ResultSet rs) throws SQLException {
        return new Ticket(
                rs.getInt("id"),
                rs.getString("codigo"),
                rs.getString("titulo"),
                rs.getString("descricao"),
                StatusTicket.valueOf(rs.getString("status")),
                PrioridadeTicket.valueOf(rs.getString("prioridade")),
                rs.getInt("categoria_id"),
                rs.getInt("solicitante_id"),
                rs.getObject("responsavel_id") != null ? rs.getInt("responsavel_id") : null,
                rs.getTimestamp("data_criacao"),
                rs.getTimestamp("data_atualizacao"),
                rs.getTimestamp("data_resolucao")
        );
    }

    public List<Ticket> listarTickets() {
        if (usuarioLogado == null) {
            return new ArrayList<>();
        }
        List<Ticket> ticketsEncontrados = new ArrayList<>();
        String sql = "SELECT * FROM tickets";

        if (usuarioLogado.getPerfil() != PerfilUsuario.ADMIN && usuarioLogado.getPerfil() != PerfilUsuario.TECNICO) {
            sql += " WHERE solicitante_id = ?";
        }

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            if (usuarioLogado.getPerfil() != PerfilUsuario.ADMIN && usuarioLogado.getPerfil() != PerfilUsuario.TECNICO) {
                stmt.setInt(1, usuarioLogado.getId());
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ticketsEncontrados.add(mapearResultSetParaTicket(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ticketsEncontrados;
    }

    public Ticket buscarTicketPorId(String id) {
        String sql = "SELECT * FROM tickets WHERE id = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, Integer.parseInt(id));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSetParaTicket(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String atualizarStatusTicket(String ticketId, StatusTicket novoStatus) {
        if (usuarioLogado == null) {
            return "Erro: Usuário não autenticado";
        }
        Ticket ticket = buscarTicketPorId(ticketId);
        if (ticket == null) {
            return "Erro: Ticket não encontrado";
        }
        if (!usuarioLogado.podeEditarTicket(ticket)) {
            return "Erro: Sem permissão para editar este ticket";
        }
        String sql = "UPDATE tickets SET status = ? WHERE id = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, novoStatus.name());
            stmt.setInt(2, Integer.parseInt(ticketId));
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                return "Status atualizado com sucesso";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Erro ao atualizar status: " + e.getMessage();
        }
        return "Erro: Não foi possível atualizar o status";
    }

    public String atribuirResponsavel(String ticketId, String responsavelId) {
        if (usuarioLogado == null || !usuarioLogado.podeAssumirTicket()) {
            return "Erro: Sem permissão para atribuir responsável";
        }
        String sql = "UPDATE tickets SET responsavel_id = ?, status = CASE WHEN status = 'ABERTO' THEN 'EM_ANDAMENTO' ELSE status END WHERE id = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, Integer.parseInt(responsavelId));
            stmt.setInt(2, Integer.parseInt(ticketId));
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                return "Responsável atribuído com sucesso";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Erro ao atribuir responsável: " + e.getMessage();
        }
        return "Erro: Não foi possível atribuir o responsável";
    }

    public String adicionarComentario(String ticketId, String conteudo, TipoComentario tipo) {
        if (usuarioLogado == null) {
            return "Erro: Usuário não autenticado";
        }
        String sql = "INSERT INTO comentarios (ticket_id, usuario_id, conteudo, tipo) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, Integer.parseInt(ticketId));
            stmt.setInt(2, usuarioLogado.getId());
            stmt.setString(3, conteudo);
            stmt.setString(4, tipo.name());
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                return "Comentário adicionado com sucesso";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Erro ao adicionar comentário: " + e.getMessage();
        }
        return "Erro: Não foi possível adicionar o comentário";
    }

    // Métodos de busca e filtros
    public List<Ticket> buscarTickets(String termo) {
        List<Ticket> ticketsEncontrados = new ArrayList<>();
        if (termo == null || termo.trim().isEmpty()) {
            return listarTickets();
        }
        String sql = "SELECT * FROM tickets WHERE (titulo LIKE ? OR descricao LIKE ? OR codigo LIKE ?)";
        if (usuarioLogado.getPerfil() != PerfilUsuario.ADMIN && usuarioLogado.getPerfil() != PerfilUsuario.TECNICO) {
            sql += " AND solicitante_id = ?";
        }

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            String termoBusca = "%" + termo + "%";
            stmt.setString(1, termoBusca);
            stmt.setString(2, termoBusca);
            stmt.setString(3, termoBusca);
            if (usuarioLogado.getPerfil() != PerfilUsuario.ADMIN && usuarioLogado.getPerfil() != PerfilUsuario.TECNICO) {
                stmt.setInt(4, usuarioLogado.getId());
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ticketsEncontrados.add(mapearResultSetParaTicket(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ticketsEncontrados;
    }

    public List<Ticket> filtrarTicketsPorStatus(StatusTicket status) {
        List<Ticket> ticketsEncontrados = new ArrayList<>();
        String sql = "SELECT * FROM tickets WHERE status = ?";

        if (usuarioLogado != null && usuarioLogado.getPerfil() != PerfilUsuario.ADMIN && usuarioLogado.getPerfil() != PerfilUsuario.TECNICO) {
            sql += " AND solicitante_id = ?";
        }

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, status.name());
            if (usuarioLogado != null && usuarioLogado.getPerfil() != PerfilUsuario.ADMIN && usuarioLogado.getPerfil() != PerfilUsuario.TECNICO) {
                stmt.setInt(2, usuarioLogado.getId());
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ticketsEncontrados.add(mapearResultSetParaTicket(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ticketsEncontrados;
    }

    public List<Ticket> filtrarTicketsPorPrioridade(PrioridadeTicket prioridade) {
        List<Ticket> ticketsEncontrados = new ArrayList<>();
        String sql = "SELECT * FROM tickets WHERE prioridade = ?";

        if (usuarioLogado != null && usuarioLogado.getPerfil() != PerfilUsuario.ADMIN && usuarioLogado.getPerfil() != PerfilUsuario.TECNICO) {
            sql += " AND solicitante_id = ?";
        }

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, prioridade.name());
            if (usuarioLogado != null && usuarioLogado.getPerfil() != PerfilUsuario.ADMIN && usuarioLogado.getPerfil() != PerfilUsuario.TECNICO) {
                stmt.setInt(2, usuarioLogado.getId());
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ticketsEncontrados.add(mapearResultSetParaTicket(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ticketsEncontrados;
    }

    // Métodos auxiliares
    private Usuario buscarUsuarioPorId(String id) {
        String sql = "SELECT * FROM usuarios WHERE id = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, Integer.parseInt(id));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Usuario(
                            rs.getInt("id"),
                            rs.getString("nome"),
                            rs.getString("email"),
                            PerfilUsuario.valueOf(rs.getString("perfil")),
                            rs.getInt("departamento_id"),
                            rs.getBoolean("ativo"),
                            rs.getTimestamp("data_criacao")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Categoria buscarCategoriaPorId(String id) {
        String sql = "SELECT * FROM categorias WHERE id = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, Integer.parseInt(id));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Categoria(
                            rs.getInt("id"),
                            rs.getString("nome"),
                            rs.getString("descricao"),
                            rs.getString("cor"),
                            rs.getBoolean("ativa")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Getters para acesso aos dados
    public List<Usuario> getUsuarios() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuarios";
        try (Statement stmt = conexao.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                usuarios.add(new Usuario(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("email"),
                        PerfilUsuario.valueOf(rs.getString("perfil")),
                        rs.getInt("departamento_id"),
                        rs.getBoolean("ativo"),
                        rs.getTimestamp("data_criacao")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usuarios;
    }

    public List<Categoria> getCategorias() {
        List<Categoria> categorias = new ArrayList<>();
        String sql = "SELECT * FROM categorias WHERE ativa = TRUE";
        try (Statement stmt = conexao.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                categorias.add(new Categoria(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("descricao"),
                        rs.getString("cor"),
                        rs.getBoolean("ativa")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categorias;
    }

    public Usuario getUsuarioLogado() {
        return usuarioLogado;
    }

    public void setUsuarioLogado(Usuario usuario) {
        this.usuarioLogado = usuario;
    }

    // Estatísticas do sistema
    public Map<String, Integer> getEstatisticasStatus() {
        Map<String, Integer> stats = new HashMap<>();
        String sql = "SELECT status, COUNT(*) as total FROM tickets GROUP BY status";
        try (Statement stmt = conexao.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                stats.put(rs.getString("status"), rs.getInt("total"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }

    public Map<String, Integer> getEstatisticasPrioridade() {
        Map<String, Integer> stats = new HashMap<>();
        String sql = "SELECT prioridade, COUNT(*) as total FROM tickets GROUP BY prioridade";
        try (Statement stmt = conexao.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                stats.put(rs.getString("prioridade"), rs.getInt("total"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }

    public int getTotalTickets() {
        String sql = "SELECT COUNT(*) FROM tickets";
        try (Statement stmt = conexao.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getTicketsAbertos() {
        String sql = "SELECT COUNT(*) FROM tickets WHERE status != 'FECHADO'";
        try (Statement stmt = conexao.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public double getTempoMedioResolucao() {
        String sql = "SELECT AVG(TIMESTAMPDIFF(HOUR, data_criacao, data_resolucao)) FROM tickets WHERE data_resolucao IS NOT NULL";
        try (Statement stmt = conexao.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public void gerarArquivosJson() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        // Gerar JSON de tickets
        try (FileWriter ticketsFile = new FileWriter("data/tickets.json")) {
            gson.toJson(listarTickets(), ticketsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Gerar JSON de usuários
        try (FileWriter usuariosFile = new FileWriter("data/usuarios.json")) {
            gson.toJson(getUsuarios(), usuariosFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Gerar JSON de categorias
        try (FileWriter categoriasFile = new FileWriter("data/categorias.json")) {
            gson.toJson(getCategorias(), categoriasFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
