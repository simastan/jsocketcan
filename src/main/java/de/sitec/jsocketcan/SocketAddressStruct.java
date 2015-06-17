/*
 * Project: 15_0013_CF4J
 * $Header: $
 * Author: Robert Lehmann
 * Author: Mattes Standfuss
 * Last Change:
 *    by:   $Author: $
 *    date: $Date:   $
 * Copyright (c): sitec systems GmbH, 2015
 */
package de.sitec.jsocketcan;

import com.sun.jna.Structure;
import java.util.Arrays;
import java.util.List;

/**
 * Mapping to structure <code>sockaddr</code>.
 * @author sitec systems GmbH
 * @since 1.0
 */
public class SocketAddressStruct extends Structure
{
    public short sa_family;
    public byte[] sa_data = new byte[14];

    public SocketAddressStruct()
    {
        super();
    }

    public SocketAddressStruct(final short sa_family, final byte[] sa_data)
    {
        this.sa_family = sa_family;
        this.sa_data = sa_data;
    }

    @Override
    protected List getFieldOrder()
    {
        return Arrays.asList("sa_family", "sa_data");
    }

    public static class ByReference extends SocketAddressStruct implements Structure.ByReference
    {
    };

    public static class ByValue extends SocketAddressStruct implements Structure.ByValue
    {
    };

}
