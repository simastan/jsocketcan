/**
 * jSocketCan is an Framework for Access to Linux SocketCAN over JNA. 
 * Implements jCanInterface.
 * 
 * Copyright (C) 2016 sitec systems GmbH <http://www.sitec-systems.de>
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
#include <de_sitec_systems_jsocketcan_SocketCan.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <errno.h>

#include <net/if.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <sys/ioctl.h>

#include <linux/can.h>
#include <linux/can/raw.h>
#include <libsocketcan.h>


#define ERROR_CODE_INDICATOR " - Err: "
#define MICROSECOND_FACTOR 1000
#define SUCCESS 0

int sock = 0;

void throw_new(JNIEnv *env, const char *exn, const char *message)
{
    const jclass cls = (*env)->FindClass(env, exn);
    if(cls != NULL)
    {
        (*env)->ThrowNew(env, cls, message);
    }
    else
    {
        perror("Find class has failed\n");
    }
}

void throw_new_err_code(JNIEnv *env, const char *exn, const char *message, const int error_code)
{
    const uint len = snprintf(NULL, 0, "%s%s%d", message, ERROR_CODE_INDICATOR, error_code);
    char *error_message = malloc(len+1);
    if(error_message != NULL)
    {
        sprintf(error_message, "%s%s%d", message, ERROR_CODE_INDICATOR, error_code);
        throw_new(env, exn, error_message);
    }
    else
    {
        perror("Pass error has failed\n");
    }
    
    free(error_message);
}

JNIEXPORT void JNICALL Java_de_sitec_1systems_jsocketcan_SocketCan_init(JNIEnv *env, jobject jobj, const jstring canInterface)
{
    const int s = socket(PF_CAN, SOCK_RAW, CAN_RAW);
    if (s > 0) 
    {
        sock = s;
        
        const char *can_intf_chars = (*env)->GetStringUTFChars(env, canInterface, (jboolean)0);
        struct ifreq ifr;
        strcpy(ifr.ifr_name, can_intf_chars);
        ioctl(sock, SIOCGIFINDEX, &ifr);

        struct sockaddr_can addr;
        addr.can_family  = AF_CAN;
        addr.can_ifindex = ifr.ifr_ifindex;
        const int bind_res = bind(sock, (struct sockaddr *)&addr, sizeof(addr));
        if(bind_res < 0) 
        {
            throw_new_err_code(env,"java/io/IOException","Binding CAN socket has failed", bind_res);
        }
    }
    else
    {
        throw_new_err_code(env,"java/io/IOException","Creating CAN socket has failed", s);
    }
}

JNIEXPORT void JNICALL Java_de_sitec_1systems_jsocketcan_SocketCan_closeNative(JNIEnv *env, jobject jobj, const jstring canInterface, const jboolean interfaceControl)
{
    if(sock != 0)
    {
        const int ret = close(sock);
        if(ret != 0)
        {
            throw_new_err_code(env,"java/io/IOException","Close CAN socket has failed", ret);
        }
    }
    
    if(interfaceControl == JNI_TRUE)
    {
        const char *temp_string = (*env)->GetStringUTFChars(env, canInterface, (jboolean)0);

        if(can_do_stop(temp_string) != SUCCESS)
        {
            throw_new(env,"java/io/IOException", "Stop of CAN interface has failed");
        }

        (*env)->ReleaseStringUTFChars(env, canInterface, temp_string);
    }
}

JNIEXPORT void JNICALL Java_de_sitec_1systems_jsocketcan_SocketCan_sendNative(JNIEnv *env, jobject jobj, const jint id, const jbyte length, const jbyteArray data)
{
    struct can_frame frame;
    
    frame.can_id  = id;
    frame.can_dlc = length;
    
    const uint new_size = (*env)->GetArrayLength(env, data);
    jbyte* bytes = (*env)->GetByteArrayElements(env, data, 0 );
    int i;
    for(i=0; i<new_size; i++)
    {
        frame.data[i] = bytes[i];
    }
    
    const int written = write(sock, &frame, sizeof(struct can_frame));
    if(written < 0)
    {
        throw_new_err_code(env,"java/io/IOException","Send CAN frame has failed", written);
    }

    (*env)->ReleaseByteArrayElements(env, data, bytes, 0);
}

JNIEXPORT jobject JNICALL Java_de_sitec_1systems_jsocketcan_SocketCan_receiveNative(JNIEnv *env, jobject jobj)
{
    struct can_frame frame;
    const int readed = read(sock, &frame, sizeof(struct can_frame));
    if(readed > 0)
    {
        jbyteArray temp_array = (*env)->NewByteArray(env, frame.can_dlc);
        if(temp_array != NULL)
        {
            jbyte *temp = (*env)->GetByteArrayElements(env, temp_array, 0);
            if(temp != NULL)
            {
                int i;
                for(i=0; i<frame.can_dlc; i++)
                {
                    temp[i] = frame.data[i];
                }
                (*env)->SetByteArrayRegion(env, temp_array, 0, frame.can_dlc, temp);

                const jclass clsObj = (*env)->FindClass(env, "de/sitec_systems/jsocketcan/SocketCan$CanFrameNative");
                if(clsObj != NULL)
                {
                    const jmethodID methodID = (*env)->GetMethodID(env, clsObj, "<init>", "(IB[B)V");
                    return (*env)->NewObject(env,clsObj, methodID, (jint)frame.can_id, (jbyte)frame.can_dlc, temp_array);
                }
                else
                {
                    throw_new(env,"java/io/IOException","Find class has failed");
                }
                
            }
            else
            {
                throw_new(env,"java/io/IOException","Getting byte array elements has failed");
            }
        }
        else
        {
            throw_new(env,"java/io/IOException","Creating new jbyteArray has failed");
        }
    }
    else
    {
        if(errno == EAGAIN)
        {
            throw_new(env,"de/sitec_systems/jcaninterface/CanTimeoutException", "Timeout occured");
        }
        else
        {
            throw_new_err_code(env,"java/io/IOException","Read CAN frame has failed", errno);
        }
    }
    
    return NULL;
}

JNIEXPORT void JNICALL Java_de_sitec_1systems_jsocketcan_SocketCan_setTimeout(JNIEnv *env, jobject jobj, const jint timeout)
{
    struct timeval tv;
    tv.tv_sec = timeout / MICROSECOND_FACTOR;
    tv.tv_usec = (timeout - (tv.tv_sec * MICROSECOND_FACTOR)) * MICROSECOND_FACTOR;

    if (setsockopt(sock, SOL_SOCKET, SO_RCVTIMEO, &tv, sizeof(tv)) < 0) 
    {
        throw_new_err_code(env,"java/io/IOException", "Setting timeout has failed", errno);
    }
}

JNIEXPORT jint JNICALL Java_de_sitec_1systems_jsocketcan_SocketCan_getTimeout(JNIEnv *env, jobject jobj)
{
    struct timeval tv;
    socklen_t len = sizeof(struct timeval);
    
    if (getsockopt(sock, SOL_SOCKET, SO_RCVTIMEO, &tv, &len) == SUCCESS) 
    {
        return (tv.tv_sec * MICROSECOND_FACTOR) + (tv.tv_usec / MICROSECOND_FACTOR);
    }
    else
    {
        throw_new_err_code(env,"java/io/IOException", "Getting timeout has failed", errno);
    }
}

JNIEXPORT void JNICALL Java_de_sitec_1systems_jsocketcan_SocketCan_setFilters(JNIEnv *env, jobject jobj, const jobjectArray filters)
{
    const uint filters_size = (*env)->GetArrayLength(env, filters);
    struct can_filter *native_filters;
    const uint filter_arr_size = filters_size * sizeof(struct can_filter);
    native_filters = (struct can_filter *)malloc(filter_arr_size);
    int i;
    for(i=0; i<filters_size; i++)
    {
        const jobject filter = (*env)->GetObjectArrayElement(env, filters, i);
        if(filter > 0)
        {
            const jclass cls_obj = (*env)->GetObjectClass(env, filter);
            if(cls_obj > 0)
            {
                const jmethodID meth_id = (*env)->GetMethodID(env, cls_obj, "getId", "()I");
                if(meth_id > 0)
                {
                    const jmethodID meth_mask = (*env)->GetMethodID(env, cls_obj, "getMask", "()I");
                    if(meth_mask > 0)
                    {
                        const jint id = (*env)->CallIntMethod(env, filter, meth_id);
                        const jint mask = (*env)->CallIntMethod(env, filter, meth_mask);
                        native_filters[i].can_id = id;
                        native_filters[i].can_mask = mask;
                    }
                    else
                    {
                        throw_new(env,"java/io/IOException", "Method getMask not found");
                        goto clear;
                    }
                }
                else
                {
                    throw_new(env,"java/io/IOException", "Method getId not found");
                    goto clear;
                }
            }
            else
            {
                throw_new(env,"java/io/IOException", "Class not found");
                goto clear;
            }
        }
        else
        {
            throw_new(env,"java/io/IOException", "Get element from filter array has failed");
            goto clear;
        }
    }
    
    if (setsockopt(sock, SOL_CAN_RAW, CAN_RAW_FILTER, native_filters, filter_arr_size) != SUCCESS)
    {
        throw_new_err_code(env,"java/io/IOException", "Cant add filters", errno);
    }
    
    clear:
        free(native_filters);
}

JNIEXPORT void JNICALL Java_de_sitec_1systems_jsocketcan_SocketCan_removeFiltersNative(JNIEnv *env, jobject jobj)
{
    if(setsockopt(sock, SOL_CAN_RAW, CAN_RAW_FILTER, NULL, 0) != SUCCESS)
    {
        throw_new_err_code(env,"java/io/IOException", "Remove filters from CAN socket has failed", errno);
    }
}

JNIEXPORT void JNICALL Java_de_sitec_1systems_jsocketcan_SocketCan_initCanInterface(JNIEnv *env, jobject jobj, const jstring canInterface, const jint bitrate)
{
    const char *temp_string = (*env)->GetStringUTFChars(env, canInterface, (jboolean)0);
    
    if(can_do_stop(temp_string) != SUCCESS)
    {
        throw_new_err_code(env,"java/io/IOException", "Stop of CAN interface has failed", errno);
        goto clear;
    }
    if(can_set_bitrate(temp_string, bitrate) != SUCCESS)
    {
        throw_new_err_code(env,"java/io/IOException", "Baudrate", errno);
        goto clear;
    }
    if(can_do_start(temp_string) != SUCCESS)
    {
        throw_new_err_code(env,"java/io/IOException", "Start of CAN interface has failed", errno);
        goto clear;
    }
    
    clear:
        (*env)->ReleaseStringUTFChars(env, canInterface, temp_string);
}