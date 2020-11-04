package ru.stankin.informationSecurity.entities;

import javax.crypto.*;
import javax.crypto.spec.ChaCha20ParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;


public class ChaCha {

    private static final String ENCRYPT_ALGORITHM = "ChaCha20";

    /**  Принимает в себя:
     *   boolean isEncrypting - шифрование(true), дешифрование(false);
     *   byte[] text - текст для шифрования/дешифрования;
     *   SecretKey key - ключ, полученный при помощи статического метода getKey() этого же класса;
     *   byte[] nonce - nonce, полученный при помощи статического метода getNonce() этого же классa;
     *   int counter - походу номер провайдера (не уверен) **/
    public byte[] processText(boolean isEncrypting, byte[] text, SecretKey key, byte[] nonce, int counter) throws NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException,
            IllegalBlockSizeException {

        Cipher cipher = Cipher.getInstance(ENCRYPT_ALGORITHM);
        ChaCha20ParameterSpec params = new ChaCha20ParameterSpec(nonce, counter);
        if (isEncrypting) {
            cipher.init(Cipher.ENCRYPT_MODE, key, params);
        } else if (!isEncrypting) {
            cipher.init(Cipher.DECRYPT_MODE, key, params);
        }
        return cipher.doFinal(text);
    }

    public static SecretKey getKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("ChaCha20");
        keyGen.init(256, SecureRandom.getInstanceStrong());
        return keyGen.generateKey();
    }

    public static byte[] getNonce() {
        byte[] newNonce = new byte[12];
        new SecureRandom().nextBytes(newNonce);
        return newNonce;
    }
}
