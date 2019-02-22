package com.yoozoo.ptools.plugin;

import com.facebook.presto.spi.Plugin;
import com.google.common.collect.ImmutableSet;
import com.yoozoo.ptools.udf.IpParsePresto;

import java.util.Set;

public class PrestoIpParseFunctionsPlugin implements Plugin{
	
	@Override
	public Set<Class<?>> getFunctions()
    {
        return ImmutableSet.<Class<?>>builder()
                .add(IpParsePresto.class)
                .build();
    }
}
