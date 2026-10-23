# Patrón Singleton con Double-Checked Locking en Java

Implementación segura del patrón de diseño **Singleton** utilizando `ReentrantLock` para garantizar la concurrencia en entornos multihilo.

## 📌 ¿Qué hace?
* Garantiza que solo exista una única instancia en todo el programa.
* Usa un candado seguro para que varios procesos al mismo tiempo no la dupliquen.

## 💻 Código Fuente

```java
package com.model;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Singleton {

    private static volatile Singleton instance;
    private static final Lock lock = new ReentrantLock();

    private Singleton() {
    }

    public static Singleton getInstance() {
        if (instance == null) { // Primera revisión sin bloquear (rápido)
            lock.lock();
            try {
                if (instance == null) { // Segunda revisión con el candado puesto
                    instance = new Singleton();
                }
            } finally {
                lock.unlock(); // ¡Siempre libera el candado!
            }
        }
        return instance;
    }

    public static void main(String[] args) {
        Singleton s1 = getInstance();
        Singleton s2 = getInstance();
        
        System.out.println(s1 == s2); // Debería imprimir true
    }
}
