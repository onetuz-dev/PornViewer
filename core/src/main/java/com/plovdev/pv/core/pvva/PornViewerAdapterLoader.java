package com.plovdev.pv.core.pvva;

import com.plovdev.pv.adapter.PornViewerAdapter;
import org.plovdev.pvva.PVVA;
import org.plovdev.pvva.model.Content;
import org.plovdev.pvva.read.PvvaReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.List;
import java.util.Objects;

public class PornViewerAdapterLoader {
    private static final Logger log = LoggerFactory.getLogger(PornViewerAdapterLoader.class);

    private PornViewerAdapterLoader() {
        throw new UnsupportedOperationException("Do not use!");
    }
    public static PornViewerAdapter loadAdapter(InputStream stream) {
        Objects.requireNonNull(stream);

        try {
            PVVA pvva = new PvvaReader(stream).readAdapter();
            List<Content> contents = pvva.data.getPvvas();
            if (contents != null && !contents.isEmpty()) {
                return load(contents.getFirst().getPlugin());
            }
        } catch (Exception e) {
            log.error("Failed to load adapter", e);
        }
        throw new RuntimeException("No adapter found in PVVA file");
    }

    private static PornViewerAdapter load(byte[] jar) throws Exception {
        PvvaClassLoader loader = new PvvaClassLoader(jar);
        Class<?> impl = loader.findFirstImplementation(PornViewerAdapter.class);
        Object instance = impl.getConstructor().newInstance();
        if (instance instanceof PornViewerAdapter adapter) {
            loader.cleanup();
            return adapter;
        } else {
            throw new IllegalArgumentException("Illegal implementation");
        }
    }
}