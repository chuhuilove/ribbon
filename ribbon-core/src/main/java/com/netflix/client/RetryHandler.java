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
package com.netflix.client;

import java.net.ConnectException;

/**
 * A handler that determines if an exception is retriable for load balancer,
 * and if an exception or error response should be treated as circuit related failures
 * so that the load balancer can avoid such server.
 *  
 * @author awang
 */
public interface RetryHandler {

    public static final RetryHandler DEFAULT = new DefaultLoadBalancerRetryHandler();
    
    /**
     * Test if an exception is retriable for the load balancer
     * 测试负载平衡器是否可以重试异常
     * 
     * @param e 原始的异常
     * @param sameServer 如果为true,则该方法正在尝试确定是否可以在同一服务器上完成重试.
     *                  否则,它将测试是否可以在不同的服务器上进行重试
     */
    public boolean isRetriableException(Throwable e, boolean sameServer);

    /**
     * Test if an exception should be treated as circuit failure. For example, 
     * a {@link ConnectException} is a circuit failure. This is used to determine
     * whether successive exceptions of such should trip the circuit breaker to a particular
     * host by the load balancer. If false but a server response is absent, 
     * load balancer will also close the circuit upon getting such exception.
     */
    public boolean isCircuitTrippingException(Throwable e);
        
    /**
     * @return Number of maximal retries to be done on one server
     */
    public int getMaxRetriesOnSameServer();

    /**
     * @return Number of maximal different servers to retry
     */
    public int getMaxRetriesOnNextServer();
}
