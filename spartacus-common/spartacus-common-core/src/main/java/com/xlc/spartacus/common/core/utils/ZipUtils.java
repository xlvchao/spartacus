package com.xlc.spartacus.common.core.utils;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;
import sun.misc.BASE64Encoder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.zip.Deflater;

/**
 * 文件压缩工具
 *
 * @author xlc, since 2021
 */
public class ZipUtils {

    /**
     * 文件流压缩
     *
     * @param bytes 需要压缩的字节输出流(ByteArrayOutputStream)的字节数组
     * @param fileName 需要压缩的文件名
     * @return  压缩后字节数组输出流转为的字符串
     * @throws IOException
     */
    public static String zipByteArrayOutputStream(String fileName, byte[] bytes) throws IOException {

        //1.将需要压缩的字节输出流，转为字节数组输入流，
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);

        //2.创建字节数组输出流，用于返回压缩后的输出流字节数组
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        //3.创建压缩输出流
        ZipOutputStream zipOut = new ZipOutputStream(baos);

        zipOut.setEncoding("UTF-8");//设置编码格式，否则中文文件名乱码

        zipOut.setMethod(ZipOutputStream.DEFLATED);//进行压缩存储

        zipOut.setLevel(Deflater.BEST_SPEED);//压缩级别值为0-9共10个级别(值越大，表示压缩越利害)

        //4.设置ZipEntry对象，并对需要压缩的文件命名
        zipOut.putNextEntry(new ZipEntry(fileName)) ;

        //5.读取要压缩的字节输出流，进行压缩
        byte[] buffer = new byte[4096];
        int n = 0;
        while (-1 != (n = bais.read(buffer))) {
            zipOut.write(buffer, 0, n);
        }

        // 关闭流
        bais.close();
        zipOut.close();

        //字节数组输出流转为base64字符串
        BASE64Encoder encoder = new BASE64Encoder();
        String result = encoder.encode(baos.toByteArray());

        baos.close();// 关闭流

        return result;
    }


    /**
     * 批量文件流压缩
     *
     * @param bytesList  List<Map<被压缩压缩文件名, 被压缩的文件流>>
     * @return 压缩后字节数组输出流转为的字符串
     * @throws IOException
     */
    public static String batchZipByteArrayOutputStream(List<Map<String, byte[]>> bytesList) throws IOException {

        //1.创建字节数组输出流，用于返回压缩后的输出流字节数组
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        //2.创建压缩输出流
        ZipOutputStream zipOut = new ZipOutputStream(baos);

        zipOut.setEncoding("UTF-8");//设置编码格式，否则中文文件名乱码

        //3.遍历要批量压缩的集合文件流
        ByteArrayInputStream bais =null;
        Map<String, byte[]> tempMap =null;
        String fileName=null;
        int temp = 0 ;
        for (int i = 0; i < bytesList.size(); i++) {

            tempMap=bytesList.get(i);

            fileName=tempMap.keySet().iterator().next();

            //3.1将需要压缩的字节输出流，转为字节数组输入流，
            bais = new ByteArrayInputStream(tempMap.get(fileName));

            zipOut.setMethod(ZipOutputStream.DEFLATED);//进行压缩存储

            zipOut.setLevel(Deflater.BEST_SPEED);//压缩级别值为0-9共10个级别(值越大，表示压缩越利害)

            //3.2设置ZipEntry对象，并对需要压缩的文件命名
            zipOut.putNextEntry(new ZipEntry(fileName));

            //3.3读取要压缩的字节输出流，进行压缩
            byte[] buffer = new byte[4096];
            int n = 0;
            while (-1 != (n = bais.read(buffer))) {
                zipOut.write(buffer, 0, n);
            }

            // 3.4关闭流
            bais.close();
        }
        zipOut.close();

        //字节数组输出流转为base64字符串
        BASE64Encoder encoder = new BASE64Encoder();
        String result = encoder.encode(baos.toByteArray());

        baos.close();// 关闭流

        return result;
    }

}
