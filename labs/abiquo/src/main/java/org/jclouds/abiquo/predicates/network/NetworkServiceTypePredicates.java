/**
 * 
 */
package org.jclouds.abiquo.predicates.network;

import org.jclouds.abiquo.domain.network.NetworkServiceType;

import com.google.common.base.Predicate;

/**
 * Container for {@link NetworkServiceType} filters.
 * 
 * @author Jaume Devesa
 */
public class NetworkServiceTypePredicates {
   public static Predicate<NetworkServiceType> theDefaultOne() {
      return new Predicate<NetworkServiceType>() {
         @Override
         public boolean apply(final NetworkServiceType ni) {
            return ni.isDefaultNST();
         }
      };
   }
}
