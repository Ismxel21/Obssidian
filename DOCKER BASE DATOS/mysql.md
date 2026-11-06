# Guía de Referencia: MySQL & Docker

Este documento resume las consultas SQL para la base de datos `employees` y los comandos de Docker ejecutados durante el proceso de configuración e importación.

## 1. Consultas SQL (Reports)

### Consulta 1: Resumen de Empleado, Departamento, Jefe, Salario y Cargo

Devuelve la información seleccionada con sus respectivos alias y sin filtros de fecha ni límite de registros.

```
USE employees;

SELECT 
    CONCAT(e.first_name, ' ', e.last_name) AS empleado,
    t.title AS alias,
    d.dept_name AS departamento,
    CONCAT(m.first_name, ' ', m.last_name) AS jefe_departamento,
    s.salary AS salario
FROM employees e
INNER JOIN dept_emp de ON e.emp_no = de.emp_no
INNER JOIN departments d ON de.dept_no = d.dept_no
INNER JOIN dept_manager dm ON d.dept_no = dm.dept_no
INNER JOIN employees m ON dm.emp_no = m.emp_no
INNER JOIN salaries s ON e.emp_no = s.emp_no
LEFT JOIN titles t ON e.emp_no = t.emp_no;

```

### Consulta 2: Todos los Campos del Empleado + Datos Complementarios

Devuelve **todas** las columnas de la tabla `employees` (`e.*`) junto con la información del departamento, jefe, salario y cargo.

```
USE employees;

SELECT 
    e.*,
    t.title AS alias,
    d.dept_name AS departamento,
    CONCAT(m.first_name, ' ', m.last_name) AS jefe_departamento,
    s.salary AS salario
FROM employees e
INNER JOIN dept_emp de ON e.emp_no = de.emp_no
INNER JOIN departments d ON de.dept_no = d.dept_no
INNER JOIN dept_manager dm ON d.dept_no = dm.dept_no
INNER JOIN employees m ON dm.emp_no = m.emp_no
INNER JOIN salaries s ON e.emp_no = s.emp_no
LEFT JOIN titles t ON e.emp_no = t.emp_no;

```

## 2. Comandos de Docker y Flujo de Trabajo

### Copia de archivos y Gestión del Contenedor

Comandos ejecutados en PowerShell o la consola del sistema operativo host:

1. **Copiar el archivo `.zip` de la base de datos al contenedor:**

   ```
   docker cp test_db-master.zip pruebaDocker:/tmp/test_db-master.zip
   
   ```

2. **Ingresar a la terminal interactiva del contenedor:**

   ```
   docker exec -it pruebaDocker bash
   
   ```

3. **Extraer la estructura DDL completa de la base de datos sin datos (vía PowerShell):**

   ```
   docker exec -i pruebaDocker mysqldump -u root -p1234 --no-data employees > estructura_employees.sql
   
   ```

### Procedimiento dentro del Contenedor (`bash`)

Pasos ejecutados dentro de la terminal de Linux del contenedor MySQL:

1. **Navegar al directorio temporal e instalar dependencias:**

   ```
   cd /tmp
   microdnf install -y unzip
   
   ```

2. **Descomprimir el archivo e ingresar a la carpeta:**

   ```
   unzip test_db-master.zip
   cd test_db-master
   
   ```

3. **Importar la base de datos a MySQL mediante redirección:**

   ```
   mysql -u root -p1234 < employees.sql
   
   ```

## 3. Comandos Útiles Adicionales de Docker

* **Ver contenedores activos:**

  ```
  docker ps
  
  ```

* **Reiniciar el contenedor:**

  ```
  docker restart pruebaDocker
  
  ```

* **Ver registros/logs del contenedor:**

  ```
  docker logs pruebaDocker
  
  ```
