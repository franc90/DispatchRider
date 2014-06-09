package gui.main;

/**
 * 
 * @author Jakub Tyrcha
 * 
 *         Implementacja wzorca singleton opakowujaca WindowGUI. Za
 *         posrednictwem tej klasy odbywa sie komunikacja z gui.
 * 
 */
public class SingletonGUI extends WindowGUI {
	private static boolean shallWork = false;
	private static boolean wasDisposed = false;

	private static class SingletonHolder {
		private static SingletonGUI instance;
	}

	public static SingletonGUI getInstance() {
		if (wasDisposed){
			wasDisposed = false;
			return (SingletonHolder.instance = new SingletonGUI());
		}
		return (SingletonHolder.instance != null ? SingletonHolder.instance
				: (SingletonHolder.instance = new SingletonGUI()));
	}

	private SingletonGUI() {
	}

	/**
	 * Ustawia zmienna statyczna shallWork. Jesli sW jest rowne false, to zamyka
	 * okno GUI, jesli bylo ono otwarte
	 * 
	 * @param sW
	 */
	public static void setShallWork(boolean sW) {
		shallWork = sW;
		if (!shallWork && SingletonHolder.instance != null) {
			SingletonHolder.instance.frame.dispose();
			wasDisposed  = true;
		}
	}

	public static boolean getShallWork() {
		return shallWork;
	}

}