package io.webfolder.cormorant.internal.undertow;

import static io.webfolder.cormorant.internal.undertow.CormorantRemoteUser.INSTANCE;

import io.undertow.attribute.ExchangeAttribute;
import io.undertow.attribute.ExchangeAttributeBuilder;

public class CormorantRemoteUserBuilder implements ExchangeAttributeBuilder {

    public static final String REMOTE_USER_SHORT = "%u";

    public static final String REMOTE_USER = "%{REMOTE_USER}";

    @Override
    public String name() {
        return "Cormorant Remote User";
    }

    @Override
    public ExchangeAttribute build(String token) {
        if (token.equals(REMOTE_USER) || token.equals(REMOTE_USER_SHORT)) {
            return INSTANCE;
        }
        return null;
    }

    @Override
    public int priority() {
        return 100;
    }
}
