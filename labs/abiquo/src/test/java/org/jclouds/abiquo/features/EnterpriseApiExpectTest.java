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
import static org.testng.Assert.assertNull;

import java.io.IOException;
import java.net.URI;

import javax.ws.rs.core.Response.Status;

import org.jclouds.abiquo.AbiquoApi;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

import com.abiquo.model.rest.RESTLink;
import com.abiquo.server.core.enterprise.DatacenterLimitsDto;
import com.abiquo.server.core.enterprise.EnterpriseDto;

/**
 * Expect tests for the {@link EnterpriseApi} class.
 * 
 * @author Sergi Castro
 */
@Test(groups = "unit", testName = "EnterpriseApiExpectTest")
public class EnterpriseApiExpectTest extends BaseAbiquoRestApiExpectTest<EnterpriseApi> {

   @Override
   protected EnterpriseApi clientFrom(AbiquoApi api) {
      return api.getEnterpriseApi();
   }

   public void testGetLimit() throws SecurityException, NoSuchMethodException, IOException {
      EnterpriseApi api = requestSendsResponse(
            HttpRequest.builder().method("GET")
                  .endpoint(URI.create("http://localhost/api/admin/enterprises/1/limits/1"))
                  .addHeader("Authorization", basicAuth).addHeader("Accept", normalize(DatacenterLimitsDto.MEDIA_TYPE))
                  .build(),
            HttpResponse
                  .builder()
                  .statusCode(Status.OK.getStatusCode())
                  .payload(
                        payloadFromResourceWithContentType("/payloads/limit.xml",
                              normalize(DatacenterLimitsDto.MEDIA_TYPE))).build());

      EnterpriseDto enterprise = new EnterpriseDto();
      RESTLink link = new RESTLink("limits", "http://localhost/api/admin/enterprises/1/limits");
      enterprise.addLink(link);

      DatacenterLimitsDto limit = api.getLimit(enterprise, 1);
      RESTLink editLink = limit.getEditLink();
      assertNotNull(editLink);
      assertEquals(editLink.getId().intValue(), 1);
      assertEquals(limit.getId().intValue(), 1);
      RESTLink entLink = limit.searchLink("enterprise");
      assertNotNull(entLink);
      assertEquals(entLink.getId().intValue(), 1);
   }

   public void testGetLimitNotExistent() throws SecurityException, NoSuchMethodException, IOException {
      EnterpriseApi api = requestSendsResponse(
            HttpRequest.builder().method("GET")
                  .endpoint(URI.create("http://localhost/api/admin/enterprises/1/limits/1"))
                  .addHeader("Authorization", basicAuth).addHeader("Accept", normalize(DatacenterLimitsDto.MEDIA_TYPE))
                  .build(), HttpResponse.builder().statusCode(Status.NOT_FOUND.getStatusCode()).build());

      EnterpriseDto enterprise = new EnterpriseDto();
      RESTLink link = new RESTLink("limits", "http://localhost/api/admin/enterprises/1/limits");
      enterprise.addLink(link);

      DatacenterLimitsDto limit = api.getLimit(enterprise, 1);
      assertNull(limit);
   }
}
