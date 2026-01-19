package com.razdeep.konsignapi.service;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.razdeep.konsignapi.model.KonsignUserDetails;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

@Service
public class CommonService {

    private static final int MAX_INITIAL_SIZE = 4;

    private final FreeMarkerConfigurer freemarkerConfigurer;

    public CommonService(FreeMarkerConfigurer freemarkerConfigurer) {
        this.freemarkerConfigurer = freemarkerConfigurer;
    }

    private boolean isVowel(char c) {
        return c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u' || c == 'A' || c == 'E' || c == 'I' || c == 'O'
                || c == 'U';
    }

    private boolean isAlphabet(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    public String generateInitials(String name) {
        if (name == null || name.equals("")) {
            return null;
        }

        int n = name.length();

        StringBuilder sb = new StringBuilder();
        sb.append(Character.toUpperCase(name.charAt(0)));

        for (int i = 1; i < n; ++i) {
            if (isAlphabet(name.charAt((i))) && !isVowel(name.charAt(i))) {
                sb.append(Character.toUpperCase(name.charAt(i)));
            }
            if (sb.length() >= MAX_INITIAL_SIZE) {
                break;
            }
        }

        return sb.toString();
    }

    public String getTenantId() {
        KonsignUserDetails konsignUserDetails = (KonsignUserDetails)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (konsignUserDetails == null) {
            return null;
        }
        return konsignUserDetails.getTenantId();
    }

    private byte[] convertHtmlToPdf(String html) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        PdfRendererBuilder builder = new PdfRendererBuilder();
        builder.withHtmlContent(html, null);
        builder.toStream(out);

        // IMPORTANT: Embed font
        //        builder.useFont(
        //                new File("fonts/DejaVuSans.ttf"),
        //                "DejaVu Sans"
        //        );

        builder.run();
        return out.toByteArray();
    }

    private String generateHtml(String templateName, Map<String, Object> payload)
            throws IOException, TemplateException {
        Template template = freemarkerConfigurer.getConfiguration().getTemplate(templateName);
        StringWriter writer = new StringWriter();
        template.process(payload, writer);

        return writer.toString();
    }

    public byte[] generatePdf(String templateName, Map<String, Object> payload) throws Exception {
        String html = generateHtml(templateName, payload);
        return convertHtmlToPdf(html);
    }
}
