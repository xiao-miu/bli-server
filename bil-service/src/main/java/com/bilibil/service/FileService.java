package com.bilibil.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService{
    String uploadFileBySlices(MultipartFile slice,
                              String fileMD5,
                              Integer sliceNo,
                              Integer totalSliceNo) throws Exception;

    String getFileMD5(MultipartFile file) throws Exception;
}
