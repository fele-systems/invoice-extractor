package com.systems.fele;

import java.io.InputStream;

public interface Extractor {
    Invoice extract(InputStream stream, String password);
}
