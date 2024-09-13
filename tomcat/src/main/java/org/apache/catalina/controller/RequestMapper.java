package org.apache.catalina.controller;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.coyote.request.HttpRequest;

public class RequestMapper {

    private static final RequestMapper INSTANCE = new RequestMapper();

    private final Map<String, Controller> controllers;

    public static RequestMapper getInstance() {
        return INSTANCE;
    }

    private RequestMapper() {
        controllers = new ConcurrentHashMap<>();
    }

    public void addMapping(String endpoint, Controller controller) {
        controllers.put(endpoint, controller);
    }

    public Controller getController(HttpRequest request) {
        String path = request.getPath();

        if (controllers.containsKey(path)) {
            return controllers.get(path);
        }

        return new DefaultController();
    }
}
