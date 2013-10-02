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
package org.jclouds.trmk.vcloud_0_8.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.trmk.vcloud_0_8.TerremarkVCloudApi;
import org.jclouds.trmk.vcloud_0_8.TerremarkVCloudMediaType;
import org.jclouds.trmk.vcloud_0_8.domain.ReferenceType;
import org.jclouds.trmk.vcloud_0_8.domain.VAppTemplate;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

/**
 * @author Adrian Cole
 */
@Singleton
public class VAppTemplatesForResourceEntities implements
         Function<Iterable<? extends ReferenceType>, Iterable<? extends VAppTemplate>> {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   public Logger logger = Logger.NULL;
   private final TerremarkVCloudApi aclient;

   @Inject
   VAppTemplatesForResourceEntities(TerremarkVCloudApi aclient) {
      this.aclient = aclient;
   }

   @Override
   public Iterable<? extends VAppTemplate> apply(Iterable<? extends ReferenceType> from) {
      return transform(filter(checkNotNull(from, "named resources"), new Predicate<ReferenceType>() {
         public boolean apply(ReferenceType input) {
            return input.getType().equals(TerremarkVCloudMediaType.VAPPTEMPLATE_XML);
         }
      }), new Function<ReferenceType, VAppTemplate>() {
         public VAppTemplate apply(ReferenceType from) {
            return aclient.getVAppTemplate(from.getHref());
         }
      });
   }

}
