# Guía Práctica de Conexión a Redis con Java (Jedis API)

Este repositorio contiene una implementación orientada a objetos en **Java** para conectar una aplicación a un servidor de **Redis**, explorando la diferencia entre **conexiones simples** y la estrategia de **Connection Pooling (Piscina de Conexiones)** con la API moderna de Jedis ([basada en la documentación oficial de Redis](https://redis.io/docs/latest/develop/clients/jedis/connect/)).

---

## 📚 Teoría: Conexión Simple vs. Connection Pooling

### 1. El Problema de las Conexiones Simples (por Iteración)
Crear un socket TCP hacia la base de datos por cada operación (o por cada iteración de un bucle):
* **Costo alto de Latencia:** El *handshake* TCP y la autenticación toman milisegundos costosos antes de ejecutar cualquier comando (`SET`/`GET`).
* **Agotamiento de File Descriptors:** Abrir cientos o miles de conexiones puede provocar un error del tipo `Too many open files` en el sistema operativo.
* **Límite de Clientes en Redis:** Redis tiene un parámetro `maxclients`. Al acumular conexiones sin cerrar o crearlas desmedidamente, el servidor empieza a rechazar nuevas peticiones (`ERR max number of clients reached`).

---

### 2. ¿Qué es un Connection Pool?
Un **Pool de Conexiones** es un patrón de diseño donde la aplicación mantiene una lista o "piscina" de conexiones abiertas y listas para usar. 

```text
  [ Aplicación Java ]
         │
         ▼ (Pide una conexión disponible)
┌─────────────────────────────────┐
│     Connection Pool (Jedis)     │
│  [Conn 1]  [Conn 2]  [Conn 3]   │
└─────────────────────────────────┘
         │
         ▼ (Reutiliza Socket TCP)
   [ Servidor Redis ]
```

#### Ciclo de Vida en el Pool:
1. **Petición:** La aplicación pide una conexión al pool.
2. **Préstamo:** Si hay una conexión inactiva (*idle*), el pool se la presta al hilo de ejecución de forma casi instantánea.
3. **Ejecución:** Se envían las órdenes (`SET`, `GET`, etc.) a Redis.
4. **Devolución:** Al invocar `close()` o finalizar el bloque, la conexión **no destruye el socket TCP**, sino que vuelve al pool para ser reutilizada por la siguiente tarea.

---

## 🛠️ Requisitos Previos e Instalación

### Dependencia Maven (`pom.xml`)
Asegúrate de incluir la versión más reciente de **Jedis** en tu proyecto:

```xml
<dependency>
    <groupId>redis.clients</groupId>
    <artifactId>jedis</artifactId>
    <version>8.0.1</version>
</dependency>
```

---

## 💻 Código Fuente

### 1. Clase `RedisDriver.java`

Esta clase encapsula la lógica de conexión utilizando `RedisClient` y `ConnectionPoolConfig`.

```java
package com.driver.redis;

import java.time.Duration;
import redis.clients.jedis.ConnectionPoolConfig;
import redis.clients.jedis.RedisClient;

public class RedisDriver {
    private final String HOST = "localhost";
    private final int PORT = 6379;
    private RedisClient client;

    /**
     * Establece una conexión directa básica usando una URL de Redis.
     */
    public RedisClient createConnection() {
        try {
            this.client = RedisClient.create("redis://" + HOST + ":" + PORT);
            System.out.println("Conexión simple establecida con Redis.");
        } catch (Exception ex) {
            System.out.println("Error al establecer conexión simple: " + ex.getMessage());
        }
        return this.client;
    }

    /**
     * Establece una conexión optimizada usando un Pool de Conexiones
     * parametrizado mediante ConnectionPoolConfig.
     */
    public RedisClient createPoolConnection() {
        ConnectionPoolConfig poolConfig = new ConnectionPoolConfig();
        
        // Número máximo de conexiones activas en el pool
        poolConfig.setMaxTotal(8);
        
        // Número máximo/mínimo de conexiones inactivas
        poolConfig.setMaxIdle(8);
        poolConfig.setMinIdle(0);

        // Si el pool se agota, bloquea la petición esperando por una disponible
        poolConfig.setBlockWhenExhausted(true);
        poolConfig.setMaxWait(Duration.ofSeconds(1));

        // Verificación de salud (PING) en conexiones inactivas
        poolConfig.setTestWhileIdle(true);
        poolConfig.setTimeBetweenEvictionRuns(Duration.ofSeconds(1));

        try {
            // Construcción del cliente mediante el Builder oficial
            this.client = RedisClient.builder()
                .hostAndPort(HOST, PORT)
                .poolConfig(poolConfig)
                .build();
                
            System.out.println("Pool de conexiones establecido exitosamente en " + HOST + ":" + PORT);
        } catch (Exception ex) {
            System.out.println("Error al inicializar el pool de Redis: " + ex.getMessage());
        }
        return this.client;
    }

    /**
     * Realiza la lectura de una clave en Redis.
     */
    public String read(String key) {
        String res = "";
        try {
            res = client.get(key);
            System.out.println("Leído [" + key + "]: " + res);
        } catch (Exception ex) {
            System.out.println("Error en lectura: " + ex.getMessage());
        }
        return res;
    }

    /**
     * Inserta un par clave-valor en Redis.
     */
    public void insert(String key, String value) {
        try {
            String status = client.set(key, value);
            System.out.println("Insertado [" + key + "]: " + status);
        } catch (Exception ex) {
            System.out.println("Error en inserción: " + ex.getMessage());
        }
    }

    /**
     * Cierra el cliente o pool de conexiones de forma limpia.
     */
    public void closeConnection() {
        if (client != null) {
            try {
                client.close();
                System.out.println("El cliente/pool se cerró exitosamente.");
            } catch (Exception ex) {
                System.out.println("Error al cerrar cliente: " + ex.getMessage());
            }
        }
    }
}
```

---

### 2. Clase `App.java` (Punto de Entrada)

Demostración de cómo ejecutar operaciones continuas sobre Redis **reutilizando el pool**, asegurando la velocidad del programa y el uso responsable de la red.

```java
package com.driver.redis;

import java.util.concurrent.TimeUnit;

public class App {
    public static void main(String[] args) {
        RedisDriver redisd = new RedisDriver();

        // 1. Inicializamos el Pool de Conexiones UNA SOLA VEZ al inicio
        redisd.createPoolConnection();

        // 2. Ejecutamos múltiples operaciones
        for (int i = 0; i < 50; i++) {
            String clave = "clave_" + i;
            String valor = "VALOR_" + i;

            redisd.insert(clave, valor);
            redisd.read(clave);

            try {
                // Pausa simulada de 1 segundo entre peticiones
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Hilo interrumpido: " + e.getMessage());
            }
        }

        // 3. Cerramos el pool de conexiones al finalizar todo el ciclo
        redisd.closeConnection();
    }
}
```

---

## ⚡ Conclusiones y Buenas Prácticas

1. **Reutiliza la instancia:** Nunca abras ni cierres `RedisClient` dentro de un bucle de alta frecuencia.
2. **Ajuste del Pool (`setMaxTotal`):** Adapta los límites de la piscina según el volumen de peticiones o hilos concurrentes que tenga tu backend.
3. **Cierre de Recursos:** Invoca siempre el método `close()` al apagar o detener tu aplicación para liberar conexiones abiertas hacia Redis.
