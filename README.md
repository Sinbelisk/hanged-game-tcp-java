# Juego del Ahorcado con TCP en Java

##  Descripción

Este es un juego del Ahorcado basado en el protocolo TCP, donde los jugadores pueden participar en partidas en solitario o multijugador a través de comandos específicos.



## 🛠️ Instalación, ejecución y requisitos
### Requisitos
**Java 17+**
Un entorno de desarrollo como **Intellij/Eclipse**
Si la aplicación no funciona en multijugador, comprueba que el **puerto TCP 2050** este abierto

### 🔹 Servidor
1. Compilar el código fuente:
   ```sh
   javac -d bin $(find . -name "*.java")
   ```
2. Ejecutar el servidor:
   ```sh
   java -cp bin server.Server
   ```
Esto abrirá el servidor, por defecto se abre en el puerto **2050**

### 🔹 Cliente
1. Compilar el código fuente:
   ```sh
   javac -d bin $(find . -name "*.java")
   ```
2. Ejecutar el cliente y establecer como parámetro el puerto del servidor.:
   ```sh
   java -cp bin client.MainClient <ip>
   ```
Reemplaza <ip> con la dirección IP del servidor (por ejemplo, `127.0.0.1` o `localhost` si está en la misma máquina)

### 🔹 Cliente
Si ejecutas la aplicación desde un IDE recuerda activar las instancias múltiples para poder ejecutar varios clientes al mismo tiempo.

## 💻 Comandos Disponibles

### 🔹 Comandos de Usuario

```
/login <usuario> <contraseña>    # Inicia sesión.
/register <usuario> <contraseña> # Registra un nuevo usuario.
/exit                            # Finaliza la conexión.
```
### 🔹 Comandos en el Juego
Comandos disponibles durante una sesión de juego.
```
/vowel <vocal>      # Adivina una vocal.
/consonant <consonante> # Adivina una consonante.
/phrase <frase>     # Intenta adivinar la palabra completa.
```

### 🔹 Comandos para Salas de Juego
Comando para empezar a jugar una partida.
```
/game <comando>      # Administra las salas.
    create <nombre>  # Crea una nueva sala.
    enter <nombre>   # Entra en una sala existente.
    exit            # Sale de la sala de juego.
    solo            # Inicia una partida en solitario.
```

## Flujo de comandos
### /login <usuario> <contraseña>
1. El cliente envía el comando `/login <usuario> <contraseña>` al servidor.
2. El servidor verifica las credenciales en UserManager.
3. Si son correctas, el servidor crea un nuevo User y lo asocia con el Worker actual.
4. El servidor envía un mensaje de éxito al cliente.
5. Si son incorrectas, el servidor envía un mensaje de error al cliente.
### /game create <nombre>
1. El cliente envía el comando `/game create <nombre>` al servidor.
2. El servidor verifica si ya existe una sala con ese nombre en `GameRoomManager`.
3. Si no existe, el servidor crea una nueva GameRoom y añade al usuario actual.
4. El servidor envía un mensaje de éxito al cliente.
5. Si ya existe, el servidor envía un mensaje de error al cliente.
### /game enter <nombre>
1. El cliente envía el comando `/game enter <nombre>` al servidor.
2. El servidor busca la sala en GameRoomManager.
3. Si la encuentra y tiene espacio, el servidor añade al usuario actual a la sala.
4. Si hay suficientes jugadores en la sala, empieza la partida.
5. Si no la encuentra o está llena, el servidor envía un mensaje de error al cliente.
### /game solo
1. El cliente envía el comando `/game solo` al servidor.
2. El servidor crea una nueva instancia de HangedGame.
3. El servidor inicia el juego en modo solitario para el usuario actual.
### /vowel <vocal> y /consonant <consonante>
1. El cliente envía el comando `/vowel <vocal>` o `/consonant <consonante>`.
2. El servidor verifica si el usuario está en una partida.
3. Si está en una partida, el servidor verifica si es su turno.
4. Si es su turno, el servidor actualiza el estado del juego en HangedGame.
5. El servidor envía un mensaje al cliente con el resultado de la jugada.
### /phrase <frase>
1. El cliente envía el comando `/phrase <frase>` .
2. El servidor verifica si el usuario está en una partida.
3. Si está en una partida, el servidor verifica si es su turno.
4. Si es su turno, el servidor verifica si la frase es correcta.
5. El servidor envía un mensaje al cliente con el resultado.
6. Si la frase es correcta, el jugador gana. De lo contrario se comprueba el jugador con puntuación más alta y este será el que gana.
### /exit
1. El cliente envía el comando /exit al servidor.
2. El servidor cierra la conexión con el cliente.

## 📜 Manual de usuario
### 🔹 Modo Solitario
1.  Iniciar el juego:
- El jugador ejecuta el cliente y se conecta al servidor.
- El jugador se registra o inicia sesión.
- El jugador escribe `/game solo` para iniciar una partida en solitario.
2. **Desarrollo del juego**:
    - El servidor muestra la palabra oculta con guiones bajos.
    - El jugador escribe `/vowel <vocal>` para adivinar una vocal o `/consonant <consonante>` para adivinar una consonante.
    - El servidor revela las letras correctas y actualiza el refrán oculto.
    - El jugador puede intentar adivinar la frase completa escribiendo `/phrase <frase>`.
3. Fin del juego:
    - El jugador gana si adivina la palabra completa o si descubre todas las letras.
    - El jugador pierde si agota el número de intentos permitidos.
    - El servidor muestra un mensaje con el resultado del juego y actualiza la puntuación del jugador.
### 🔹 Modo Multijugador
1. **Crear o unirse a una sala**:
    - El jugador ejecuta el cliente y se conecta al servidor.
    - El jugador se registra o inicia sesión.
    - El jugador escribe `/game create <nombre>` para crear una sala o `/game enter <nombre>` para unirse a una sala existente.
2. **Desarrollo del juego**:
    - El servidor espera a que se unan 3 jugadores a la sala.
    - El servidor asigna turnos a los jugadores.
    - El jugador en turno escribe `/vowel <vocal>` para adivinar una vocal o `/consonant <consonante>` para adivinar una consonante.
    - El servidor revela las letras correctas y actualiza la palabra oculta.
    - El jugador en turno puede intentar adivinar la palabra completa escribiendo `/phrase <frase>`.
3. **Fin del juego**:
    - El jugador gana si adivina la palabra completa.
    - El jugador con más puntos gana si nadie adivina la palabra completa.
    - El servidor muestra un mensaje con el resultado del juego y actualiza la puntuación de los jugadores.

## 🔹 Ejemplos de interacción
### Registro e inicio de sesión
```bash
/register Usuario usuario
Server: Registro completado correctamente, utiliza '/login <usuario> <contraseña>' para iniciar sesión
/login usuario usuario
Server: ERROR: Credenciales inválidas, prueba de nuevo
/login Usuario usuario
Server: Sesión iniciada correctamente, utiliza '/help' para ver los comandos disponibles
```
### Modo solitario
Ejemplo de una partida individual en solitario.
```bash
/game solo
Server: Partida de un solo jugador iniciada.
Server: [GAME] El juego ha comenzado.
Server: [PROVERB] Refrán actual: _______
/vowel a
Server: [GUESS] Usuario intentó 'a'. [OK] Correcta!
Server: [PROVERB] Refrán actual: _a__a_a
/consonant n
Server: [GUESS] Usuario intentó 'n'. [OK] Correcta!
Server: [PROVERB] Refrán actual: _an_ana
/phrase manzana
Server: [WIN] ¡Has ganado con 150 puntos!
Server: [Usuario] Tus estadísticas son: <Victorias: 1> <Derrotas: 0> <Puntuación total: 150> 

```

### Modo Multijugador
Ejemplo de una breve partida multijugador
```bash
/game enter lobby1
Server: Has entrado en la sala lobby1
Server: [WAIT] Faltan 1 jugadores para comenzar.
Server: [JOIN] Prueba se ha unido a la sala.
Server: [GAME] El juego ha comenzado.
Server: [PROVERB] Refrán actual: ____ ___ _____ _____ _ __ ____ _____ ____
Server: [TURN] Es tu turno.
/vowel a
Server: [GUESS] Usuario intentó 'a'. [OK] Correcta!
Server: [PROVERB] Refrán actual: ____ ___ _____ a__a_ _ __ ____ _____ ____
Server: [TURN] Es el turno de Prueba
Server: [GUESS] Prueba intentó 'n'. [OK] Correcta!
Server: [PROVERB] Refrán actual: ____ __n ____n an_a_ _ __ ____ ____n ____
Server: [TURN] Es el turno de paco
Server: [GUESS] paco intentó 'p'. [X] Incorrecta.
Server: [PROVERB] Refrán actual: ____ __n ____n an_a_ _ __ ____ ____n ____
Server: [TURN] Es tu turno.
/vowel e
Server: [GUESS] Usuario intentó 'e'. [OK] Correcta!
Server: [PROVERB] Refrán actual: ___e __n ____n an_a_ _ _e ____ ____n e_e_
Server: [TURN] Es el turno de Prueba
Server: [GUESS] Prueba intentó 'i'. [OK] Correcta!
Server: [PROVERB] Refrán actual: _i_e __n __i_n an_a_ _ _e _i__ __i_n e_e_
Server: [TURN] Es el turno de paco
Server: [GUESS] paco intentó 'p'. [X] Incorrecta.
Server: [PROVERB] Refrán actual: _i_e __n __i_n an_a_ _ _e _i__ __i_n e_e_
Server: [TURN] Es tu turno.
/phrase dime con quién andas y te diré quién eres
Server: [WIN] ¡Has ganado con 150 puntos!
Server: [REMOVE] paco ha salido de la sala.
Server: [Usuario] Tus estadísticas son: <Victorias: 1> <Derrotas: 0> <Puntuación total: 150> 
```

## 📂 Estructura del Proyecto

```
/src
 ├── server
 │   │   Server.java              # Servidor TCP principal
 │   │   Worker.java              # Hilo de conexión con cada cliente
 │   │
 │   ├── commands                 # Sistema de comandos
 │   │   │   Command.java         # Interfaz que define un comando
 │   │   │   CommandFactory.java  # Fábrica de comandos
 │   │   │   AbstractCommand.java # Implementación abstracta de una interfaz con algo de funcionalidad añadida
 │   │   ├── commands             # Implementaciones específicas de comandos
 │   │   │   │   LoginCommand.java
 │   │   │   │   RegisterCommand.java
 │   │   │   │   ExitCommand.java
 │   │   │   │   VowelCommand.java
 │   │   │   │   GameCommand.java
 │   │   │   │   ConsonantCommand.java
 │   │   │   │   PhraseCommand.java
 │   │   │   │   HelpCommand.java
 │   │
 │   ├── game                     # Lógica del juego
 │   │   │   GameRoom.java         # Representa una sala de juego con jugadores, los jugadores la utilizan para jugar la partida.
 │   │   │   User.java             # Modelo de usuario
 │   │   │   ScoreManager.java     # Sistema de puntuación
 │   │   ├── model                # Modelos específicos del juego
 │   │   │   │   HangedGame.java   # Lógica del juego del ahorcado
 │   │   │   │   HiddenSaying.java # Palabra oculta
 │   │
 │   ├── services                 # Servicios principales del servidor
 │   │   │   UserManager.java     # Registro de usuarios registrados.
 │   │   │   ServiceRegistry.java # Registro de 'servicios' que utiliza el servidor y los hilos que invoca.
 │   │   │   MessageService.java  # Sistema de mensajeria para envio de mensajes al cliente
 │   │   │   GameRoomManager.java # Registros de salas de juego
 │   │   │   CommandManager.java  # Gestor para crear y ejecutar comandos.
 │   │
 │   ├── util                     # Utilidades generales
 │   │   │   SayingUtils.java      # Gestión de frases del ahorcado
 │   │   │   SimpleLogger.java     # Sistema de logging simple
 │   │
 ├── client                       # Código del cliente TCP
 │   │   Client.java               # Cliente TCP principal
 │   │   MainClient.java           # Punto de entrada del cliente
 │
 ├── common                        # Código compartido entre cliente y servidor
 │   │   Connection.java           # Interfaz de conexión
 │   │   SocketConnection.java     # Implementación de la conexión por sockets
```

## 🧑‍💻 Tecnologías Usadas

- **Java**: Implementación del servidor y cliente con sockets TCP.
- **Patrón Command**: Gestión eficiente de comandos del juego.
- **Sockets TCP**: Comunicación confiable entre cliente y servidor.
- **Concurrencia**: Manejo simultáneo de conexiones mediante hilos.
- **Mapas Concurrentes**: Gestión segura de usuarios y salas.
- **Logging**: Registro de eventos para depuración y monitoreo.

## 📜 Licencia
Este proyecto es de código abierto y está disponible bajo la licencia MIT.

---

**Desarrollado por:** Rafael Francisco Jiménez Rayo
