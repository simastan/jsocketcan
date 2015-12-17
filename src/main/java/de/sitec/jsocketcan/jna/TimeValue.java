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
 * Mapping to structure <code>timeval</code>.
 * @author sitec systems GmbH
 * @since 1.0
 */
public class TimeValue extends Structure
{
    public long tv_sec;
    public long tv_usec;
    
    private static final short MICROSECOND_FACTOR = 1000;

    public TimeValue(final long tv_sec, final long tv_usec)
    {
        this.tv_sec = tv_sec;
        this.tv_usec = tv_usec;
    }

    public TimeValue()
    {
    }
    
    /**
     * Constructor.
     * @param millis The value in milliseconds
     * @since 1.0
     */
    public TimeValue(final long millis)
    {
        tv_sec = millis / MICROSECOND_FACTOR;
        tv_usec = (millis - (tv_sec * MICROSECOND_FACTOR)) * MICROSECOND_FACTOR;
    }
    
    /**
     * Gets the milliseconds.
     * @return The milliseconds
     * @since 1.0
     */
    public long getMillis() 
    {
        return (tv_sec * MICROSECOND_FACTOR) + (tv_usec / MICROSECOND_FACTOR);
    }
    
    @Override
    protected List getFieldOrder()
    {
        return Arrays.asList("tv_sec", "tv_usec");
    }
    
    public static class ByReference extends CanFrameStruct implements Structure.ByReference
    {
    };

    public static class ByValue extends CanFrameStruct implements Structure.ByValue
    {
    };
}
