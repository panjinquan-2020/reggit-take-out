package com.mytest.reggie.controller;

import com.mytest.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

/**
 * @author PJQ
 * 文件上传下载
 */
@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {
    @Value("${reggie.path}")
    private String basePath;

    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    /*
    localhost:8080/upload post请求
    file为二进制格式 封装成MultipartFile
    前端返回只需要确认上传是否成功，返回R<String>
    */
    public R<String> upload(MultipartFile file){
        //file是一个临时文件，需要转存到指定位置，否则本次请求完成后临时文件会删除
        log.info(file.toString());
        //原始文件名
        String originalFilename = file.getOriginalFilename();
        assert originalFilename != null;
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        //由于可能出现文件重名导致后上传文件覆盖前上传文件
        //使用UUID重新生成文件名
        String fileName= UUID.randomUUID().toString();
        //创建一个目录对象
        File dir=new File(basePath);
        //判断当前目录是否存在
        if (!dir.exists()){
            dir.mkdirs();
        }
        try {
            file.transferTo(new File(basePath+fileName+suffix));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return R.success(fileName+suffix);
    }

    /**
     * 文件下载
     * @param name
     * @param response
     */
    @GetMapping("/download")
    /*
    localhost:8080/download?name=*** get请求
    name,由于需要获取输出流将文件下载 传入HttpServletResponse 调用ServletOutputStream
    前端返回只需要确认下载是否成功，返回R<String>
    */
    public void download(String name, HttpServletResponse response){
        try {
            //输入流，通过输入流读取文件夹
            FileInputStream fileInputStream = new FileInputStream(new File(basePath + name));
            //输出流，通过输出流文件写回浏览器，在浏览器展示图片
            ServletOutputStream outputStream=response.getOutputStream();
            response.setContentType("image/jpeg");
            int len=0;
            byte[] bytes=new byte[1024];
            while ((len = fileInputStream.read(bytes))!= -1) {
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }
            outputStream.close();
            fileInputStream.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
