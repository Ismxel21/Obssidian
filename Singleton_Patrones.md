public class Singleton {

	private static Singleton instance;
	private static final Lock lock = new ReentrantLock();
	
/*Garantiza que solo exista una única instancia en todo el programa.
Usa un candado seguro para que varios procesos al mismo tiempo no la dupliquen.
 * */
 * 
	private Singleton() {

	}
	
	public static Singleton getInstance() {
		if (instance == null) { // Primera revisión sin bloquear (rápido)
			lock.lock();
			try {
				if (instance == null) { // Segunda revisión con el candado puesto
					instance = new Singleton();
				}
			} finally {
				lock.unlock(); // ¡Siempre libera el candado!
			}
		}
		return instance;
	}
}
