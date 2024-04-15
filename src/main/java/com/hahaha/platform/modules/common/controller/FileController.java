package com.hahaha.platform.modules.common.controller;

import com.hahaha.platform.common.exception.BaseException;
import com.hahaha.platform.common.web.domain.AjaxResult;
import com.hahaha.platform.modules.common.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * 文件处理
 */
@RestController
@RequestMapping("/file")
@Slf4j
public class FileController {

    @Resource
    private FileService fileService;

    /**
     * 通用上传请求
     */
     
    @PostMapping("/upload")
    public AjaxResult upload(MultipartFile file) {
        if (file == null) {
            throw new BaseException("上传文件不能为空");
        }
        return AjaxResult.success(fileService.uploadFile(file));
    }

    /**
     * 生成视频封面图
     */
     
    @PostMapping("/uploadVideo")
    public AjaxResult createVideoCover(MultipartFile file) {
        if (file == null) {
            throw new BaseException("上传文件不能为空");
        }
        // 调用视频处理工具类
        return AjaxResult.success(fileService.uploadVideo(file));
    }

    /**
     * 生成音频文字
     */
     
    @PostMapping("/uploadAudio")
    public AjaxResult uploadAudio(MultipartFile file) {
        if (file == null) {
            throw new BaseException("上传文件不能为空");
        }
        // 调用视频处理工具类
        return AjaxResult.success(fileService.uploadAudio(file));
    }

}
