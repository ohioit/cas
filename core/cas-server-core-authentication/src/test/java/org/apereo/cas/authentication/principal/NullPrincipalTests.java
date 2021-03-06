package org.apereo.cas.authentication.principal;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

/**
 * @author Misagh Moayyed
 * @since 5.0.0
 */
@Slf4j
public class NullPrincipalTests {

    private static final File JSON_FILE = new File(FileUtils.getTempDirectoryPath(), "nullPrincipal.json");

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    public void verifySerializeANullPrincipalToJson() throws IOException {
        final var serviceWritten = NullPrincipal.getInstance();
        MAPPER.writeValue(JSON_FILE, serviceWritten);
        final var serviceRead = MAPPER.readValue(JSON_FILE, NullPrincipal.class);
        assertEquals(serviceWritten, serviceRead);
    }
}
