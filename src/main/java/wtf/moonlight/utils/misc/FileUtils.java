package wtf.moonlight.utils.misc;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileUtils {

    public static String readInputStream(InputStream inputStream) {
        StringBuilder stringBuilder = new StringBuilder();

        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = bufferedReader.readLine()) != null)
                stringBuilder.append(line).append('\n');

        } catch (Exception ignored) {
        }
        return stringBuilder.toString();
    }

    public static InputStream getResourceStream(final String path) {
        return FileUtils.class.getResourceAsStream(path);
    }
}
