package io.webfolder.cormorant.internal.undertow;

import static io.undertow.util.HttpString.tryFromString;

import java.security.Principal;
import java.util.Map;

import io.undertow.attribute.ExchangeAttribute;
import io.undertow.attribute.ReadOnlyAttributeException;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;
import static java.util.Collections.emptyMap;

public class CormorantRemoteUser implements ExchangeAttribute {

    protected static final ExchangeAttribute INSTANCE = new CormorantRemoteUser();

    private final String AUTH_TOKEN = "X-Auth-Token";

    private final HttpString H_AUTH_TOKEN = tryFromString(AUTH_TOKEN);

    public static Map<String, Principal> principals = emptyMap();

    @Override
    public String readAttribute(HttpServerExchange exchange) {
        String token = exchange.getRequestHeaders().getFirst(H_AUTH_TOKEN);
        if ( token != null ) {
            Principal principal = principals.get(token);
            if ( principal != null ) {
                return principal.getName();
            }
        }
        return null;
    }

    @Override
    public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
        throw new ReadOnlyAttributeException("Cormorant Remote User", newValue);
    }
}
