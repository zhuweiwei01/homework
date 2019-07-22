package com.gupao.rpc.discover;

import java.util.List;
import java.util.Random;

public class RandomLoadBalance extends AbstractLoadBalanceStrategy {

    @Override
    protected String doSelect(List<String> serviceRepos) {
        Random random = new Random();
        int i = random.nextInt(serviceRepos.size());
        return serviceRepos.get(i);
    }
}
