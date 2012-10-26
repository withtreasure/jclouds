/**
 * 
 */
package org.jclouds.abiquo.domain.network;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.abiquo.AbiquoApi;
import org.jclouds.abiquo.AbiquoAsyncApi;
import org.jclouds.abiquo.domain.DomainWrapper;
import org.jclouds.abiquo.domain.infrastructure.Datacenter;
import org.jclouds.abiquo.domain.infrastructure.Tier;
import org.jclouds.abiquo.reference.ValidationErrors;
import org.jclouds.rest.RestContext;

import com.abiquo.server.core.infrastructure.network.NetworkServiceTypeDto;

/**
 * <pre>
 * 
 * Network Service Type defines a network service. 
 * 
 * It is used to determine to which {@link NetworkInterface} will be attached 
 * to a {@link Nic} when it (the Nic) is used by an {@link Ip} that belong to 
 * a {@link Network}. 
 * 
 * It executes the same role in the Network configuration as the {@link Tier} does in 
 * the Storage Configuration: a way to classify and name different kind of services
 * that are configured in the 'real' world.
 * 
 * It only needs a name just to be identified. 
 * 
 * They are defined at {@link Datacenter} level: Two {@link NetworkServiceType} can have the 
 * same name if they belong to a different {@link Datacenter}
 * 
 * </pre>
 * 
 * @author Jaume Devesa
 */
public class NetworkServiceType extends DomainWrapper<NetworkServiceTypeDto> {
   public static Builder builder(final RestContext<AbiquoApi, AbiquoAsyncApi> context, final Datacenter datacenter) {
      return new Builder(context, datacenter);
   }

   /**
    * Helper class to build {@link NetworkServiceType} in a controlled way.
    * 
    * @author Jaume Devesa
    */
   public static class Builder {
      private RestContext<AbiquoApi, AbiquoAsyncApi> context;

      private Datacenter datacenter;

      private String name;

      public Builder(final RestContext<AbiquoApi, AbiquoAsyncApi> context, final Datacenter datacenter) {
         super();
         checkNotNull(datacenter, ValidationErrors.NULL_RESOURCE + Datacenter.class);
         this.datacenter = datacenter;
         this.context = context;
      }

      public NetworkServiceType build() {
         NetworkServiceTypeDto dto = new NetworkServiceTypeDto();
         dto.setName(this.name);

         NetworkServiceType nst = new NetworkServiceType(context, dto);
         nst.datacenter = this.datacenter;
         return nst;
      }

      public Builder name(final String name) {
         this.name = name;
         return this;
      }
   }

   /** The datacenter where the NetworkServiceType belongs. */
   private Datacenter datacenter;

   /** Constructor will only be used by the builder. */
   protected NetworkServiceType(final RestContext<AbiquoApi, AbiquoAsyncApi> context, final NetworkServiceTypeDto target) {
      super(context, target);
   }

   /**
    * Delete the Network Service Type.
    */
   public void delete() {
      context.getApi().getInfrastructureApi().deleteNetworkServiceType(target);
      target = null;
   }

   /**
    * Create a new Network Service Type
    */
   public void save() {
      target = context.getApi().getInfrastructureApi().createNetworkServiceType(datacenter.unwrap(), target);
   }

   /**
    * Update Network Service Type information in the server with the data from
    * this NST.
    */
   public void update() {
      target = context.getApi().getInfrastructureApi().updateNetworkServiceType(target);
   }

   public Integer getId() {
      return target.getId();
   }

   public String getName() {
      return target.getName();
   }

   public Boolean isDefaultNST() {
      return target.isDefaultNST();
   }

   public void setName(final String name) {
      target.setName(name);
   }

   @Override
   public String toString() {
      return "NetworkServiceType [id=" + getId() + ", name=" + getName() + ", isDefault=" + isDefaultNST() + "]";
   }

}
