package midiaboxserverside;

import SQLiteBanco.UsuarioDAO;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Jess
 */
public class MidiaBoxServerSide {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
       try {
            ServerSocket servidor = new ServerSocket(12345);
            while(true){
                try (Socket cliente = servidor.accept()) {
                    ObjectOutputStream saida = new ObjectOutputStream(cliente.getOutputStream());
                    ObjectInputStream entrada = new ObjectInputStream(cliente.getInputStream());
                    System.out.println("conecção aceita");
                    String ipCliente = cliente.getInetAddress().getHostAddress();
                    String usuario = (String) entrada.readObject();
                    System.out.println("recebido usuario"+usuario);
                    String senha = (String) entrada.readObject();
                    System.out.println("recebido senha" + senha);
                    boolean autenticar = new UsuarioDAO().autenticar(usuario, senha);
                    saida.flush();
                    saida.writeObject(autenticar);
                    saida.flush();
                }
            }
        }   
        catch(Exception e) {
           System.out.println("Erro: " + e.getMessage());
        }
    }
    
}
