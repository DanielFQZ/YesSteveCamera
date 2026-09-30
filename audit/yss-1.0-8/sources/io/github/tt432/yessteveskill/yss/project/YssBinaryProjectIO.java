/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package io.github.tt432.yessteveskill.yss.project;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.LinkedHashMap;
import java.util.Map;
import org.jetbrains.annotations.Nullable;

public final class YssBinaryProjectIO {
    private static final int MAGIC = 1498633009;
    private static final int VERSION = 1;

    private YssBinaryProjectIO() {
    }

    public static void write(Path path, String modelId, Map<String, byte[]> files) throws IOException {
        Path parent = path.getParent();
        if (parent != null) {
            Files.createDirectories(parent, new FileAttribute[0]);
        }
        try (DataOutputStream output = new DataOutputStream(new BufferedOutputStream(Files.newOutputStream(path, new OpenOption[0])));){
            output.writeInt(1498633009);
            output.writeInt(1);
            output.writeUTF(modelId);
            output.writeInt(files.size());
            for (Map.Entry<String, byte[]> entry : files.entrySet()) {
                output.writeUTF(entry.getKey());
                output.writeInt(entry.getValue().length);
                output.write(entry.getValue());
            }
        }
    }

    public static LoadedBinaryProject read(Path path) throws IOException {
        try (DataInputStream input = new DataInputStream(new BufferedInputStream(Files.newInputStream(path, new OpenOption[0])));){
            int magic = input.readInt();
            if (magic != 1498633009) {
                throw new IOException("Invalid .yss magic: " + path.getFileName());
            }
            int version = input.readInt();
            if (version != 1) {
                throw new IOException("Unsupported .yss version " + version + " for " + path.getFileName());
            }
            String modelId = input.readUTF();
            int fileCount = input.readInt();
            if (fileCount < 0) {
                throw new IOException("Invalid .yss file count in " + path.getFileName());
            }
            LinkedHashMap<String, byte[]> files = new LinkedHashMap<String, byte[]>();
            for (int i = 0; i < fileCount; ++i) {
                String name = input.readUTF();
                int length = input.readInt();
                if (length < 0) {
                    throw new IOException("Invalid .yss entry length for " + name);
                }
                byte[] bytes = input.readNBytes(length);
                if (bytes.length != length) {
                    throw new IOException("Unexpected EOF while reading .yss entry " + name);
                }
                files.put(name, bytes);
            }
            LoadedBinaryProject loadedBinaryProject = new LoadedBinaryProject(path, modelId, files);
            return loadedBinaryProject;
        }
    }

    public record LoadedBinaryProject(Path path, String modelId, Map<String, byte[]> files) {
        @Nullable
        public byte[] file(String name) {
            return this.files.get(name);
        }
    }
}

