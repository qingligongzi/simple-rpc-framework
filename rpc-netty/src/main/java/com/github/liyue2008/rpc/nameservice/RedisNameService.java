package com.github.liyue2008.rpc.nameservice;

import com.github.liyue2008.rpc.NameService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class RedisNameService implements NameService {
    private static final Logger logger = LoggerFactory.getLogger(RedisNameService.class);
    private final Collection<String> schemes = Collections.singleton("redis");
    private Jedis jedis;
    private final String SERVICE_NAME_PREFIX = "simple-rpc:service:register:";
    private final String SERVICE_LOCK = "simple-rpc:service:lock";
    private final Integer EXPIRE = 1;
    private final String RESULT_SUCCESS = "OK";

    @Override
    public Collection<String> supportedSchemes() {
        return schemes;
    }

    @Override
    public void connect(URI nameServiceUri) {
        if (schemes.contains(nameServiceUri.getScheme())) {
            jedis = new Jedis(nameServiceUri);
        } else {
            throw new RuntimeException("Unsupported scheme !");
        }
    }

    @Override
    public void registerService(String serviceName, URI uri) throws IOException {
        logger.info("Register service: {}, uri: {}.", serviceName, uri);

        String redisServiceName = SERVICE_NAME_PREFIX + serviceName;
        String lockVal = UUID.randomUUID().toString();
        if (RESULT_SUCCESS.equals(jedis.set(SERVICE_LOCK, lockVal, "NX", "EX", EXPIRE))) {
            if(!jedis.sismember(redisServiceName, uri.toString())) {
                jedis.sadd(redisServiceName, uri.toString());
            }

            List<URI> uriList = jedis.smembers(serviceName).stream()
                    .map(URI::create).collect(Collectors.toList());
            Metadata metadata = new Metadata();
            metadata.put(serviceName, uriList);
            logger.info(metadata.toString());

            jedis.del(SERVICE_LOCK);
        } else {
            throw new RuntimeException("Redis register is locked !");
        }
    }

    @Override
    public URI lookupService(String serviceName) throws IOException {
        URI uri = null;
        String redisServiceName = SERVICE_NAME_PREFIX + serviceName;
        String lockVal = UUID.randomUUID().toString();
        if (RESULT_SUCCESS.equals(jedis.set(SERVICE_LOCK, lockVal, "NX", "EX", EXPIRE))) {
            List<URI> uris = jedis.smembers(redisServiceName).stream()
                    .map(URI::create).collect(Collectors.toList());
            if (null == uris || uris.isEmpty()) {
                return null;
            } else {
                uri = uris.get(ThreadLocalRandom.current().nextInt(uris.size()));
            }

            jedis.del(SERVICE_LOCK);
        } else {
            throw new RuntimeException("Redis register is locked !");
        }
        return uri;
    }
}
