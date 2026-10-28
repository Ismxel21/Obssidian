# Guía de Estudio: Java Reflection y Anotaciones Personalizadas

---

## 1. Introducción: ¿Para qué sirve esto?

### ¿Qué es Java Reflection (Reflexión)?
La **Reflexión** es una característica avanzada de Java que permite a un programa **inspeccionar, examinar y modificar su propia estructura interna en tiempo de ejecución** (*runtime*). 

Normalmente, en Java escribes código sabiendo qué clases y métodos existen antes de compilar. Con Reflection, puedes:
* Consultar qué campos, métodos o constructores tiene una clase sin conocerla previamente.
* Crear instancias de objetos dinámicamente sin usar el operador `new`.
* Invocación dinámica de métodos mediante su nombre en texto.
* Leer metadatos asociados a las clases a través de **Anotaciones**.

---

## 2. Patrones de Diseño y Conceptos Clave Envolventes

### A. Patrón Metaprogramación y Contenedor de Inversión de Control (IoC)
El código implementa la base sobre la que funcionan frameworks populares como **Spring Boot**, **Hibernate** o **JUnit**:
* **Lectura de Metadatos vía Anotaciones:** En lugar de acoplar el código escribiendo llamadas manuales a cada método `get`, se escanea la clase y se detecta automáticamente qué métodos tienen `@VisibleEnReporte`.
* **Desacoplamiento:** El código procesador no necesita conocer la lógica interna de `Producto`; solo busca la presencia del "marcador" (la anotación).

### B. Patrón Factory Dinámica (Fábrica por Reflexión)
Al utilizar `Constructor.newInstance(...)`, estás aplicando una variación dinámica del patrón **Factory Method**, permitiendo instanciar tipos de datos de forma flexible a partir de sus metadatos (`Class<?>`).

---

## 3. Estructura del Proyecto y Código Comentado

### Archivo 1: `@VisibleEnReporte` (Anotación Personalizada)

```java
package com.anotaciones;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotación personalizada que actúa como una "etiqueta" o marcador.
 */
// @Target: Define DÓNDE se puede usar la anotación. 
// ElementType.METHOD restringe su uso únicamente a métodos.
@Target(ElementType.METHOD)

// @Retention: Define HASTA CUÁNDO estará disponible la anotación.
// RetentionPolicy.RUNTIME es fundamental para poder leerla mediante Reflection mientras la app corre.
@Retention(RetentionPolicy.RUNTIME)
public @interface VisibleEnReporte {
    // Es una anotación marcadora (Marker Annotation), por lo que no requiere atributos internos.
}
```

---

### Archivo 2: `Producto.java` (Modelo / Entidad)

```java
package com.model;

import com.anotaciones.VisibleEnReporte;

/**
 * Clase POJO (Plain Old Java Object) que representa el modelo de datos.
 */
public class Producto {
    
    // Campos/Atributos privados (Encapsulamiento)
    private int cod;
    private String nombre;
    private double precio;

    // Constructor 1: Por defecto (sin parámetros)
    public Producto() {
       this.nombre = "sin nombre";
    }

    // Constructor 2: Sobrecargado con parámetros
    public Producto(int cod, String nombre, double precio) {
        this.cod = cod;
        this.nombre = nombre;
        this.precio = precio;
    }
    
    // Marcamos los métodos de interés con la anotación personalizada
    @VisibleEnReporte
    public String getNombre() {
        return nombre;
    }

    @VisibleEnReporte
    public double getPrecio() {
        return precio;
    }

    // Método no anotado (Reflection lo ignorará durante el reporte)
    public int getCod() {
        return cod;
    }
}
```

---

### Archivo 3: `Principal.java` (Clase de Ejecución e Inspección)

```java
import com.model.Producto;
import com.anotaciones.VisibleEnReporte;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class Principal {
    public static void main(String[] args) {
        try {
            // =========================================================================
            // PASO 1: Obtener la representación de metadatos de la clase (Objeto Class)
            // =========================================================================
            Class<?> ClaseP = Producto.class;

            System.out.println("=== METADATOS DE LA CLASE ===");
            System.out.println("Nombre completo: " + ClaseP.getName());
            System.out.println("Nombre simple: " + ClaseP.getSimpleName());

            // =========================================================================
            // PASO 2: Inspección de Campos (Fields) y Modificadores
            // =========================================================================
            System.out.println("\n=== MODIFICADORES DE CAMPOS ===");
            for (var campo : ClaseP.getDeclaredFields()) {
                // Modifier.toString convierte los códigos enteros de modificadores a texto ("private", "public", etc.)
                System.out.println("Campo: " + campo.getName() + " | Modificador: " + Modifier.toString(campo.getModifiers()));
            }

            // =========================================================================
            // PASO 3: Inspección de Constructores
            // =========================================================================
            System.out.println("\n=== CONSTRUCTORES DECLARADOS ===");
            for (var cto : ClaseP.getDeclaredConstructors()) {
                System.out.println("Constructor: " + cto);
            }

            // =========================================================================
            // PASO 4: Instanciación Dinámica vía Reflection
            // =========================================================================
            System.out.println("\n=== INSTANCIACIÓN DINÁMICA ===");
            // Obtenemos la referencia al constructor que recibe (int, String, double)
            var constructor = ClaseP.getConstructor(int.class, String.class, double.class);
            
            // Instanciamos el objeto pasando los 3 argumentos requeridos en orden exacto
            Producto miProd = (Producto) constructor.newInstance(1, "Smartphone Redux", 350.00);
            System.out.println("Objeto instanciado correctamente.");

            // =========================================================================
            // PASO 5: Invocación Dinámica de un Método Específico
            // =========================================================================
            System.out.println("\n=== INVOCACIÓN DIRECTA POR REFLECTION ===");
            Method met = ClaseP.getMethod("getNombre");
            // invoke(objeto, argumentos) ejecuta el método sobre la instancia 'miProd'
            String r = (String) met.invoke(miProd);
            System.out.println("Resultado de getNombre(): " + r);

            // =========================================================================
            // PASO 6: Detección y Ejecución Dinámica por Anotaciones (Automático)
            // =========================================================================
            System.out.println("\n=== PROCESAMIENTO AUTOMÁTICO DE ANOTACIONES ===");
            for (var method : ClaseP.getDeclaredMethods()) {
                // Comprobamos si el método tiene la anotación @VisibleEnReporte
                if (method.isAnnotationPresent(VisibleEnReporte.class)) {
                    // Invocamos el método anotado dinámicamente
                    var valorMetodo = method.invoke(miProd);
                    System.out.println("Método Detectado: " + method.getName() + "() -> Valor: " + valorMetodo);
                }
            }

        } catch (Exception e) {
            // Manejo de excepciones que puede lanzar Reflection
            // (NoSuchMethodException, IllegalAccessException, InvocationTargetException, etc.)
            e.printStackTrace();
        }
    }
}
```

---

## 4. Resumen para Repaso / Examen

1. **`Class.forName()` o `Clase.class`**: Punto de entrada principal para acceder a la reflexión en Java.
2. **`getDeclaredFields()` vs `getFields()`**: `getDeclaredFields()` obtiene TODOS los campos declarados en la clase (incluyendo privados), mientras que `getFields()` solo obtiene los públicos.
3. **`newInstance()`**: Permite crear instancias dinámicas. Importante: la firma de tipos en `getConstructor(...)` debe coincidir exactamente con los argumentos enviados a `newInstance(...)`.
4. **`isAnnotationPresent(...)`**: Método clave para consultar en tiempo de ejecución si un elemento posee una anotación configurada con `@Retention(RetentionPolicy.RUNTIME)`.
