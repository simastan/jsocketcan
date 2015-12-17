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
package de.sitec.jsocketcan;

import com.sun.jna.Library;
import com.sun.jna.Native;

/**
 * An native interface for controlling the CAN interface.
 * @author sitec systems GmbH
 * @since 1.1
 */
/* package */ interface InterfaceControl extends Library
{
    /* package */ final InterfaceControl INSTANCE = (InterfaceControl) Native.loadLibrary("socketcan", InterfaceControl.class);
    
    /**
     * Starts the CAN interface. This starts the CAN interface with the given 
     * name. It simply changes the if state of the interface to up. All 
     * initialisation works will be done in kernel. The if state can also be 
     * queried by a simple ifconfig.
     * @param name Name of the can device. This is the netdev name, as 
     *        <code>ifconfig -a</code> shows in your system. usually it contains 
     *        prefix "can" and the number of the can line. e.g. "can0"
     * @return <code>0</code> - If success / <code>false</code> - If failed
     * @since 1.1
     */
    /* package */ int can_do_start(final String name);
    
    /**
     * Stops the CAN interface. This stops the CAN interface with the given name. 
     * It simply changes the if state of the interface to down. Any running 
     * communication would be stopped.
     * @param name Name of the can device. This is the netdev name, as 
     *        <code>ifconfig -a</code> shows in your system. usually it contains 
     *        prefix "can" and the numer of the can line. e.g. "can0"
     * @return <code>0</code> - If success / <code>false</code> - If failed
     * @since 1.1
     */
    /* package */ int can_do_stop(final String name);
    
    /**
     * Setups the bitrate. This is the recommended way to setup the bus bit 
     * timing. You only have to give a bitrate value here. The exact bit timing 
     * will be calculated automatically. To use this function, make sure that 
     * CONFIG_CAN_CALC_BITTIMING is set to y in your kernel configuration. 
     * bitrate can be a value between 1000(1kbit/s) and 1000000(1000kbit/s).
     * @param name Name of the can device. This is the netdev name, as 
     *        <code>ifconfig -a</code> shows in your system. usually it contains 
     *        prefix "can" and the numer of the can line. e.g. "can0"
     * @param bitrate Bitrate of the CAN bus, can be a value between 1000(1kbit/s) 
     *        and 1000000(1000kbit/s)
     * @return <code>0</code> - If success / <code>false</code> - If failed
     * @since 1.1
     */
    /* package */ int can_set_bitrate(final String name, final int bitrate);
}
