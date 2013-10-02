/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.trmk.vcloudexpress.config;

import javax.inject.Singleton;

import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.trmk.vcloud_0_8.TerremarkVCloudApi;
import org.jclouds.trmk.vcloud_0_8.config.TerremarkVCloudHttpApiModule;
import org.jclouds.trmk.vcloudexpress.TerremarkVCloudExpressApi;

import com.google.inject.Provides;

/**
 * Configures the VCloud authentication service connection, including logging
 * and http transport.
 * 
 * @author Adrian Cole
 */
@ConfiguresRestClient
public class TerremarkVCloudExpressHttpApiModule extends
        TerremarkVCloudHttpApiModule<TerremarkVCloudExpressApi> {

   @Provides
   @Singleton
   protected TerremarkVCloudApi provideVCloudApi(TerremarkVCloudExpressApi in) {
      return in;
   }

}
