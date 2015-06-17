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
 * Mapping to structure <code>can_frame</code>.
 * @author sitec systems GmbH
 * @since 1.0
 */
public class CanFrameStruct extends Structure
{
    /**
     * 32 bit CAN_ID + EFF/RTR/ERR flags
     */
    public int can_id;
    public byte can_dlc;
    public byte[] data = new byte[CAN_FRAME_LENGTH];
    
    private static final byte CAN_FRAME_LENGTH = 11;
    // TODO: Investigate reason for this offset
    private static final byte NATIVE_OFFSET = 3;
    /**
     * Its recommend to use constructor with parametes. The member data must have
     * every time a length of 8.
     */
    public CanFrameStruct()
    {
        super();
    }

    @Override
    protected List getFieldOrder()
    {
        return Arrays.asList("can_id", "can_dlc", "data");
    }

    public CanFrameStruct(final int can_id, final byte can_dlc, final byte data[])
    {
        super();
        this.can_id = can_id;
        this.can_dlc = can_dlc;
        
        if ((data.length > this.data.length))
        {
            throw new IllegalArgumentException("Can frame can't be longer like " 
                    + CAN_FRAME_LENGTH);
        }
        System.arraycopy(data, 0, this.data, NATIVE_OFFSET, data.length);
    }

    public static class ByReference extends CanFrameStruct implements Structure.ByReference
    {
    };

    public static class ByValue extends CanFrameStruct implements Structure.ByValue
    {
    };
}
