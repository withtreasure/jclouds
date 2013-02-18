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

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.abiquo.binders.AppendToPath;
import org.jclouds.abiquo.binders.BindToPath;
import org.jclouds.abiquo.binders.BindToXMLPayloadAndPath;
import org.jclouds.abiquo.binders.cloud.BindMoveVolumeToPath;
import org.jclouds.abiquo.binders.cloud.BindNetworkRefToPayload;
import org.jclouds.abiquo.binders.cloud.BindVirtualDatacenterRefToPayload;
import org.jclouds.abiquo.domain.cloud.VirtualDatacenter;
import org.jclouds.abiquo.domain.cloud.options.VirtualApplianceOptions;
import org.jclouds.abiquo.domain.cloud.options.VirtualDatacenterOptions;
import org.jclouds.abiquo.domain.cloud.options.VirtualMachineOptions;
import org.jclouds.abiquo.domain.cloud.options.VirtualMachineTemplateOptions;
import org.jclouds.abiquo.domain.cloud.options.VolumeOptions;
import org.jclouds.abiquo.domain.enterprise.Enterprise;
import org.jclouds.abiquo.domain.infrastructure.Datacenter;
import org.jclouds.abiquo.domain.network.options.IpOptions;
import org.jclouds.abiquo.fallbacks.MovedVolume;
import org.jclouds.abiquo.functions.ReturnTaskReferenceOrNull;
import org.jclouds.abiquo.functions.enterprise.ParseEnterpriseId;
import org.jclouds.abiquo.functions.infrastructure.ParseDatacenterId;
import org.jclouds.abiquo.http.filters.AbiquoAuthentication;
import org.jclouds.abiquo.http.filters.AppendApiVersionToMediaType;
import org.jclouds.abiquo.reference.annotations.EnterpriseEdition;
import org.jclouds.abiquo.rest.annotations.EndpointLink;
import org.jclouds.http.functions.ReturnStringIf2xx;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.JAXBResponseParser;
import org.jclouds.rest.annotations.ParamParser;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.binders.BindToXMLPayload;

import com.abiquo.model.transport.AcceptedRequestDto;
import com.abiquo.model.transport.LinksDto;
import com.abiquo.server.core.appslibrary.VirtualMachineTemplateDto;
import com.abiquo.server.core.appslibrary.VirtualMachineTemplatesDto;
import com.abiquo.server.core.cloud.LayerDto;
import com.abiquo.server.core.cloud.LayersDto;
import com.abiquo.server.core.cloud.VirtualApplianceDto;
import com.abiquo.server.core.cloud.VirtualApplianceStateDto;
import com.abiquo.server.core.cloud.VirtualAppliancesDto;
import com.abiquo.server.core.cloud.VirtualDatacenterDto;
import com.abiquo.server.core.cloud.VirtualDatacentersDto;
import com.abiquo.server.core.cloud.VirtualMachineDto;
import com.abiquo.server.core.cloud.VirtualMachineInstanceDto;
import com.abiquo.server.core.cloud.VirtualMachineStateDto;
import com.abiquo.server.core.cloud.VirtualMachineTaskDto;
import com.abiquo.server.core.cloud.VirtualMachineWithNodeExtendedDto;
import com.abiquo.server.core.cloud.VirtualMachinesWithNodeExtendedDto;
import com.abiquo.server.core.enterprise.EnterpriseDto;
import com.abiquo.server.core.infrastructure.DatacenterDto;
import com.abiquo.server.core.infrastructure.network.PrivateIpDto;
import com.abiquo.server.core.infrastructure.network.PrivateIpsDto;
import com.abiquo.server.core.infrastructure.network.PublicIpDto;
import com.abiquo.server.core.infrastructure.network.PublicIpsDto;
import com.abiquo.server.core.infrastructure.network.VLANNetworkDto;
import com.abiquo.server.core.infrastructure.network.VLANNetworksDto;
import com.abiquo.server.core.infrastructure.network.VMNetworkConfigurationDto;
import com.abiquo.server.core.infrastructure.network.VMNetworkConfigurationsDto;
import com.abiquo.server.core.infrastructure.storage.DiskManagementDto;
import com.abiquo.server.core.infrastructure.storage.DisksManagementDto;
import com.abiquo.server.core.infrastructure.storage.MovedVolumeDto;
import com.abiquo.server.core.infrastructure.storage.TierDto;
import com.abiquo.server.core.infrastructure.storage.TiersDto;
import com.abiquo.server.core.infrastructure.storage.VolumeManagementDto;
import com.abiquo.server.core.infrastructure.storage.VolumesManagementDto;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to Abiquo Cloud API.
 * 
 * @see API: <a href="http://community.abiquo.com/display/ABI20/API+Reference">
 *      http://community.abiquo.com/display/ABI20/API+Reference</a>
 * @see CloudApi
 * @author Ignasi Barrera
 * @author Francesc Montserrat
 */
@RequestFilters({ AbiquoAuthentication.class, AppendApiVersionToMediaType.class })
@Path("/cloud")
public interface CloudAsyncApi {
   /*********************** Virtual Datacenter ***********************/

   /**
    * @see CloudApi#listVirtualDatacenters(VirtualDatacenterOptions)
    */
   @Named("vdc:list")
   @GET
   @Path("/virtualdatacenters")
   @Consumes(VirtualDatacentersDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<VirtualDatacentersDto> listVirtualDatacenters(VirtualDatacenterOptions options);

   /**
    * @see CloudApi#getVirtualDatacenter(Integer)
    */
   @Named("vdc:get")
   @GET
   @Path("/virtualdatacenters/{virtualdatacenter}")
   @Fallback(NullOnNotFoundOr404.class)
   @Consumes(VirtualDatacenterDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<VirtualDatacenterDto> getVirtualDatacenter(
         @PathParam("virtualdatacenter") Integer virtualDatacenterId);

   /**
    * @see CloudApi#createVirtualDatacenter(VirtualDatacenterDto, Datacenter,
    *      Enterprise)
    */
   @Named("vdc:create")
   @POST
   @Path("/virtualdatacenters")
   @Consumes(VirtualDatacenterDto.BASE_MEDIA_TYPE)
   @Produces(VirtualDatacenterDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<VirtualDatacenterDto> createVirtualDatacenter(
         @BinderParam(BindToXMLPayload.class) final VirtualDatacenterDto virtualDatacenter,
         @QueryParam("datacenter") @ParamParser(ParseDatacenterId.class) final DatacenterDto datacenter,
         @QueryParam("enterprise") @ParamParser(ParseEnterpriseId.class) final EnterpriseDto enterprise);

   /**
    * @see CloudApi#updateVirtualDatacenter(VirtualDatacenterDto)
    */
   @Named("vdc:update")
   @PUT
   @Consumes(VirtualDatacenterDto.BASE_MEDIA_TYPE)
   @Produces(VirtualDatacenterDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<VirtualDatacenterDto> updateVirtualDatacenter(
         @EndpointLink("edit") @BinderParam(BindToXMLPayloadAndPath.class) VirtualDatacenterDto virtualDatacenter);

   /**
    * @see CloudApi#deleteVirtualDatacenter(VirtualDatacenterDto)
    */
   @Named("vdc:delete")
   @DELETE
   ListenableFuture<Void> deleteVirtualDatacenter(
         @EndpointLink("edit") @BinderParam(BindToPath.class) VirtualDatacenterDto virtualDatacenter);

   /**
    * @see CloudApi#listAvailableTemplates(VirtualDatacenterDto)
    */
   @Named("vdc:listtemplates")
   @GET
   @Consumes(VirtualMachineTemplatesDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<VirtualMachineTemplatesDto> listAvailableTemplates(
         @EndpointLink("templates") @BinderParam(BindToPath.class) VirtualDatacenterDto virtualDatacenter);

   /**
    * @see CloudApi#listAvailableTemplates(VirtualDatacenterDto,
    *      VirtualMachineTemplateOptions)
    */
   @Named("vdc:listtemplates")
   @GET
   @Consumes(VirtualMachineTemplatesDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<VirtualMachineTemplatesDto> listAvailableTemplates(
         @EndpointLink("templates") @BinderParam(BindToPath.class) VirtualDatacenterDto virtualDatacenter,
         VirtualMachineTemplateOptions options);

   /**
    * @see CloudApi#listStorageTiers(VirtualDatacenterDto)
    */
   @Named("vdc:listtiers")
   @EnterpriseEdition
   @GET
   @Consumes(TiersDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<TiersDto> listStorageTiers(
         @EndpointLink("tiers") @BinderParam(BindToPath.class) VirtualDatacenterDto virtualDatacenter);

   /**
    * @see CloudApi#getStorageTier(VirtualDatacenterDto, Integer)
    */
   @Named("vdc:gettier")
   @EnterpriseEdition
   @GET
   @Fallback(NullOnNotFoundOr404.class)
   @Consumes(TierDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<TierDto> getStorageTier(
         @EndpointLink("tiers") @BinderParam(BindToPath.class) VirtualDatacenterDto virtualDatacenter,
         @BinderParam(AppendToPath.class) Integer tierId);

   /*********************** Public IP ***********************/

   /**
    * @see CloudApi#listAvailablePublicIps(VirtualDatacenterDto, IpOptions)
    */
   @Named("vdc:listavailablepublicips")
   @GET
   @Consumes(PublicIpsDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<PublicIpsDto> listAvailablePublicIps(
         @EndpointLink("topurchase") @BinderParam(BindToPath.class) VirtualDatacenterDto virtualDatacenter,
         IpOptions options);

   /**
    * @see CloudApi#listPurchasedPublicIps(VirtualDatacenterDto, IpOptions)
    */
   @Named("vdc:listpurchasedpublicips")
   @GET
   @Consumes(PublicIpsDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<PublicIpsDto> listPurchasedPublicIps(
         @EndpointLink("purchased") @BinderParam(BindToPath.class) VirtualDatacenterDto virtualDatacenter,
         IpOptions options);

   /**
    * @see CloudApi#purchasePublicIp(PublicIpDto)
    */
   @Named("vdc:purchasepublicip")
   @PUT
   @Consumes(PublicIpDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<PublicIpDto> purchasePublicIp(
         @EndpointLink("purchase") @BinderParam(BindToPath.class) PublicIpDto publicIp);

   /**
    * @see CloudApi#releasePublicIp(PublicIpDto)
    */
   @Named("vdc:releasepublicip")
   @PUT
   @Consumes(PublicIpDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<PublicIpDto> releasePublicIp(
         @EndpointLink("release") @BinderParam(BindToPath.class) PublicIpDto publicIp);

   /*********************** Private Network ***********************/

   /**
    * @see CloudApi#listPrivateNetworks(VirtualDatacenter)
    */
   @Named("privatenetwork:list")
   @GET
   @Consumes(VLANNetworksDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<VLANNetworksDto> listPrivateNetworks(
         @EndpointLink("privatenetworks") @BinderParam(BindToPath.class) VirtualDatacenterDto virtualDatacenter);

   /**
    * @see CloudApi#getPrivateNetwork(VirtualDatacenterDto, Integer)
    */
   @Named("privatenetwork:get")
   @GET
   @Fallback(NullOnNotFoundOr404.class)
   @Consumes(VLANNetworkDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<VLANNetworkDto> getPrivateNetwork(
         @EndpointLink("privatenetworks") @BinderParam(BindToPath.class) VirtualDatacenterDto virtualDatacenter,
         @BinderParam(AppendToPath.class) Integer privateNetworkId);

   /**
    * @see CloudApi#createPrivateNetwork(VirtualDatacenterDto, VLANNetworkDto)
    */
   @Named("privatenetwork:create")
   @POST
   @Consumes(VLANNetworkDto.BASE_MEDIA_TYPE)
   @Produces(VLANNetworkDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<VLANNetworkDto> createPrivateNetwork(
         @EndpointLink("privatenetworks") @BinderParam(BindToPath.class) VirtualDatacenterDto virtualDatacenter,
         @BinderParam(BindToXMLPayload.class) VLANNetworkDto privateNetwork);

   /**
    * @see CloudApi#updatePrivateNetwork(VLANNetworkDto)
    */
   @Named("privatenetwork:update")
   @PUT
   @Consumes(VLANNetworkDto.BASE_MEDIA_TYPE)
   @Produces(VLANNetworkDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<VLANNetworkDto> updatePrivateNetwork(
         @EndpointLink("edit") @BinderParam(BindToXMLPayloadAndPath.class) VLANNetworkDto privateNetwork);

   /**
    * @see CloudApi#deletePrivateNetwork(VLANNetworkDto)
    */
   @Named("privatenetwork:delete")
   @DELETE
   ListenableFuture<Void> deletePrivateNetwork(
         @EndpointLink("edit") @BinderParam(BindToPath.class) VLANNetworkDto privateNetwork);

   /**
    * @see CloudApi#getDefaultNetwork(VirtualDatacenterDto)
    */
   @Named("vdc:getdefaultnetwork")
   @GET
   @Consumes(VLANNetworkDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<VLANNetworkDto> getDefaultNetwork(
         @EndpointLink("defaultnetwork") @BinderParam(BindToPath.class) VirtualDatacenterDto virtualDatacenter);

   /**
    * @see CloudApi#setDefaultNetwork(VirtualDatacenterDto, VLANNetworkDto)
    */
   @Named("vdc:setdefaultnetwork")
   @PUT
   @Produces(LinksDto.BASE_MEDIA_TYPE)
   ListenableFuture<Void> setDefaultNetwork(
         @EndpointLink("defaultvlan") @BinderParam(BindToPath.class) VirtualDatacenterDto virtualDatacenter,
         @BinderParam(BindNetworkRefToPayload.class) VLANNetworkDto network);

   /*********************** Private Network IPs ***********************/

   /**
    * @see CloudApi#listPrivateNetworkIps(VLANNetworkDto)
    */
   @Named("privatenetwork:listips")
   @GET
   @Consumes(PrivateIpsDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<PrivateIpsDto> listPrivateNetworkIps(
         @EndpointLink("ips") @BinderParam(BindToPath.class) VLANNetworkDto network);

   /**
    * @see CloudApi#listPrivateNetworkIps(VLANNetworkDto, IpOptions)
    */
   @Named("privatenetwork:listips")
   @GET
   @Consumes(PrivateIpsDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<PrivateIpsDto> listPrivateNetworkIps(
         @EndpointLink("ips") @BinderParam(BindToPath.class) VLANNetworkDto network, IpOptions options);

   /**
    * @see CloudApi#getPrivateNetworkIp(VLANNetworkDto, Integer)
    */
   @Named("privatenetwork:getip")
   @GET
   @Consumes(PrivateIpDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<PrivateIpDto> getPrivateNetworkIp(
         @EndpointLink("ips") @BinderParam(BindToPath.class) VLANNetworkDto network,
         @BinderParam(AppendToPath.class) Integer ipId);

   /*********************** Virtual Appliance ***********************/

   /**
    * @see CloudApi#listVirtualAppliances(VirtualDatacenterDto)
    */
   @Named("vapp:list")
   @GET
   @Consumes(VirtualAppliancesDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<VirtualAppliancesDto> listVirtualAppliances(
         @EndpointLink("virtualappliances") @BinderParam(BindToPath.class) VirtualDatacenterDto virtualDatacenter);

   /**
    * @see CloudApi#getVirtualAppliance(VirtualDatacenterDto, Integer)
    */
   @Named("vapp:get")
   @GET
   @Fallback(NullOnNotFoundOr404.class)
   @Consumes(VirtualApplianceDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<VirtualApplianceDto> getVirtualAppliance(
         @EndpointLink("virtualappliances") @BinderParam(BindToPath.class) VirtualDatacenterDto virtualDatacenter,
         @BinderParam(AppendToPath.class) Integer virtualApplianceId);

   /**
    * @see CloudApi#getVirtualApplianceState(VirtualApplianceDto)
    */
   @Named("vapp:getstate")
   @GET
   @Consumes(VirtualApplianceStateDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<VirtualApplianceStateDto> getVirtualApplianceState(
         @EndpointLink("state") @BinderParam(BindToPath.class) VirtualApplianceDto virtualAppliance);

   /**
    * @see CloudApi#createVirtualAppliance(VirtualDatacenterDto,
    *      VirtualApplianceDto)
    */
   @Named("vapp:create")
   @POST
   @Consumes(VirtualApplianceDto.BASE_MEDIA_TYPE)
   @Produces(VirtualApplianceDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<VirtualApplianceDto> createVirtualAppliance(
         @EndpointLink("virtualappliances") @BinderParam(BindToPath.class) VirtualDatacenterDto virtualDatacenter,
         @BinderParam(BindToXMLPayload.class) VirtualApplianceDto virtualAppliance);

   /**
    * @see CloudApi#updateVirtualAppliance(VirtualApplianceDto)
    */
   @Named("vapp:update")
   @PUT
   @Consumes(VirtualApplianceDto.BASE_MEDIA_TYPE)
   @Produces(VirtualApplianceDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<VirtualApplianceDto> updateVirtualAppliance(
         @EndpointLink("edit") @BinderParam(BindToXMLPayloadAndPath.class) VirtualApplianceDto virtualAppliance);

   /**
    * @see CloudApi#deleteVirtualAppliance(VirtualApplianceDto)
    */
   @Named("vapp:delete")
   @DELETE
   ListenableFuture<Void> deleteVirtualAppliance(
         @EndpointLink("edit") @BinderParam(BindToPath.class) VirtualApplianceDto virtualAppliance);

   /**
    * @see CloudApi#deleteVirtualAppliance(VirtualApplianceDto,
    *      VirtualApplianceOptions)
    */
   @Named("vapp:delete")
   @DELETE
   ListenableFuture<Void> deleteVirtualAppliance(
         @EndpointLink("edit") @BinderParam(BindToPath.class) VirtualApplianceDto virtualAppliance,
         VirtualApplianceOptions options);

   /**
    * @see CloudApi#deployVirtualAppliance(VirtualApplianceDto,
    *      VirtualMachineTaskDto)
    */
   @Named("vapp:deploy")
   @POST
   @Consumes(AcceptedRequestDto.BASE_MEDIA_TYPE)
   @Produces(VirtualMachineTaskDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<AcceptedRequestDto<String>> deployVirtualAppliance(
         @EndpointLink("deploy") @BinderParam(BindToPath.class) VirtualApplianceDto virtualAppliance,
         @BinderParam(BindToXMLPayload.class) VirtualMachineTaskDto task);

   /**
    * @see CloudApi#undeployVirtualAppliance(VirtualApplianceDto,
    *      VirtualMachineTaskDto)
    */
   @Named("vapp:undeploy")
   @POST
   @Consumes(AcceptedRequestDto.BASE_MEDIA_TYPE)
   @Produces(VirtualMachineTaskDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<AcceptedRequestDto<String>> undeployVirtualAppliance(
         @EndpointLink("undeploy") @BinderParam(BindToPath.class) VirtualApplianceDto virtualAppliance,
         @BinderParam(BindToXMLPayload.class) VirtualMachineTaskDto task);

   /**
    * @see CloudApi#getVirtualAppliancePrice(VirtualApplianceDto)
    */
   @Named("vapp:gerprice")
   @GET
   @Consumes(MediaType.TEXT_PLAIN)
   @ResponseParser(ReturnStringIf2xx.class)
   ListenableFuture<String> getVirtualAppliancePrice(
         @EndpointLink("price") @BinderParam(BindToPath.class) VirtualApplianceDto virtualAppliance);

   /*********************** Virtual Machine ***********************/

   /**
    * @see CloudApi#listAllVirtualMachines()
    */
   @GET
   @Path("/virtualmachines")
   @Consumes(VirtualMachinesWithNodeExtendedDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<VirtualMachinesWithNodeExtendedDto> listAllVirtualMachines();

   /**
    * @see CloudApi#listVirtualMachines(VirtualApplianceDto)
    */
   @Named("vm:list")
   @GET
   @Consumes(VirtualMachinesWithNodeExtendedDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<VirtualMachinesWithNodeExtendedDto> listVirtualMachines(
         @EndpointLink("virtualmachines") @BinderParam(BindToPath.class) VirtualApplianceDto virtualAppliance);

   /**
    * @see CloudApi#listVirtualMachines(VirtualApplianceDto,
    *      VirtualMachineOptions)
    */
   @Named("vm:list")
   @GET
   @Consumes(VirtualMachinesWithNodeExtendedDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<VirtualMachinesWithNodeExtendedDto> listVirtualMachines(
         @EndpointLink("virtualmachines") @BinderParam(BindToPath.class) VirtualApplianceDto virtualAppliance,
         VirtualMachineOptions options);

   /**
    * @see CloudApi#getVirtualMachine(VirtualApplianceDto, Integer)
    */
   @Named("vm:get")
   @GET
   @Fallback(NullOnNotFoundOr404.class)
   @Consumes(VirtualMachineWithNodeExtendedDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<VirtualMachineWithNodeExtendedDto> getVirtualMachine(
         @EndpointLink("virtualmachines") @BinderParam(BindToPath.class) VirtualApplianceDto virtualAppliance,
         @BinderParam(AppendToPath.class) Integer virtualMachineId);

   /**
    * @see CloudApi#createVirtualMachine(VirtualApplianceDto,
    *      VirtualMachineWithNodeExtendedDto)
    */
   @Named("vm:create")
   @POST
   @Consumes(VirtualMachineWithNodeExtendedDto.BASE_MEDIA_TYPE)
   @Produces(VirtualMachineWithNodeExtendedDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<VirtualMachineWithNodeExtendedDto> createVirtualMachine(
         @EndpointLink("virtualmachines") @BinderParam(BindToPath.class) VirtualApplianceDto virtualAppliance,
         @BinderParam(BindToXMLPayload.class) VirtualMachineWithNodeExtendedDto virtualMachine);

   /**
    * @see CloudApi#deleteVirtualMachine(VirtualMachineDto)
    */
   @Named("vm:delete")
   @DELETE
   ListenableFuture<Void> deleteVirtualMachine(
         @EndpointLink("edit") @BinderParam(BindToPath.class) VirtualMachineDto virtualMachine);

   /**
    * @see CloudApi#updateVirtualMachine(VirtualMachineWithNodeExtendedDto)
    */
   @Named("vm:update")
   @PUT
   @ResponseParser(ReturnTaskReferenceOrNull.class)
   @Consumes(AcceptedRequestDto.BASE_MEDIA_TYPE)
   @Produces(VirtualMachineWithNodeExtendedDto.BASE_MEDIA_TYPE)
   ListenableFuture<AcceptedRequestDto<String>> updateVirtualMachine(
         @EndpointLink("edit") @BinderParam(BindToXMLPayloadAndPath.class) VirtualMachineWithNodeExtendedDto virtualMachine);

   /**
    * @see CloudApi#updateVirtualMachine(VirtualMachineDto,
    *      VirtualMachineOptions)
    */
   @Named("vm:update")
   @PUT
   @ResponseParser(ReturnTaskReferenceOrNull.class)
   @Consumes(AcceptedRequestDto.BASE_MEDIA_TYPE)
   @Produces(VirtualMachineWithNodeExtendedDto.BASE_MEDIA_TYPE)
   ListenableFuture<AcceptedRequestDto<String>> updateVirtualMachine(
         @EndpointLink("edit") @BinderParam(BindToXMLPayloadAndPath.class) VirtualMachineWithNodeExtendedDto virtualMachine,
         VirtualMachineOptions options);

   /**
    * @see CloudApi#changeVirtualMachineState(VirtualMachineDto,
    *      VirtualMachineStateDto)
    */
   @Named("vm:changestate")
   @PUT
   @Consumes(AcceptedRequestDto.BASE_MEDIA_TYPE)
   @Produces(VirtualMachineStateDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<AcceptedRequestDto<String>> changeVirtualMachineState(
         @EndpointLink("state") @BinderParam(BindToPath.class) VirtualMachineDto virtualMachine,
         @BinderParam(BindToXMLPayload.class) VirtualMachineStateDto state);

   /**
    * @see CloudApi#getVirtualMachineState(VirtualMachineDto)
    */
   @Named("vm:getstate")
   @GET
   @Consumes(VirtualMachineStateDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<VirtualMachineStateDto> getVirtualMachineState(
         @EndpointLink("state") @BinderParam(BindToPath.class) VirtualMachineDto virtualMachine);

   /**
    * @see CloudApi#listNetworkConfigurations(VirtualMachineDto)
    */
   @Named("vm:listnetworkconfigurations")
   @GET
   @Consumes(VMNetworkConfigurationsDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<VMNetworkConfigurationsDto> listNetworkConfigurations(
         @EndpointLink("configurations") @BinderParam(BindToPath.class) VirtualMachineDto virtualMachine);

   /**
    * @see CloudApi#rebootVirtualMachine(VirtualMachineDto)
    */
   @Named("vm:reboot")
   @POST
   @Consumes(AcceptedRequestDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<AcceptedRequestDto<String>> rebootVirtualMachine(
         @EndpointLink("reset") @BinderParam(BindToPath.class) VirtualMachineDto virtualMachine);

   /**
    * @see CloudApi#snapshotVirtualMachine(VirtualMachineDto,
    *      VirtualMachineInstanceDto)
    */
   @POST
   @Consumes(AcceptedRequestDto.BASE_MEDIA_TYPE)
   @Produces(VirtualMachineInstanceDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<AcceptedRequestDto<String>> snapshotVirtualMachine(
         @EndpointLink("instance") @BinderParam(BindToPath.class) VirtualMachineDto virtualMachine,
         @BinderParam(BindToXMLPayload.class) VirtualMachineInstanceDto snapshotConfig);

   /**
    * @see CloudApi#listAttachedVolumes(VirtualMachineDto)
    */
   @Named("vm:listvolumes")
   @GET
   @Consumes(VolumesManagementDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<VolumesManagementDto> listAttachedVolumes(
         @EndpointLink("volumes") @BinderParam(BindToPath.class) VirtualMachineDto virtualMachine);

   /**
    * @see CloudApi#listAttachedHardDisks(VirtualMachineDto)
    */
   @Named("vm:listharddisks")
   @GET
   @Consumes(DisksManagementDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<DisksManagementDto> listAttachedHardDisks(
         @EndpointLink("harddisks") @BinderParam(BindToPath.class) VirtualMachineDto virtualMachine);

   /**
    * @see CloudApi#deployVirtualMachine(VirtualMachineDto,
    *      VirtualMachineTaskDto)
    */
   @Named("vm:deploy")
   @POST
   @Consumes(AcceptedRequestDto.BASE_MEDIA_TYPE)
   @Produces(VirtualMachineTaskDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<AcceptedRequestDto<String>> deployVirtualMachine(
         @EndpointLink("deploy") @BinderParam(BindToPath.class) VirtualMachineDto virtualMachine,
         @BinderParam(BindToXMLPayload.class) VirtualMachineTaskDto task);

   /**
    * @see CloudApi#undeployVirtualMachine(VirtualMachineDto,
    *      VirtualMachineTaskDto)
    */
   @Named("vm:undeploy")
   @POST
   @Consumes(AcceptedRequestDto.BASE_MEDIA_TYPE)
   @Produces(VirtualMachineTaskDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<AcceptedRequestDto<String>> undeployVirtualMachine(
         @EndpointLink("undeploy") @BinderParam(BindToPath.class) VirtualMachineDto virtualMachine,
         @BinderParam(BindToXMLPayload.class) VirtualMachineTaskDto task);

   /*********************** Virtual Machine Template ***********************/

   /**
    * @see CloudApi#getVirtualMachineTemplate(VirtualMachineTemplateDto)
    */
   @GET
   @Consumes(VirtualMachineTemplateDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<VirtualMachineTemplateDto> getVirtualMachineTemplate(
         @EndpointLink("virtualmachinetemplate") @BinderParam(BindToPath.class) VirtualMachineDto virtualMachine);

   /*********************** Hard disks ***********************/

   /**
    * @see CloudApi#listHardDisks(VirtualDatacenterDto)
    */
   @Named("harddisk:list")
   @GET
   @Consumes(DisksManagementDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<DisksManagementDto> listHardDisks(
         @EndpointLink("disks") @BinderParam(BindToPath.class) VirtualDatacenterDto virtualDatacenter);

   /**
    * @see CloudApi#getHardDisk(VirtualDatacenterDto, Integer)
    */
   @Named("harddisk:get")
   @GET
   @Fallback(NullOnNotFoundOr404.class)
   @Consumes(DiskManagementDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<DiskManagementDto> getHardDisk(
         @EndpointLink("disks") @BinderParam(BindToPath.class) VirtualDatacenterDto virtualDatacenter,
         @BinderParam(AppendToPath.class) Integer diskId);

   /**
    * @see CloudApi#createHardDisk(VirtualDatacenterDto, DiskManagementDto)
    */
   @Named("harddisk:create")
   @POST
   @Consumes(DiskManagementDto.BASE_MEDIA_TYPE)
   @Produces(DiskManagementDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<DiskManagementDto> createHardDisk(
         @EndpointLink("disks") @BinderParam(BindToPath.class) VirtualDatacenterDto virtualDatacenter,
         @BinderParam(BindToXMLPayload.class) DiskManagementDto hardDisk);

   /**
    * @see CloudApi#deleteHardDisk(DiskManagementDto)
    */
   @Named("harddisk:delete")
   @DELETE
   ListenableFuture<Void> deleteHardDisk(@EndpointLink("edit") @BinderParam(BindToPath.class) DiskManagementDto hardDisk);

   /*********************** Volumes ***********************/

   /**
    * @see CloudApi#listVolumes(VirtualDatacenterDto)
    */
   @Named("volume:list")
   @EnterpriseEdition
   @GET
   @Consumes(VolumesManagementDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<VolumesManagementDto> listVolumes(
         @EndpointLink("volumes") @BinderParam(BindToPath.class) VirtualDatacenterDto virtualDatacenter);

   /**
    * @see CloudApi#listVolumes(VirtualDatacenterDto, VolumeOptions)
    */
   @Named("volume:list")
   @EnterpriseEdition
   @GET
   @Consumes(VolumesManagementDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<VolumesManagementDto> listVolumes(
         @EndpointLink("volumes") @BinderParam(BindToPath.class) VirtualDatacenterDto virtualDatacenter,
         VolumeOptions options);

   /**
    * @see CloudApi#getVolume(VirtualDatacenterDto, Integer)
    */
   @Named("volume:get")
   @EnterpriseEdition
   @GET
   @Fallback(NullOnNotFoundOr404.class)
   @Consumes(VolumeManagementDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<VolumeManagementDto> getVolume(
         @EndpointLink("volumes") @BinderParam(BindToPath.class) VirtualDatacenterDto virtualDatacenter,
         @BinderParam(AppendToPath.class) Integer volumeId);

   /**
    * @see CloudApi#createVolume(VirtualDatacenterDto, VolumeManagementDto)
    */
   @Named("volume:create")
   @EnterpriseEdition
   @POST
   @Consumes(VolumeManagementDto.BASE_MEDIA_TYPE)
   @Produces(VolumeManagementDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<VolumeManagementDto> createVolume(
         @EndpointLink("volumes") @BinderParam(BindToPath.class) VirtualDatacenterDto virtualDatacenter,
         @BinderParam(BindToXMLPayload.class) VolumeManagementDto volume);

   /**
    * @see CloudApi#updateVolume(VolumeManagementDto)
    */
   @Named("volume:update")
   @EnterpriseEdition
   @PUT
   @ResponseParser(ReturnTaskReferenceOrNull.class)
   @Consumes(AcceptedRequestDto.BASE_MEDIA_TYPE)
   @Produces(VolumeManagementDto.BASE_MEDIA_TYPE)
   ListenableFuture<AcceptedRequestDto<String>> updateVolume(
         @EndpointLink("edit") @BinderParam(BindToXMLPayloadAndPath.class) VolumeManagementDto volume);

   /**
    * @see CloudApi#deleteVolume(VolumeManagementDto)
    */
   @Named("volume:delete")
   @EnterpriseEdition
   @DELETE
   ListenableFuture<Void> deleteVolume(@EndpointLink("edit") @BinderParam(BindToPath.class) VolumeManagementDto volume);

   /**
    * @see CloudApi#moveVolume(VolumeManagementDto, VirtualDatacenterDto)
    */
   @Named("volume:move")
   @EnterpriseEdition
   @POST
   @Fallback(MovedVolume.class)
   @Consumes(MovedVolumeDto.BASE_MEDIA_TYPE)
   @Produces(LinksDto.BASE_MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<VolumeManagementDto> moveVolume(
         @BinderParam(BindMoveVolumeToPath.class) VolumeManagementDto volume,
         @BinderParam(BindVirtualDatacenterRefToPayload.class) VirtualDatacenterDto newVirtualDatacenter);

   /*********************** AntiAffinity ***********************/

   /**
    * @see CloudApi#createLayer(VirtualApplianceDto, LayerDto)
    */
   @POST
   @Consumes(LayerDto.MEDIA_TYPE)
   @Produces(LayerDto.MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<LayerDto> createLayer(
         @EndpointLink("layers") @BinderParam(BindToPath.class) VirtualApplianceDto virtualAppliance,
         @BinderParam(BindToXMLPayload.class) LayerDto layer);

   /**
    * @see CloudApi#deleteLayer(LayerDto)
    */
   @DELETE
   ListenableFuture<Void> deleteLayer(@EndpointLink("edit") @BinderParam(BindToPath.class) LayerDto layer);

   /**
    * @see CloudApi#getLayers(VirtualApplianceDto)
    */
   @GET
   @Consumes(LayersDto.MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<LayersDto> listLayers(
         @EndpointLink("layers") @BinderParam(BindToPath.class) VirtualApplianceDto virtualAppliance);

   /**
    * @see CloudApi#getLayer(VirtualApplianceDto, String)
    */
   @GET
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @Consumes(LayerDto.MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<LayerDto> getLayer(
         @EndpointLink("layers") @BinderParam(BindToPath.class) VirtualApplianceDto virtualAppliance,
         @BinderParam(AppendToPath.class) String layerName);

   /**
    * @see CloudApi#updateLayer(LayerDto)
    */
   @PUT
   @Consumes(LayerDto.MEDIA_TYPE)
   @Produces(LayerDto.MEDIA_TYPE)
   @JAXBResponseParser
   ListenableFuture<LayerDto> updateLayer(
         @EndpointLink("edit") @BinderParam(BindToXMLPayloadAndPath.class) LayerDto layer);

}
