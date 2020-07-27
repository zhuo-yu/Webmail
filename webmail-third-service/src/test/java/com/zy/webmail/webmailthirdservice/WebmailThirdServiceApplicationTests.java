package com.zy.webmail.webmailthirdservice;

import com.aliyun.oss.OSSClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

@SpringBootTest
class WebmailThirdServiceApplicationTests {

    @Autowired
    OSSClient ossClient;
    @Test
    public void testUpload() throws FileNotFoundException {
//        // Endpoint以杭州为例，其它Region请按实际情况填写。
//        String endpoint = "http://oss-cn-shenzhen.aliyuncs.com";
//        // 云账号AccessKey有所有API访问权限，建议遵循阿里云安全最佳实践，创建并使用RAM子账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建。
//        String accessKeyId = "LTAI4GEKp6NkYmVTwDVYZbHL";
//        String accessKeySecret = "4Re1sr9fOdbPmvbqxYxvDNG9iOWytB";
//
//        // 创建OSSClient实例。
//        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        // 上传文件流。
        InputStream inputStream = new FileInputStream("C:\\Users\\卓\\Pictures\\Saved Pictures\\picture.jpg");
        ossClient.putObject("webmail-zy", "picture.jpg", inputStream);

        // 关闭OSSClient。
        ossClient.shutdown();
        System.out.println("打印成功");
    }
    @Test
    void contextLoads() {
    }

}
