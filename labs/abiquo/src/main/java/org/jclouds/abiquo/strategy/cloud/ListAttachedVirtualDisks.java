/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.abiquo.strategy.cloud;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;

import javax.inject.Singleton;

import org.jclouds.abiquo.domain.cloud.HardDisk;
import org.jclouds.abiquo.domain.cloud.VirtualDisk;
import org.jclouds.abiquo.domain.cloud.VirtualMachine;
import org.jclouds.abiquo.domain.cloud.Volume;
import org.jclouds.abiquo.domain.util.LinkUtils;
import org.jclouds.abiquo.strategy.ListEntities;

import com.abiquo.model.rest.RESTLink;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.inject.Inject;

/**
 * List all {@link HardDisk} and {@link Volume} attached to a
 * {@link VirtualMachine}.
 * 
 * @author Ignasi Barrera
 */
@Singleton
public class ListAttachedVirtualDisks implements ListEntities<VirtualDisk<?>, VirtualMachine> {

   private final Function<RESTLink, VirtualDisk<?>> linkToVirtualDisk;

   @Inject
   public ListAttachedVirtualDisks(Function<RESTLink, VirtualDisk<?>> linkToVirtualDisk) {
      this.linkToVirtualDisk = checkNotNull(linkToVirtualDisk, "linkToVirtualDisk");
   }

   @Override
   public Iterable<VirtualDisk<?>> execute(VirtualMachine parent) {
      parent.refresh();
      Iterable<RESTLink> diskLinks = LinkUtils.filterExternalDiskLinks(parent.unwrap().getLinks());
      return transform(diskLinks, linkToVirtualDisk);
   }

   @Override
   public Iterable<VirtualDisk<?>> execute(VirtualMachine parent, Predicate<VirtualDisk<?>> selector) {
      return filter(execute(parent), selector);
   }
}
