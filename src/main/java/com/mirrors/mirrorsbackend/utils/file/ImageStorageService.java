package com.mirrors.mirrorsbackend.utils.file;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

@Service
public class ImageStorageService implements ImageStorage {

    private final StringBuilder path = new StringBuilder("src/main/resources/static/img/post/");

    @Override
    public void save(String dirName, MultipartFile image, short fileName) {
        String extension;

        if (!Objects.equals(image.getContentType(), "image/png"))
            extension = ".png";
        else if (!Objects.equals(image.getContentType(), "image/jpeg"))
            extension = ".jpg";
        else
            throw new IllegalStateException("Invalid file type!");

        int mbInBytes = 1_048_576;
        if (image.getSize() > mbInBytes * 4)
            throw new IllegalStateException("Image size cannot be larger than 4 MB!");

        try {
            path.append(dirName).append("/");
            if (!new File(String.valueOf(path)).mkdirs())
                throw new IOException();
            path.append(fileName).append(extension);
            if (!new File(String.valueOf(path)).createNewFile())
                throw new IOException();
            Files.copy(
                    image.getInputStream(),
                    Paths.get(String.valueOf(path)),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException("Couldn't save image(s)");
        }
    }

    @Override
    public void load(String dirName) {

    }

    @Override
    public void delete(String dirName, short fileName) {

    }

    @Override
    public void deleteAll(String dirName) {

    }
}
