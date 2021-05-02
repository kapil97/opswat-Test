package calls;

import java.io.File;

public interface RestCallsI {
    void setFile(String filepath);
    void setApikey(String apiKey);
    boolean ifHashExists(String hash);
    boolean uploadFile();
}
