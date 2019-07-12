/*
 * Copyright (C) 2018-Present Pivotal Software, Inc. All rights reserved.
 * This program and the accompanying materials are made available under
 * the terms of the under the Apache License, Version 2.0 (the "License”);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.pivotal.cloudcache.app.config;


import io.pivotal.cloudcache.app.model.Pizza;
import io.pivotal.cloudcache.app.model.Topping;

import javax.annotation.PostConstruct;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.CacheFactory;
import org.apache.geode.pdx.ReflectionBasedAutoSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.gemfire.config.annotation.EnableEntityDefinedRegions;
import org.springframework.data.gemfire.config.annotation.EnableLogging;
import org.springframework.data.gemfire.config.annotation.EnablePdx;
import org.springframework.data.gemfire.config.annotation.EnableSsl;
import org.springframework.data.gemfire.repository.config.EnableGemfireRepositories;
import org.springframework.geode.config.annotation.EnableDurableClient;
import org.springframework.geode.config.annotation.UseMemberName;

/**
 * This configuration is used when you start this app with !tls spring profile.
 *
 */
@Configuration
@EnablePdx
@EnableDurableClient(id = "pizza-store")
@EnableEntityDefinedRegions(basePackageClasses = { Pizza.class })
@EnableLogging
@UseMemberName("SpringBootPivotalCloudCachePizzaStoreApplication")
@SuppressWarnings("unused")
@EnableGemfireRepositories(basePackages = "io.pivotal.cloudcache.app.repository")
public class GemFireConfiguration {

	@Profile("tls")
	@Configuration
	@EnableSsl
	static class TlsConfiguration{}

	
	@Bean public ReflectionBasedAutoSerializer getSerializer() {
		
		ReflectionBasedAutoSerializer s = new ReflectionBasedAutoSerializer("io.pivotal.cloudcache.app.model.*");
		return s;
			
	}

}
