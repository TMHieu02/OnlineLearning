package src.config.utils;

import lombok.Data;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Test {
    public static void main(String[] args) throws IOException {
        Path uploadPath = Paths.get("../uploads");

        System.out.println(uploadPath.toAbsolutePath());

    }


    @Data
    class a {
        public int c;
        public int d;

        public a(int i, int i1) {
            c = i;
            d = i1;
        }
    }
}
