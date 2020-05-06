/*
*
* Copyright 2013 Netflix, Inc.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
*/
package com.netflix.loadbalancer;

import java.util.List;

/**
 *
 * AbstractLoadBalancer包含大多数负载均衡实现所需的功能.
 *
 * 一个典型的LoadBalancer剖析包括:
 * 1. 基于特定条件可能被捕获的服务器(节点)列表.
 * 2. 通过<code>IRule</code>定义并实现loadbal策略的类
 * 3. 定义并实现一种机制来确定列表中节点/服务器的适用性/可用性的类.
 * 
 * An anatomy of a typical LoadBalancer consists of 1. A List of Servers (nodes)
 * that are potentially bucketed based on a specific criteria. 2. A Class that
 * defines and implements a LoadBalacing Strategy via <code>IRule</code> 3. A
 * Class that defines and implements a mechanism to determine the
 * suitability/availability of the nodes/servers in the List.
 * 
 * 
 * @author stonse
 * 
 */
public abstract class AbstractLoadBalancer implements ILoadBalancer {
    
    public enum ServerGroup{
        ALL,
        STATUS_UP,
        STATUS_NOT_UP        
    }
        
    /**
     * delegate to {@link #chooseServer(Object)} with parameter null.
     */
    public Server chooseServer() {
    	return chooseServer(null);
    }

    
    /**
     * List of servers that this Loadbalancer knows about
     * 
     * @param serverGroup Servers grouped by status, e.g., {@link ServerGroup#STATUS_UP}
     */
    public abstract List<Server> getServerList(ServerGroup serverGroup);
    
    /**
     * 获取负载均衡相关的状态
     */
    public abstract LoadBalancerStats getLoadBalancerStats();    
}
