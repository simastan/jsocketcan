# About

**jSocketCan** is an framework for access to Linux SocketCAN from Java. Implements jCanInterface.

* Control the CAN bus interface
* Read and write CAN messages

**more documentation is available on overview page of javaDoc**

# License

[LGPLv3](http://www.gnu.org/licenses/lgpl.html)

Copyright (C) 2016 sitec systems GmbH

Library libsocketcan from Luotao Fu ([l.fu@pengutronix.de](l.fu@pengutronix.de))
from Pengutronix is an Part of jSocketCAN. It is published under GNU Lesser 
General Public License verion 2.1 and taken from Debian project
 [libsocketcan_0.0.10+git20140721.orig.tar.bz2](https://packages.debian.org/source/sid/libsocketcan).

# Example

Reads module information and prints the file listing of the connected module

```java
try(final Can can = SocketCan.createSocketCan("can0", 500000);)
{
    can.send(new CanFrame(0x123, CanFrame.Type.STANDARD, (byte)8, new byte[]{(byte)55, (byte)1, (byte)2, (byte)3, (byte)4, (byte)5, (byte)6, (byte)7}));
    can.addFilters(new CanFilter(0x21, 0xFF, CanFrame.Type.STANDARD));
    can.addFilters(new CanFilter(0x22, 0xFF, CanFrame.Type.STANDARD));

    System.out.println("Start receive");
    System.out.println(can.receive());
    System.out.println("End receive");
}
catch(final IOException ex)
{
    ex.printStackTrace();
}
```