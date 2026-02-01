package com.bsys.gateway.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.util.*;

public class HeaderMapRequestWrapper extends HttpServletRequestWrapper {

    private final Map<String, String> headerMap = new HashMap<>();
    private final Set<String> blockedHeaders = new HashSet<>();

    public HeaderMapRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    // Adiciona ou sobrescreve um cabecalho confiavel e desfaz bloqueio anterior
    public void addHeader(String name, String value) {
        headerMap.put(name, value);
        blockedHeaders.remove(name);
    }

    // Marca cabecalho como bloqueado e remove sobrescritas
    public void removeHeader(String name) {
        blockedHeaders.add(name);
        headerMap.remove(name);
    }

    @Override
    public String getHeader(String name) {
        if (blockedHeaders.contains(name)) {
            return null;
        }
        if (headerMap.containsKey(name)) {
            return headerMap.get(name);
        }
        return super.getHeader(name);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        if (blockedHeaders.contains(name)) {
            return Collections.emptyEnumeration();
        }
        if (headerMap.containsKey(name)) {
            return Collections.enumeration(Collections.singletonList(headerMap.get(name)));
        }
        return super.getHeaders(name);
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        List<String> names = Collections.list(super.getHeaderNames());

        names.removeIf(blockedHeaders::contains);

        for (String key : headerMap.keySet()) {
            if (!names.contains(key)) {
                names.add(key);
            }
        }

        return Collections.enumeration(names);
    }
}
