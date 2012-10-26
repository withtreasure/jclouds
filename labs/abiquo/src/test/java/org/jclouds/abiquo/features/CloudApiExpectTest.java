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

package org.jclouds.abiquo.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.net.URI;

import org.jclouds.abiquo.AbiquoApi;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

import com.abiquo.server.core.cloud.VirtualMachinesWithNodeExtendedDto;

/**
 * Expect tests for the {@link CloudApi} class.
 * 
 * @author Ignasi Barrera
 */
@Test(groups = "unit", testName = "CloudApiExpectTest")
public class CloudApiExpectTest extends BaseAbiquoRestApiExpectTest<CloudApi> {
   public void testListAllVirtualMachinesWhenResponseIs2xx() {
      CloudApi api = requestSendsResponse(
            HttpRequest.builder() //
                  .method("GET") //
                  .endpoint(URI.create("http://localhost/api/cloud/virtualmachines")) //
                  .addHeader("Authorization", basicAuth) //
                  .addHeader("Accept", normalize(VirtualMachinesWithNodeExtendedDto.MEDIA_TYPE)) //
                  .build(),
            HttpResponse
                  .builder()
                  .statusCode(200)
                  .payload(
                        payloadFromResourceWithContentType("/payloads/all-vms.xml",
                              normalize(VirtualMachinesWithNodeExtendedDto.MEDIA_TYPE))) //
                  .build());

      VirtualMachinesWithNodeExtendedDto vms = api.listAllVirtualMachines();
      assertEquals(vms.getCollection().size(), 1);
      assertEquals(vms.getCollection().get(0).getId(), Integer.valueOf(1));
      assertEquals(vms.getCollection().get(0).getName(), "VM");
      assertNotNull(vms.getCollection().get(0).getEditLink());
   }

   @Override
   protected CloudApi clientFrom(final AbiquoApi api) {
      return api.getCloudApi();
   }
}
