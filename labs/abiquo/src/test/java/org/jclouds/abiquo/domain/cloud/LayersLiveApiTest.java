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
package org.jclouds.abiquo.domain.cloud;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import java.util.List;

import org.jclouds.abiquo.AbiquoApi;
import org.jclouds.abiquo.AbiquoAsyncApi;
import org.jclouds.abiquo.internal.BaseAbiquoApiLiveApiTest;
import org.jclouds.rest.RestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Test(groups = "api", testName = "LayersLiveApiTest")
public class LayersLiveApiTest extends BaseAbiquoApiLiveApiTest {

   private Layer l1;

   private Layer l2;

   private VirtualMachine vm_l1;

   private VirtualMachine vmother_l1;

   private VirtualMachine vm_l2;

   @BeforeClass
   public void setupLayers() {

      @SuppressWarnings("unchecked")
      RestContext<AbiquoApi, AbiquoAsyncApi> abqContext = (RestContext<AbiquoApi, AbiquoAsyncApi>) context;

      vm_l1 = VirtualMachine.Builder.fromVirtualMachine(env.virtualMachine).build();
      vm_l1.save();

      vm_l2 = VirtualMachine.Builder.fromVirtualMachine(env.virtualMachine).build();
      vm_l2.save();

      l1 = Layer.builder(abqContext).name("layer1").virtualMachine(vm_l1).build();
      l1.save();

      l2 = Layer.builder(abqContext).name("layer2").virtualMachine(vm_l2).build();
      l2.save();

      // creates a new virtual machine inside the layer
      vmother_l1 = VirtualMachine.Builder.fromVirtualMachine(env.virtualMachine).layer(l1).build();
      vmother_l1.save();
   }

   @AfterClass
   public void tearDownLayers() {
      vmother_l1.delete();

      l1.delete();
      l2.delete();

      vm_l1.delete();
      vm_l2.delete();
   }

   // Returns layers with its virtualmachines, so in that case we should have
   // two LayersDto. First
   // would contain three links, edit link, vm1, vm2 and secod would contain two
   // links, edit link
   // and vm3 link
   public void testListVirtualMachinesByLayers() {
      List<Layer> layers = env.virtualAppliance.listLayers();
      assertEquals(layers.size(), 2);
   }

   public void testListVirtualMachinesByLayer() {
      Layer layer = env.virtualAppliance.getLayer("layer2");
      assertEquals(layer.getName(), "layer2");
   }

   public void testGetVirtualMachinesInLayer() {
      assertEquals(l1.getVirtualMachines().size(), 2);
      assertEquals(l2.getVirtualMachines().size(), 1);
   }

   public void testUpdateLayer() {
      Layer layer = env.virtualAppliance.getLayer("layer2");
      layer.setName("updatedName");
      layer.update();

      Layer updated = env.virtualAppliance.getLayer("updatedName");
      assertNotNull(updated);

      Layer old = env.virtualAppliance.getLayer("layer2");
      assertNull(old);
   }
}
