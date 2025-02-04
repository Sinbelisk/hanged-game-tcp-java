package server.services;

import server.commands.CommandFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ServiceRegistry {
    private final ConcurrentMap<Class<?>, Object> services = new ConcurrentHashMap<>() {
    };

    public ServiceRegistry() {
        registerService(UserManager.class, new UserManager());
        registerService(CommandFactory.class, new CommandFactory(this));
        registerService(GameRoomManager.class, new GameRoomManager());
    }

    public <T> void registerService(Class<T> serviceClass, T instance) {
        services.put(serviceClass, instance);
    }

    public synchronized  <T> T getService(Class<T> serviceClass) {
        return serviceClass.cast(services.get(serviceClass));
    }
}
