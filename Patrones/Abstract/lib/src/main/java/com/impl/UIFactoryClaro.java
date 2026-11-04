package com.impl;

import com.anotacion.Tema;
import com.inter.Boton;
import com.inter.UIFactory;
import com.inter.Ventana;

@Tema("Claro")
public class UIFactoryClaro implements UIFactory{

	@Override
	public Boton crearBoton() {
		// TODO Auto-generated method stub
		return new BotonClaro();
	}

	@Override
	public Ventana crearVentana() {
		// TODO Auto-generated method stub
		return new VentanaClaro();
	}

}
