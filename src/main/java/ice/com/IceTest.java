package ice.com;


import common.com.CommnTest;
import org.moqui.context.ExecutionContext;

import java.util.HashMap;
import java.util.Map;

public class IceTest {
    public Map<String, Object> IcePrint(ExecutionContext ec) {
        Map<String, Object> result = new HashMap<>();
        CommnTest ct = new CommnTest();
        ct.commonPrint(ec);
        System.out.println("iceDocument-----------------iceDocument");

        return result;
    }
}
