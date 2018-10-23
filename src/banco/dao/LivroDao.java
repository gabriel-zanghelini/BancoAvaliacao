package banco.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import banco.modelo.Autor;
import banco.modelo.Cliente;
import banco.modelo.Conta;
import banco.modelo.Livro;

public class LivroDao implements Dao<Livro> {
	private static final String GET_BY_ID = "SELECT * FROM livro NATURAL JOIN autor WHERE id = ?";
	private static final String GET_ALL = "SELECT * FROM livro NATURAL JOIN autor";
	private static final String INSERT = "INSERT INTO livro (titulo, ano_publicacao, editora, autor_id) "
			+ "VALUES (?, ?, ?, ?)";
	private static final String UPDATE = "UPDATE livro SET titulo = ?, ano_publicacao = ?, editora = ?, cliente_id = ? WHERE id = ?";
	private static final String DELETE = "DELETE FROM livro WHERE id = ?";
	
	public LivroDao() {
		try {
			createTable();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void createTable() throws SQLException {
	    final String sqlCreate = "CREATE TABLE IF NOT EXISTS livro"
	            + "  (id             	INTEGER,"
	            + "   titulo         	VARCHAR(255),"
	            + "   ano_publicacao	INTEGER,"
	            + "   editora          	VARCHAR(50),"
	            + "   autor_id   		INTEGER,"
	            + "   FOREIGN KEY (autor_id) REFERENCES autor(id),"
	            + "   PRIMARY KEY (id))";
	    
	    Connection conn = DbConnection.getConnection();

	    Statement stmt = conn.createStatement();
	    stmt.execute(sqlCreate);
	}
	
	private Livro getLivroFromRS(ResultSet rs) throws SQLException
    {
		Livro livro = new Livro();
			
		livro.setId( rs.getInt("id") );
		livro.setTitulo(rs.getString("titulo"));
		livro.setAnoPublicacao(rs.getInt("numero"));
		livro.setEditora(rs.getString("editora"));
		livro.setAutor( new Autor(rs.getInt("autor_id"), rs.getString("nome"), 
				rs.getLong("cpf")) );
	
		return livro;
    }
	
	@Override
	public Livro getByKey(int id) {
		Connection conn = DbConnection.getConnection();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		Livro livro = null;
		
		try {
			stmt = conn.prepareStatement(GET_BY_ID);
			stmt.setInt(1, id);
			rs = stmt.executeQuery();
			
			if (rs.next()) {
				livro = getLivroFromRS(rs);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(conn, stmt, rs);
		}
		
		return livro;
	}
	
	@Override
	public List<Livro> getAll() {
		Connection conn = DbConnection.getConnection();
		Statement stmt = null;
		ResultSet rs = null;
		
		List<Livro> livro = new ArrayList<>();
		
		try {
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery(GET_ALL);
			
			while (rs.next()) {
				livro.add(getLivroFromRS(rs));
			}			
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(conn, stmt, rs);
		}
		
		return livro;
	}
	
	@Override
	public void insert(Livro livro) {
		Connection conn = DbConnection.getConnection();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			stmt = conn.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS);
			stmt.setString(1, livro.getTitulo());
			stmt.setInt(2, livro.getAnoPublicacao());
			stmt.setString(1, livro.getEditora());
			stmt.setInt(2, livro.getAutor().getId());
			
			stmt.executeUpdate();
			rs = stmt.getGeneratedKeys();
			
			if (rs.next()) {
				livro.setId(rs.getInt(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(conn, stmt, rs);
		}

	}
}
