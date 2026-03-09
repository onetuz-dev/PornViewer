package com.plovdev.pornviewer.utility.files;

import io.github.cdimascio.dotenv.Dotenv;

public class EnvReader {
    private static final Dotenv dotenv = Dotenv.load();

    public static String getEnv(String path) {
        return dotenv.get(path);
    }
}