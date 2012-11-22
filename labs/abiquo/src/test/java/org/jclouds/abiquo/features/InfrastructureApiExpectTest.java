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

import java.io.IOException;
import java.net.URI;

import javax.ws.rs.core.Response.Status;

import org.jclouds.abiquo.AbiquoApi;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

import com.abiquo.model.rest.RESTLink;
import com.abiquo.server.core.enterprise.EnterprisesDto;
import com.abiquo.server.core.infrastructure.storage.TierDto;

/**
 * Expect tests for the {@link InfrastructureApi} class.
 * 
 * @author Sergi Castro
 */
@Test(groups = "unit", testName = "InfrastuctureApiExpectTest")
public class InfrastructureApiExpectTest extends BaseAbiquoRestApiExpectTest<InfrastructureApi> {

   @Override
   protected InfrastructureApi clientFrom(AbiquoApi api) {
      return api.getInfrastructureApi();
   }

   public void testAllowTierToAllEnterprises() throws SecurityException, NoSuchMethodException, IOException {
      InfrastructureApi api = requestSendsResponse(
            HttpRequest
                  .builder()
                  .method("PUT")
                  .endpoint(
                        URI.create("http://localhost/api/admin/datacenters/1/storage/tiers/1/action/allowallenterprises"))
                  .addHeader("Authorization", basicAuth).build(),
            HttpResponse.builder().statusCode(Status.NO_CONTENT.getStatusCode()).build());

      TierDto tier = new TierDto();
      RESTLink link = new RESTLink("allowallenterprises",
            "http://localhost/api/admin/datacenters/1/storage/tiers/1/action/allowallenterprises");
      tier.addLink(link);

      api.allowTierToAllEnterprises(tier);
   }

   public void testRestrictTierToAllEnterprises() throws SecurityException, NoSuchMethodException, IOException {
      InfrastructureApi api = requestSendsResponse(
            HttpRequest
                  .builder()
                  .method("PUT")
                  .endpoint(
                        URI.create("http://localhost/api/admin/datacenters/1/storage/tiers/1/action/restrictallenterprises"))
                  .addHeader("Authorization", basicAuth).addQueryParam("force", "true").build(), HttpResponse.builder()
                  .statusCode(Status.NO_CONTENT.getStatusCode()).build());

      TierDto tier = new TierDto();
      RESTLink link = new RESTLink("restrictallenterprises",
            "http://localhost/api/admin/datacenters/1/storage/tiers/1/action/restrictallenterprises");
      tier.addLink(link);

      api.restrictTierToAllEnterprises(tier, Boolean.TRUE);
   }

   public void testGetEnterprisesByTier() {
      InfrastructureApi api = requestSendsResponse(
            HttpRequest.builder().method("GET")
                  .endpoint(URI.create("http://localhost/api/admin/datacenters/1/storage/tiers/1/enterprises"))
                  .addHeader("Authorization", basicAuth).addHeader("Accept", normalize(EnterprisesDto.MEDIA_TYPE))
                  .build(),
            HttpResponse
                  .builder()
                  .statusCode(200)
                  .payload(
                        payloadFromResourceWithContentType("/payloads/enterprisesbytier.xml",
                              normalize(EnterprisesDto.MEDIA_TYPE))).build());

      TierDto tier = new TierDto();
      RESTLink link = new RESTLink("enterprises",
            "http://localhost/api/admin/datacenters/1/storage/tiers/1/enterprises");
      tier.addLink(link);
      EnterprisesDto enterprises = api.getEnterprisesByTier(tier);

      assertEquals(enterprises.getCollection().size(), 1);
      assertEquals(enterprises.getCollection().get(0).getId(), Integer.valueOf(1));

   }
}
