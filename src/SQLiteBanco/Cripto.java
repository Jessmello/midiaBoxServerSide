package SQLiteBanco;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author Jessica
 */
public class Cripto {

    private static String key = "laissakjsdg55saasd?!";

    public byte[] criptografada(byte[] midiaPura) {

        try {

            SecretKey chaveDES = new SecretKeySpec(key.getBytes(), 0, key.length(), "DES");

            Cipher cifraDES;

            cifraDES = Cipher.getInstance("DES/ECB/PKCS5Padding");

            cifraDES.init(Cipher.ENCRYPT_MODE, chaveDES);

            return cifraDES.doFinal(midiaPura);

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            Logger.getLogger(Cripto.class.getName()).log(Level.SEVERE, null, e);
        }

        return null;
    }

    public byte[] descriptografada(byte[] midiaEncriptada) {

        try {

            SecretKey chaveDES = new SecretKeySpec(key.getBytes(), 0, key.length(), "DES");

            Cipher cifraDES;

            cifraDES = Cipher.getInstance("DES/ECB/PKCS5Padding");

            cifraDES.init(Cipher.DECRYPT_MODE, chaveDES);

            return cifraDES.doFinal(midiaEncriptada);

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            Logger.getLogger(Cripto.class.getName()).log(Level.SEVERE, null, e);
        }

        return null;

    }

}
