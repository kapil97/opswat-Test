package calls;

public class ScanResult {
    String engine, threat_found, def_time;
    int scan_result;

    public ScanResult(String engine, String threat_found, String def_time, int scan_result) {
        this.engine = engine;
        this.threat_found = threat_found;
        this.def_time = def_time;
        this.scan_result = scan_result;
    }

    @Override
    public String toString() {
        return "engine: "+ engine +"\n"
                +"threat_found: "+threat_found+"\n"
                +"scan_result: "+scan_result+"\n"
                +"def_time: "+def_time;
    }

}
