package com.jixianda.controller.admin;

import com.jixianda.constant.MessageConstant;
import com.jixianda.result.Result;
import com.jixianda.utils.AliOssUtil;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * 公共接口
 * @author huanzi
 * @date 2025/12/14 20:55
 */
@RestController
@RequestMapping("/admin/common")
@Api(tags = "通用接口")
@Slf4j
public class CommonController {
    private final AliOssUtil aliOssUtil;

    public CommonController(AliOssUtil aliOssUtil) {
        this.aliOssUtil = aliOssUtil;
    }

    /**
     * 文件上传
     * @return
     */
    @RequestMapping("/upload")
    public Result<String> upload(MultipartFile file){
        log.info("文件上传：{}",file);
        try {
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String objectname = UUID.randomUUID().toString() + extension;
            String filePath = aliOssUtil.upload(file.getBytes(), objectname);
            return Result.success(filePath);

        }catch (IOException e){
            e.printStackTrace();
            log.error(MessageConstant.UPLOAD_FAILED);
        }
        return Result.error(MessageConstant.UPLOAD_FAILED);

    }
}
