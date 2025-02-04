package server.services;

import server.commands.CommandFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ServiceRegistry {
    private final ConcurrentMap<Class<?>, Object> services = new ConcurrentHashMap<>() {
    };

    public ServiceRegistry() {
        registerService(MessageService.class, new MessageService());
        registerService(UserManager.class, new UserManager());
        registerService(GameRoomManager.class, new GameRoomManager());
        registerService(CommandManager.class, new CommandManager(this));
    }

    public <T> void registerService(Class<T> serviceClass, T instance) {
        services.put(serviceClass, instance);
    }

    public synchronized  <T> T getService(Class<T> serviceClass) {
        return serviceClass.cast(services.get(serviceClass));
    }
}
