package com.gupao.vip;

import com.gupao.vip.config.ZkConfig;
import org.I0Itec.zkclient.ZkClient;

import java.util.List;

public class ZkClientDemo {

    public static void main(String[] args) {
        ZkClient client = new ZkClient(ZkConfig.ZK_ADDRESS, ZkConfig.ZK_SEESION_TIMEOUT);
        for(int i=0;i<10;i++) {
            client.createEphemeralSequential("/locks/lock-", "data");
        }
        List<String> children = client.getChildren("/locks");
        for(String a : children) {
            System.out.println(a);
        }

    }
}
