package net.twomini.hybridcontent.auth.dummy;

import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.server.impl.inject.AbstractHttpContextInjectable;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.spi.inject.InjectableProvider;
import net.twomini.hybridcontent.auth.AuthRequirements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DummyProvider<T> implements InjectableProvider<AuthRequirements, Parameter> {

    private static final Logger L = LoggerFactory.getLogger(DummyProvider.class);

    private static class DummyHttpContextInjectable<T> extends AbstractHttpContextInjectable<T> {

        private DummyHttpContextInjectable() {
        }

        @Override
        public T getValue(HttpContext c) {
            return null;
        }

    }

    public DummyProvider() {
    }

    @Override
    public ComponentScope getScope() {
        return ComponentScope.PerRequest;
    }

    @Override
    public Injectable<?> getInjectable(ComponentContext ic,
                                       AuthRequirements authRequirements,
                                       Parameter c) {
        return new DummyHttpContextInjectable<T>();
    }

}
