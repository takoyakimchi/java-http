package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.coyote.exception.CoyoteException;

public class HttpCookie {

    private static final String JSESSIONID = "JSESSIONID";
    private static final String COOKIE_DELIMITER = ";";
    private static final String NAME_VALUE_DELIMITER = "=";
    private static final int SPLIT_LIMIT = -1;

    private final Map<String, String> cookies;

    public HttpCookie() {
        this.cookies = new HashMap<>();
    }

    public static HttpCookie ofSessionId(String sessionId) {
        HttpCookie cookie = new HttpCookie();
        cookie.setSession(sessionId);
        return cookie;
    }

    public HttpCookie(String rawCookies) {
        this.cookies = new HashMap<>();
        parse(rawCookies);
    }

    private void parse(String rawCookies) {
        if (rawCookies == null) {
            throw new CoyoteException("cookie는 null일 수 없습니다.");
        }

        Arrays.stream(rawCookies.split(COOKIE_DELIMITER, SPLIT_LIMIT))
                .map(String::trim)
                .forEach(rawCookie -> cookies.put(parseCookieName(rawCookie), parseCookieValue(rawCookie)));
    }

    private String parseCookieName(String rawCookie) {
        validateCookieFormat(rawCookie);
        return rawCookie.substring(0, rawCookie.indexOf(NAME_VALUE_DELIMITER));
    }

    private String parseCookieValue(String rawCookie) {
        validateCookieFormat(rawCookie);
        return rawCookie.substring(rawCookie.indexOf(NAME_VALUE_DELIMITER) + NAME_VALUE_DELIMITER.length());
    }

    private void validateCookieFormat(String rawCookie) {
        if (rawCookie == null || rawCookie.isBlank()) {
            throw new CoyoteException("형식이 올바르지 않은 쿠키가 포함되어 있습니다.");
        }
        if (!rawCookie.contains(NAME_VALUE_DELIMITER)) {
            throw new CoyoteException("형식이 올바르지 않은 쿠키가 포함되어 있습니다.");
        }
        if (rawCookie.startsWith(NAME_VALUE_DELIMITER)) {
            throw new CoyoteException("형식이 올바르지 않은 쿠키가 포함되어 있습니다.");
        }
    }

    public String buildMessage() {
        return cookies.keySet().stream()
                .map(key -> key + NAME_VALUE_DELIMITER + cookies.get(key))
                .collect(Collectors.joining(COOKIE_DELIMITER + " "));
    }

    public boolean hasSession() {
        return cookies.containsKey(JSESSIONID) && !cookies.get(JSESSIONID).isBlank();
    }

    public String getSession() {
        return cookies.get(JSESSIONID);
    }

    public void setSession(String value) {
        cookies.put(JSESSIONID, value);
    }
}
