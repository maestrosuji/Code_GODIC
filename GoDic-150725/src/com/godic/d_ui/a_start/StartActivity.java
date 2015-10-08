package com.godic.d_ui.a_start;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.GregorianCalendar;

import com.go.dic.R;
import com.godic.d_ui.a_start.StartActivity;
import com.godic.d_ui.b_scan.ScanActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.Toast;

public class StartActivity extends Activity{

	public static final int WHAT_DB_RECOVERY = 2;
	public static final int WHAT_DB_UPDATE = 3;
	public static final int WHAT_DB_SKIP = 4;
	public static final int WHAT_DB_CHECK_ERROR = 5;
	public static final int WHAT_DB_DOWNLOAD_ERROR = 6;
	public static final int WHAT_DB_DOWNLOADED = 7;
	public static final int WHAT_DB_COMPLETE = 8;

	public static final int DIALOG_DB_FIRST = 1;
	public static final int DIALOG_DB_RECOVERY = 2;
	public static final int DIALOG_DB_UPDATE = 3;
	public static final int DIALOG_DB_CHECK_ERROR = 4;
	public static final int DIALOG_DB_DOWNLOAD = 5;
	public static final int DIALOG_DB_PROGRESS_DOWNLOAD = 6;
	public static final int DIALOG_DB_FAIL_AND_RETRY = 7;
	
	private static long SPLASH_MILLIS = 450;
	public static final String dbName = "dic";
	public static SQLiteDatabase db = null;
	public static Toast toast;
	static Context mContext;
	
	private ProgressDialog progressDialog;

	DBInfo dbInfo = new DBInfo();
	protected long lastModified;
	protected long lastDBsize;
	static long currentDBsize;
	private String dbinfodirpath;
	private String dbinfofilepath;
	public Date date;
	
	// static Ŭ������ ��� ���� ����ϴ� URL�� DB_path�� ���ȭ ��Ų��.
	static class K {
		protected static final String DB_URL = "https://github.com/maestrosuji/godic/blob/master/dic.db?raw=true";
		protected static final String DB_UURL = "https://github.com/maestrosuji/godic/commits/master/dic.db";
		
		public static String getDbAbsolutePath(StartActivity splashActivity) {
			return "/data/data/"+splashActivity.getPackageName()+"/databases/dic.db";
		}

		public static String getDbDirPath(StartActivity splashActivity) {
			// TODO Auto-generated method stub
//			return Environment.getDataDirectory().getAbsolutePath() + "/data/"
//					+ splashActivity.getPackageName() + "/dic.db";
			return "/data/data/"+splashActivity.getPackageName()+"/databases";
		};
	}

	@SuppressLint("InflateParams") @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        LayoutInflater inflater = LayoutInflater.from(this);
        LinearLayout layout = (LinearLayout) inflater.inflate(
            R.layout.godic_intro, null, false);
        
        addContentView(layout, new LayoutParams(LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT));
        
        /**/
    
        dbinfodirpath = getFilesDir().getPath()+"/info";
    	dbinfofilepath = dbinfodirpath+"/dbinfo";
    	date = new Date();
		// TODO onCreate()���� �ؾ��� �ϵ�
		initDB();		
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.obj instanceof StartActivity) {
				Activity activity = (Activity) msg.obj;
				switch (msg.what) {
				case WHAT_DB_RECOVERY:
					activity.showDialog(DIALOG_DB_RECOVERY, null);
					break;
				case WHAT_DB_UPDATE:
					activity.showDialog(DIALOG_DB_UPDATE, null);
					break;
				case WHAT_DB_SKIP:
					startMainActivity();
					break;
				case WHAT_DB_CHECK_ERROR:
					activity.showDialog(DIALOG_DB_CHECK_ERROR, null);
					break;
				case WHAT_DB_DOWNLOAD_ERROR:
					activity.showDialog(DIALOG_DB_FAIL_AND_RETRY, null);
					break;
				case WHAT_DB_DOWNLOADED:					
					startMainActivity();					
					break;
				}
			}
		}
	};

	protected void startMainActivity() {
		// TODO ���� ȭ������ ����
		final Handler handler = new Handler();
        handler.postDelayed(new Runnable()
        {
        	@Override
            public void run()
            {
                Intent intent = new Intent(StartActivity.this,ScanActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_MILLIS);
    }

	@SuppressWarnings("deprecation")
	private void initDB() {
		final File dbFile = new File(K.getDbAbsolutePath(StartActivity.this));
		Log.e("@@@@package name", StartActivity.this.getPackageName() + "");

		if (dbFile.exists()) {

			// ������ ���� ���, DB�� ���¸� Ȯ���Ѵ�. SELECT���� ������ �����Ͱ� ������� �ʴ´ٸ� DB���Ͽ� �̻��� ����
			// ���̹Ƿ� �ٽ� �ٿ�ε� �޴´�.
			db = SQLiteDatabase.openDatabase(
					K.getDbAbsolutePath(StartActivity.this), null,
					SQLiteDatabase.OPEN_READONLY
							| SQLiteDatabase.NO_LOCALIZED_COLLATORS);
			Cursor cursor = db.rawQuery("SELECT * FROM dic LIMIT 1, 10",
					null);
			// SELECT���� ���ڵ带 ��ã�� ��� count�� -1�̴�.
			if (!db.isOpen() || cursor.getCount() <= 0) {
				// DB�� �������̶�� ���ο� DB�� �ޱ� �� ����ڿ��� ��ȭ���ڷ� �˸���.
				Message msg = Message.obtain();
				msg.obj = this;
				msg.what = WHAT_DB_RECOVERY;
				handler.sendMessage(msg);
				return;
			} else {
				
				if (date.getDay() != 1) {
					Log.e("@@@getDay()",date.getDay()+"");
					Message msg = Message.obtain();
					msg.obj = StartActivity.this;
					msg.what = WHAT_DB_SKIP;
					handler.sendMessage(msg);
				} else {

					// ��Ʈ��ũ ó���� ���� Thread ����
					Thread thread = new Thread(new Runnable() {
						@Override
						public void run() {
							try {

								URL url = new URL(K.DB_URL);
								// �ش� URL�� �����Ѵ�.
								URLConnection conn = url.openConnection();
								// �ش� ��ο� ��ġ�� ����� ���� �ֱٿ� ������ ��¥��
								// �޾ƿ´�.(Millisecond)
								// lastModified ������ long ���·� ����Ǿ�� �Ѵ�. Ŭ���� �����.
								// lastModified = conn.getLastModified();
								lastDBsize = conn.getContentLength();
								// �޾ƿ� ���� 0���� ũ�� ����� ��������Ƿ� ����ó���Ѵ�.
								// Log.e("@@@lastModified", lastModified + "");
								Log.e("@@@lastDBsize", conn.getContentLength()
										+ "");

								if (lastDBsize > 0) {

									// �ܸ��� ���� ����� DB�� �ֽ� ���� ��¥�� ��ȸ�ϱ� ���� �����Ѵ�.
									FileInputStream fisdb = null;
									ObjectInputStream oisdb = null;
									fisdb = new FileInputStream(dbinfofilepath);
									oisdb = new ObjectInputStream(fisdb);
									try {
										dbInfo = (DBInfo) oisdb.readObject();
										currentDBsize = dbInfo.InnerDBsize;
										Log.e("@@@currentDBsize", currentDBsize
												+ "");
									} catch (ClassNotFoundException e) {
										e.printStackTrace();
									}
									oisdb.close();
									fisdb.close();

									// ����� DB�� ���� ��¥�� ��ȸ�� ������ ���� ��¥�� ���Ѵ�.
									if (lastDBsize != currentDBsize) {
										Log.e("@@@currentDBsize", currentDBsize
												+ "");
										Message msg = Message.obtain();
										msg.obj = StartActivity.this;
										msg.what = WHAT_DB_UPDATE;
										handler.sendMessage(msg);
										// ���� DB�� ���ŵ��� �ʾҴٸ� ����ȭ������ �Ѿ��. (�� �ٷ�
										// ���)
									} else {
										Message msg = Message.obtain();
										msg.obj = StartActivity.this;
										msg.what = WHAT_DB_SKIP;
										handler.sendMessage(msg);
									}

								} else {
									// �ƴϸ� (lastModified�� 0�̸�) ������ �˸���.
									Message msg = Message.obtain();
									msg.obj = StartActivity.this;
									msg.what = WHAT_DB_CHECK_ERROR;
									handler.sendMessageDelayed(msg, 1000);
								}

							} catch (MalformedURLException malformedUrlE) {
								malformedUrlE.printStackTrace();
								Message msg = Message.obtain();
								msg.obj = StartActivity.this;
								msg.what = WHAT_DB_CHECK_ERROR;
								handler.sendMessageDelayed(msg, 1000);
							} catch (IOException ioE) {
								ioE.printStackTrace();
								Message msg = Message.obtain();
								msg.obj = StartActivity.this;
								msg.what = WHAT_DB_CHECK_ERROR;
								handler.sendMessageDelayed(msg, 1000);
							}
						}
					});
					thread.start();
				}
			}
			// ����� DB�� Ŀ���� �ݾ��ش�.
			if (cursor != null) {
				cursor.close();
			}
			if (db != null) {
				db.close();
			}
			// ������ ���� ��� DB ���� ��ġ �������� �����Ѵ�.
		} else {
			showDialog(DIALOG_DB_FIRST);
		}
	}

	@SuppressWarnings("deprecation")
	private void downloadDB() {
		// �����ޱ� �����Ȳ�� �����ֱ� ���� ProgressDialog�� ����.
		showDialog(DIALOG_DB_PROGRESS_DOWNLOAD);
		// �����ޱ� �ϷḦ �˸��� Toast�޼����� �����.
		toast = Toast.makeText(this, "DOWNLOAD COMPLETE.", Toast.LENGTH_LONG);
		
		Thread thread = new Thread(new Runnable() {
			private int totalLength = 0;

			@Override
			public void run() {
				InputStream is = null;
				FileOutputStream fos = null;
				BufferedOutputStream bos = null;
				URL url;
				URLConnection conn;
				try {
					// ������ DB ��η� URL ��ü�� �����Ѵ�.
					url = new URL(K.DB_URL);
					Log.e("@@@DB_url", url.getFile());
					// �ش� URL�� �����Ѵ�.
					conn = url.openConnection();					
					conn.connect();
					// ������ �ִ� DB ������ �뷮�� �޾ƿ´�.
					int lengthOnServer = conn.getContentLength();					
					Log.e("@@@DB_size", lengthOnServer+"");
					// �뷮�� 0���� ũ�� ����� ����Ǿ��ٰ� �Ǵ��ϰ� ���� �����Ѵ�.
					if (lengthOnServer > 0) {
						progressDialog.setMax(lengthOnServer);
					} else {
						// // �뷮�� 0���� ũ�� ���� ��� �������� ǥ���Ѵ�.
						Message msg = Message.obtain();
						msg.obj = StartActivity.this;
						msg.what = WHAT_DB_DOWNLOAD_ERROR;
						handler.sendMessageDelayed(msg, 1000);
						return;
					}
					if (lastModified == 0) {
						lastModified = conn.getLastModified();
						Log.e("@@@LastModified", conn.getLastModified()+"");
					}
					if (lastDBsize == 0){
						dbInfo.InnerDBsize = conn.getContentLength();
						lastDBsize = conn.getContentLength();
						
						FileOutputStream fosDB = null;
						ObjectOutputStream oosDB = null;
						
						File dir = new File(dbinfodirpath);
						if (!dir.exists()) {
							dir.mkdir();
						}
						fosDB = new FileOutputStream(dbinfofilepath);
						oosDB = new ObjectOutputStream(fosDB);
						oosDB.writeObject(dbInfo);
						
						oosDB.close();
						fosDB.close();
					}
					
					// �ٿ�ε� ���� ������ �ý��ۿ� �����ϱ� ���� InputStream�� ��´�.
					is = conn.getInputStream();

					// DB ������ ������ ��η� File ��ü�� �����Ѵ�. (���� ��θ�)
					// ������ ��� K.getDbDirPath()�� ȣ���ϸ� �Ʒ� ��θ� ��ȯ�ϵ��� �ۼ��Ͽ���.
					// Environment.getDataDirectory().getAbsolutePath() +
					// "/data/" + getPackageName() + "/mydb.sqlite"
					File dir = new File(K.getDbDirPath(StartActivity.this));
					// ���� ������ ���ٸ� �����Ѵ�.
					if (!dir.exists()) {
						dir.mkdir();
					}
					// ������ο� ���ϸ���� ���� ��η� File ��ü�� �����Ѵ�.
					
					File target = new File(
							K.getDbAbsolutePath(StartActivity.this));
					// ������ �����Ѵٸ� �����Ѵ�.
					if (target.exists()) {
						target.delete();
					}
					// �׸��� ���ο� ������ �����Ѵ�.
					target.createNewFile();

					// �Ʒ��� ������ ���Ͽ� ������ DB�� ���� �����̴�.
					fos = new FileOutputStream(target);
					bos = new BufferedOutputStream(fos);
					int bufferLength = 0;
					// �ٿ�ε尡 ����Ǵµ��� �ٿ�ε�� ũ�⸦ �����ϴ� �ɹ� ������.
					totalLength = 0;
					// ���� ũ�Ⱑ Ŭ ���� �ٿ�ε�� ��������. ������ �޸𸮸� ���� ��� �ִٴ� ���� �������.
					byte[] buffer = new byte[1024];
					while ((bufferLength = is.read(buffer)) > 0) {
						// bos.write()�� ������ �� �� �ִ�.
						bos.write(buffer, 0, bufferLength);
						// �� ���� ���� ����Ѵ�.
						totalLength += bufferLength;
						//ftpUtill.DownloadContents("dic");

						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								// ���� ���� ��ȭâ�� ���෮�� ������Ų��.
								progressDialog.setProgress(totalLength);
							}
						});
						// sleep()���� �����غ��ø� �ƽð�����, ���� �� �ӵ��� �������� ����ڿ��� ���������
						// �����ֱ� ���� ��Ȳ�� �߻��Ѵ�. ���� ������ �ε巴�� �������� �ʴ´�. ������ ������ �ٿ�ε尡
						// ��������.
						Thread.sleep(1);
					}
					
					// ���������� 100% �޼��� �����ֱ� ���� ������¸� �ִ�ġ�� �Է��Ѵ�.
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							progressDialog.setProgress(progressDialog.getMax());
						}
					});

					// �������� �뷮�� ������ �ִ� DB ������ �뷮�� ���� ������ ������ ǥ���Ѵ�.
					if (totalLength != lengthOnServer) {
						Message msg = Message.obtain();
						msg.obj = StartActivity.this;
						msg.what = WHAT_DB_DOWNLOAD_ERROR;
						handler.sendMessageDelayed(msg, 1000);
						return;
					}		
				
					// �ٿ�ε尡 �Ϸ�� �� �ൿ�� ���ϱ� ���� Handler�� Message�� �����Ѵ�.
					Message msg = Message.obtain();
					msg.obj = StartActivity.this;
					msg.what = WHAT_DB_DOWNLOADED;
					handler.sendMessageDelayed(msg, 1000);	
					
				
				} catch (MalformedURLException malformdUrlE) {
					malformdUrlE.printStackTrace();
					Message msg = Message.obtain();
					msg.obj = StartActivity.this;
					msg.what = WHAT_DB_DOWNLOAD_ERROR;
					handler.sendMessageDelayed(msg, 1000);
				} catch (IOException ioE) {
					ioE.printStackTrace();
					Message msg = Message.obtain();
					msg.obj = StartActivity.this;
					msg.what = WHAT_DB_DOWNLOAD_ERROR;
					handler.sendMessageDelayed(msg, 1000);
				} catch (InterruptedException interruptedE) {
					interruptedE.printStackTrace();
				} finally {
					// ���������� ����ߴ� Stream���� �����Ѵ�.
					try {
						if (bos != null)
							bos.close();
						if (fos != null)
							fos.close();
						if (is != null)
							is.close();
						progressDialog.dismiss();
						toast.show();
						
						
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

		});
		thread.start();
	}

	@SuppressWarnings("deprecation")
	@Override
	protected Dialog onCreateDialog(int id, Bundle args) {
		switch (id) {
		case DIALOG_DB_FIRST:
			return new AlertDialog.Builder(this)
					.setTitle(R.string.title_alert_db)
					.setMessage(R.string.message_alert_db_first)
					.setPositiveButton(R.string.download,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									downloadDB();
								}
							})
					.setNegativeButton(R.string.exit,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									finish();
								}
							}).create();
		case DIALOG_DB_RECOVERY:
			return new AlertDialog.Builder(this)
					.setTitle(R.string.title_alert_db)
					.setMessage(R.string.message_alert_db_recovery)
					.setPositiveButton(R.string.download,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									downloadDB();
								}
							})
					.setNegativeButton(R.string.exit,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									finish();
								}
							}).create();
		case DIALOG_DB_UPDATE:
			return new AlertDialog.Builder(this)
					.setTitle(R.string.title_alert_db)
					.setMessage(R.string.message_alert_db_update)
					.setPositiveButton(R.string.download,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									downloadDB();
								}
							})
					.setNegativeButton(R.string.later,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									startMainActivity();
								}
							}).create();
		case DIALOG_DB_CHECK_ERROR:
			return new AlertDialog.Builder(this)
					.setTitle(R.string.title_alert_db)
					.setMessage(R.string.message_alert_db_check_error)
					.setPositiveButton(R.string.download,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									downloadDB();
								}
							})
					.setNegativeButton(R.string.exit,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									finish();
								}
							}).create();
		case DIALOG_DB_PROGRESS_DOWNLOAD:
			progressDialog = new ProgressDialog(StartActivity.this);
			progressDialog.setTitle(R.string.title_alert_db);
			progressDialog
					.setMessage(getString(R.string.message_progress_download));
			progressDialog.setIndeterminate(false);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			return progressDialog;
		case DIALOG_DB_FAIL_AND_RETRY:
			return new AlertDialog.Builder(this)
					.setTitle(R.string.title_alert_db)
					.setMessage(R.string.message_alert_db_download_fail)
					.setPositiveButton(R.string.retry,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									downloadDB();
								}
							})
					.setNegativeButton(R.string.exit,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									finish();
								}
							}).create();
		}
		return super.onCreateDialog(id, args);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onPrepareDialog(int id, Dialog dialog, Bundle args) {
		switch (id) {
		case DIALOG_DB_FIRST:
			((AlertDialog) dialog)
					.setMessage(getString(R.string.message_alert_db_first));
			((AlertDialog) dialog).setButton(Dialog.BUTTON_POSITIVE,
					getString(R.string.download),
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							downloadDB();
						}
					});
			((AlertDialog) dialog).setButton(Dialog.BUTTON_NEGATIVE,
					getString(R.string.exit),
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							finish();
						}
					});
			break;
		case DIALOG_DB_RECOVERY:
			((AlertDialog) dialog)
					.setMessage(getString(R.string.message_alert_db_recovery));
			((AlertDialog) dialog).setButton(Dialog.BUTTON_POSITIVE,
					getString(R.string.download),
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							downloadDB();
						}
					});
			((AlertDialog) dialog).setButton(Dialog.BUTTON_NEGATIVE,
					getString(R.string.exit),
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							finish();
						}
					});
			break;
		case DIALOG_DB_UPDATE:
			((AlertDialog) dialog)
					.setMessage(getString(R.string.message_alert_db_update));
			((AlertDialog) dialog).setButton(Dialog.BUTTON_POSITIVE,
					getString(R.string.download),
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							downloadDB();
						}
					});
			((AlertDialog) dialog).setButton(Dialog.BUTTON_NEGATIVE,
					getString(R.string.later),
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							startMainActivity();
						}
					});
			break;
		case DIALOG_DB_CHECK_ERROR:
			((AlertDialog) dialog)
					.setMessage(getString(R.string.message_alert_db_check_error));
			((AlertDialog) dialog).setButton(Dialog.BUTTON_POSITIVE,
					getString(R.string.download),
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							downloadDB();
						}
					});
			((AlertDialog) dialog).setButton(Dialog.BUTTON_NEGATIVE,
					getString(R.string.exit),
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							finish();
						}
					});
			break;
		case DIALOG_DB_PROGRESS_DOWNLOAD:
			progressDialog.setTitle(R.string.title_alert_db);
			progressDialog
					.setMessage(getString(R.string.message_progress_download));
			progressDialog.setIndeterminate(false);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			break;
		case DIALOG_DB_FAIL_AND_RETRY:
			((AlertDialog) dialog)
					.setMessage(getString(R.string.message_alert_db_download_fail));
			((AlertDialog) dialog).setButton(Dialog.BUTTON_POSITIVE,
					getString(R.string.download),
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							downloadDB();
						}
					});
			((AlertDialog) dialog).setButton(Dialog.BUTTON_NEGATIVE,
					getString(R.string.exit),
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							finish();
						}
					});
			break;
		}
		super.onPrepareDialog(id, dialog, args);
	}
}