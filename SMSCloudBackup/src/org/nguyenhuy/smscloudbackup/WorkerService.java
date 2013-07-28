package org.nguyenhuy.smscloudbackup;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.widget.Toast;
import com.dropbox.sync.android.*;
import com.google.gson.Gson;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Author: Huy Nguyen
 * Date: 7/28/13
 * Time: 11:34 AM
 * <p/>
 * A worker service that performs backup and restore operations.
 */
public class WorkerService extends IntentService {

    public static final String EXTRA_OPERATION = "operation";
    public static final int OPERATION_BACKUP = 1;
    public static final int OPERATION_RESTORE = 2;

    public WorkerService() {
        super("WorkerService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        DbxAccountManager accountManager = DbxAccountManager.getInstance(
                getApplicationContext(),
                getString(R.string.dbx_api_key),
                getString(R.string.dbx_api_secret));
        try {
            DbxFileSystem fileSystem = DbxFileSystem.forAccount(accountManager.getLinkedAccount());

            int operation = intent.getIntExtra(EXTRA_OPERATION, 0);
            switch (operation) {
                case OPERATION_BACKUP:
                    backup(fileSystem);
                    break;
                case OPERATION_RESTORE:
                    restore(fileSystem);
                    break;
                default:
                    throw new UnsupportedOperationException("Operation with code " +
                            operation + "is not supported.");
            }
        } catch (DbxException.Unauthorized unauthorized) {
            Toast.makeText(this, unauthorized.getLocalizedMessage(), Toast.LENGTH_LONG)
                    .show();
            unauthorized.printStackTrace();
        } catch (DbxException e) {
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_LONG)
                    .show();
            e.printStackTrace();
        } catch (IOException e) {
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_LONG)
                    .show();
            e.printStackTrace();
        }
    }

    private void backup(DbxFileSystem fileSystem) throws DbxException, IOException {
        Toast.makeText(this, R.string.backing_up, Toast.LENGTH_LONG)
                .show();
        DbxFile file = fileSystem.create(new DbxPath("huy" + System.currentTimeMillis() + ".json"));
        try {
            List<ContentValues> result = SmsHelper.loadAllSMSes(getContentResolver());
            String json = new Gson().toJson(result);
            file.writeString(json);
        } finally {
            file.close();
        }
    }

    private void restore(DbxFileSystem fileSystem) {
        Toast.makeText(this, R.string.restoring, Toast.LENGTH_LONG)
                .show();
    }
}
