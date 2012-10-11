package org.jclouds.abiquo.domain.infrastructure;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import org.jclouds.abiquo.domain.network.NetworkServiceType;
import org.jclouds.abiquo.internal.BaseAbiquoApiLiveApiTest;
import org.testng.annotations.Test;

/**
 * Live integration tests for the {@link NetworkServiceType} domain class.
 * 
 * @author Jaume Devesa
 */
@Test(groups = "live")
public class NetworkServiceTypeLiveApiTest extends BaseAbiquoApiLiveApiTest
{

    NetworkServiceType nst = null;

    @Test
    public void testCreate()
    {
        nst =
            NetworkServiceType.builder(env.context.getApiContext(), env.datacenter)
                .name("Storage Service").build();
        nst.save();

        assertNotNull(nst.getId());
        NetworkServiceType copy = env.datacenter.getNetworkServiceType(nst.getId());
        assertEquals(copy.getName(), nst.getName());

    }

    @Test(dependsOnMethods = "testCreate")
    public void testUpdate()
    {
        nst.setName("Storage Service Updated");
        nst.update();

        NetworkServiceType copy = env.datacenter.getNetworkServiceType(nst.getId());
        assertEquals(copy.getName(), nst.getName());
    }

    @Test(dependsOnMethods = "testUpdate")
    public void testDelete()
    {
        Integer deleteId = nst.getId();
        nst.delete();

        // Assert it is deleted
        assertNull(env.datacenter.getNetworkServiceType(deleteId));

    }
}
