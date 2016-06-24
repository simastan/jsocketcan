cmake_minimum_required(VERSION 2.8)
project(socket_can)
#set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")
set(CLASSPATH "" CACHE STRING "Path from which to load classes")
set(TARGET_DIR "${CMAKE_CURRENT_BINARY_DIR}/../..")
set(GEN_SRC_DIR "${TARGET_DIR}/generated-sources")
set(ARCH "" CACHE STRING "Target architecture")
include_directories(${GEN_SRC_DIR} $ENV{JAVA_HOME}/include $ENV{JAVA_HOME}/include/linux)

add_custom_target(socket_can_jni_header javah -classpath ${CLASSPATH} -o 
    ${GEN_SRC_DIR}/de_sitec_jsocketcan_SocketCan.h de.sitec.jsocketcan.SocketCan)
add_library(socketcan_jni SHARED ${CMAKE_CURRENT_SOURCE_DIR}/src/socketcan_jni.c)
add_dependencies(socketcan_jni socket_can_jni_header)
target_link_libraries(socketcan_jni socketcan)
install(TARGETS socketcan_jni DESTINATION ${TARGET_DIR}/classes/native/de/sitec/jsocketcan/linux/${ARCH})

