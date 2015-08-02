package com.tchepannou.wistia.service.impl;

import com.google.common.io.Files;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

public class DbImplTest {
    private File dir;
    private DbImpl db;

    @Before
    public void setUp (){
        dir = Files.createTempDir();
        db = new DbImpl(dir);
    }

    @Test
    public void testGetFile () throws Exception {
        assertThat(db.getFile("1")).isEqualTo(new File(dir.getAbsolutePath() + "/1/1"));
        assertThat(db.getFile("12")).isEqualTo(new File(dir.getAbsolutePath() + "/1/2/12"));
        assertThat(db.getFile("123")).isEqualTo(new File(dir.getAbsolutePath() + "/1/2/3/123"));
        assertThat(db.getFile("1234")).isEqualTo(new File(dir.getAbsolutePath() + "/1/2/3/1234"));
    }

    @Test
    public void testPut() throws Exception {
        // When
        db.put("1234", "value");

        // Then
        File file = new File(dir.getAbsolutePath() + "/1/2/3/1234");
        assertThat(file).exists();
        assertThat(file).hasContent("value");
    }

    @Test
    public void testRemove() throws Exception {
        // Given
        File file = Paths.get(dir.getAbsolutePath(), "1", "2", "3", "1234").toFile();
        file.getParentFile().mkdirs();
        try (OutputStream out = new FileOutputStream(file)) {
            IOUtils.copy(new ByteArrayInputStream("value".getBytes()), out);
        }

        // When
        db.remove("1234");

        // Then
        assertThat(file).doesNotExist();
    }

    @Test
    public void testGet() throws Exception {
        // Given
        File file = new File(dir.getAbsolutePath() + "/1/2/3/1234");
        file.getParentFile().mkdirs();
        try (OutputStream out = new FileOutputStream(file)) {
            IOUtils.copy(new ByteArrayInputStream("value".getBytes()), out);
        }

        // When
        assertThat(db.get("1234")).isEqualTo("value");
        assertThat(db.get("???")).isNull();

    }
}
