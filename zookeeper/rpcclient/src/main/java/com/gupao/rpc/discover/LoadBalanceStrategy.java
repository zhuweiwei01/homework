package com.gupao.rpc.discover;

import java.util.List;

public interface LoadBalanceStrategy {
    String selectHost(List<String> serviceRepos);
}
