package server.services;


import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/**
 * Registro de servicios que permite la gestión y recuperación de instancias de servicios compartidos en la aplicación.
 * <p>
 * Este registro almacena instancias de diferentes servicios y permite acceder a ellos de forma segura.
 * Se utiliza un {@link ConcurrentHashMap} para permitir el acceso concurrente sin bloqueos innecesarios.
 * </p>
 */
public class ServiceRegistry {
    /**
     * Mapa concurrente que almacena las instancias de los servicios, donde la clave es la clase del servicio
     * y el valor es la instancia correspondiente.
     */
    private final ConcurrentMap<Class<?>, Object> services = new ConcurrentHashMap<>();

    /**
     * Constructor de la clase, que inicializa y registra los servicios principales del sistema.
     */
    public ServiceRegistry() {
        registerService(MessageService.class, new MessageService());
        registerService(UserManager.class, new UserManager());
        registerService(GameRoomManager.class, new GameRoomManager());
        registerService(CommandManager.class, new CommandManager(this));
    }

    /**
     * Registra un nuevo servicio en el registro.
     * <p>
     * La clave del servicio es su clase, y se asocia con la instancia proporcionada.
     * Si la clase del servicio ya está registrada, la sobrescribe con la nueva instancia.
     * </p>
     *
     * @param serviceClass Clase del servicio que se desea registrar.
     * @param instance     Instancia del servicio que se desea almacenar.
     * @param <T>          Tipo del servicio.
     */
    public <T> void registerService(Class<T> serviceClass, T instance) {
        services.put(serviceClass, instance);
    }

    /**
     * Obtiene un servicio previamente registrado.
     * <p>
     * Si el servicio solicitado no se encuentra en el registro, se devuelve {@code null}.
     * </p>
     *
     * @param serviceClass Clase del servicio que se desea obtener.
     * @param <T>          Tipo del servicio.
     * @return La instancia del servicio si está registrado, o {@code null} si no se encuentra.
     */
    public synchronized <T> T getService(Class<T> serviceClass) {
        return serviceClass.cast(services.get(serviceClass));
    }
}

