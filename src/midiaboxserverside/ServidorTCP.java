package midiaboxserverside;

import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServidorTCP {

    public void enviarDadosArquivo(int tamanhoArquivo, String nomeArquivo) {
        try {
            ServerSocket servidor = new ServerSocket(12345);
            while (true) {
                try (Socket cliente = servidor.accept()) {
                    String ipCliente = cliente.getInetAddress().getHostAddress();
                    ObjectOutputStream saida = new ObjectOutputStream(cliente.getOutputStream());
                    saida.flush();
                    saida.writeObject(tamanhoArquivo);
                    saida.writeObject(nomeArquivo);
                    saida.close();
                }
            }
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }
}
