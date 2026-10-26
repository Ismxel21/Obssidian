# OpenGL ES (OpenGL for Embedded Systems)

## 📌 Definición
**OpenGL ES** es una **API (Application Programming Interface)** de procesamiento gráfico multiplataforma y estándar de la industria, diseñada específicamente para dispositivos con recursos limitados como teléfonos móviles (Android e iOS), tabletas, consolas portátiles y sistemas embebidos.

Funciona como una versión optimizada y ligera del **OpenGL** tradicional de escritorio, proporcionando una interfaz de bajo nivel entre el código de la aplicación y la **GPU (Graphics Processing Unit), su lenguaje es GLSL (Open GL Shading Language**.

---

## 🚀 Características Principales

* **Aceleración por Hardware:** Delega el trabajo pesado de renderizado, transformaciones geométricas y cálculo de píxeles directamente a la GPU, liberando de carga de trabajo al CPU.
* **Pipeline Programable:** Utiliza un flujo de procesamiento gráfico basado en programas ejecutables en la GPU llamados **Shaders**, escritos en el lenguaje de sombreado **GLSL ES** (*OpenGL Shading Language for Embedded Systems*).
* **Consumo Eficiente de Recursos:** Diseñado para minimizar el consumo de memoria y energía en dispositivos móviles sin sacrificar rendimiento.
* **Procesamiento Vectorial en 2D y 3D:** Permite generar tanto gráficos bidimensionales simples como entornos tridimensionales complejos e interactivos.
* **Control:  quien maneja los detalles de los graficos es el contorlador/driver automaticamente

---

## 🎨 Funcionalidad Básica

OpenGL ES permite construir contenido visual mediante los siguientes pasos fundamentales:

1. **Dibujar Geometría:** Procesa primitivas geométricas básicas (puntos, líneas y principalmente **triángulos**) mediante coordenadas de vértices.
2. **Generar Imágenes (Rasterización):** Convierte las figuras geométricas vectoriales en fragmentos o píxeles discretos en la pantalla.
3. **Aplicar Shaders:**
   * **Vertex Shader:** Determina las posiciones 2D/3D y transformaciones de cada vértice.
   * **Fragment Shader:** Calcula el color final, texturas, efectos e iluminación de cada píxel.

---

## 📱 Casos de Uso Comunes

* **Desarrollo de Videojuegos:** Motores gráficos móviles (Unity, Unreal Engine, Godot) y desarrollo nativo en Android/iOS.
* **Interfaces de Usuario Avanzadas:** Animaciones y renderizado de IU de alto rendimiento dentro de aplicaciones móviles.
* **Visualización de Datos:** Representación de gráficos interactivos, mapas 2D/3D y simulaciones en tiempo real.
* **Procesamiento de Imágenes y Video:** Aplicación de filtros visuales y efectos en tiempo real sobre la cámara o la galería.

## VERSIONES
 * ** OpenGL ES 2.0 introduce shaders programables, como control de color y vertices
