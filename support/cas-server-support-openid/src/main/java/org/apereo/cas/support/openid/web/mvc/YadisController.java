package org.apereo.cas.support.openid.web.mvc;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

/**
 * This is {@link YadisController}.
 *
 * @author Misagh Moayyed
 * @since 5.1.0
 */
@Controller("yadisController")
@Slf4j
public class YadisController {



    @Autowired
    private CasConfigurationProperties casProperties;

    @Autowired
    private ResourceLoader resourceLoader;

    /**
     * Generates the Yadis XML snippet.
     *
     * @param response the response
     * @throws Exception the exception
     */
    @GetMapping(path = "/yadis.xml")
    public void yadis(final HttpServletResponse response) throws Exception {
        final var template = this.resourceLoader.getResource("classpath:/yadis.template");
        try (var writer = new StringWriter()) {
            IOUtils.copy(template.getInputStream(), writer, StandardCharsets.UTF_8);
            final var yadis = writer.toString().replace("$casLoginUrl", casProperties.getServer().getLoginUrl());
            response.setContentType("application/xrds+xml");
            final Writer respWriter = response.getWriter();
            respWriter.write(yadis);
            respWriter.flush();
        }
    }
}
