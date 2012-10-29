package org.jclouds.abiquo.domain.cloud;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import java.util.List;

import org.jclouds.abiquo.internal.BaseAbiquoApiLiveApiTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Test(groups = "api", testName = "LayersLiveApiTest")
public class LayersLiveApiTest extends BaseAbiquoApiLiveApiTest {
   private VirtualMachine vm1;

   private VirtualMachine vm2;

   private VirtualMachine vm3;

   @BeforeClass
   public void setupLayers() {
      vm1 = VirtualMachine.Builder.fromVirtualMachine(env.virtualMachine).build();
      vm1.setLayer("layer1");
      vm1.save();

      vm2 = VirtualMachine.Builder.fromVirtualMachine(env.virtualMachine).build();
      vm2.setLayer("layer1");
      vm2.save();

      vm3 = VirtualMachine.Builder.fromVirtualMachine(env.virtualMachine).build();
      vm3.setLayer("layer2");
      vm3.save();
   }

   @AfterClass
   public void tearDownLayers() {
      vm1.delete();
      vm2.delete();
      vm3.delete();
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
