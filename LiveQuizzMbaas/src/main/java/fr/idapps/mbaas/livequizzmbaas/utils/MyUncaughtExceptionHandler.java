package fr.idapps.mbaas.livequizzmbaas.utils;

import java.lang.Thread.UncaughtExceptionHandler;

/**
 * Exception Handler.
 */
public class MyUncaughtExceptionHandler implements UncaughtExceptionHandler {

	private UncaughtExceptionHandler mDefautHandler;

	public MyUncaughtExceptionHandler() {
		mDefautHandler = Thread.getDefaultUncaughtExceptionHandler();
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		GLog.e(getClass().getName(), ex.getMessage());
		mDefautHandler.uncaughtException(thread, ex);
	}
}
