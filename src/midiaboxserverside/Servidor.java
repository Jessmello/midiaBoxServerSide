package midiaboxserverside;

import Model.DAO;
import Model.Midia;
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
import java.net.SocketException;
import java.sql.SQLException;
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

    private static final int SUPERMAN = 12346;
    private static final int SENTINELA = 12347;
    private static final String SUPERMANHOST = "localhost";
    private static final String SENTINELAHOST = "localhost";
    private boolean ativo = true;

    public Servidor(Socket cliente) {
        this.cliente = cliente;
    }

    @Override
    public void run() {
        try {
            saidaClient = new ObjectOutputStream(cliente.getOutputStream());
            entradaClient = new ObjectInputStream(cliente.getInputStream());
            saida = saidaClient;
            entrada = entradaClient;
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
                    case "getLista":    //servidor para servidores secundarios                  
                        listar();
                        break;
                    case "getMidia": //servidor principal para servidores de armazenamento
                        //pega sua parte e envia
                        getMidia();
                        break;
                    case "getVideo":
                        getVideo();
                        break;
                }
            }

        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void getVideo() throws IOException, ClassNotFoundException {
        //client que pede
        //chama os 2 servidores de armazenamento pega a parte de cada e envia para o client
        Midia midia = new DAO().getUrlVideo((String) entrada.readObject());
        System.out.println("Solicitando midia aos servidores secundarios " );
        String url = midia.getUrl();
        String nomeArquivo = midia.getNome();
        String[] split = url.split("[|]");

        byte[] arq = getFromSuperman(split);
        byte[] arq2 = getFromSentinela(split);
        byte[] arquivo = ArrayUtils.addAll(arq, arq2);
        saida = saidaClient;
        entrada = entradaClient;
        saida.writeInt(arquivo.length);
        saida.writeObject(nomeArquivo);
        saida.write(arquivo);
        saida.flush();
    }

    private byte[] getFromSentinela(String[] split) throws IOException {
        Socket clienteSentinela = new Socket(SENTINELAHOST, SENTINELA);
        System.out.println("Pegando midia no servidor: " + clienteSentinela.getInetAddress().getHostAddress());
        saidaSentinela = new ObjectOutputStream(clienteSentinela.getOutputStream());
        entradaSentinela = new ObjectInputStream(clienteSentinela.getInputStream());
        saida = saidaSentinela;
        entrada = entradaSentinela;
        byte[] arq2 = getVideoFromServer(split[1]);
        saidaSentinela.close();
        entradaSentinela.close();
        clienteSentinela.close();
        return arq2;
    }

    private byte[] getFromSuperman(String[] split) throws IOException {
        Socket clienteSuperman = new Socket(SUPERMANHOST, SUPERMAN);
        System.out.println("Pegando midia no servidor: " + clienteSuperman.getInetAddress().getHostAddress());
        saidaSuperman = new ObjectOutputStream(clienteSuperman.getOutputStream());
        entradaSuperman = new ObjectInputStream(clienteSuperman.getInputStream());
        saida = saidaSuperman;
        entrada = entradaSuperman;
        byte[] arq = getVideoFromServer(split[0]);
        saidaSuperman.close();
        entradaSuperman.close();
        clienteSuperman.close();
        return arq;
    }

    private void receberArquivo() throws IOException, ClassNotFoundException {
        int tamanho = entrada.readInt();
        String nomeArquivo = (String) entrada.readObject();
        nomeArquivo = nomeArquivo.replace(" ", "_");
        System.out.println("recebendo midia do cliente: " + cliente.getInetAddress().getHostAddress());
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

    private String sendToServer(byte[] arq, String nomeArquivo, int porta, String host) throws IOException, ClassNotFoundException {
        Socket clienteSuperman = new Socket(SUPERMANHOST, SUPERMAN);
        System.out.println("Enviando midia para servidor: " + clienteSuperman.getInetAddress().getHostAddress());
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
        System.out.println("Enviando midia para servidor: " + clienteSentinela.getInetAddress().getHostAddress());
        saidaSentinela = new ObjectOutputStream(clienteSentinela.getOutputStream());
        entradaSentinela = new ObjectInputStream(clienteSentinela.getInputStream());
        saidaSentinela.writeObject("salvar");
        saidaSentinela.writeInt(arq.length);
        saidaSentinela.writeObject(nomeArquivo);
        saidaSentinela.write(arq);
        saidaSentinela.flush();
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
        System.out.println("salvando arquivo no servidor secundario");
        entrada.readFully(arquivo);
        String path = "c:\\" + cliente.getLocalPort() + "\\" + nomeArquivo;
        saida.writeObject(path);
        saida.flush();
        FileOutputStream fos = new FileOutputStream(path);
        fos.write(arquivo);
        ativo = false;
    }
    
    private byte[] getVideoFromServer(String string) throws IOException {
        saida.writeObject("getMidia");
        saida.writeObject(string);
        saida.flush();
        int tamanho = entrada.readInt();
        byte[] arquivo = new byte[tamanho];
        entrada.readFully(arquivo);
        return arquivo;
    }

    private void getMidia() throws IOException, ClassNotFoundException {
        String urlArquivo = (String) entrada.readObject();
        System.out.println("buscando midia no servidor secundario");
        FileInputStream fileInputStream;
        File file = new File(urlArquivo);
        byte[] bFile = new byte[(int) file.length()];
        fileInputStream = new FileInputStream(file);
        fileInputStream.read(bFile);
        fileInputStream.close();
        saida.writeInt(bFile.length);
        saida.write(bFile);
        saida.flush();
    }

    private void listar() throws IOException, SQLException, ClassNotFoundException {
        System.out.println("Carregando lista de arquivos para cliente"+ cliente.getInetAddress().getHostAddress());
        
        saida.writeObject(new DAO().listar());
        saida.flush();
    }

}
