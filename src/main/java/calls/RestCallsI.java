package calls;

import java.io.File;

public interface RestCallsI {
    void testJsonGETReq();
    void setFile(File file);
    void setApikey(String apiKey);
    public boolean ifHashExists(String hash);
}
