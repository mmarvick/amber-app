package marvick.play.awesome2;

import android.app.Application;

public class MyApp extends Application {
	private Cat cat;
	
	public Cat getActiveCat() {
		return cat;
	}
	
	public void setActiveCat(Cat newCat) {
		this.cat = newCat;
	}
}
