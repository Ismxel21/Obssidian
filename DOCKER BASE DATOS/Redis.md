# Manual Completo de Redis: Teoría, Docker e Instrucciones de Uso

Este documento es una guía integral de referencia sobre **Redis** (*REmote DIctionary Server*), pensada para comprender sus conceptos teóricos, arquitectura en memoria, integración en contenedores con **Docker**, administración desde la consola interactivas (`redis-cli`) y conexión mediante clientes gráficos (*GUI*).

---

## 1. Introducción Teórica a Redis

### ¿Qué es Redis?
**Redis** es un almacén de estructura de datos en memoria de código abierto que se utiliza principalmente como **base de datos NoSQL de clave-valor**, **capa de caché** de alta velocidad y **gestor de mensajes** (*Message Broker*)[cite: 2, 4].

* **Almacenamiento en Memoria RAM:** Toda la información vive directamente en la memoria RAM principal del servidor, lo que permite realizar operaciones de lectura y escritura a velocidades extremas de **microsegundos** (a diferencia de las bases de datos relacionales tradicionales o NoSQL en disco que leen de HDD/SSD).
* **Estructura Clave-Valor Avanzada:** No es un simple diccionario de texto; permite asociar una clave única a estructuras de datos complejas (cadenas, hashes, listas, conjuntos, rankings ordenados).
* **Sencillez:** No utiliza un lenguaje complejo con uniones (*joins*) ni esquemas rígidos; opera mediante comandos directos y atómicos sobre las claves.

---

## 2. Redis como Capa de Caché

En arquitecturas de software modernas, Redis se coloca estratégicamente por delante de la base de datos principal (como MySQL, PostgreSQL o MongoDB) para servir como una **capa de caché de baja latencia**[cite: 2, 4]:

```text
[ Cliente / App Web ] 
         │
         ├─── 1. Consulta Rápida (RAM) ────► [ Redis (Caché) ]  ── (Microsegundos)
         │                                       │
         │                                       │ (Si NO existe el dato / Cache Miss)
         └─── 2. Consulta en Disco ──────────────► [ BD Principal (Disco) ] ── (Milisegundos)
