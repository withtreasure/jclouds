package org.jclouds.abiquo.predicates.infrastructure;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Arrays;

import org.jclouds.abiquo.domain.infrastructure.NetworkInterface;

import com.google.common.base.Predicate;

/**
 * Container for {@link NetworkInterface} filters.
 * 
 * @author Jaume Devesa
 */
public class NetworkInterfacePredicates
{
    public static Predicate<NetworkInterface> name(final String... names)
    {
        checkNotNull(names, "names must be defined");

        return new Predicate<NetworkInterface>()
        {
            @Override
            public boolean apply(final NetworkInterface ni)
            {
                return Arrays.asList(names).contains(ni.getName());
            }
        };
    }

}
