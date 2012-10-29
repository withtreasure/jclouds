package org.jclouds.abiquo.domain.cloud;

import org.jclouds.abiquo.AbiquoApi;
import org.jclouds.abiquo.AbiquoAsyncApi;
import org.jclouds.abiquo.domain.DomainWrapper;
import org.jclouds.rest.RestContext;

import com.abiquo.server.core.cloud.LayerDto;

/**
 * Abiquo has added layer concept in order to offer Anti Host Affinity
 * allocation between virtual machines. That means that one virtual appliance
 * can have different layers, and a virtual machine could belong to a layer (it
 * is not mandatory). Virtual machines with same layer will deploy in different
 * host to assure appliance stability.
 * 
 * @author Susana Acedo
 */
public class Layer extends DomainWrapper<LayerDto> {
   /**
    * Constructor to be used only by the builder. This resource cannot be
    * created.
    */
   private Layer(final RestContext<AbiquoApi, AbiquoAsyncApi> context, final LayerDto target) {
      super(context, target);
   }

   /**
    * Updates the layer.
    */
   public void update() {
      target = context.getApi().getCloudApi().updateLayer(target);
   }

   // Builder

   public static Builder builder(final RestContext<AbiquoApi, AbiquoAsyncApi> context) {
      return new Builder(context);
   }

   public static class Builder {
      private RestContext<AbiquoApi, AbiquoAsyncApi> context;

      private String name;

      public Builder(final RestContext<AbiquoApi, AbiquoAsyncApi> context) {
         super();
         this.context = context;
      }

      public Builder name(final String name) {
         this.name = name;
         return this;
      }

      public Layer build() {
         LayerDto dto = new LayerDto();
         dto.setName(name);
         Layer layer = new Layer(context, dto);

         return layer;
      }

      public static Builder fromLayer(final Layer in) {
         Builder builder = Layer.builder(in.context).name(in.getName());

         return builder;
      }
   }

   public String getName() {
      return target.getName();
   }

   public void setName(final String name) {
      target.setName(name);
   }

   @Override
   public String toString() {
      return "Layer [name=" + getName() + "]";
   }
}
