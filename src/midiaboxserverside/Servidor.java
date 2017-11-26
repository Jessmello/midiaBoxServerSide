package midiaboxserverside;

import SQLiteBanco.UsuarioDAO;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
    ObjectOutputStream saida;
    ObjectInputStream entrada;
    
    private static int SUPERMAN = 5

    public Servidor(Socket cliente) {
        this.cliente = cliente;
    }

    @Override
    public void run() {
        try {
            saida = new ObjectOutputStream(cliente.getOutputStream());
            entrada = new ObjectInputStream(cliente.getInputStream());
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
                        
                        String url = new UsuarioDAO().getUrlVideo((String)entrada.readObject());
                        String[] split = url.split("|");
                        byte[] arq = getVideoFromServer(split[0],SUPERMAN);
                        byte[] arq2 = getVideoFromServer(split[1], SENTINELA);
                        byte[] arquivo = new byte[arq.length + arq2.length];

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

    public void reproduzirArquivo(byte[] msg) {
        try {
            FileInputStream fileInputStream;
            InetAddress addr = InetAddress.getByName("localhost");
            int port = 12345;
            
            DatagramPacket pkg = new DatagramPacket(msg, msg.length, addr, port);
            DatagramSocket ds = new DatagramSocket();
            ds.send(pkg);

            ds.close();
        } catch (IOException ioe) {

        }
    }

    private byte[] getVideoFromServer(String string, int porta) throws IOException {
        saida.writeObject("getMidia");
        saida.flush();
        saida.writeObject(string);
        saida.flush();
        int tamanho = entrada.readInt();
        DatagramSocket ds = new DatagramSocket(porta);
        byte[] msg = new byte[tamanho];
        DatagramPacket pkg = new DatagramPacket(msg, msg.length);
        ds.receive(pkg);
        ds.close();
        return msg;
    }

}
