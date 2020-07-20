package com.ajithab.RNReceiveSharingIntent;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ReceiveSharingIntentGetFileDirectory {


    @SuppressLint("NewApi")
    public static String getFilePath(Context context, Uri uri) {
        String selection = null;
        String[] selectionArgs = null;
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        if (Build.VERSION.SDK_INT >= 19 && DocumentsContract.isDocumentUri(context.getApplicationContext(), uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                return Environment.getExternalStorageDirectory() + "/" + split[1];
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("image".equals(type)) {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                selection = "_id=?";
                selectionArgs = new String[]{
                        split[1]
                };
            }
        }
        if ("content".equalsIgnoreCase(uri.getScheme())) {


          if (isGooglePhotosUri(uri)) {
            return uri.getLastPathSegment();
          }
          if (isGooglePhotosUriProvider(uri)) {
            InputStream inputStream = null;
            String filePath = null;
            try {
              inputStream = context.getContentResolver().openInputStream(uri);
              File photoFile = createTemporalFileFrom(inputStream,context);

              filePath = photoFile.getPath();
              return filePath;
            } catch (FileNotFoundException e) {

            } catch (IOException e) {

            } finally {
              try {
                if (inputStream != null) {
                  inputStream.close();
                }
              } catch (IOException e) {
                e.printStackTrace();
              }
            }
          }

          String[] projection = {
                    MediaStore.Images.Media.DATA
            };
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver()
                        .query(uri, projection, selection, selectionArgs, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }


  private static File createTemporalFileFrom(InputStream inputStream,Context context) throws IOException {
    File targetFile = null;

    if (inputStream != null) {
      int read;
      byte[] buffer = new byte[8 * 1024];

      targetFile = createTemporalFile(context);
      OutputStream outputStream = new FileOutputStream(targetFile);

      while ((read = inputStream.read(buffer)) != -1) {
        outputStream.write(buffer, 0, read);
      }
      outputStream.flush();

      try {
        outputStream.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    return targetFile;
  }

  private static File createTemporalFile(Context context) {
    return new File(context.getCacheDir(), "tempPicture.jpg");
  }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

  public static boolean isGooglePhotosUri(Uri uri) {
    return "com.google.android.apps.photos.content".equals(uri.getAuthority());
  }

  public static boolean isGooglePhotosUriProvider(Uri uri) {
      return "com.google.android.apps.photos.contentprovider".equals(uri.getAuthority());
  }

}
