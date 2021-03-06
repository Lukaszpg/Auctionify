package pro.lukasgorny.service.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import pro.lukasgorny.model.Auction;
import pro.lukasgorny.service.storage.exception.StorageException;
import pro.lukasgorny.service.storage.exception.StorageFileNotFoundException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

/**
 * Created by Łukasz on 31.01.2018.
 */
@Service
public class StorageServiceImpl implements StorageService {

    private final Path rootLocation;
    private final Environment environment;

    @Autowired
    public StorageServiceImpl(StorageProperties storageProperties, Environment environment) {
        this.rootLocation = Paths.get(storageProperties.getLocation());
        this.environment = environment;
    }

    @Override
    public String store(MultipartFile file, Auction auction) {
        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file " + filename);
            }
            if (filename.contains("..")) {
                throw new StorageException("Cannot store file with relative path outside current directory " + filename);
            }

            Path storeLocation;

            if (!isProduction()) {
                storeLocation = Paths.get(rootLocation + "\\" + auction.getId());

                if (!Files.exists(storeLocation)) {
                    Files.createDirectories(storeLocation);
                }

                storeLocation = Paths.get(rootLocation + "\\" + auction.getId() + "\\" + filename);

                Files.copy(file.getInputStream(), storeLocation, StandardCopyOption.REPLACE_EXISTING);

            } else {
                storeLocation = Paths.get(rootLocation + "/" + auction.getId());

                if (!Files.exists(storeLocation)) {
                    Files.createDirectories(storeLocation);
                }

                storeLocation = Paths.get(rootLocation + "/" + auction.getId() + "/" + filename);

                Files.copy(file.getInputStream(), storeLocation, StandardCopyOption.REPLACE_EXISTING);
            }

            return storeLocation.toString();
        } catch (IOException e) {
            throw new StorageException("Failed to store file " + filename, e);
        }
    }

    private boolean isProduction() {
        for (String s : environment.getActiveProfiles()) {
            if (s.equals("production")) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.rootLocation, 1).filter(path -> !path.equals(this.rootLocation)).map(this.rootLocation::relativize);
        } catch (IOException e) {
            throw new StorageException("Failed to read stored files", e);
        }

    }

    @Override
    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    @Override
    public Resource loadAsResource(String filename) {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new StorageFileNotFoundException("Could not read file: " + filename);

            }
        } catch (MalformedURLException e) {
            throw new StorageFileNotFoundException("Could not read file: " + filename, e);
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }
}
