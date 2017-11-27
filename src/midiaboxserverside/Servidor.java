package midiaboxserverside;

import SQLiteBanco.DAO;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.ArrayUtils;

/**
 *
 * @author Jess
 */
public class Servidor extends Thread {

    Socket cliente;
    ObjectOutputStream saida;
    ObjectInputStream entrada;
    ObjectOutputStream saidaClient;
    ObjectInputStream entradaClient;
    ObjectOutputStream saidaSuperman;
    ObjectInputStream entradaSuperman;
    ObjectOutputStream saidaSentinela;
    ObjectInputStream entradaSentinela;

    private static int BATMAN = 12345;
    private static int SUPERMAN = 12346;
    private static int SENTINELA = 12347;
    private static String SUPERMANHOST = "localhost";
    private static String SENTINELAHOST = "localhost";
    private static String BATMANHOST = "localhost";
    private boolean ativo = true;

    public Servidor(Socket cliente) {
        this.cliente = cliente;
    }

    @Override
    public void run() {
        try {
            saida = new ObjectOutputStream(cliente.getOutputStream());
            entrada = new ObjectInputStream(cliente.getInputStream());
            System.out.println("conecção aceita");

            while (ativo) {
                String op = "";
                try {
                    op = (String) entrada.readObject();
                } catch (IOException e) {
                    continue;
                }

                switch (op) {
                    case "logar":
                        String usuario;
                        usuario = (String) entrada.readObject();
                        System.out.println("recebido usuario" + usuario);
                        String senha = (String) entrada.readObject();
                        System.out.println("recebido senha" + senha);
                        boolean autenticar = new DAO().autenticar(usuario, senha);
                        saida.flush();
                        saida.writeObject(autenticar);
                        saida.flush();
                        break;
                    case "salvarMidia":    //client para servidor principal       
                        receberArquivo();
                        break;
                    case "salvar":    //servidor para servidores secundarios                  
                        salvar();
                        break;
                    case "getMidia": //servidor principal para servidores de armazenamento
                        //pega sua parte e envia
                        getMidia();
                        break;
                    case "getVideo":
                        getVideo();
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

    private void getVideo() throws IOException, ClassNotFoundException {
        //client que pede
        //chama os 2 servidores de armazenamento pega a parte de cada e envia para o client

        String url = new DAO().getUrlVideo((String) entrada.readObject());
        String[] split = url.split("|");
        saida = saidaSuperman;
        entrada = entradaSuperman;
        byte[] arq = getVideoFromServer(split[0], SUPERMAN);
        saida = saidaSentinela;
        entrada = entradaSentinela;
        byte[] arq2 = getVideoFromServer(split[1], SENTINELA);
        byte[] arquivo = ArrayUtils.addAll(arq, arq2);
        saida = saidaClient;
        entrada = entradaClient;
        saida.writeInt(arquivo.length);
        saida.flush();
        String[] split1 = url.split("/");
        saida.writeObject(split1[split1.length - 1]);
        saida.flush();
        saida.write(arquivo);
        saida.flush();
//        enviarArquivo(arquivo, cliente.getLocalAddress(), cliente.getPort());
    }

    private void receberArquivo() throws IOException, ClassNotFoundException {
        int tamanho = entrada.readInt();
        String nomeArquivo = (String) entrada.readObject();
        System.out.println("recebendo midia do cliente: "+cliente.getInetAddress().getHostAddress());
        byte[] arquivo = new byte[tamanho];
        entrada.readFully(arquivo);
        int part1 = tamanho / 2;
        byte[] arq1 = ArrayUtils.subarray(arquivo, 0, part1);
        byte[] arq2 = ArrayUtils.subarray(arquivo, part1, tamanho);

        String url1 = sendToServer(arq1, nomeArquivo, SUPERMAN, SUPERMANHOST);
        String url2 = sendToServer2(arq2, nomeArquivo, SENTINELA, SENTINELAHOST);
        String url = url1 + "|" + url2;
        new DAO().insertMidia(url, nomeArquivo, "", "", "");
        System.out.println("Arquivo Salvo!");
    }

//    public void enviarArquivo(byte[] msg, InetAddress addr, int port) throws SocketException, IOException {
//        DatagramPacket pkg = new DatagramPacket(msg, msg.length, addr, port);
//        DatagramSocket ds = new DatagramSocket();
//        ds.send(pkg);
//
//        ds.close();
//    }
    private byte[] getVideoFromServer(String string, int porta) throws IOException {
        saida.writeObject("getMidia");
        saida.flush();
        saida.writeObject(string);
        saida.flush();
        int tamanho = entrada.readInt();
        System.out.println("recebendo video do servidor");
        byte[] arquivo = new byte[tamanho];
        entrada.read(arquivo);
//        return getArquivo(porta, tamanho);
        return arquivo;
    }

//    private byte[] getArquivo(int porta, int tamanho) throws IOException, SocketException {
//        DatagramSocket ds = new DatagramSocket(porta);
//        byte[] midia = new byte[tamanho];
//        DatagramPacket pkg = new DatagramPacket(midia, midia.length);
//        ds.receive(pkg);
//        ds.close();
//        return midia;
//    }
    private String sendToServer(byte[] arq, String nomeArquivo, int porta, String host) throws IOException, ClassNotFoundException {
        Socket clienteSuperman = new Socket(SUPERMANHOST, SUPERMAN);
        saidaSuperman = new ObjectOutputStream(clienteSuperman.getOutputStream());
        entradaSuperman = new ObjectInputStream(clienteSuperman.getInputStream());
        saidaSuperman.writeObject("salvar");
        saidaSuperman.writeInt(arq.length);
        saidaSuperman.writeObject(nomeArquivo);
        saidaSuperman.write(arq);
        saidaSuperman.flush();
        String url = (String) entradaSuperman.readObject();
        saidaSuperman.close();
        entradaSuperman.close();
        clienteSuperman.close();
        return url;
    }

    private String sendToServer2(byte[] arq, String nomeArquivo, int porta, String host) throws IOException, ClassNotFoundException {
        Socket clienteSentinela = new Socket(SENTINELAHOST, SENTINELA);
        saidaSentinela = new ObjectOutputStream(clienteSentinela.getOutputStream());
        entradaSentinela = new ObjectInputStream(clienteSentinela.getInputStream());
        saidaSentinela.writeObject("salvar");
        saidaSentinela.writeInt(arq.length);
        saidaSentinela.writeObject(nomeArquivo);
        saidaSentinela.write(arq);
        saidaSentinela.flush();
//        enviarArquivo(arq, InetAddress.getByName(host),porta);
        String url = (String) entradaSentinela.readObject();
        saidaSentinela.close();
        entradaSentinela.close();
        clienteSentinela.close();
        return url;
    }

    private void salvar() throws IOException, ClassNotFoundException {
        int tamanho = entrada.readInt();
        String nomeArquivo = (String) entrada.readObject();
        byte[] arquivo = new byte[tamanho];
        System.out.println("recebendo arquivo servidor secundario");
        entrada.readFully(arquivo);
        String path = "c:\\" + cliente.getLocalPort() + "\\" + nomeArquivo;
        saida.writeObject(path);
        saida.flush();
        FileOutputStream fos = new FileOutputStream(path);
        fos.write(arquivo);
        ativo = false;
    }

    private void getMidia() {

    }

    private void conectarServidoresSecundarios() throws IOException {
//        if (cliente.getLocalPort() == BATMAN) {
        Socket clienteSuperman = new Socket(SUPERMANHOST, SUPERMAN);
        Socket clienteSentinela = new Socket(SENTINELAHOST, SENTINELA);
        saidaSuperman = new ObjectOutputStream(clienteSuperman.getOutputStream());
        entradaSuperman = new ObjectInputStream(clienteSuperman.getInputStream());
        saidaSentinela = new ObjectOutputStream(clienteSentinela.getOutputStream());
        entradaSentinela = new ObjectInputStream(clienteSentinela.getInputStream());
//        }
    }

}
