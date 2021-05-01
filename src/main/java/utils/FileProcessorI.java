package utils;

import java.io.File;

public interface FileProcessorI {

    String getChecksum(String hash);
    File getFile();
}
