package com.rastreio.geradoretiquetas.store;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class PrePostagemStore {

    private static final String FILE_PATH = "data/prepostagens.json";
    private final ObjectMapper mapper;
    private Map<Integer, String> cache = new HashMap<>();

    public PrePostagemStore(ObjectMapper mapper) {
        this.mapper = mapper;
        carregar();
    }

    private void carregar() {
        try {
            File dir = new File("data");
            if (!dir.exists()) {
                dir.mkdirs();
            }

            File file = new File(FILE_PATH);
            if (file.exists()) {
                cache = mapper.readValue(file, new TypeReference<Map<Integer, String>>() {});
            }
        } catch (IOException e) {
            e.printStackTrace();
            cache = new HashMap<>();
        }
    }

    private void salvar() {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(FILE_PATH), cache);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized String getId(int numeroPedido) {
        return cache.get(numeroPedido);
    }

    public synchronized void putId(int numeroPedido, String prePostagemId) {
        cache.put(numeroPedido, prePostagemId);
        salvar();
    }
}
