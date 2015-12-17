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

import com.sun.jna.Structure;
import com.sun.jna.Union;
import java.util.Arrays;
import java.util.List;

/**
 * Mapping to structure <code>sockaddr_can</code>.
 * @author sitec systems GmbH
 * @since 1.0
 */
public class SocketAddressCanStruct extends Structure
{

    public short can_family;
    public int can_ifindex;
    public CanAddrUnion can_addr;

    public static class CanAddrUnion extends Union
    {

        /**
         * transport protocol class address information (e.g. ISOTP)
         */
        public TpStruct tp;

        public static class TpStruct extends Structure
        {

            public int rx_id;
            public int tx_id;

            public TpStruct()
            {
                super();
            }

            @Override
            protected List getFieldOrder()
            {
                return Arrays.asList("rx_id", "tx_id");
            }

            public TpStruct(int rx_id, int tx_id)
            {
                super();
                this.rx_id = rx_id;
                this.tx_id = tx_id;
            }

            public static class ByReference extends TpStruct implements Structure.ByReference
            {
            };

            public static class ByValue extends TpStruct implements Structure.ByValue
            {
            };
        };

        public CanAddrUnion()
        {
            super();
        }

        public CanAddrUnion(TpStruct tp)
        {
            super();
            this.tp = tp;
            setType(TpStruct.class);
        }

        public static class ByReference extends CanAddrUnion implements Structure.ByReference
        {
        };

        public static class ByValue extends CanAddrUnion implements Structure.ByValue
        {
        };
    };

    public SocketAddressCanStruct()
    {
        super();
    }

    @Override
    protected List getFieldOrder()
    {
        return Arrays.asList("can_family", "can_ifindex", "can_addr");
    }

    public SocketAddressCanStruct(short can_family, int can_ifindex, CanAddrUnion can_addr)
    {
        super();
        this.can_family = can_family;
        this.can_ifindex = can_ifindex;
        this.can_addr = can_addr;
    }

    public static class ByReference extends SocketAddressCanStruct implements Structure.ByReference
    {
    };

    public static class ByValue extends SocketAddressCanStruct implements Structure.ByValue
    {
    };

}
