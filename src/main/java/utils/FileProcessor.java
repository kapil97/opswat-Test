package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Class to process the file
 */
public class FileProcessor{
    private String inputFile;

    /**
     *
     * @param inputFile path to the input file
     */
    public FileProcessor(String inputFile){
        this.inputFile = inputFile;
    }

    /**
     * Get the checksum for the file by specifying the Algorithm
     * @param hash hash need to be calculated: MD5, SHA1, SHA256
     * @return the hash value of the file
     */
    public String getChecksum(String hash){
        MessageDigest messageDigest = null;
        File file = null;
        try{
            messageDigest = MessageDigest.getInstance(hash);
            file = new File(inputFile);
        }
        catch (NoSuchAlgorithmException exception){
            System.err.println("No such Algorithm found please enter appropriate file hashing Algorithm! Such as MD5, SHA-256");
        }

        return getFileChecksum(messageDigest,file);
    }

    /**
     *
     * @param digest md5, sha1, sha256
     * @param file input file of which the hash need to be calculated
     * @return the hash value
     */
    private String getFileChecksum(MessageDigest digest, File file){
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] byteArray = new byte[1024];
            int bytesCount;

            while ((bytesCount = fileInputStream.read(byteArray)) != -1) {
                digest.update(byteArray, 0, bytesCount);
            }
            fileInputStream.close();
            byte[] bytes = digest.digest();

            StringBuilder stringBuilder = new StringBuilder();
            for (byte aByte : bytes) {
                stringBuilder.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
            return stringBuilder.toString();
        }
        catch (IOException exception)
        {
            System.err.println("Error while processing the file");
        }
        return null;
    }
}
