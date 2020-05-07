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

/**
 * 定义我们如何"ping"服务器以检查其是否存在的接口
 * @author stonse
 *
 */
public interface IPing {
    
    /**
     * 检查给定<code>Server</code>是否还"存活",即在负载平衡时应被视为候选.
     * 
     */
    public boolean isAlive(Server server);
}
