🔍 Introspección y Reflexión en JavaGuía de estudio sobre la capacidad de inspeccionar y manipular el comportamiento en tiempo de ejecución de clases, métodos, campos y constructores en Java.📌 1. Definición y Teoría¿Qué es Introspección?Es la capacidad de un programa para inspeccionar y examinar la estructura de una clase o sus componentes (métodos, campos, tipos de datos, anotaciones) en tiempo de ejecución sin modificar su estado ni ejecutar comportamiento.Propósito: Leer o auditar metadatos sobre la estructura del código.Uso común: Lectura de anotaciones, inspección de propiedades, validación de estructuras.¿Qué es Reflexión?Es la capacidad de un programa para modificar su propio comportamiento o estructura en tiempo de ejecución. Va un paso más allá de la introspección: permite invocar métodos dinámicamente, modificar valores de campos (incluso private), y crear nuevas instancias sin usar explícitamente el operador new en tiempo de compilación.Propósito: Manipulación e instanciación dinámica en tiempo de ejecución.Uso común: Frameworks (Spring, Hibernate, Jackson, JUnit), Inyección de Dependencias, Mapeadores relacionales (ORM).⚔️ Cuadro ComparativoCriterioIntrospecciónReflexiónAcción principalObservar / Leer metadatosModificar / Invocar / AlterarCambia el estado❌ No✅ Sí (puede invocar métodos o alterar campos)Nivel de riesgoBajo (solo consulta de información)Alto (puede violar el encapsulamiento)Uso representativoConsultar si una anotación está presenteCrear una instancia o invocar un método privado🛠️ Métodos Clave y Simples de la API java.lang.reflect1. Obtener el Objeto Class<?>// Forma 1: Desde una instancia
Class<?> clase = objeto.getClass();

// Forma 2: Desde el nombre del tipo
Class<?> clase = MiClase.class;

// Forma 3: Desde un String con la ruta completa (útil para carga dinámica)
Class<?> clase = Class.forName("uce.edu.ec.modelo.Cliente");
2. Métodos Útiles de Introspección (Lectura)clase.getName(): Retorna el nombre completo de la clase.clase.getDeclaredMethods(): Devuelve un arreglo (Method[]) de todos los métodos declarados en la clase.clase.getDeclaredFields(): Devuelve un arreglo (Field[]) de todos los atributos declarados.clase.isAnnotationPresent(MiAnotacion.class): Verifica si la clase o método contiene una anotación específica.metodo.getAnnotation(MiAnotacion.class): Obtiene la instancia de la anotación aplicada.3. Métodos Útiles de Reflexión (Modificación / Ejecución)constructor.newInstance(args): Crea una nueva instancia dinámicamente.metodo.invoke(instancia, args): Ejecuta/invoca un método en tiempo de ejecución.campo.setAccessible(true): Rompe el encapsulamiento privado para permitir lectura o modificación.campo.set(instancia, nuevoValor): Asigna un valor a un campo específico.💻 Ejemplos Prácticos en CódigoEjemplo 1: Introspección Simple (Inspeccionar la Clase)package introspeccion;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class EjemploIntrospeccion {
    public static void inspeccionarClase(Object objeto) {
        Class<?> clase = objeto.getClass();
        
        System.out.println("Nombre de la clase: " + clase.getName());

        System.out.println("\n--- Campos (Atributos) ---");
        for (Field campo : clase.getDeclaredFields()) {
            System.out.println("Campo: " + campo.getName() + " | Tipo: " + campo.getType().getSimpleName());
        }

        System.out.println("\n--- Métodos ---");
        for (Method metodo : clase.getDeclaredMethods()) {
            System.out.println("Método: " + metodo.getName() + " | Retorno: " + metodo.getReturnType().getSimpleName());
        }
    }
}
Ejemplo 2: Reflexión Simple (Invocar Métodos y Cambiar Campos Privados)package reflexion;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class EjemploReflexion {
    public static void main(String[] args) throws Exception {
        // Objeto de prueba
        Persona persona = new Persona("Carlos", 25);

        Class<?> clase = persona.getClass();

        // 1. Invocación dinámica de un método en tiempo de ejecución
        Method metodoSaludar = clase.getDeclaredMethod("saludar");
        metodoSaludar.invoke(persona); // Imprime el saludo original

        // 2. Modificación de un campo PRIVADO mediante reflexión
        Field campoNombre = clase.getDeclaredField("nombre");
        campoNombre.setAccessible(true); // Romper temporalmente la visibilidad privada
        campoNombre.set(persona, "Juan");  // Cambiar valor

        // 3. Comprobación del cambio
        metodoSaludar.invoke(persona); // Ahora saludará como 'Juan'
    }
}

class Persona {
    private String nombre;
    private int edad;

    public Persona(String nombre, int edad) {
        this.nombre = nombre;
        this.edad = edad;
    }

    public void saludar() {
        System.out.println("Hola, mi nombre es " + nombre);
    }
}
⚠️ Ventajas y DesventajasVentajasDesacoplamiento extremo: Permite cargar o instanciar módulos sin conocerlos en tiempo de compilación.Base para Frameworks: Permite crear anotaciones personalizadas, inyectar dependencias y mapear datos automáticamente.DesventajasImpacto en el rendimiento: La reflexión es sensiblemente más lenta que la invocación directa de métodos Java.Riesgos de Seguridad: Permite vulnerar reglas de encapsulamiento (private).Pérdida de comprobación en tiempo de compilación: Si un método o campo cambia de nombre, los errores ocurrirán en tiempo de ejecución (RuntimeException).
