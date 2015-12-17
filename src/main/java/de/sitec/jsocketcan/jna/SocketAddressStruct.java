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
