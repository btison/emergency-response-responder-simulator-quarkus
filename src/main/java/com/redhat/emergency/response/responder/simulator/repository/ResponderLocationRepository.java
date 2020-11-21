package com.redhat.emergency.response.responder.simulator.repository;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.redhat.emergency.response.responder.simulator.model.ResponderLocation;
import io.quarkus.runtime.StartupEvent;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import io.vertx.core.json.Json;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class ResponderLocationRepository {

    private static final Logger log = LoggerFactory.getLogger(ResponderLocationRepository.class);

    @ConfigProperty(name = "infinispan.cache.responder-simulator", defaultValue = "responder-simulator")
    String cacheName;

    @ConfigProperty(name = "infinispan.cache.create.lazy", defaultValue = "false")
    boolean lazy;

    @Inject
    RemoteCacheManager cacheManager;

    volatile RemoteCache<String, String> cache;

    void onStart(@Observes StartupEvent e) {
        // do not initialize the cache at startup when remote cache is not available, e.g. in QuarkusTests
        if (!lazy) {
            cache = initCache();
        }
    }

    public String put(ResponderLocation responderLocation) {
        getCache().put(responderLocation.key(), Json.encode(responderLocation));
        return responderLocation.key();
    }

    public ResponderLocation get(String key) {
        String s = getCache().get(key);
        if (s == null) {
            return null;
        }
        return Json.decodeValue(s, ResponderLocation.class);
    }

    public Uni<Void> clear() {
        return Uni.createFrom().<Void>item(() -> {
            getCache().clear();
            return null;
        }).runSubscriptionOn(Infrastructure.getDefaultWorkerPool());
    }

    public void remove(String key) {
        getCache().remove(key);
    }

    private RemoteCache<String, String> getCache() {
        RemoteCache<String, String> cache = this.cache;
        if (cache == null) {
            synchronized(this) {
                if (this.cache == null) {
                    this.cache = cache = initCache();
                }
            }
        }
        return cache;
    }

    private RemoteCache<String, String> initCache() {
        log.info("Creating remote cache '" + cacheName + "'");
        Configuration configuration = Configuration.builder().name(cacheName).mode("SYNC").owners(2).build();
        return cacheManager.administration().getOrCreateCache(cacheName, configuration);
    }

}
