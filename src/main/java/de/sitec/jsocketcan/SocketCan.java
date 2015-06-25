/*
 * Project: 15_0013_CF4J
 * $Header: $
 * Author:  Mattes Standfuss
 * Last Change:
 *    by:   $Author: $
 *    date: $Date:   $
 * Copyright (c): sitec systems GmbH, 2015
 */
package de.sitec.jsocketcan;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import de.sitec.ci4j.Can;
import de.sitec.ci4j.CanFilter;
import de.sitec.ci4j.CanFrame;
import de.sitec.ci4j.CanFrame.Type;
import de.sitec.ci4j.CanTimeoutException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
//import net.jazdw.jnacan.c.can_filter;

/**
 * An implementation of <code>Can</code> interface for SocketCAN.
 * @author sitec systems GmbH
 * @since 1.0
 */
public class SocketCan implements Can
{
    private final List<CanFilterStruct> currentFilters = new ArrayList<>();
    private final String canInterface;
    private int socket;
    
    private final static int SOCK_RAW = 3;   /* Choose Socket Type */
    private final static int CAN_RAW = 1;    /* Choose CAN Protocol */
    private final static int PF_CAN = 29;    /* Chose Socket Protocol */
    private final static int AF_CAN = 29;
//    private final static int SIOCGIFINDEX = 0x8933;  /* for ioctl request */
    private final static int SOL_CAN_RAW = 101;
    private static final int EAGAIN = 11;
    
    
    // TODO: check for ARMHF, ARMEL, AMD64, X86
    private static final byte SOL_SOCKET = 1;
    private static final byte SO_RCVTIMEO = 20;
    
    
    
    private final static int CAN_RAW_FILTER = 1;
    private static final int CAN_EFF_FLAG = 0x80000000;
    private static final int CAN_RTR_FLAG = 0x40000000;
    private static final CanFilterStruct DISABLE_FILTER = new CanFilterStruct(0, 0);

    private static native int socket(int socket_family, int socket_type, int protocol);
    private static native int bind(int sockfd, SocketAddressCanStruct addr, int len);
    private static native int write(int sockfd, CanFrameStruct frame, int len);
    private static native int read(int sockfd, CanFrameStruct frame, int len);
//    private static native int ioctl(int d, int request, InterfaceRequestStruct ifr);
    private static native int setsockopt(int sockfd, int level, int option_name,
            Pointer filters, int len);
    private static native int setsockopt(int sockfd, int level, int option_name,
            CanFilterStruct filter, int len);
    private static native int setsockopt(int sockfd, int level, int option_name,
            TimeValue timeval, int len);
    private static native int getsockopt(int sockfd, int level, int option_name,
            TimeValue timeval, IntByReference len);
    private static native int close(int fd);

    static
    {
        Native.register("c");
    }

    /**
     * Constructur-
     * @param canInterface The CAN interface
     * @since 1.0
     */
    private SocketCan(final String canInterface)
    {
        // TODO: Input check
//        if(canInterface < 0)
//        {
//            throw new IllegalArgumentException("CAN interface cant be '0'");
//        }
        this.canInterface = canInterface;
    }
    
    /**
     * Creates an instance of the <code>SocketCAN</code> class.
     * @param canInterface The CAN interface
     * @return An instance of <code>SocketCAN</code> 
     * @throws IOException Creation of socket or the binding has failed
     * @since 1.0
     */
    public static final Can createSocketCan(final String canInterface) throws IOException
    {
        final SocketCan socketCan = new SocketCan(canInterface);
        try
        {
            socketCan.init();
            
            return socketCan;
        }
        catch (final IOException ex)
        {
            socketCan.close();
            throw ex;
        }
    }

    /**
     * Initialize the connection to CAN socket.
     * @throws IOException Creation of socket or the binding has failed
     * @since 1.0
     */
    private void init() throws IOException
    {
        final InterfaceRequestStruct interfaceRequest = new InterfaceRequestStruct();
        interfaceRequest.ifr_ifrn.setType(String.class);
        interfaceRequest.ifr_ifru.setType(int.class);
        
        final SocketAddressCanStruct address = new SocketAddressCanStruct();

        /* Open the socket */
        if ((socket = socket(PF_CAN, SOCK_RAW, CAN_RAW)) <= 0)
        {
            throw new IOException("Can't create Socket for CAN interface: " 
                    + canInterface);
        }

        /* Set the Device */
        address.can_family = AF_CAN;
        interfaceRequest.ifr_ifrn.ifrn_name = canInterface;

        /* 
         * TODO: FIXME -- ioctl call doesn't work set the index of the interface by 
         * hand
         */
//        int ret = ioctl(socket, SIOCGIFINDEX, interfaceRequest);
//        if (ret != 0)
//        {
//            perror("Unable to read interface index");
//            throw new IOException("Unable to read interface index");
//        }
        
        address.can_ifindex = 3;
        
        System.out.println("Socket pre bind: " + socket);
        // bind socket to interface
        if (bind(socket, address, address.size()) != 0)
        {
            throw new IOException("Can't bind Socket to CAN interface: " 
                    + canInterface);
        }
        
    }

    /** {@inheritDoc } */
    @Override
    public void send(final CanFrame canFrame) throws IOException
    {
        final CanFrameStruct frameNative = frameMapper(canFrame);
        
        if (write(socket, frameNative, frameNative.size()) <= 0)
        {
            throw new IOException("Can't send CAN frame");
        }
    }

    /** {@inheritDoc } */
    @Override
    public CanFrame receive() throws IOException
    {
        final CanFrameStruct frame = new CanFrameStruct();
        
        final int bytes = read(socket, frame, frame.size());
        if (bytes < 0)
        {
            if(Native.getLastError() == EAGAIN)
            {
                throw new CanTimeoutException();
            }
            else
            {
                throw new IOException("Can't recieve CAN frame from CAN interface: " 
                    + canInterface);
            }
        }
        
        return frameMapper(frame);
    }
    
    /**
     * Adds the filter to the local buffer.
     * @param filter The filter to add
     * @since 1.0
     */
    private void addFilterToBuffer(final CanFilter filter)
    {
        final int id;
        final int mask;

        if(filter.getType() == Type.EXTENDED)
        {
            id = filter.getId() | CAN_EFF_FLAG;
            mask = filter.getMask() | CAN_EFF_FLAG;
        }
        else if(filter.getType() == Type.REMOTE_TRANSMISSION_REQUEST)
        {
            id = filter.getId() | CAN_RTR_FLAG;
            mask = filter.getMask() | CAN_RTR_FLAG;
        }
        else
        {
            id = filter.getId();
            mask = filter.getMask();
        }


        final CanFilterStruct filterNative = new CanFilterStruct();
        filterNative.can_id = id;
        filterNative.can_mask = mask;

        currentFilters.add(filterNative);
    }
    
    /**
     * Sets the filters from local buffer to active socket.
     * @throws IOException Setting the filters has failed
     * @since 1.0
     */
    private void setFilters() throws IOException
    {
        final CanFilterStruct[] filtersStruct = currentFilters.toArray(new CanFilterStruct[currentFilters.size()]);
        final CanFilterStruct.ByReference filterReference = new CanFilterStruct.ByReference();
        final CanFilterStruct[] filtersParameter = (CanFilterStruct[])filterReference.toArray(currentFilters.size());
        for(int i=0; i<filtersStruct.length; i++)
        {
            filtersParameter[i].can_id = filtersStruct[i].can_id;
            filtersParameter[i].can_mask = filtersStruct[i].can_mask;
            filtersParameter[i].write();
        }
        
        if (setsockopt(socket, SOL_CAN_RAW, CAN_RAW_FILTER, filterReference.getPointer(),
                    filtersParameter[0].size() * filtersParameter.length) != 0)
        {
            throw new IOException("Can't add filters for: " + canInterface);
        }
    }

    /** {@inheritDoc } */
    @Override
    public void addFilters(final CanFilter... filters) throws IOException
    {
        for(final CanFilter filter: filters)
        {
            addFilterToBuffer(filter);
        }
        
        setFilters();
    }

    /** {@inheritDoc } */
    @Override
    public void removeFilters() throws IOException
    {
        currentFilters.clear();
        if(setsockopt(socket, SOL_CAN_RAW, CAN_RAW_FILTER, DISABLE_FILTER, DISABLE_FILTER.size()) != 0)
        {
            throw new IOException("Removing of CAN filters on: " + canInterface 
                    + " has failed");
        }
    }

    /**
     * Maps from the JNA CAN Structure to <code>CanFrame</code>.
     * @param frame JNA CanFrameStructure for the mapping
     * @return The mapped <code>CanFrame</code> Object
     * @since 1.0
     */
    private static CanFrame frameMapper(final CanFrameStruct frame)
    {
        final int id = frame.can_id;
        final byte length = frame.can_dlc;
        final Type type;
        
        if((id & CAN_EFF_FLAG) == CAN_EFF_FLAG)
        {
            type = CanFrame.Type.EXTENDED;
        }
        else if((id & CAN_RTR_FLAG) == CAN_RTR_FLAG)
        {
            type = CanFrame.Type.REMOTE_TRANSMISSION_REQUEST;
        }
        else
        {
            type = CanFrame.Type.STANDARD;
        }
        
        final byte[] data = Arrays.copyOfRange(frame.data, 3, length + 3);

        return new CanFrame(id, type, length, data);
    }

    /**
     * Maps from the <code>CanFrame</code> object to JNA based CAN frame structure.
     * @param frame The <code>CanFrame</code> object
     * @return Th mapped JNA based CAN frame structure
     * @since 1.0
     */
    private static CanFrameStruct frameMapper(final CanFrame frame)
    {
        final int id;
        if(frame.getType() == Type.EXTENDED)
        {
            id = frame.getId() | CAN_EFF_FLAG;
        }
        else if(frame.getType() == Type.REMOTE_TRANSMISSION_REQUEST)
        {
            id = frame.getId() | CAN_RTR_FLAG;
        }
        else
        {
            id = frame.getId();
        }
        
        final CanFrameStruct frameNative = new CanFrameStruct(id, frame.getLength(), frame.getData());

        return frameNative;
    }

    /**
     * Closes the connection to CAN socket.
     * @throws IOException If the clsoing of socket has failed
     * @since 1.0
     */
    @Override
    public void close() throws IOException
    {
        if(socket != 0)
        {
            try
            {
                if (close(socket) < 0)
                {
                    throw new IOException("Could not close the socket for CAN interface: " 
                            + canInterface);
                }
            }
            finally
            {
                socket = 0;
            }
        }
    }

    /** {@inheritDoc } */
    @Override
    public void setTimeout(final int timeout) throws IOException
    {
        final TimeValue timeValue = new TimeValue(timeout);
        if(setsockopt(socket, SOL_SOCKET, SO_RCVTIMEO, timeValue, timeValue.size()) != 0)
        {
            throw new IOException("Setting the timeout on: " + canInterface 
                    + " has failed");
        }
    }

    /** {@inheritDoc } */
    @Override
    public int getTimeout() throws IOException
    {
        final TimeValue timeValue = new TimeValue();
        final IntByReference timevalSize = new IntByReference(timeValue.size());
        if(getsockopt(socket, SOL_SOCKET, SO_RCVTIMEO, timeValue, timevalSize) != 0)
        {
            throw new IOException("Reading CAN timeout value has failed");
        }
        
        return (int)timeValue.getMillis();
    }
}
