package com.plovdev.pornviewer.utility.deeplink;

import java.net.URI;
import java.util.*;

public class Deeplink {
    private String protocol;
    private String module;
    private List<String> paths = new ArrayList<>();
    private String action;
    private Map<String, String> params = new HashMap<>();

    public Deeplink(URI uri) {
        this.protocol = uri.getScheme();
        this.module = uri.getHost();
        String[] path = uri.getPath().substring(1).split("/");
        paths.addAll(Arrays.asList(path).subList(0, path.length - 1));
        action = path[path.length - 1];

        String q = uri.getQuery();
        if (q != null) {
            String[] queries = uri.getQuery().split("&");
            for (String query : queries) {
                String name = query.substring(0, query.indexOf("="));
                String value = query.substring(query.indexOf("=") + 1);
                params.put(name, value);
            }
        }
    }

    public Deeplink() {
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public List<String> getPaths() {
        return paths;
    }

    public void setPaths(List<String> paths) {
        this.paths = paths;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    @Override
    public String toString() {
        return String.format("%s %s %s %s %s", protocol, module, paths, action, params);
    }
}