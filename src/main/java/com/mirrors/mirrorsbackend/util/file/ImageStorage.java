package com.mirrors.mirrorsbackend.util.file;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImageStorage {

    void save(String dirName, MultipartFile image, short fileName);

    List<String> loadPaths(String dirName);

    void delete(String dirName);
}
