package com.mieyde.tx.common.util;

import cn.hutool.core.util.StrUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 文件工具
 *
 * @author 我吃稀饭面
 * @date 2023/6/25 14:52
 */
public class FileUtils {

    private static final Logger log = LoggerFactory.getLogger(FileUtils.class);

    /**
     * 文件复制
     */
    public static void copyFile(String oldPath,String newPath){
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            int byteRead = 0;
            File oldFile = load(oldPath);

            if (oldFile.isDirectory() || !oldFile.exists()){
                throw new RuntimeException("源文件不存在");
            }
            inputStream = new FileInputStream(oldFile);
            outputStream = new FileOutputStream(newPath);
            byte[] buffer = new byte[2048];
            while ((byteRead = inputStream.read(buffer)) != -1){
                outputStream.write(buffer,0,byteRead);
            }
        }catch (Exception e){
            throw new RuntimeException("复制文件操作出错",e);
        }finally {
            IOUtils.close(outputStream,inputStream);
        }
    }

    /**
     * 删除目录下的所有文件和目录
     */
    public static void delAllFile(String path){
        File file = load(path);
        if (!file.exists()){
            return;
        }
        if (file.isFile()){
            return;
        }
        String[] tempList = file.list();
        File temp = null;
        for (String tempFile : tempList) {
            if (path.endsWith(File.separator)){
                temp = load(path + tempFile);
            }else {
                temp = load(path + File.separator + tempFile);
            }
            if (temp.isFile()){
                boolean result = temp.delete();
                int tryCount = 0;
                while (!result && tryCount++ < 3){
                    //回收资源
                    System.gc();
                    result = temp.delete();
                }
            }
            if (temp.isDirectory()){
                //先删除文件夹里面的文件
                delAllFile(path + "/" + tempFile);
                //再删除空文件夹
                delFolder(path + "/" + tempFile);
            }
        }
    }

    /**
     * 删除目录
     */
    public static void delFolder(String folderPath){
        try {
            //删除里面的文件
            delAllFile(folderPath);
            File file = load(folderPath);
            boolean result = file.delete();
            int tryCount = 0;
            while (!result && tryCount++ < 3){
                System.gc();
                result = file.delete();
            }
        }catch (Exception e){
            throw new RuntimeException("删除文件夹操作出错",e);
        }
    }

    /**
     * 删除文件
     */
    public static void delFile(String fileNameAndPath){
        try {
            if (StringUtls.isBlank(fileNameAndPath)){
                return;
            }
            File file = load(fileNameAndPath);
            if (file.isDirectory()){
                return;
            }
            file.delete();
        }catch (Exception e){
            throw new RuntimeException("删除文件操作出错",e);
        }
    }

    /**
     * 新建文件
     * @param fileNameAndPath 绝对路径和文件名
     * @param fileContent 文件内容
     */
    public static void newFile(String fileNameAndPath,String fileContent){
        if (StringUtls.isBlank(fileNameAndPath)){
            return;
        }
        FileWriter resultFile = null;
        try {
            String filePath = fileNameAndPath;
            File file = load(filePath);
            if (file.isDirectory()){
                throw new RuntimeException("该文件路径是一个目录不是文件");
            }
            if (!file.exists()){
                file.createNewFile();
            }
            if (StringUtls.isBlank(fileContent)){
                return;
            }
            String content = fileContent;
            resultFile = new FileWriter(file);
            PrintWriter writer = new PrintWriter(resultFile);
            writer.println(content);
        }catch (Exception e){
            throw new RuntimeException("新建文件操作出错",e);
        }finally {
            IOUtils.close(resultFile);
        }
    }

    /**
     * 创建目录（包括父目录）
     */
    public static void newFolder(String folderPath){
        try {
            if (StringUtls.isBlank(folderPath)){
                return;
            }
            File file = load(folderPath);
            if (!file.exists()){
                file.mkdirs();
            }
        }catch (Exception e){
            throw new IllegalArgumentException("新建目录操作出错",e);
        }
    }

    /**
     * 读取文件内容
     */
    public static String getContent(File file){
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            StringBuilder content = new StringBuilder();
            String line = null;
            while ((line = bufferedReader.readLine()) != null){
                content.append(line).append("\n");
            }
            return content.toString();
        } catch (Exception e) {
            return StringUtils.EMPTY;
        }
    }

    /**
     * 加载文件
     */
    public static File load(String name){
        try {
            if (StrUtil.isBlank(name)){
                throw new IllegalArgumentException("name cant't be null");
            }
            String decodePath = URLDecoder.decode(name, StandardCharsets.UTF_8.name());
            return getFileFromSystem(decodePath);
        }catch (Exception e){
            throw new RuntimeException("file is not find",e);
        }
    }

    private static File getFileFromSystem(String decodePath){
        URL resource = FileUtils.class.getClassLoader().getResource("");
        List<String> paths = new ArrayList<>();
        paths.add(decodePath);
        if (Objects.nonNull(resource)){
            paths.add(resource.getPath() + decodePath);
        }
        for (String path : paths) {
            File targetFile = new File(path);
            if (targetFile.exists()){
                return targetFile;
            }
        }
        throw new RuntimeException("file is not find");
    }
}
