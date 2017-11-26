package midiaboxserverside;

import SQLiteBanco.UsuarioDAO;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jess
 */
public class Servidor extends Thread {

    Socket cliente;

    public Servidor(Socket cliente) {
        this.cliente = cliente;
    }

    @Override
    public void run() {
        try {
            ObjectOutputStream saida = new ObjectOutputStream(cliente.getOutputStream());
            ObjectInputStream entrada = new ObjectInputStream(cliente.getInputStream());
            System.out.println("conecção aceita");

            boolean ativo = true;
            while (ativo) {
                switch ((String) entrada.readObject()) {
                    case "logar":
                        String ipCliente = cliente.getInetAddress().getHostAddress();
                        String usuario;
                        usuario = (String) entrada.readObject();
                        System.out.println("recebido usuario" + usuario);
                        String senha = (String) entrada.readObject();
                        System.out.println("recebido senha" + senha);
                        boolean autenticar = new UsuarioDAO().autenticar(usuario, senha);
                        saida.flush();
                        saida.writeObject(autenticar);
                        saida.flush();
                        break;
                    case "salvarMidia":    //servidor principal para servidores de armazenamento                     
                        receberArquivo();
                        break;
                    case "salvar":    //servidor de armazenamento                  
                        
                        break;
                    case "getMidia": //servidor principal para servidores de armazenamento
                        //pega sua parte e envia
                        break;
                    case "getVideo": //client que pede
                        //chama os 2 servidores de armazenamento pega a parte de cada e envia para o client
                        break;
                    case "fechar":
                        ativo = false;
                        break;
                }
            }

        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void receberArquivo() {
        
        
        sendToServer1();
        sendToServer2();
    }

    public void reproduzirArquivo() {
        try {
            FileInputStream fileInputStream;
            InetAddress addr = InetAddress.getByName("localhost");
            int port = 12345;
            File file = new File("C:\\Users\\Jess\\Desktop\\videos\\teste.mp4");
            byte[] msg = new byte[(int) file.length()];

            fileInputStream = new FileInputStream(file);
            fileInputStream.read(msg);
            fileInputStream.close();
            DatagramPacket pkg = new DatagramPacket(msg, msg.length, addr, port);
            DatagramSocket ds = new DatagramSocket();
            ds.send(pkg);

            ds.close();
        } catch (IOException ioe) {

        }
    }

}
