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
 * Mapping to structure <code>can_filter</code>.
 * @author sitec systems GmbH
 * @since 1.0
 */
public class CanFilterStruct extends Structure
{
    public int can_id;
    public int can_mask;
    
    public CanFilterStruct()
    {
        super();
    }
    
    @Override
    protected List getFieldOrder() 
    {
        return Arrays.asList("can_id", "can_mask");
    }
    
    public CanFilterStruct(final int can_id, final int can_mask) 
    {
        super();
        this.can_id = can_id;
        this.can_mask = can_mask;
    }
    
    public static class ByReference extends CanFilterStruct implements Structure.ByReference
    {
    };

    public static class ByValue extends CanFilterStruct implements Structure.ByValue
    {
    };
}
