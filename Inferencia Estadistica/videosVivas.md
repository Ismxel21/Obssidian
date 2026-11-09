# Guia Definitiva y Super Explicada: Distribuciones Muestrales (Proporción y Varianza)

¡Hola! Si sientes que la estadística te habla en ruso y las fórmulas te dan ganas de llorar, respira hondo. Aquí vamos a explicar esto desde cero, con peras y manzanas, imaginando que somos dueños de una fábrica de chocolates o votantes en unas elecciones locas. 

---

## Parte 1: ¿Qué demonios es una "Distribución Muestral"? (El concepto base)

Imagina que quieres saber qué opinan los 10 millones de habitantes de un país sobre si el chocolate blanco es mejor que el negro. Preguntarles a los 10 millones es imposible, gastarías mucho dinero y tiempo. ¿Qué haces? Sacas una **muestra** (por ejemplo, le preguntas a 500 personas). 

* La respuesta de esos 500 te da un número (una proporción o una varianza). A ese número de la muestra le llamamos **estadístico** (como $\hat{p}$ o $s^2$).
* Si sacas *otra* muestra de 500 personas, te dará un número ligeramente diferente.
* Si sacas *mil muestras* de 500 personas cada una, tendrás mil resultados distintos. 

¿Qué pasa si agarras todos esos resultados y haces un gráfico de cómo se comportan? ¡Eso es una **distribución muestral**! Es la gráfica de todos los resultados posibles que podrías obtener al sacar muestras una y otra vez.

---

## Parte 2: La Distribución Muestral de la Proporción ($\hat{p}$)

### ¿De qué trata?
La proporción muestral ($\hat{p}$ - se lee "p sombrero") es simplemente el porcentaje o fracción de "éxitos" que encuentras en tu muestra. 
* *Ejemplo:* Si encuestas a 100 personas y 40 dicen que aman el helado de menta con chocolate, tu proporción muestral es $\hat{p} = \frac{40}{100} = 0.4$.

### Las reglas del juego (Las Fórmulas Mágicas)
Cuando estudias a todas las proporciones posibles de muchas muestras, la teoría nos dice que se comportan de una manera muy ordenada:

1. **La Media ($\mu_{\hat{p}}$):** En promedio, el promedio de tus muestras va a ser exactamente igual a la verdad de la población ($p$). 
   $$\mu_{\hat{p}} = p$$

2. **La Varianza ($\sigma_{\hat{p}}^2$):** Mide qué tan dispersos están los resultados. Si la muestra es más grande, los resultados se amontonan más (hay menos error).
   $$\sigma_{\hat{p}}^2 = \frac{p(1-p)}{n} = \frac{pq}{n}$$
   *(Donde $q = 1-p$ es la probabilidad de fracaso).*

3. **La Estandarización (La llave para usar la Tabla Normal):** Para poder calcular probabilidades usando la clásica campana de Gauss, transformamos nuestra proporción en una variable $Z$:
   $$Z = \frac{\hat{p} - p}{\sqrt{\frac{pq}{n}}}$$

### La Gran Regla: ¿Cuándo podemos usar la Campana Normal?
No siempre puedes usar la distribución normal. Para que la naturaleza te permita hacerlo, se tiene que cumplir esta condición de oro (si no se cumple, la gráfica queda chueca y sesgada):
$$np \ge 10 \quad \text{y} \quad nq \ge 10$$
*Es decir: el número de éxitos esperados y de fracasos esperados en tu muestra debe ser mayor o igual a 10.*

---

### Ejercicio Práctico 1 (La Proporción)

**Enunciado:**
> Supongamos que el 40% de los votantes está a favor de la reelección del actual alcalde ($p = 0.4$, por lo tanto $q = 0.6$). Se selecciona una muestra aleatoria de $600$ electores ($n = 600$). 
> 
> **Pregunta A:** ¿Cuál es la probabilidad de que la proporción muestral de votos a favor esté entre el 37% y el 45%?
> **Pregunta B:** ¿Cuál debe ser el tamaño de la muestra para tener una probabilidad del 97% de que la proporción de la muestra no se diferencie de la población en más del 2%?

#### Resolución de la Pregunta A:
1. **Verificamos la condición de normalidad:**
   * $np = 600 \times 0.4 = 240$ (¡Es mayor que 10! Sí sirve).
   * $nq = 600 \times 0.6 = 360$ (¡Es mayor que 10! Sí sirve).

2. **Planteamos lo que buscamos:**
   Queremos la probabilidad de que $\hat{p}$ esté entre $0.37$ y $0.45$:
   $$P(0.37 \le \hat{p} \le 0.45)$$

3. **Estandarizamos restando la media y dividiendo para la desviación estándar:**
   Aplicamos la fórmula a los tres lados de la desigualdad:
   $$\frac{0.37 - 0.4}{\sqrt{\frac{0.4 \times 0.6}{600}}} \le Z \le \frac{0.45 - 0.4}{\sqrt{\frac{0.4 \times 0.6}{600}}}$$

4. **Calculamos los valores de $Z$:**
   * Límite inferior: $\frac{-0.03}{0.02} = -1.5$
   * Límite superior: $\frac{0.05}{0.02} = 2.5$

5. **Buscamos la probabilidad:**
   Buscamos en nuestra tabla normal o aplicativo el área entre $Z = -1.5$ y $Z = 2.5$.
   * **Resultado:** **$0.927$** (es decir, hay un 92.7% de probabilidad).

#### Resolución de la Pregunta B:
1. **Planteamos la condición del texto:**
   Nos piden una probabilidad del 97% ($0.97$) de que la diferencia entre la proporción de la muestra y la real no sea mayor al 2% ($0.02$):
   $$P(-0.02 \le \hat{p} - p \le 0.02) = 0.97$$

2. **Estandarizamos dividiendo para la raíz:**
   $$\frac{-0.02}{\sqrt{\frac{pq}{n}}} \le Z \le \frac{0.02}{\sqrt{\frac{pq}{n}}}$$
   Simplificando con nuestros datos ($p=0.4, q=0.6$):
   $$-0.04 \sqrt{n} \le Z \le 0.04 \sqrt{n}$$

3. **Usamos la simetría de la campana:**
   Si el centro acumula el 97% ($0.97$), en las dos colas sobrantes queda el 3% ($0.03$). Dividiendo para dos, a cada cola le toca el 1.5% ($0.015$).
   Buscando en la tabla normal el valor de $Z$ que encierra un área de $0.015$ a la izquierda, obtenemos:
   $$Z = -2.17$$

4. **Igualamos y despejamos $n$:**
   $$-0.04 \sqrt{n} = -2.17$$
   $$\sqrt{n} = \frac{-2.17}{-0.04} = 54.25$$
   Elevamos al cuadrado ambos lados para eliminar la raíz:
   $$n = (54.25)^2 \approx 2943$$
   * **Resultado:** Se necesita una muestra de **$2943$** personas.

---

## Parte 3: La Distribución Muestral de la Varianza ($s^2$)

### ¿De qué trata?
Imagínate que ya no te importa el promedio o el porcentaje, sino el **desorden o la dispersión** de los datos (la varianza). Si mides las estaturas en diferentes salones de clase, cada salón tendrá una varianza muestral ($s^2$) distinta. 

Cuando la población original es **normal**, resulta que la varianza de las muestras **ya no se comporta como una campana normal**, ¡sino como una distribución llamada **Chi cuadrada ($\chi^2$)**!

### El Estadístico Chi Cuadrada:
La fórmula oficial que inventaron los estadísticos para conectar la varianza de la muestra ($s^2$) con la de la población ($\sigma^2$) es:
$$\chi^2 = \frac{(n - 1)s^2}{\sigma^2}$$
*Donde los **grados de libertad** son simplemente $n - 1$.*

---

### Ejercicio Práctico 2 (La Varianza)

**Enunciado:**
> Encuentra la probabilidad de que una muestra aleatoria de $n = 20$ observaciones extraída de una población normal con varianza poblacional $\sigma^2 = 5$, tenga una varianza muestral $s^2$ **mayor que 8.1**.

#### Resolución paso a paso:
1. **Planteamos lo que nos piden:**
   Queremos calcular:
   $$P(s^2 > 8.1)$$

2. **Construimos el estadístico Chi cuadrada multiplicando a ambos lados:**
   Para transformar $s^2$ en $\chi^2$, debemos multiplicar por $(n-1)$ y dividir para $\sigma^2$. Como $n = 20$ y $\sigma^2 = 5$, multiplicamos todo por $\frac{20 - 1}{5}$, es decir, $\frac{19}{5}$:
   $$P\left( \frac{19 \cdot s^2}{5} > \frac{19 \cdot 8.1}{5} \right)$$

3. **Simplificamos los cálculos:**
   * Lo del lado izquierdo se convierte en nuestra querida variable **$\chi^2$**.
   * Lo del lado derecho da: $\frac{19 \times 8.1}{5} = \frac{153.9}{5} = 30.78$.
   * Entonces la pregunta se transforma en:
     $$P(\chi^2 > 30.78)$$
     con **$19$ grados de libertad** ($20 - 1$).

4. **Buscamos en la tabla o aplicativo:**
   * Si buscamos en una tabla de Chi cuadrada con 19 grados de libertad, el valor $30.78$ nos da una probabilidad aproximada entre $0.05$ y $0.025$.
   * Si usamos un software exacto (como GeoGebra), el resultado preciso es:
     **$0.0427$** (es decir, un 4.27% de probabilidad).

---

## ¡Resumen mental para sobrevivir al examen!
* Si el ejercicio habla de **porcentajes, votos o proporciones ($\hat{p}$)** $\rightarrow$ Usas la **Distribución Normal ($Z$)** y revisas que $np \ge 10$ y $nq \ge 10$.
* Si el ejercicio habla de **varianza, dispersión o $s^2$** $\rightarrow$ Usas la **Distribución Chi cuadrada ($\chi^2$)** con $n-1$ grados de libertad.
