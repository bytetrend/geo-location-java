package io.bytetrend.geo.location.source;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.net.MalformedURLException;

/**
 * Contains methods related to read/write to files.
 */
public final class FileUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileUtils.class);

    public static Resource getResource(String path) throws MalformedURLException {
        if (StringUtils.stripToEmpty(path).isEmpty())
            throw new IllegalArgumentException("Can not load resource with empty path");
        if (path.startsWith("http:") || path.startsWith("https:") || path.startsWith("file:")) {
            try {
                return new UrlResource(path);
            } catch (MalformedURLException e) {
                LOGGER.error(ExceptionUtils.getStackTrace(e));
                throw e;
            }
        } else if (path.startsWith("/")) {
            return new FileSystemResource(path);
        } else {
            return new ClassPathResource(path);
        }
    }

}
