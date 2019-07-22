package com.gupao.rpc.discover;

import java.util.List;

public abstract class AbstractLoadBalanceStrategy implements LoadBalanceStrategy {
    @Override
    public String selectHost(List<String> serviceRepos) {
        if(serviceRepos == null || serviceRepos.size() <= 0) {
            return null;
        }
        if(serviceRepos.size() == 1) {
            return serviceRepos.get(0);
        }
        return doSelect(serviceRepos);
    }

    protected abstract String doSelect(List<String> serviceRepos);
}
