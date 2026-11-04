package com.model;

import com.inter.Boton;
import com.inter.UIFactory;
import com.inter.Ventana;

public class Aplicacion {

	private Boton boton;
	private Ventana ventana;

	public Aplicacion(UIFactory factory) {
		this.boton = factory.crearBoton();
		this.ventana = factory.crearVentana();
	}

	public void ejecutar() {
		boton.dibujar();
		ventana.abrir();

	}

}
