# 🛒 Sistema de Pedidos y Patrones de Diseño (Factory Method & Anotaciones)

Repositorio dedicado a la implementación práctica de patrones de diseño de software en Java, utilizando **Eclipse**, **Maven** y una base de datos ligera **SQLite**, enfocado en la lección actual sobre el patrón **Factory Method** y metadatos con **Anotaciones**.

---

## 📌 PARTE 1: Patrón de Diseño Factory Method (Método Fábrica)

### 1. Definición y Concepto General
El **Factory Method** es un **patrón de diseño creacional** que proporciona una interfaz en una superclase para crear objetos, pero permite a las subclases alterar el tipo exacto de objetos que se crearán o instanciarán.

* **El cambio fundamental:** En lugar de utilizar de forma rígida y directa la palabra clave `new` para instanciar clases concretas, el código cliente delega la creación llamando a un **método fábrica** especializado.
* **Encapsulamiento:** La lógica de creación queda totalmente encapsulada tras una interfaz o clase común, desacoplando por completo al cliente de las implementaciones específicas.

### 2. Contexto y Problema que Resuelve
* **Contexto:** El código cliente necesita trabajar con objetos y crearlos, pero **no debe depender de las clases concretas** ni acoplarse a ellas de forma rígida.
* **Problema:** ¿Cómo permitir que las subclases decidan qué clase concreta instanciar sin modificar el código del cliente ni alterar la arquitectura principal?
* **Solución:** Definir un método de creación abstracto (`factoryMethod`) dentro de una clase base (creadora), dejando que las subclases implementen dicho método para instanciar los objetos específicos según se requiera.

### 3. Elementos de la Arquitectura (Componentes Clave)
1. **`Product` (Producto / Interfaz o Clase Abstracta):** Define la interfaz común de todos los objetos que el método fábrica puede producir. El código cliente interactúa exclusivamente con esta interfaz.
2. **`ConcreteProduct` (Producto Concreto):** Las implementaciones reales y específicas que heredan de `Product`. Representan el objeto final que la fábrica crea y que la aplicación utilizará.
3. **`Creator` (Creador / Clase Abstracta):** Declara el método fábrica (`factoryMethod()`), el cual retorna un objeto de tipo `Product`. Contiene la lógica de negocio general y delega la instanciación.
4. **`ConcreteCreator` (Creatores Concretos):** Son las subclases que sobrescriben el método fábrica para devolver una instancia de un `ConcreteProduct` específico.

### 4. Ventajas y Desventajas
* **Ventajas:**
  * **Flexibilidad:** Desacopla la lógica de instanciación del flujo principal del programa.
  * **Principio Abierto/Cerrado (OCP):** Permite introducir nuevas familias o clases concretas de productos sin romper ni modificar el código cliente existente.
  * **Principio de Responsabilidad Única:** Aísla la lógica de creación, evitando estructuras condicionales masivas (`if-else` o `switch`).
* **Desventajas:**
  * **Aumento de clases:** Cada nuevo producto suele requerir su propia subclase creadora, incrementando la complejidad estructural del proyecto.
  * **Sobreingeniería:** Si el sistema es demasiado simple, aplicar este patrón puede resultar innecesario.

---

## 🛠️ PARTE 2: Anotaciones en Java (Estándar y Personalizadas)

### 1. ¿Qué son las Anotaciones?
Las anotaciones en Java (`@AnnotationName`) son una forma de **metadatos** (datos sobre los datos). No afectan directamente la lógica de ejecución del código por sí mismas, pero proporcionan información adicional que puede ser leída por el compilador, herramientas de desarrollo o frameworks en tiempo de compilación o de ejecución.

### 2. Creación y Uso de Anotaciones Personalizadas
Para crear una anotación propia, se utiliza la palabra clave `@interface` acompañada de **meta-anotaciones** que controlan su comportamiento:

* **`@Retention`**: Define cuánto tiempo vivirá la anotación (ej. `RetentionPolicy.RUNTIME` para leerla mediante Reflexión).
* **`@Target`**: Define dónde se puede colocar la anotación (`ElementType.METHOD`, `ElementType.TYPE`, etc.).

### Ejemplo Práctico de Código

#### **Paso 1: Definir la Anotación Personalizada**
```java
package anotaciones;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MetodoImportante {
    String descripcion() default "Sin descripción";
    int prioridad() default 1;
}
