package com.mirrors.mirrorsbackend.utils.file;

import org.springframework.web.multipart.MultipartFile;

public interface ImageStorage {

    void save(String dirName, MultipartFile image, short fileName);

    void load(String dirName);

    void delete(String dirName, short fileName);

    void deleteAll(String dirName);
}
