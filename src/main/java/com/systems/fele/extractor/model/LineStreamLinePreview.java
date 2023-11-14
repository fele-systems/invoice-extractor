package com.systems.fele.extractor.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LineStreamLinePreview {
    String rawLine;
    String escapedLine;
    boolean hasEscapedChar;
}
