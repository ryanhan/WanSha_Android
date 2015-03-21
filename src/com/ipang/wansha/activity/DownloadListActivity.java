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
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.ipang.wansha.R;
import com.ipang.wansha.adapter.DownloadListAdapter;
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

		downloadList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				int status = downloads.get((int) id).getStatus();
				Log.v(Const.DOWNLOAD, "Click on Download List ID: " + id
						+ ", Product ID: "
						+ downloads.get((int) id).getProductId()
						+ ", Status is" + status);

				if (status == Download.STARTED) {
					downloadService.stopDownload(downloads.get((int) id)
							.getProductId());
					downloads.get((int) id).setStatus(Download.ISSTOPPING);
					Log.v(Const.DOWNLOAD,
							"Project ID "
									+ downloads.get((int) id).getProductId()
									+ " has send stopping request. --> ACTIVITY STATUS: ISSTOPPING");
				} else if (status == Download.NOTSTARTED) {
					downloadService.stopDownload(downloads.get((int) id)
							.getProductId());
					downloads.get((int) id).setStatus(Download.STOPPED);
					Log.v(Const.DOWNLOAD,
							"Project ID "
									+ downloads.get((int) id).getProductId()
									+ " has not started. --> ACTIVITY STATUS: STOPPED");
				} else if (status == Download.STOPPED
						|| status == Download.ERROR) {
					offlineDao.startDownloadService(DownloadListActivity.this,
							downloads.get((int) id).getProductId());
					downloads.get((int) id).setStatus(Download.NOTSTARTED);
					Log.v(Const.DOWNLOAD,
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

				final String[] options = new String[] { getResources()
						.getString(R.string.delete_item) };
				optionAdapter = new OptionMenuAdapter(
						DownloadListActivity.this, options);

				if (id != 0) {
					final int index = (int) id;
					Dialog alertDialog = new AlertDialog.Builder(
							DownloadListActivity.this)
							.setTitle(
									getResources().getString(
											R.string.select_option))
							.setAdapter(optionAdapter, new OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {

								}
							}).create();
					alertDialog.show();

					return true;
				}

				return false;
			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.finish();
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
						Log.v(Const.DOWNLOAD,
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
						Log.v(Const.DOWNLOAD,
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
						Log.v(Const.DOWNLOAD,
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
						Log.v(Const.DOWNLOAD,
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
