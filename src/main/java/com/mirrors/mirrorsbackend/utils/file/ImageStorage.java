package com.mirrors.mirrorsbackend.utils.file;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

public interface ImageStorage {

    void save(String dirName, MultipartFile image, short fileName);

    List<File> load(String dirName);

    void delete(String dirName);
}
