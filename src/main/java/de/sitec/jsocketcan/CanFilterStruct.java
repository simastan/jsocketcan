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
    
}
