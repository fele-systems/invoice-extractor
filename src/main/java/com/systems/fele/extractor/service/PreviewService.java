package com.systems.fele.extractor.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.systems.fele.common.util.StringUtils;
import com.systems.fele.extractor.banks.LineStream;
import com.systems.fele.extractor.model.LineStreamLinePreview;

@Service
public class PreviewService {
    
    public List<LineStreamLinePreview> generatePreview(LineStream lineStream) {
        return lineStream.lines().stream()
            .map(PreviewService::linePreviewFromRawLine)
            .collect(Collectors.toList());
    } 

    public static LineStreamLinePreview linePreviewFromRawLine(String line) {
        var escapedLine = new StringBuilder();
        var hasEscapedChar = escape(line, escapedLine);
        return new LineStreamLinePreview(line, escapedLine.toString(), hasEscapedChar);
    }

    public static boolean escape(String unescaped, StringBuilder sb) {
        boolean changed = false;
        for (int i = 0; i < unescaped.length(); i++) {
            switch (unescaped.charAt(i)) {
            case '\0':
                sb.append("\\0");
                changed = true;
                break;
            case '\t':
                sb.append("\\t");
                changed = true;
                break;
            case '\n':
                sb.append("\\n");
                changed = true;
                break;
            default:
                if (Character.isISOControl(unescaped.charAt(i))) {
                    var asInt = Character.getNumericValue(unescaped.charAt(i));
                    sb.append( "\\x" + Integer.toHexString(asInt) );
                    changed = true;
                } else {
                    sb.append(unescaped.charAt(i));    
                }
            }
        }
        return changed;
    }
}
