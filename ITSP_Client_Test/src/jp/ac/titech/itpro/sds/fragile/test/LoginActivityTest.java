package jp.ac.titech.itpro.sds.fragile.test;

import java.util.EventListener;
import java.util.concurrent.CountDownLatch;

import jp.ac.titech.itpro.sds.fragile.LoginActivity;
import jp.ac.titech.itpro.sds.fragile.LoginActivity.UserLoginTask;
import jp.ac.titech.itpro.sds.fragile.R;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LoginActivityTest extends ActivityInstrumentationTestCase2<LoginActivity> {
	private LoginActivity mActivity;
	private EditText email;
	private EditText password;
	private Button button;
	private TextView loginStatusMessage;
	private LinearLayout loginStatus;
	CountDownLatch signal;
	
	private EventListener mListener;

	public LoginActivityTest() {
		super(LoginActivity.class);
	}


	private void taskExecute(EventListener l) throws Throwable {
		// create CountDownLatch.
		final CountDownLatch signal = new CountDownLatch(1);

		mListener = l;

		runTestOnUiThread(new Runnable() {
			public void run() {
				try {
					UserLoginTask task = new UserLoginTask (mActivity, new EventListener() {

						public void onFinish(int result) {

							if(mListener!=null){
								mListener.onFinish(result);
							}
							signal.countDown(); // release
						}
					});
					task.execute("nickname");
				} catch (Exception e) {
					Log.e("ERROR", e.getMessage());
					fail();
				}
			}
		});

		try {
			signal.await();// wait for callback
		} catch (InterruptedException e1) {
			fail();
			e1.printStackTrace();
		}
	}
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		mActivity = getActivity();
		email = (EditText)mActivity.findViewById(R.id.email);
		password = (EditText)mActivity.findViewById(R.id.password);
		button = (Button)mActivity.findViewById(R.id.sign_in_button);
		loginStatusMessage = (TextView)mActivity.findViewById(R.id.login_status_message);
		loginStatus = (LinearLayout)mActivity.findViewById(R.id.login_status);
	}

	public void testAttempotLogin_initialize() throws Exception {
		assertEquals("初期値は空文字", "", email.getText().toString());
		assertEquals("初期値は空文字", "", password.getText().toString());
		assertEquals("ログインステータスは非表示", View.GONE, loginStatus.getVisibility());

	}

	public void testAttempotLogin_emptyEmailInput() throws Exception {
		TouchUtils.clickView(this, button);
		assertEquals("空のemailに対するメッセージ", "入力して下さい", loginStatusMessage.getText().toString());
	}

	public void testAttempotLogin_invalidEmailInput() throws Exception {
		TouchUtils.clickView(this, email);
		// email:		A
		sendKeys(KeyEvent.KEYCODE_A);
		TouchUtils.clickView(this, button);
		assertEquals("無効なemailに対するメッセージ", "メールアドレスを入力して下さい", loginStatusMessage.getText().toString());
	}

	public void testAttemptLogin_wrongPassword() throws Exception {
		TouchUtils.clickView(this, email);
		// email:		A@B
		sendKeys(KeyEvent.KEYCODE_A); sendKeys(KeyEvent.KEYCODE_AT); sendKeys(KeyEvent.KEYCODE_B);
		TouchUtils.clickView(this, password);
		// password:	WRONG
		sendKeys(KeyEvent.KEYCODE_W); sendKeys(KeyEvent.KEYCODE_R); sendKeys(KeyEvent.KEYCODE_O);
		sendKeys(KeyEvent.KEYCODE_N); sendKeys(KeyEvent.KEYCODE_G);
		TouchUtils.clickView(this, button);
		assertEquals("無効なpasswordに対するメッセージ", "パスワードが間違っています", loginStatusMessage.getText().toString());
	}

}
