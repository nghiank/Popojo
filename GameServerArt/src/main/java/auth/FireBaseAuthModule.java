package auth;

import com.google.inject.AbstractModule;

public class FireBaseAuthModule extends AbstractModule{
    @Override
    protected void configure() {
        bind(LoginAuth.class).to(FireBaseLoginAuth.class);
    }
}
