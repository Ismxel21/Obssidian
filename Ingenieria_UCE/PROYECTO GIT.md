## Comandos Diarios para Guardar Cambios (PC $\rightarrow$ GitHub)

cd C:\Users\USER\Documents\Obsidian

Cuando termines de escribir o actualizar tus notas en la computadora, abre la terminal dentro de tu carpeta de Obsidian y ejecuta:

```
# 1. Preparar todos los cambios
git add .

# 2. Guardar una instantánea con tu mensaje
git commit -m "Notas actualizadas"

# 3. Subir las notas a GitHub
git push
```

> **Nota:** Ya no necesitas poner la URL de GitHub. Git recuerda tu repositorio automáticamente.

## ⬇️ Descargar Cambios en la PC (GitHub $\rightarrow$ PC)

Si escribiste notas desde el iPhone u otro dispositivo y quieres bajarlas a la PC:

```
git pull
```
## 🔍 Comandos Útiles de Consulta

- **Ver estado de los archivos:** `git status`
    
- **Ver historial de commits:** `git log --oneline`
    
- **Verificar el repositorio remoto conectado:** `git remote -v`
    

## 💡 Reglas de Oro

1. **Esta Bóveda (`Ismxel21/Obssidian`):** Es **solo** para notas, resúmenes, archivos `.md` e imágenes de soporte de la universidad.
    
2. **Proyectos Nuevos de Código:** Si mañana creas una app, sistema o proyecto de programación nuevo, crea un **repositorio independiente** en GitHub para ese proyecto (no lo subas dentro de la carpeta de Obsidian).