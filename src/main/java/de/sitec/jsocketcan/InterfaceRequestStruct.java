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

import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Union;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Mapping to structure <code>ifreq</code>.
 * @author sitec systems GmbH
 * @since 1.0
 */
public class InterfaceRequestStruct extends Structure
{
    public IfrIfrnUnion ifr_ifrn;
    public IfrIfruUnion ifr_ifru;
    
    private static final byte IF_NAME_SIZE = 16;
    private static final Logger LOG = LoggerFactory.getLogger(InterfaceRequestStruct.class);

    public static class IfrIfrnUnion extends Union
    {
        public byte[] ifrn_name = new byte[IF_NAME_SIZE];

        public IfrIfrnUnion()
        {
            super();
        }
        
        public IfrIfrnUnion(final String interfaceName) 
        {
            super();
            byte[] unfixedName;
            
            try
            {
                unfixedName = interfaceName.getBytes(Native.getDefaultStringEncoding());
            }
            catch (final UnsupportedEncodingException ex)
            {
                LOG.warn("Native encoding is not supported: {}"
                        , Native.getDefaultStringEncoding(), ex);
                unfixedName = interfaceName.getBytes();
            }
            
            ifrn_name = new byte[InterfaceRequestStruct.IF_NAME_SIZE];
            System.arraycopy(unfixedName, 0, ifrn_name, 0, unfixedName.length);
            
            setType(byte[].class);
        }
        
        public static class ByReference extends IfrIfrnUnion implements Structure.ByReference {};
        public static class ByValue extends IfrIfrnUnion implements Structure.ByValue {};
    }

    public static class IfrIfruUnion extends Union
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
        public byte[] ifru_slave = new byte[IF_NAME_SIZE];
        public byte[] ifru_newname = new byte[IF_NAME_SIZE];
        public Pointer ifru_data;
        
        public IfrIfruUnion() 
        {
            super();
        }
        
        public static class ByReference extends IfrIfruUnion implements Structure.ByReference {};
		
        public static class ByValue extends IfrIfruUnion implements Structure.ByValue {};
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
