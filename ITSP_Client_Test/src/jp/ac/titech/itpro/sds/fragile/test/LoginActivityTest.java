package jp.ac.titech.itpro.sds.fragile.test;

import java.util.EventListener;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import jp.ac.titech.itpro.sds.fragile.LoginActivity;
import jp.ac.titech.itpro.sds.fragile.R;
import android.os.AsyncTask;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LoginActivityTest extends
		ActivityInstrumentationTestCase2<LoginActivity> {
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
		signal = new CountDownLatch(1);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		mActivity = getActivity();
		email = (EditText) mActivity.findViewById(R.id.email);
		password = (EditText) mActivity.findViewById(R.id.password);
		button = (Button) mActivity.findViewById(R.id.sign_in_button);
		loginStatusMessage = (TextView) mActivity
				.findViewById(R.id.login_status_message);
		loginStatus = (LinearLayout) mActivity.findViewById(R.id.login_status);
	}

	public void testAttempotLogin_initialize() throws Exception {
		assertEquals("初期値は空文字", "", email.getText().toString());
		assertEquals("初期値は空文字", "", password.getText().toString());
		assertEquals("ログインステータスは非表示", View.GONE, loginStatus.getVisibility());

	}

	public void testTestableAsyncTask() throws Throwable {
        final CountDownLatch signal = new CountDownLatch(1);
        
        final AsyncTask testTask
            = mActivity.new UserLoginTask() {
                @Override
                protected void onPostExecute(final Boolean result) {
                    super.onPostExecute(result);
                    signal.countDown();
                }
        };
        
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
            	testTask.execute(this);
            }
        });
        
				TouchUtils.clickView(this, email);
				// email: A@B
				sendKeys(KeyEvent.KEYCODE_A);
				sendKeys(KeyEvent.KEYCODE_AT);
				sendKeys(KeyEvent.KEYCODE_B);
				TouchUtils.clickView(this, password);
				// password: WRONG
				sendKeys(KeyEvent.KEYCODE_W);
				sendKeys(KeyEvent.KEYCODE_R);
				sendKeys(KeyEvent.KEYCODE_O);
				sendKeys(KeyEvent.KEYCODE_N);
				sendKeys(KeyEvent.KEYCODE_G);
				TouchUtils.clickView(this, button);
        signal.await(3, TimeUnit.SECONDS);
	}

	public void testAttempotLogin_emptyEmailInput() throws Exception {
		TouchUtils.clickView(this, button);
		sleep(2000);
		assertEquals("空のemailに対するメッセージ", "入力して下さい", loginStatusMessage
				.getText().toString());
	}

	public void testAttempotLogin_invalidEmailInput() throws Exception {
		TouchUtils.clickView(this, email);
		// email: A
		sendKeys(KeyEvent.KEYCODE_A);
		TouchUtils.clickView(this, button);
		assertEquals("無効なemailに対するメッセージ", "メールアドレスを入力して下さい", loginStatusMessage
				.getText().toString());
	}

	public void testAttemptLogin_wrongPassword() throws Exception {
		TouchUtils.clickView(this, email);
		// email: A@B
		sendKeys(KeyEvent.KEYCODE_A);
		sendKeys(KeyEvent.KEYCODE_AT);
		sendKeys(KeyEvent.KEYCODE_B);
		TouchUtils.clickView(this, password);
		// password: WRONG
		sendKeys(KeyEvent.KEYCODE_W);
		sendKeys(KeyEvent.KEYCODE_R);
		sendKeys(KeyEvent.KEYCODE_O);
		sendKeys(KeyEvent.KEYCODE_N);
		sendKeys(KeyEvent.KEYCODE_G);
		TouchUtils.clickView(this, button);
		assertEquals("無効なpasswordに対するメッセージ", "パスワードが間違っています",
				loginStatusMessage.getText().toString());
	}

	public synchronized void sleep(long msec)
    {	
    	try
    	{
    		wait(msec);
    	}catch(InterruptedException e){}
    }
}
