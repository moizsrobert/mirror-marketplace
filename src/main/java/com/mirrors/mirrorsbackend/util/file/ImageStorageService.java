package com.mirrors.mirrorsbackend.util.file;

import com.mirrors.mirrorsbackend.exception.MarketplaceException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Service
public class ImageStorageService implements ImageStorage {

    private final String path = "src/main/resources/media/";
    private StringBuilder pathBuilder;

    @Override
    public void save(String dirName, MultipartFile image, short fileName) {
        pathBuilder = new StringBuilder(path);
        String extension;

        if (!Objects.equals(image.getContentType(), "image/png"))
            extension = ".png";
        else if (!Objects.equals(image.getContentType(), "image/jpeg"))
            extension = ".jpg";
        else
            throw new MarketplaceException("Invalid file type!");

        int mbInBytes = 1_048_576;
        if (image.getSize() > mbInBytes * 4)
            throw new MarketplaceException("Image size cannot be larger than 4 MB!");

        try {
            pathBuilder.append(dirName).append("/");
            new File(String.valueOf(pathBuilder)).mkdirs();
            pathBuilder.append(fileName).append(extension);
            new File(String.valueOf(pathBuilder)).createNewFile();
            Files.copy(
                    image.getInputStream(),
                    Paths.get(String.valueOf(pathBuilder)),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
            throw new MarketplaceException("Couldn't save image(s)");
        }
    }

    @Override
    public List<String> loadPaths(String dirName) {
        pathBuilder = new StringBuilder(path);
        pathBuilder.append(dirName);
        File dir = new File(String.valueOf(pathBuilder));
        List<String> paths = new ArrayList<>();

        if (dir.exists() && dir.listFiles() != null) {
            for (var file : dir.listFiles())
                paths.add("/media/" + dirName + "/" + file.getName());
        }

        if (paths.isEmpty())
            paths.add("/img/no-image.jpg");

        return paths;
    }

    @Override
    public void delete(String dirName) {
        pathBuilder = new StringBuilder(path);
        pathBuilder.append(dirName);
        File dir = new File(String.valueOf(pathBuilder));
        File[] dirContent = dir.listFiles();
        if (dirContent != null) {
            for (var f : dirContent)
                f.delete();
        }
        dir.delete();
    }
}
