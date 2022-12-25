package com.grampus.hualauncherkai.TcpSock;


public class SockTransfer {
    public static byte[] toLH(int n) {
        byte[] b = new byte[4];
        b[0] = (byte) (n & 0xff);
        b[1] = (byte) (n >> 8 & 0xff);
        b[2] = (byte) (n >> 16 & 0xff);
        b[3] = (byte) (n >> 24 & 0xff);
        return b;
    }

    /**
     * 将int转为高字节在前，低字节在后的byte数组
     * @param n int
     * @return byte[]
     */
    public static byte[] toHH(int n) {
        byte[] b = new byte[4];
        b[3] = (byte) (n & 0xff);
        b[2] = (byte) (n >> 8 & 0xff);
        b[1] = (byte) (n >> 16 & 0xff);
        b[0] = (byte) (n >> 24 & 0xff);
        return b;
    }

    /**
     * 将short转为低字节在前，高字节在后的byte数组
     * @param n short
     * @return byte[]
     */
    public static byte[] toLH(short n) {
        byte[] b = new byte[2];
        b[0] = (byte) (n & 0xff);
        b[1] = (byte) (n >> 8 & 0xff);
        return b;
    }

    /**
     * 将short转为高字节在前，低字节在后的byte数组
     * @param n short
     * @return byte[]
     */
    public static byte[] toHH(short n) {
        byte[] b = new byte[2];
        b[1] = (byte) (n & 0xff);
        b[0] = (byte) (n >> 8 & 0xff);
        return b;
    }

    public static short bytesToInt16(byte[] data) {
        return (short) (
                (data[0] & 0x00ff) |
                        ((data[1] << 8) & 0xff00)
        );
    }

    public static int bytesToInt32(byte[] data) {
        return (
                (data[0] & 0x000000ff) |
                        ((data[1] << 8) & 0x0000ff00) |
                        ((data[2] << 16) & 0x00ff0000) |
                        ((data[3] << 24) & 0xff000000)
        );
    }
    public static int int32Reverse(int n) {
        int a0= (byte) (n & 0xff);
        int a1 = (byte) (n >> 8 & 0xff);
        int a2 = (byte) (n >> 16 & 0xff);
        int a3 = (byte) (n >> 24 & 0xff);


        return (
                (a3 & 0x000000ff) |
                        ((a2 << 8) & 0x0000ff00) |
                        ((a1 << 16) & 0x00ff0000) |
                        ((a0 << 24) & 0xff000000)
        );
    }

}
