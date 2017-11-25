package SQLiteBanco;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jess
 */
public class UsuarioDAO {
   
    public boolean autenticar(String usuario, String senha){
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
        }catch(SQLException ex ){
            Logger.getLogger(UsuarioDAO.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(UsuarioDAO.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(UsuarioDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return autenticou;
    }
}
