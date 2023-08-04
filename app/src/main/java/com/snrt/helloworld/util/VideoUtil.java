package com.snrt.helloworld.util;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import com.snrt.helloworld.video.Video;

public class VideoUtil {
    public static Video getVideoFromCursor(Cursor cursor) {
        int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
        int nameColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME);
        int durationColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION);
        int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE);
        long id = cursor.getLong(idColumn);
        String name = cursor.getString(nameColumn);
        Long duration = cursor.getLong(durationColumn);
        Long size = cursor.getLong(sizeColumn);
        Uri contentUri = ContentUris.withAppendedId(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id);
        Video video = new Video(id, contentUri, name, duration, size);
        return video;
    }
}
