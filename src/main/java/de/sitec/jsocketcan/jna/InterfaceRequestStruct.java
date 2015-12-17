/**
 * jSocketCan is an Framework for Access to Linux SocketCAN over JNA. 
 * Implements jCanInterface.
 * 
 * Copyright (C) 2015 sitec systems GmbH <http://www.sitec-systems.de>
 * 
 * This file is part of jSocketCan.
 * 
 * jSocketCan is free software: you can redistribute it and/or modify it 
 * under the terms of the GNU Lesser General Public License as published by the 
 * Free Software Foundation, either version 3 of the License, or (at your option) 
 * any later version.
 * 
 * jSocketCan is distributed in the hope that it will be useful, but WITHOUT 
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS 
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more 
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with jSocketCan. If not, see <http://www.gnu.org/licenses/>.
 */
/*
 * Author: Mattes Standfuss
 * Copyright (c): sitec systems GmbH, 2015
 */
package de.sitec.jsocketcan.jna;

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
