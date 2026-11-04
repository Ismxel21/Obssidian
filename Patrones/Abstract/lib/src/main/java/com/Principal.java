package com;

import com.config.ConfigLoader;
import com.inter.UIFactory;
import com.model.Aplicacion;

public class Principal {

    public static void main(String[] args) {
        try {
            ConfigLoader config = new ConfigLoader("config.properties");
            String nombreTema = config.get("tema"); 

            String rutaClase = "com.impl.UIFactory" + nombreTema;

            // Buscamos la clase en tiempo de ejecución por su nombre en texto
            Class<?> claseFabricante = Class.forName(rutaClase);

            // Instanciamos la clase dinámicamente usando su constructor por defecto
            UIFactory factory = (UIFactory) claseFabricante.getDeclaredConstructor().newInstance();

            Aplicacion ap = new Aplicacion(factory);
            ap.ejecutar();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Ocurrió un error al instanciar la clase por reflexión.");
        }
    }
}