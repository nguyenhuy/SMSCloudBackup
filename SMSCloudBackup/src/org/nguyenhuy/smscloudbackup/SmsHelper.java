package org.nguyenhuy.smscloudbackup;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Huy Nguyen
 * Date: 7/28/13
 * Time: 11:49 AM
 */
public class SmsHelper {
    public static List<ContentValues> loadAllSMSes(ContentResolver contentResolver) {
        Uri allMessages = Uri.parse("content://sms/");
        Cursor cursor = contentResolver.query(allMessages, null, null, null, null);
        List<ContentValues> result = new ArrayList<ContentValues>(cursor.getCount());
        while (cursor.moveToNext()) {
            ContentValues contentValues = new ContentValues();
            DatabaseUtils.cursorRowToContentValues(cursor, contentValues);
            result.add(contentValues);
        }
        return result;
    }
}
