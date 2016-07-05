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
 * Copyright (c): sitec systems GmbH, 2016
 */
package de.sitec.jsocketcan;

import de.sitec.jcaninterface.Can;
import de.sitec.jcaninterface.CanFilter;
import de.sitec.jcaninterface.CanFrame;
import de.sitec.jcaninterface.CanFrame.Type;
import de.sitec.jcaninterface.CanTimeoutException;
import de.sitec_systems.nativelibraryloader.NativeLibraryLoader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * An implementation of <code>Can</code> interface for SocketCAN. For creating 
 * an instance use {@link #createSocketCan(java.lang.String, int) } if you wan't
 * control the CAN bus interface or use {@link #createSocketCan(java.lang.String) }
 * if the CAN bus interface should controlled by external.
 * @author sitec systems GmbH
 * @since 1.0
 */
public class SocketCan implements Can
{
    private final List<CanFilterNative> currentFilters = new ArrayList<>();
    private final String canInterface;
    private final boolean interfaceControl;
    
    private static final int CAN_EFF_FLAG = 0x80000000;
    private static final int CAN_RTR_FLAG = 0x40000000;
    
    static
    {
        NativeLibraryLoader.loadLibrary("de.sitec.jsocketcan", "libsocketcan_jni");
    }
    
    /**
     * Constructor.
     * @param canInterface The CAN interface
     * @since 1.0
     */
    private SocketCan(final String canInterface, final boolean interfaceControl)
    {
        if(canInterface == null || !canInterface.contains("can"))
        {
            throw new IllegalArgumentException("CAN interface parameter must contain keyword 'can'");
        }
        this.canInterface = canInterface;
        this.interfaceControl = interfaceControl;
    }
    
    /**
     * Creates an instance of the <code>SocketCAN</code> class. Its sets the 
     * bitrate and starts the CAN bus interface.
     * @param canInterface The CAN interface (example: 'vcan0', 'can0', 'can1', ...)
     * @param bitrate Bitrate of the CAN bus, can be a value between 1000(1kbit/s) 
     *        and 1000000(1000kbit/s)
     * @return An instance of <code>SocketCAN</code> 
     * @throws IOException Creation of socket or the binding has failed
     * @since 1.1
     */
    public static final Can createSocketCan(final String canInterface
            , final int bitrate) throws IOException
    {
        final SocketCan socketCan = new SocketCan(canInterface, true);
        try
        {
            socketCan.initCanInterface(canInterface, bitrate);
            socketCan.init(canInterface);
            
            return socketCan;
        }
        catch (final IOException ex)
        {
            socketCan.close();
            throw ex;
        }
    }
    
    /**
     * Creates an instance of the <code>SocketCAN</code> class. Don't starts the
     * CAN bus interface. This must be controlled by external or use 
     * {@link #createSocketCan(java.lang.String, int) }.
     * @param canInterface The CAN interface (example: 'vcan0', 'can0', 'can1', ...)
     * @return An instance of <code>SocketCAN</code> 
     * @throws IOException Creation of socket or the binding has failed
     * @since 1.0
     */
    public static final Can createSocketCan(final String canInterface) throws IOException
    {
        final SocketCan socketCan = new SocketCan(canInterface, false);
        try
        {
            socketCan.init(canInterface);
            
            return socketCan;
        }
        catch (final IOException ex)
        {
            socketCan.close();
            throw ex;
        }
    }
    
    /**
     * Starts the CAN interface and sets the bitrate.
     * @param bitrate Bitrate of the CAN bus, can be a value between 1000(1kbit/s) 
     *        and 1000000(1000kbit/s)
     * @throws IOException Starting the CAN bus interface or setting of bitrate 
     *         has failed
     * @since 1.1
     */
    private native void initCanInterface(final String canInterface
            , final int bitrate) throws IOException;

    /**
     * Initialize the connection to CAN socket.
     * @throws IOException Creation of socket or the binding has failed
     * @since 1.0
     */
    private native void init(final String canInterface) throws IOException;

    /** {@inheritDoc } */
    @Override
    public void send(final CanFrame canFrame) throws IOException
    {
        final CanFrameNative frameNative = mapFrame(canFrame);
        sendNative(frameNative.id, frameNative.length, frameNative.data);
    }

    /** {@inheritDoc } */
    @Override
    public CanFrame receive() throws CanTimeoutException, IOException
    {
        return mapFrame(receiveNative());
    }
    
    /**
     * Sets the filters from local buffer to active socket.
     * @throws IOException Setting the filters has failed
     * @since 1.0
     */
    private native void setFilters(final CanFilterNative[] canFilters) throws IOException;

    /** {@inheritDoc } */
    @Override
    public void addFilters(final CanFilter... filters) throws IOException
    {
        if(filters == null || filters.length <= 0)
        {
            throw new IllegalArgumentException("Parameter filter can't be null or empty");
        }
        
        for(final CanFilter filter: filters)
        {
            currentFilters.add(mapFilter(filter));
        }
        
        setFilters(currentFilters.toArray(new CanFilterNative[currentFilters.size()]));
    }

    /** {@inheritDoc } */
    @Override
    public void removeFilters() throws IOException
    {
        currentFilters.clear();
        removeFiltersNative();
    }
    
    /**
     * Closes the connection to CAN socket and shutdown the CAN interface if 
     * factory method {@link #createSocketCan(java.lang.String, int) } was used 
     * for object creation.
     * @throws IOException If the closing of socket or the interface shutdown 
     *         has failed
     * @since 1.0
     */
    @Override
    public void close() throws IOException
    {
        removeFilters();
        closeNative(canInterface, interfaceControl);
    }

    /** {@inheritDoc } */
    @Override
    public native void setTimeout(final int timeout) throws IOException;

    /** {@inheritDoc } */
    @Override
    public native int getTimeout() throws IOException;

    /**
     * Maps from the <code>CanFrameNative</code> to <code>CanFrame</code>.
     * @param frame <code>CanFrameNative</code> for the mapping
     * @return The mapped <code>CanFrame</code> Object
     * @since 1.2
     */
    private static CanFrame mapFrame(final CanFrameNative frame)
    {
        final Type type;
        
        if((frame.id & CAN_EFF_FLAG) == CAN_EFF_FLAG)
        {
            type = CanFrame.Type.EXTENDED;
        }
        else if((frame.id & CAN_RTR_FLAG) == CAN_RTR_FLAG)
        {
            type = CanFrame.Type.REMOTE_TRANSMISSION_REQUEST;
        }
        else
        {
            type = CanFrame.Type.STANDARD;
        }
        
        // From JNA implementation, why?
//        final byte[] data = Arrays.copyOfRange(frame.data, 3, length + 3);

        return new CanFrame(frame.id, type, frame.length, frame.data);
    }

    /**
     * Maps from the <code>CanFrame</code> object to <code>CanFrameNative</code>.
     * @param frame The <code>CanFrame</code> object
     * @return The mapped <code>CanFrameNative</code>
     * @since 1.2
     */
    private static CanFrameNative mapFrame(final CanFrame frame)
    {
        final int id;
        switch(frame.getType())
        {
            case EXTENDED:
                id = frame.getId() | CAN_EFF_FLAG;
                break;
            case REMOTE_TRANSMISSION_REQUEST:
                id = frame.getId() | CAN_RTR_FLAG;
                break;
            default:
                id = frame.getId();
                break;
        }
        
        final CanFrameNative frameNative = new CanFrameNative(id, frame.getLength()
                , frame.getData());

        return frameNative;
    }
    
    /**
     * Maps from the <code>CanFilter</code> object to <code>CanFilterNative</code>.
     * @param filter The <code>CanFilter</code> object
     * @return The mapped <code>CanFilterNative</code>
     * @since 1.2
     */
    private static CanFilterNative mapFilter(final CanFilter filter)
    {
        final int id;
        final int mask;
        
        switch(filter.getType())
        {
            case EXTENDED:
                id = filter.getId() | CAN_EFF_FLAG;
                mask = filter.getMask() | CAN_EFF_FLAG;
                break;
            case REMOTE_TRANSMISSION_REQUEST:
                id = filter.getId() | CAN_RTR_FLAG;
                mask = filter.getMask() | CAN_RTR_FLAG;
                break;
            default:
                id = filter.getId();
                mask = filter.getMask();
                break;
        }
        
        return new CanFilterNative(id, mask);
    }
    
    /**
     * Sends an CAN frame over the socket.
     * @param id The ID of the frame in SocketCAN notation
     * @param length The length of the frame
     * @param data The data of the frame
     * @throws IOException If the sending over the socket has failed
     * @since 1.2
     */
    private native void sendNative(final int id, final byte length
            , final byte[] data) throws IOException;
    
    /**
     * Receives an CAN frame from socket in native format <code>CanFrameNative</code>.
     * @return The CAN frame in native format <code>CanFrameNative</code>
     * @throws CanTimeoutException An timeout is occured {@link #setTimeout(int) }
     * @throws IOException If the receiving from socket has failed
     * @since 1.2
     * @see #setTimeout(int) 
     */
    private native CanFrameNative receiveNative() 
            throws CanTimeoutException, IOException;
    
    /**
     * Removes filters from CAN socket.
     * @throws IOException Set the socket option has failed
     * @since 1.2
     */
    private native void removeFiltersNative() throws IOException;
    
    /**
     * Closes the socket and sets the CAN interface down if factory method 
     * {@link #createSocketCan(java.lang.String, int) } was used for object 
     * creation.
     * @param canInterface The name of the interface (e.g. vcan0, can0, ...)
     * @throws IOException If the socket close or the interface shutdown has failed
     * @since 1.2
     */
    private native void closeNative(final String canInterface
            , final boolean interfaceControl) throws IOException;
    
    /**
     * Class for transfering CAN frame content via JNI.
     * @since 1.2
     */
    private static class CanFrameNative
    {
        private final int id;
        private final byte length;
        private final byte[] data;

        public CanFrameNative(final int id, final byte length, final byte[] data)
        {
            this.id = id;
            this.length = length;
            this.data = data;
        }
    }
    
    /**
     * Class for transfering CAN filter content via JNI.
     * @since 1.2
     */
    private static class CanFilterNative
    {
        private final int id;
        private final int mask;

        public CanFilterNative(final int id, final int mask)
        {
            this.id = id;
            this.mask = mask;
        }

        public int getId()
        {
            return id;
        }

        public int getMask()
        {
            return mask;
        }
    }
}
