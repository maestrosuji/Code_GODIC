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
	
	// static 클래스를 잡아 자주 사용하는 URL과 DB_path를 상수화 시킨다.
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
		// TODO onCreate()에서 해야할 일들
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
		// TODO 다음 화면으로 진행
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

			// 파일이 있을 경우, DB의 상태를 확인한다. SELECT문을 날려서 데이터가 날라오지 않는다면 DB파일에 이상이 생긴
			// 것이므로 다시 다운로드 받는다.
			db = SQLiteDatabase.openDatabase(
					K.getDbAbsolutePath(StartActivity.this), null,
					SQLiteDatabase.OPEN_READONLY
							| SQLiteDatabase.NO_LOCALIZED_COLLATORS);
			Cursor cursor = db.rawQuery("SELECT * FROM dic LIMIT 1, 10",
					null);
			// SELECT문이 레코드를 못찾을 경우 count는 -1이다.
			if (!db.isOpen() || cursor.getCount() <= 0) {
				// DB가 비정상이라면 새로운 DB를 받기 전 사용자에게 대화상자로 알린다.
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

					// 네트워크 처리를 위해 Thread 생성
					Thread thread = new Thread(new Runnable() {
						@Override
						public void run() {
							try {

								URL url = new URL(K.DB_URL);
								// 해당 URL로 연결한다.
								URLConnection conn = url.openConnection();
								// 해당 경로에 위치한 대상의 가장 최근에 수정된 날짜를
								// 받아온다.(Millisecond)
								// lastModified 변수는 long 형태로 선언되어야 한다. 클래스 멤버다.
								// lastModified = conn.getLastModified();
								lastDBsize = conn.getContentLength();
								// 받아온 수가 0보다 크면 제대로 연결됐으므로 정상처리한다.
								// Log.e("@@@lastModified", lastModified + "");
								Log.e("@@@lastDBsize", conn.getContentLength()
										+ "");

								if (lastDBsize > 0) {

									// 단말기 내에 저장된 DB의 최신 변경 날짜를 조회하기 위해 생성한다.
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

									// 저장된 DB의 수정 날짜를 조회해 서버의 수정 날짜와 비교한다.
									if (lastDBsize != currentDBsize) {
										Log.e("@@@currentDBsize", currentDBsize
												+ "");
										Message msg = Message.obtain();
										msg.obj = StartActivity.this;
										msg.what = WHAT_DB_UPDATE;
										handler.sendMessage(msg);
										// 서버 DB가 갱신되지 않았다면 메인화면으로 넘어간다. (앱 바로
										// 사용)
									} else {
										Message msg = Message.obtain();
										msg.obj = StartActivity.this;
										msg.what = WHAT_DB_SKIP;
										handler.sendMessage(msg);
									}

								} else {
									// 아니면 (lastModified가 0이면) 오류를 알린다.
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
			// 사용한 DB와 커서를 닫아준다.
			if (cursor != null) {
				cursor.close();
			}
			if (db != null) {
				db.close();
			}
			// 파일이 없을 경우 DB 최초 설치 과정으로 진행한다.
		} else {
			showDialog(DIALOG_DB_FIRST);
		}
	}

	@SuppressWarnings("deprecation")
	private void downloadDB() {
		// 내려받기 진행상황을 보여주기 위한 ProgressDialog를 띄운다.
		showDialog(DIALOG_DB_PROGRESS_DOWNLOAD);
		// 내려받기 완료를 알리는 Toast메세지를 만든다.
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
					// 서버의 DB 경로로 URL 객체를 생성한다.
					url = new URL(K.DB_URL);
					Log.e("@@@DB_url", url.getFile());
					// 해당 URL로 연결한다.
					conn = url.openConnection();					
					conn.connect();
					// 서버에 있는 DB 파일의 용량을 받아온다.
					int lengthOnServer = conn.getContentLength();					
					Log.e("@@@DB_size", lengthOnServer+"");
					// 용량이 0보다 크면 제대로 연결되었다고 판단하고 정상 진행한다.
					if (lengthOnServer > 0) {
						progressDialog.setMax(lengthOnServer);
					} else {
						// // 용량이 0보다 크지 않을 경우 오류임을 표시한다.
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
					
					// 다운로드 받은 파일을 시스템에 저장하기 위해 InputStream을 얻는다.
					is = conn.getInputStream();

					// DB 파일을 저장할 경로로 File 객체를 생성한다. (폴더 경로만)
					// 본인의 경우 K.getDbDirPath()를 호출하면 아래 경로를 반환하도록 작성하였다.
					// Environment.getDataDirectory().getAbsolutePath() +
					// "/data/" + getPackageName() + "/mydb.sqlite"
					File dir = new File(K.getDbDirPath(StartActivity.this));
					// 만약 폴더가 없다면 생성한다.
					if (!dir.exists()) {
						dir.mkdir();
					}
					// 폴더경로에 파일명까지 붙인 경로로 File 객체를 생성한다.
					
					File target = new File(
							K.getDbAbsolutePath(StartActivity.this));
					// 파일이 존재한다면 삭제한다.
					if (target.exists()) {
						target.delete();
					}
					// 그리고 새로운 파일을 생성한다.
					target.createNewFile();

					// 아래는 생성한 파일에 서버의 DB를 쓰는 과정이다.
					fos = new FileOutputStream(target);
					bos = new BufferedOutputStream(fos);
					int bufferLength = 0;
					// 다운로드가 진행되는동안 다운로드된 크기를 축적하는 맴버 변수다.
					totalLength = 0;
					// 버퍼 크기가 클 수록 다운로드는 빨라진다. 하지만 메모리를 많이 잡고 있다는 것을 명심하자.
					byte[] buffer = new byte[1024];
					while ((bufferLength = is.read(buffer)) > 0) {
						// bos.write()로 파일을 쓸 수 있다.
						bos.write(buffer, 0, bufferLength);
						// 총 받은 양을 기록한다.
						totalLength += bufferLength;
						//ftpUtill.DownloadContents("dic");

						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								// 진행 상태 대화창에 진행량을 증가시킨다.
								progressDialog.setProgress(totalLength);
							}
						});
						// sleep()없이 진행해보시면 아시겠지만, 없을 때 속도는 빠를지라도 사용자에게 명시적으로
						// 보여주기 힘든 상황이 발생한다. 진행 과정이 부드럽게 보여지지 않는다. 하지만 없으면 다운로드가
						// 빨라진다.
						Thread.sleep(1);
					}
					
					// 마지막으로 100% 달성을 보여주기 위해 진행상태를 최대치로 입력한다.
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							progressDialog.setProgress(progressDialog.getMax());
						}
					});

					// 내려받은 용량과 서버에 있는 DB 파일의 용량이 같지 않으면 오류를 표시한다.
					if (totalLength != lengthOnServer) {
						Message msg = Message.obtain();
						msg.obj = StartActivity.this;
						msg.what = WHAT_DB_DOWNLOAD_ERROR;
						handler.sendMessageDelayed(msg, 1000);
						return;
					}		
				
					// 다운로드가 완료된 후 행동을 취하기 위해 Handler에 Message를 전달한다.
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
					// 최종적으로 사용했던 Stream들을 정리한다.
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