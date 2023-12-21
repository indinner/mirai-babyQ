package com.example.miraihellp.server.catchServer;

import com.example.miraihellp.server.twoClass.TwoClassServer;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author indinner
 * @Date 2023/12/21 13:41
 * @Version 1.0
 * @Doc:
 */
@Component
public class TwoClassCatch {



    public static List<String> getNewActivity() throws Exception {
        TwoClassServer twoClassServer=new TwoClassServer();
        return twoClassServer.getNewActivity();
    }


}
