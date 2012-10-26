package org.jclouds.abiquo.domain.infrastructure;

import org.jclouds.abiquo.AbiquoApi;
import org.jclouds.abiquo.AbiquoAsyncApi;
import org.jclouds.abiquo.domain.DomainWrapper;
import org.jclouds.abiquo.domain.network.NetworkServiceType;
import org.jclouds.abiquo.rest.internal.ExtendedUtils;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseXMLWithJAXB;
import org.jclouds.rest.RestContext;

import com.abiquo.model.rest.RESTLink;
import com.abiquo.server.core.infrastructure.network.NetworkInterfaceDto;
import com.abiquo.server.core.infrastructure.network.NetworkServiceTypeDto;
import com.google.inject.TypeLiteral;

/**
 * <pre>
 * Network Interface object represents a physical attached NIC.
 * 
 * You are able to tag its {@link NetworkServiceType}. The idea is to 
 * say to let know Abiquo to which Network Service this network interface is attached to.
 * </pre>
 * 
 * @author Jaume Devesa
 */
public class NetworkInterface extends DomainWrapper<NetworkInterfaceDto> {
   /**
    * Constructor to be used only by the builder. This resource cannot be
    * created.
    */
   protected NetworkInterface(final RestContext<AbiquoApi, AbiquoAsyncApi> context, final NetworkInterfaceDto target) {
      super(context, target);
   }

   public String getName() {
      return target.getName();
   }

   public String getMac() {
      return target.getMac();
   }

   public void setNetworkServiceType(final NetworkServiceType type) {
      target.setNetworkServiceTypeLink(type.unwrap().getEditLink().getHref());
   }

   public NetworkServiceType getNetworkServiceType() {
      RESTLink link = target.getNetworkServiceTypeLink();

      ExtendedUtils utils = (ExtendedUtils) context.getUtils();
      HttpResponse response = utils.getAbiquoHttpClient().get(link);

      ParseXMLWithJAXB<NetworkServiceTypeDto> parser = new ParseXMLWithJAXB<NetworkServiceTypeDto>(utils.getXml(),
            TypeLiteral.get(NetworkServiceTypeDto.class));

      return wrap(context, NetworkServiceType.class, parser.apply(response));
   }
}
