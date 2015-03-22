package com.ipang.wansha.activity;

import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.ipang.wansha.R;
import com.ipang.wansha.adapter.DownloadListAdapter;
import com.ipang.wansha.adapter.DownloadListAdapter.DownloadListListener;
import com.ipang.wansha.adapter.OptionMenuAdapter;
import com.ipang.wansha.dao.OfflineDao;
import com.ipang.wansha.dao.impl.OfflineDaoImpl;
import com.ipang.wansha.model.Download;
import com.ipang.wansha.service.OfflineGuideDownloadService;
import com.ipang.wansha.utils.Const;

public class DownloadListActivity extends Activity {

	private ActionBar actionBar;
	private ListView downloadList;
	private DownloadListAdapter adapter;
	private List<Download> downloads;
	private OfflineDao offlineDao;
	private DownloadProgressReceiver receiver;
	private OfflineGuideDownloadService downloadService;
	private OptionMenuAdapter optionAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_download_list);
		setActionBar();
		bindService();
	}

	private void bindService() {
		Intent intent = new Intent(DownloadListActivity.this,
				OfflineGuideDownloadService.class);
		bindService(intent, conn, Context.BIND_AUTO_CREATE);
	}

	private ServiceConnection conn = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {

		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			downloadService = ((OfflineGuideDownloadService.DownloadBinder) service)
					.getService();
			setViews();
			registerReceiver();
		}
	};

	private void registerReceiver() {
		receiver = new DownloadProgressReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Const.DOWNLOADRECEIVER);
		registerReceiver(receiver, intentFilter);
	}

	private void setActionBar() {
		actionBar = this.getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(getResources().getString(R.string.is_downloading));
		actionBar.setDisplayUseLogoEnabled(false);
	}

	private void setViews() {
		offlineDao = new OfflineDaoImpl();

		downloadList = (ListView) findViewById(R.id.list_download);

		downloads = offlineDao.getDownloadList(DownloadListActivity.this);

		if (downloads != null && downloads.size() > 0
				&& downloadService != null) {
			Download download = downloads.get(0);
			if (download.getStatus() == Download.STARTED) {
				download.setFileSize(downloadService.getFileSize(download
						.getProductId()));
				download.setDownloadedSize(downloadService
						.getDownloadedSize(download.getProductId()));
			}
		}

		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		int height = metric.widthPixels / 5;

		adapter = new DownloadListAdapter(this, downloads, height);
		downloadList.setAdapter(adapter);

		adapter.setDownloadListListener(new DownloadListListener() {

			@Override
			public void delete(int position) {
				final int status = downloads.get(position).getStatus();
				if (status == Download.STARTED || status == Download.NOTSTARTED) {
					downloadService.stopDownload(downloads.get(position)
							.getProductId());
				} else if (status == Download.STOPPED
						|| status == Download.ERROR) {
					offlineDao.rollback(DownloadListActivity.this, downloads
							.get(position).getProductId());
				}
				offlineDao.removeDownload(DownloadListActivity.this, downloads
						.get(position).getProductId());
				Log.d(Const.DOWNLOAD,
						"Project ID "
								+ downloads.get(position).getProductId()
								+ " cancelled downloading. --> ACTIVITY STATUS: REMOVED");
				downloads.remove(position);
				adapter.notifyDataSetChanged();
			}
		});

		downloadList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				int status = downloads.get((int) id).getStatus();
				Log.d(Const.DOWNLOAD, "Click on Download List ID: " + id
						+ ", Product ID: "
						+ downloads.get((int) id).getProductId()
						+ ", Status is" + status);

				if (status == Download.STARTED) {
					downloadService.stopDownload(downloads.get((int) id)
							.getProductId());
					downloads.get((int) id).setStatus(Download.ISSTOPPING);
					Log.d(Const.DOWNLOAD,
							"Project ID "
									+ downloads.get((int) id).getProductId()
									+ " has send stopping request. --> ACTIVITY STATUS: ISSTOPPING");
				} else if (status == Download.NOTSTARTED) {
					downloadService.stopDownload(downloads.get((int) id)
							.getProductId());
					downloads.get((int) id).setStatus(Download.STOPPED);
					Log.d(Const.DOWNLOAD,
							"Project ID "
									+ downloads.get((int) id).getProductId()
									+ " has not started. --> ACTIVITY STATUS: STOPPED");
				} else if (status == Download.STOPPED
						|| status == Download.ERROR) {
					offlineDao.startDownloadService(DownloadListActivity.this,
							downloads.get((int) id).getProductId());
					downloads.get((int) id).setStatus(Download.NOTSTARTED);
					Log.d(Const.DOWNLOAD,
							"Project ID "
									+ downloads.get((int) id).getProductId()
									+ " restart downloading. --> ACTIVITY STATUS: NOTSTARTED");
				}
				adapter.notifyDataSetChanged();
			}
		});

		downloadList.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {

				final int index = (int) id;
				final int status = downloads.get((int) id).getStatus();

				String[] options = null;

				if (status == Download.STARTED || status == Download.NOTSTARTED) {
					options = new String[] {
							getResources().getString(R.string.stop_download),
							getResources().getString(R.string.delete_item) };
				} else if (status == Download.STOPPED
						|| status == Download.ERROR) {
					options = new String[] {
							getResources().getString(R.string.start_download),
							getResources().getString(R.string.delete_item) };
				}

				optionAdapter = new OptionMenuAdapter(
						DownloadListActivity.this, options);

				final int opId = downloads.get(index).getProductId();
				Dialog alertDialog = new AlertDialog.Builder(
						DownloadListActivity.this)
						.setTitle(
								getResources()
										.getString(R.string.select_option))
						.setAdapter(optionAdapter, new OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								if (opId == downloads.get(index).getProductId()) {
									switch (which) {
									case 0:
										if (status == Download.STARTED) {
											downloadService.stopDownload(opId);
											downloads.get(index).setStatus(
													Download.ISSTOPPING);
											Log.d(Const.DOWNLOAD,
													"Project ID "
															+ opId
															+ " has send stopping request. --> ACTIVITY STATUS: ISSTOPPING");
										} else if (status == Download.NOTSTARTED) {
											downloadService.stopDownload(opId);
											downloads.get(index).setStatus(
													Download.STOPPED);
											Log.d(Const.DOWNLOAD,
													"Project ID "
															+ opId
															+ " has not started. --> ACTIVITY STATUS: STOPPED");
										} else if (status == Download.STOPPED
												|| status == Download.ERROR) {
											offlineDao.startDownloadService(
													DownloadListActivity.this,
													opId);
											downloads.get(index).setStatus(
													Download.NOTSTARTED);
											Log.d(Const.DOWNLOAD,
													"Project ID "
															+ opId
															+ " restart downloading. --> ACTIVITY STATUS: NOTSTARTED");
										}
										adapter.notifyDataSetChanged();
										break;
									case 1:
										if (status == Download.STARTED
												|| status == Download.NOTSTARTED) {
											downloadService.stopDownload(opId);
										} else if (status == Download.STOPPED
												|| status == Download.ERROR) {
											offlineDao.rollback(
													DownloadListActivity.this,
													opId);
										}
										offlineDao
												.removeDownload(
														DownloadListActivity.this,
														opId);
										downloads.remove(index);
										Log.d(Const.DOWNLOAD,
												"Project ID "
														+ opId
														+ " cancelled downloading. --> ACTIVITY STATUS: REMOVED");
										adapter.notifyDataSetChanged();
										break;
									}
								}
							}
						}).create();
				alertDialog.show();

				return true;
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_download_list, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.finish();
			break;
		case R.id.clear_download_list:
			Dialog alertDialog = new AlertDialog.Builder(
					DownloadListActivity.this)
					.setMessage("是否清空当前下载？")
					.setNegativeButton("取消", null)
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									for (Download download : downloads) {
										int status = download.getStatus();
										if (status == Download.STARTED || status == Download.NOTSTARTED) {
											downloadService.stopDownload(download.getProductId());
										} else if (status == Download.STOPPED
												|| status == Download.ERROR) {
											offlineDao.rollback(DownloadListActivity.this,
													download.getProductId());
										}
										offlineDao.removeDownload(DownloadListActivity.this,
												download.getProductId());
									}
									downloads.clear();
									adapter.notifyDataSetChanged();
								}
							}).create();
			alertDialog.show();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onDestroy() {
		unbindService(conn);
		unregisterReceiver(receiver);
		super.onDestroy();
	}

	private class DownloadProgressReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			String command = intent.getStringExtra(Const.COMMAND);
			int productId = intent.getIntExtra(Const.PRODUCTID, 0);
			if (command.equals(Const.UPDATEFILESIZE)) {
				int size = intent.getIntExtra(Const.FILESIZE, 0);
				System.out.println("Receiver file size = " + size);
				Download download = null;
				for (int i = 0; i < downloads.size(); i++) {
					download = downloads.get(i);
					if (download.getProductId() == productId) {
						download.setFileSize(size);
						break;
					}
				}
			} else if (command.equals(Const.UPDATEPROGRESS)) {
				int progress = intent.getIntExtra(Const.DOWNLOADPROGRESS, 0);
				Download download = null;
				for (int i = 0; i < downloads.size(); i++) {
					download = downloads.get(i);
					if (download.getProductId() == productId) {
						download.setDownloadedSize(progress);
						break;
					}
				}
			} else if (command.equals(Const.DOWNLOADSTART)) {
				Download download = null;
				for (int i = 0; i < downloads.size(); i++) {
					download = downloads.get(i);
					if (download.getProductId() == productId) {
						download.setStatus(Download.STARTED);
						Log.d(Const.DOWNLOAD,
								"Project ID "
										+ productId
										+ " start downloading. --> ACTIVITY STATUS: STARTED");
						break;
					}
				}
			} else if (command.equals(Const.DOWNLOADCOMPLETE)) {
				Download download = null;
				for (int i = 0; i < downloads.size(); i++) {
					download = downloads.get(i);
					if (download.getProductId() == productId) {
						downloads.remove(i);
						Log.d(Const.DOWNLOAD,
								"Project ID "
										+ productId
										+ " complete downloading. --> ACTIVITY STATUS: REMOVED");
						break;
					}
				}
			} else if (command.equals(Const.DOWNLOADCANCELLED)) {
				Download download = null;
				for (int i = 0; i < downloads.size(); i++) {
					download = downloads.get(i);
					if (download.getProductId() == productId) {
						download.setStatus(Download.STOPPED);
						Log.d(Const.DOWNLOAD,
								"Project ID "
										+ productId
										+ " cancelled downloading. --> ACTIVITY STATUS: STOPPED");
						break;
					}
				}
			} else if (command.equals(Const.DOWNLOADERROR)) {
				Download download = null;
				for (int i = 0; i < downloads.size(); i++) {
					download = downloads.get(i);
					if (download.getProductId() == productId) {
						download.setStatus(Download.ERROR);
						Log.d(Const.DOWNLOAD,
								"Project ID "
										+ productId
										+ " has error in downloading. --> ACTIVITY STATUS: ERROR");
						break;
					}
				}
			}
			adapter.notifyDataSetChanged();
		}
	}

}
