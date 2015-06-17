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
import de.sitec.ci4j.Can;
import de.sitec.ci4j.CanFilter;
import de.sitec.ci4j.CanFrame;
import de.sitec.ci4j.CanFrame.Type;
import java.io.IOException;
import java.util.Arrays;

/**
 * An implementation of <code>Can</code> interface for SocketCAN.
 * @author sitec systems GmbH
 * @since 1.0
 */
public class SocketCan implements Can
{
    private final String canInterface;
    private int socket;
    
    private final static int SOCK_RAW = 3;   /* Choose Socket Type */
    private final static int CAN_RAW = 1;    /* Choose CAN Protocol */
    private final static int PF_CAN = 29;    /* Chose Socket Protocol */
    private final static int AF_CAN = 29;
//    private final static int SIOCGIFINDEX = 0x8933;  /* for ioctl request */
    private final static int SOL_CAN_RAW = 101;
    private final static int CAN_RAW_FILTER = 1;
//    private final static int EF_MASK = 0x1fffffff;
    private static final int CAN_EFF_FLAG = 0x80000000;
    private static final int CAN_RTR_FLAG = 0x40000000;

    private static native int socket(int socket_family, int socket_type, int protocol);
    private static native int bind(int sockfd, SocketAddressCanStruct addr, int len);
    private static native int write(int sockfd, CanFrameStruct frame, int len);
    private static native int read(int sockfd, CanFrameStruct frame, int len);
//    private static native int ioctl(int d, int request, InterfaceRequestStruct ifr);
    private static native int setsockopt(int sockfd, int level, int option_name,
            CanFilterStruct filter, int len);
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

        /* Close_the_input_no_read */
//        if(setsockopt(socket, SOL_CAN_RAW, CAN_RAW_FILTER, null, 0) != 0)
//        {
//            throw new IOException("Setting the CAN options on: " + canInterface 
//                    + " has failed");
//        }

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

    /**
     * Set the device state down
     */
//    private void setDeviceDown()
//    {
//        Runtime rt = Runtime.getRuntime();
//        Process process;
//        try
//        {
//            /* Powerdown the device */
//            process = rt.exec("ip link set " + dev + " down");
//            process.waitFor();
//
//        }
//        catch (IOException | InterruptedException ex)
//        {
//            Logger.getLogger(CanJna.class.getName()).log(Level.SEVERE,
//                    "Can't set the device down", ex);
//        }
//    }

    /**
     * Set the device state down
     */
//    private void setDeviceUp()
//    {
//        Runtime rt = Runtime.getRuntime();
//        Process process;
//        try
//        {
//            /* Powerdown the device */
//            process = rt.exec("ip link set " + dev + " up");
//            process.waitFor();
//
//        }
//        catch (IOException | InterruptedException ex)
//        {
//            Logger.getLogger(CanJna.class.getName()).log(Level.SEVERE,
//                    "Can't set the device up", ex);
//        }
//    }

//    /**
//     * Set a new bitrate on the device
//     *
//     * @param bitrate New bitrate for the device
//     */
//    @Override
//    public void setBitrate(int bitrate)
//    {
////        Runtime rt = Runtime.getRuntime();
////        Process process;
////
////        /* Set the device down */
////        setDeviceDown();
////        try
////        {
////            /*Change the bitrate */
////            process = rt.exec("ip link set " + dev + " type can bitrate " + bitrate);
////            process.waitFor();
////            setDeviceUp();
////        }
////        catch (IOException | InterruptedException ex)
////        {
////            Logger.getLogger(CanJna.class.getName()).log(Level.SEVERE,
////                    "Can't set the device bitrate", ex);
////        }
//        throw new UnsupportedOperationException();
//    }

//    /**
//     * Get the bitrate of an CAN-Device
//     *
//     * @return Bitrate
//     */
//    @Override
//    public int getBitrate()
//    {
////        int bitrate = 0;
////        Runtime rt = Runtime.getRuntime();
////        Process process;
////
////        int readedChar;
////        String[] lines;
////        String[] words;
////
////        try
////        {
////            process = rt.exec("ip -details link show " + dev);
////            InputStream is = process.getInputStream();
////            process.waitFor();
////
////            StringBuilder sb = new StringBuilder();
////            for (int i = 0; i < 300; i++)
////            {
////                readedChar = is.read();
////                sb.append((char) readedChar);
////            }
////
////            /*
////             * Output has multiple lines. First we have to split the text in 
////             * his lines
////             */
////            lines = sb.toString().split("\n");
////
////            /*
////             * The intresting Line is the line 3. Now we split the line in the
////             * words
////             */
////            words = lines[3].split(" ");
////
////            /* The 6th word is the bitrate */
////            bitrate = Integer.parseInt(words[5]);
////
////        }
////        catch (IOException | InterruptedException ex)
////        {
////            Logger.getLogger(CanJna.class.getName()).log(Level.SEVERE,
////                    "Can't read the bitrate", ex);
////        }
////        finally
////        {
////            return bitrate;
////        }
//        
//        throw new UnsupportedOperationException();
//    }

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
            throw new IOException("Can't recieve CAN frame from CAN interface: " 
                    + canInterface);
        }
        
        return frameMapper(frame);
    }

    /** {@inheritDoc } */
    @Override
    public void setFilter(final CanFilter filter) throws IOException
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
        
        if (setsockopt(socket, SOL_CAN_RAW, CAN_RAW_FILTER, filterNative,
                    filterNative.size()) != 0)
        {
            throw new IOException("Can't set the filter for: " + canInterface);
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
}
