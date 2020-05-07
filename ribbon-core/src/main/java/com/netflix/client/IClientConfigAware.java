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

import com.netflix.client.config.IClientConfig;

/**
 * There are multiple classes (and components) that need access to the configuration.
 * 有多个类(和组件)需要访问配置.
 * 通过使用{@link IClientConfig}作为携带这些配置的对象,并定义一个需要这些配置的组件可以实现的公共接口,从而更容易做到这一点.
 * Its easier to do this by using {@link IClientConfig} as the object that carries these configurations
 * and to define a common interface that components that need this can implement and hence be aware of.
 *
 * @author stonse
 * @author awang 
 *
 */
public interface IClientConfigAware {

    /**
     * Concrete implementation should implement this method so that the configuration set via 
     * {@link IClientConfig} (which in turn were set via Archaius properties) will be taken into consideration
     *
     * @param clientConfig
     */
    public abstract void initWithNiwsConfig(IClientConfig clientConfig);
    
}
