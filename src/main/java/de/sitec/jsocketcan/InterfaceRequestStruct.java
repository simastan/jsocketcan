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

import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Union;
import java.util.Arrays;
import java.util.List;

/**
 * Mapping to structure <code>ifreq</code>.
 * @author sitec systems GmbH
 * @since 1.0
 */
public class InterfaceRequestStruct extends Structure
{
    public IfrnUnion ifr_ifrn;
    public IfruUnion ifr_ifru;

    public static class IfrnUnion extends Union
    {
        public String ifrn_name;
    }

    public static class IfruUnion extends Union
    {
        public SocketAddressStruct ifru_addr;
        public SocketAddressStruct ifru_dstaddr;
        public SocketAddressStruct ifru_broadaddr;
        public SocketAddressStruct ifru_netmask;
        public SocketAddressStruct ifru_hwaddr;
        public short ifru_flags;
        public int ifru_ivalue;
        public int ifru_mtu;
        public ifmap ifru_map;
        public byte ifru_slave;
        public byte ifru_newname;
        public Pointer ifru_data;
    }

    public static class ifmap extends Structure
    {

        public NativeLong mem_start;
        public NativeLong mem_end;
        public short base_addr;
        public byte irq;
        public byte dma;
        public byte port;

        public ifmap()
        {
            super();
        }

        @Override
        protected List getFieldOrder()
        {
            return Arrays.asList("mem_start", "mem_end", "base_addr", "irq",
                    "dma", "port");
        }
    }

    public InterfaceRequestStruct()
    {
        super();
    }

    @Override
    protected List getFieldOrder()
    {
        return Arrays.asList("ifr_ifrn", "ifr_ifru");
    }
}
