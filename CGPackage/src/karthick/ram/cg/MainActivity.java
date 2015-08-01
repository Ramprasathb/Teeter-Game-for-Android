package karthick.ram.cg;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class MainActivity extends Activity {
	private GameView View;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		View = new GameView(getApplicationContext(), this);
		View.setFocusable(true);
		setContentView(View);
	}

	@Override
	protected void onResume() {
		super.onResume();
		View.registerListener();
	}

	@Override
	public void onSaveInstanceState(Bundle icicle) {
		super.onSaveInstanceState(icicle);
		View.unregisterListener();
	}
}