package one.tranic.t.base.parse.resource;

import com.google.common.io.Resources;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class ResourceParser {
    /**
     * Retrieves an InputStream for a specified resource file by its filename.
     *
     * @param filename the name of the resource file to be loaded
     * @return an InputStream for the resource file, or null if the resource cannot be found or accessed
     */
    public static InputStream resource(@NotNull String filename) {
        try {
            URL url = Resources.getResource(filename);
            URLConnection connection = url.openConnection();
            connection.setUseCaches(false);
            return connection.getInputStream();
        } catch (IOException ex) {
            return null;
        }
    }
}
