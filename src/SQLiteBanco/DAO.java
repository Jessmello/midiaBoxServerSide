package SQLiteBanco;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jess
 */
public class DAO {

    public boolean autenticar(String usuario, String senha) {
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        boolean autenticou = false;
        try {
            conn = Connect.ConnectDB(); // classe connecttDB da classe SQLiteBanco
            String sql = "SELECT * FROM tableUSER  WHERE Nome=? and Senha=?"; // selecionando todo campo username e passoword 

            pst = (PreparedStatement) conn.prepareStatement(sql);
            pst.setString(1, usuario);
            pst.setString(2, senha);
            rs = pst.executeQuery();

            if (rs.next()) {
                autenticou = true;
            }
            rs.close();
            pst.close();
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return autenticou;
    }
    
    public void insertMidia (String url, String nome, String responsavel, String tipo, String descricao ){
        Connection conn = null;
        PreparedStatement pst = null;
               
        try {
            conn = Connect.ConnectDB(); // classe connecttDB da classe SQLiteBanco
            String sql = "INSERT INTO tb_midia  (URL, nome, responsavel, tipo, descricao) values (?, ?, ?, ?, ?)"; 

            pst = (PreparedStatement) conn.prepareStatement(sql);
            pst.setString(1, url);  
            pst.setString(2, nome);
            pst.setString(3, responsavel);
            pst.setString(4, tipo);
            pst.setString(5, descricao);
            pst.executeUpdate(); 
                      
            pst.close();
            conn.close();
        }catch(SQLException ex ){
            Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
   
    public String getUrlVideo (String id_codigo){
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        String url = "";
        try {
            conn = Connect.ConnectDB(); // classe connecttDB da classe SQLiteBanco
            String sql = "SELECT URL FROM tb_midia  WHERE id_codigo=?"; // selecionando todo campo username e passoword 

            pst = (PreparedStatement) conn.prepareStatement(sql);
            pst.setInt(1, Integer.parseInt(id_codigo)); 
            rs = pst.executeQuery(); 
            
            if (rs.next()) {
                url = rs.getString("URL");
            }
            rs.close();
            pst.close();
            conn.close();
        }catch(SQLException ex ){
            Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return url;
    }
    
    public List<Midia> listar() throws SQLException, ClassNotFoundException {
        List<Midia> midia = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pst = null;
        
        try {
            conn = Connect.ConnectDB();
            try {
                PreparedStatement stmt = conn. prepareStatement("SELECT * FROM tb_midia");
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    Midia c = new Midia();
                    c.setId_codigo(rs.getInt("id_codigo"));
                    c.setUrl(rs.getString("url"));
                    c.setNome(rs.getString("nome"));
                    c.setResponsavel(rs.getString("responsavel"));
                    c.setTipo(rs.getString("tipo"));
                    c.setDescricao(rs.getString("descrição"));
                    midia.add(c);
                }
                stmt.close();
                rs.close();
            } finally {
                conn.close();
            }
        }catch(SQLException ex ){
            Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return midia;
    }
    
}
