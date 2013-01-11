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

package org.jclouds.abiquo.domain.infrastructure;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jclouds.abiquo.domain.DomainWrapper;
import org.jclouds.abiquo.domain.enterprise.Enterprise;
import org.jclouds.abiquo.domain.enterprise.options.EnterpriseOptions;
import org.jclouds.abiquo.internal.BaseAbiquoApiLiveApiTest;
import org.jclouds.abiquo.predicates.infrastructure.DatacenterPredicates;
import org.jclouds.abiquo.predicates.infrastructure.TierPredicates;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import com.abiquo.server.core.enterprise.DatacentersLimitsDto;
import com.abiquo.server.core.enterprise.EnterpriseDto;
import com.abiquo.server.core.enterprise.EnterprisesDto;
import com.google.common.collect.Iterables;

/**
 * Live integration tests for the {@link StorageDevice} domain class.
 * 
 * @author Ignasi Barrera
 * @author Francesc Montserrat
 */
@Test(groups = "api", testName = "TierLiveApiTest")
public class TierLiveApiTest extends BaseAbiquoApiLiveApiTest {

   @AfterClass
   public void restoreTiers() {
      env.tier.allowTierToAllEnterprises();

      DatacentersLimitsDto dto = env.enterpriseApi.getLimits(env.enterprise.unwrap(), env.datacenter.unwrap());
      assertNotNull(dto.getCollection().get(0).searchLink("tier"));
      assertNotNull(dto.getCollection().get(0).searchLinkByHref(env.tier.unwrap().getEditLink().getHref()));
   }

   public void testUpdate() {
      Tier tier = env.datacenter.listTiers().get(0);
      assertNotNull(tier);

      String previousName = tier.getName();
      tier.setName("Updated tier");
      tier.update();

      // Recover the updated tier
      Tier updated = env.datacenter.findTier(TierPredicates.name("Updated tier"));
      assertEquals(updated.getName(), "Updated tier");

      // Set original name
      tier.setName(previousName);
      tier.update();
   }

   public void testListTiers() {
      Iterable<Tier> tiers = env.datacenter.listTiers();
      assertEquals(Iterables.size(tiers), 4);

      tiers = env.datacenter.listTiers(TierPredicates.name("FAIL"));
      assertEquals(Iterables.size(tiers), 0);
   }

   public void testAllowTierToAllEnterprises() {
      Tier tier = env.datacenter.getTier(env.datacenter.listTiers().get(0).getId());
      tier.allowTierToAllEnterprises();

      DatacentersLimitsDto dto = env.enterpriseApi.getLimits(env.enterprise.unwrap(), env.datacenter.unwrap());
      assertNotNull(dto.getCollection().get(0).searchLink("tier"));
      assertNotNull(dto.getCollection().get(0).searchLinkByHref(tier.unwrap().getEditLink().getHref()));
   }

   public void testRestrictTierToAllEnterprises() {
      Tier tier = env.datacenter.getTier(env.datacenter.listTiers().get(0).getId());
      tier.restrictTierToAllEnterprises(true);

      DatacentersLimitsDto dto = env.enterpriseApi.listLimits(env.enterprise.unwrap());
      assertNull(dto.searchLinkByHref(tier.getURI().getPath()));
   }

   public void testGetEnterprisesByTier() {
      int enterpriseAllowed = 0;
      List<EnterpriseDto> ents = env.enterpriseApi.listEnterprises().getCollection();
      for (EnterpriseDto ent : ents) {
         Enterprise e = DomainWrapper.wrap(env.context.getApiContext(), Enterprise.class, ent);
         if (e.findAllowedDatacenter(DatacenterPredicates.id(env.datacenter.getId())) != null) {
            enterpriseAllowed++;
         }
      }

      Tier tier = env.datacenter.getTier(env.datacenter.listTiers().get(0).getId());
      assertEquals(tier.getEnterprisesByTier().getCollection().size(), enterpriseAllowed);
   }

   public void testGetEnterprisesByTierwithOptions() {
      String entName = StringUtils.EMPTY;

      List<EnterpriseDto> ents = env.enterpriseApi.listEnterprises().getCollection();
      for (EnterpriseDto ent : ents) {
         Enterprise e = DomainWrapper.wrap(env.context.getApiContext(), Enterprise.class, ent);
         if (e.findAllowedDatacenter(DatacenterPredicates.id(env.datacenter.getId())) != null) {
            entName = e.getName();
            break;
         }
      }

      EnterpriseOptions options = EnterpriseOptions.builder().has(entName).limit(1).build();
      Tier tier = env.datacenter.getTier(env.datacenter.listTiers().get(0).getId());

      EnterprisesDto entsDto = tier.getEnterprisesByTier(options);

      assertEquals(entsDto.getCollection().size(), 1);
      assertEquals(entsDto.getCollection().get(0).getName(), entName);
   }
}
