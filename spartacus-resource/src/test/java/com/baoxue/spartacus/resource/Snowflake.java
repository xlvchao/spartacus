package com.xlc.spartacus.resource;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

import java.net.Inet4Address;
import java.net.UnknownHostException;

/**
 * Twitter_Snowflake
 * 
 * SnowFlake的结构如下(每部分用-分开):
 * 
 * 0 - 0000000000 0000000000 0000000000 0000000000 0 - 00000 - 00000 - 000000000000
 * 1位标识，由于long基本类型在Java中是带符号的，最高位是符号位，正数是0，负数是1，所以id一般是正数，最高位是0
 * 
 * 41位时间截(毫秒级)，注意，41位时间截不是存储当前时间的时间截，而是存储时间截的差值（当前时间截 - 开始时间截 得到的值）
 * 这里的开始时间截，一般是我们的id生成器开始使用的时间，由我们程序来指定（如下面程序IdWorker类的startTime属性）
 * 41位的时间截，可以使用69年，年T = (1L << 41) / (1000L * 60 * 60 * 24 * 365) = 69
 * 
 * 10位的数据机器位，可以部署在1024个节点，包括5位datacenterId和5位workerId
 * 
 * 12位序列，毫秒内的计数，12位的计数顺序号支持每个节点每毫秒(同一机器，同一时间截)产生4096个ID序号，加起来刚好64位，为一个Long型
 * 
 * SnowFlake的优点：
 * 整体上按照时间自增排序，并且整个分布式系统内不会产生ID碰撞(由数据中心ID和机器ID作区分)，并且效率较高，经测试，SnowFlake每秒能够产生26万ID左右
 * 
 * 
 * 本项目中的遗留问题：
 * 1、各个数据中心的各个机器，防止出现时钟回退
 * 2、workId使用服务器hostName生成，dataCenterId使用IP生成，这样可以最大限度防止10位机器码重复，但是由于两个ID都不能超过32，只能取余数，还
 * 是难免产生重复，但是实际使用中，hostName和IP的配置一般连续或相近，只要不是刚好相隔32位，就不会有问题，况且，hostName和IP同时相隔32的情况更
 * 加是几乎不可能的事，平时做的分布式部署，一般也不会超过10台容器
 */
public class Snowflake {
 
    // ==============================Fields===========================================
	
    /** 开始时间截 (2019-05-23) */
    private final long startTimeStamp = 1558602353745L;
 
    /** 机器id所占的位数 */
    private final long workerIdBits = 5L;
 
    /** 数据标识id所占的位数 */
    private final long dataCenterIdBits = 5L;
 
    /** 支持的最大机器id，结果是31 (这个移位算法可以很快的计算出几位二进制数所能表示的最大十进制数) */
    private final long maxWorkerId = -1L ^ (-1L << workerIdBits);
 
    /** 支持的最大数据中心id，结果是31 */
    private final long maxDataCenterId = -1L ^ (-1L << dataCenterIdBits);
 
    /** 序列在id中占的位数 */
    private final long sequenceBits = 12L;
 
    /** 机器ID向左移12位 */
    private final long workerIdShift = sequenceBits;
 
    /** 数据中心id向左移17位(12+5) */
    private final long dataCenterIdShift = sequenceBits + workerIdBits;
 
    /** 时间截向左移22位(5+5+12) */
    private final long timestampLeftShift = sequenceBits + workerIdBits + dataCenterIdBits;
 
    /** 生成序列的掩码，这里为4095 (0b111111111111=0xfff=4095) */
    private final long sequenceMask = -1L ^ (-1L << sequenceBits);
 
    /** 工作机器ID(0~31) */
    private long workerId;
 
    /** 数据中心ID(0~31) */
    private long dataCenterId;
 
    /** 毫秒内序列(0~4095) */
    private long sequence = 0L;
 
    /** 上次生成ID的时间截 */
    private long lastTimestamp = -1L;
 
    private static Snowflake snowflake;
 
    static {
        snowflake = new Snowflake(getWorkId(),getDataCenterId());
    }
 
    //==============================Constructors=====================================
    
    /**
     * 构造函数
     * @param workerId 工作ID (0~31)
     * @param dataCenterId 数据中心ID (0~31)
     */
    public Snowflake(long workerId, long dataCenterId) {
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("workerId can't be greater than %d or less than 0", maxWorkerId));
        }
        if (dataCenterId > maxDataCenterId || dataCenterId < 0) {
            throw new IllegalArgumentException(String.format("dataCenterId can't be greater than %d or less than 0", maxDataCenterId));
        }
        this.workerId = workerId;
        this.dataCenterId = dataCenterId;
    }
 
    // ==============================Methods==========================================
    
    /**
     * 获得下一个ID（线程安全）
     * @return SnowflakeId
     */
    public synchronized long nextId() {
        long timestamp = getNowTimeMillis();
 
        //如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过这个时候应当抛出异常
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(
                    String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }
 
        //如果是同一时间生成的，则进行毫秒内序列
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            //毫秒内序列溢出
            if (sequence == 0) {
                //阻塞到下一个毫秒,获得新的时间戳
                timestamp = getNextTimeMillis(lastTimestamp);
            }
        }
        //时间戳改变，毫秒内序列重置
        else {
            sequence = 0L;
        }
 
        //上次生成ID的时间截
        lastTimestamp = timestamp;
 
        //移位并通过或运算拼到一起组成64位的ID
        return ((timestamp - startTimeStamp) << timestampLeftShift)
                | (dataCenterId << dataCenterIdShift)
                | (workerId << workerIdShift)
                | sequence;
    }
 
    /**
     * 阻塞到下一个毫秒，直到获得新的时间戳
     * @param lastTimestamp 上次生成ID的时间截
     * @return 当前时间戳
     */
    protected long getNextTimeMillis(long lastTimestamp) {
        long timestamp = getNowTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = getNowTimeMillis();
        }
        return timestamp;
    }
 
    /**
     * 返回以毫秒为单位的当前时间
     * @return 当前时间(毫秒)
     */
    protected long getNowTimeMillis() {
        return System.currentTimeMillis();
    }
 
    /**
     * 生成服务器id
     *  
     * @return
     */
    private static Long getWorkId(){
        try {
            String hostAddress = Inet4Address.getLocalHost().getHostAddress();
            int[] ints = StringUtils.toCodePoints(hostAddress);
            int sums = 0;
            for(int b : ints){
                sums += b;
            }
            return (long)(sums % 32);
        } catch (UnknownHostException e) {
            // 如果获取失败，则使用随机数备用
            return RandomUtils.nextLong(0,31);
        }
    }
 
    /**
     * 生成数据中心id
     *  
     * @return
     */
    private static Long getDataCenterId(){
        int[] ints = StringUtils.toCodePoints(SystemUtils.getHostName());
        int sums = 0;
        for (int i: ints) {
            sums += i;
        }
        return (long)(sums % 32);
    }
 
 
    /**
     * 生成一个全局分布式唯一的id
     *  
     * @return
     */
    public static Long generateId(){
        long id = snowflake.nextId();
        return id;
    }
 
    
    /*public static void main(String[] args) throws UnknownHostException {
        long startTime = System.nanoTime();
        for (int i = 0; i < 50; i++) {
            long id = Snowflake.generateId();
            System.out.println(id);
        }
        System.out.println((System.nanoTime()-startTime)/1000000+"ms");
    }*/

}