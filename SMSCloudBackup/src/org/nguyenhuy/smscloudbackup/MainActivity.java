package org.nguyenhuy.smscloudbackup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.dropbox.sync.android.DbxAccountManager;

public class MainActivity extends Activity {

    private static final int REQUEST_LINK_TO_DBX = 1;

    private static final String EXTRA_PENDING_ACTION = "pending_action";
    private static final int PENDING_ACTION_BACKUP = 1;
    private static final int PENDING_ACTION_RESTORE = 2;


    private DbxAccountManager mDbxAccountManager;
    private int mPendingAction;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_backup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backup();
            }
        });
        findViewById(R.id.btn_restore).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                restore();
            }
        });

        mDbxAccountManager = DbxAccountManager.getInstance(
                getApplicationContext(),
                getString(R.string.dbx_api_key),
                getString(R.string.dbx_api_secret));

        if (savedInstanceState != null) {
            mPendingAction = savedInstanceState.getInt(EXTRA_PENDING_ACTION);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_LINK_TO_DBX) {
            if (resultCode == RESULT_OK) {
                if (mPendingAction == PENDING_ACTION_BACKUP) {
                    startBackupService();
                } else if (mPendingAction == PENDING_ACTION_RESTORE) {
                    startRestoreService();
                } else {
                    Toast.makeText(
                            this,
                            R.string.no_pending_action_after_linking,
                            Toast.LENGTH_LONG)
                            .show();
                }
            } else {
                Toast.makeText(
                        this,
                        R.string.link_dbx_account_failed,
                        Toast.LENGTH_LONG)
                        .show();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(EXTRA_PENDING_ACTION, mPendingAction);
    }

    private void backup() {
        if (mDbxAccountManager.hasLinkedAccount()) {
            startBackupService();
        } else {
            // link to user's account. onActivityResult() will be called when
            // linking complete.
            mPendingAction = PENDING_ACTION_BACKUP;
            mDbxAccountManager.startLink(this, REQUEST_LINK_TO_DBX);
        }
    }

    private void restore() {
        if (mDbxAccountManager.hasLinkedAccount()) {
            startRestoreService();
        } else {
            // link to user's account. onActivityResult() will be called when
            // linking complete.
            mPendingAction = PENDING_ACTION_RESTORE;
            mDbxAccountManager.startLink(this, REQUEST_LINK_TO_DBX);
        }
    }

    private void startBackupService() {
        Intent i = new Intent(this, WorkerService.class);
        i.putExtra(WorkerService.EXTRA_OPERATION, WorkerService.OPERATION_BACKUP);
        startService(i);
    }

    private void startRestoreService() {
        Intent i = new Intent(this, WorkerService.class);
        i.putExtra(WorkerService.EXTRA_OPERATION, WorkerService.OPERATION_RESTORE);
        startService(i);
    }
}
