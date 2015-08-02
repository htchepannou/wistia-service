package com.tchepannou.wistia.service.impl;

import com.tchepannou.wistia.service.Db;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.List;

public class DbImpl implements Db{
    //-- Attributes
    private static final Logger LOG = LoggerFactory.getLogger(DbImpl.class);

    private final File directory;

    //-- Constructor
    public DbImpl(File directory){
        this.directory = directory;
        directory.mkdirs();
    }


    //-- Db overrides
    @Override
    public void put(String id, String value) throws IOException{
        File file = getFile(id);
        file.getParentFile().mkdirs();

        LOG.info("Storing {}={} into {}", id, value, file);
        try(FileOutputStream out = new FileOutputStream(file)){
            out.write(value.getBytes());
        }
    }

    @Override
    public boolean remove(String id) {
        File file = getFile(id);

        LOG.info("Storing {} from {}", id, file);
        if (file.exists()){
            return file.delete();
        }
        return false;
    }

    @Override
    public String get(String id) throws IOException{
        File file = getFile(id);
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            return lines.size() > 0 ? lines.get(0) : null;
        } catch (NoSuchFileException e){
            return null;
        }
    }


    protected File getFile (String id){
        int len = id.length();
        if (len == 1) {
            return Paths.get(
                    directory.getAbsolutePath(),
                    String.valueOf(id.charAt(0)),
                    id
            ).toFile();
        } else if (len == 2) {
            return Paths.get(
                    directory.getAbsolutePath(),
                    String.valueOf(id.charAt(0)),
                    String.valueOf(id.charAt(1)),
                    id
            ).toFile();
        } else {
            return Paths.get(
                    directory.getAbsolutePath(),
                    String.valueOf(id.charAt(0)),
                    String.valueOf(id.charAt(1)),
                    String.valueOf(id.charAt(2)),
                    id
            ).toFile();
        }
    }
}
