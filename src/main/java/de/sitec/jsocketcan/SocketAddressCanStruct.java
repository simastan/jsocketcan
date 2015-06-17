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
