/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SQLiteBanco;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author Jessica
 */
public class Cripto {

    private static String key = "laissakjsdg55saasd?!";
    
    public byte[] criptografada (byte[] midiaPura) {

        try {

            
            SecretKey chaveDES = new SecretKeySpec(key.getBytes(),0,key.length(),"DES");

            Cipher cifraDES;

            // Cria a cifra 
            cifraDES = Cipher.getInstance("DES/ECB/PKCS5Padding");

            // Inicializa a cifra para o processo de encriptação
            cifraDES.init(Cipher.ENCRYPT_MODE, chaveDES);
            
            // Texto encriptado
            return cifraDES.doFinal(midiaPura);          

            
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public byte[] descriptografada (byte[] midiaEncriptada) {

        try {

            SecretKey chaveDES = new SecretKeySpec(key.getBytes(),0,key.length(),"DES");

            Cipher cifraDES;

            // Cria a cifra 
            cifraDES = Cipher.getInstance("DES/ECB/PKCS5Padding");
            
            // Inicializa a cifra também para o processo de decriptação
            cifraDES.init(Cipher.DECRYPT_MODE, chaveDES);

            // Decriptografa o texto
            return cifraDES.doFinal(midiaEncriptada);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        
        return null;

    }

}


