package com.photoselector.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore.Images.ImageColumns;
import android.provider.MediaStore.Images.Media;

import com.photoselector.R;
import com.photoselector.model.AlbumModel;
import com.photoselector.model.PhotoModel;

public class AlbumController {

	private ContentResolver resolver;
	public static String RECCENT_PHOTO = null;

	public AlbumController(Context context) {
		resolver = context.getContentResolver();
		RECCENT_PHOTO = context.getResources().getString(R.string.recent_photos);
	}

	/** ������������������������ */
	public List<PhotoModel> getCurrent() {
		Cursor cursor = resolver.query(Media.EXTERNAL_CONTENT_URI, new String[] { ImageColumns.DATA,
				ImageColumns.DATE_ADDED, ImageColumns.SIZE }, null, null, ImageColumns.DATE_ADDED);
		if (cursor == null || !cursor.moveToNext())
			return new ArrayList<PhotoModel>();
		List<PhotoModel> photos = new ArrayList<PhotoModel>();
		cursor.moveToLast();
		do {
			if (cursor.getLong(cursor.getColumnIndex(ImageColumns.SIZE)) > 1024 * 10) {
				PhotoModel photoModel = new PhotoModel();
				photoModel.setOriginalPath(cursor.getString(cursor.getColumnIndex(ImageColumns.DATA)));
				photos.add(photoModel);
			}
		} while (cursor.moveToPrevious());
		return photos;
	}	
	public List<PhotoModel> getCurrentNew(Handler handler) {
		Cursor cursor = resolver.query(Media.EXTERNAL_CONTENT_URI, new String[] { ImageColumns.DATA,
				ImageColumns.DATE_ADDED, ImageColumns.SIZE }, null, null, ImageColumns.DATE_ADDED);
		if (cursor == null || !cursor.moveToNext())
			return new ArrayList<PhotoModel>();
		List<PhotoModel> photos = new ArrayList<PhotoModel>();
		cursor.moveToLast();
		int i = 0;
		int j = 0;
		boolean sent = false;
		do {
			if (cursor.getLong(cursor.getColumnIndex(ImageColumns.SIZE)) > 1024 * 10) {
				PhotoModel photoModel = new PhotoModel();
				photoModel.setOriginalPath(cursor.getString(cursor.getColumnIndex(ImageColumns.DATA)));
				photos.add(photoModel);
			}
			if (++i > 300 && !sent){
				i = 0;
				Message msg = new Message();
				msg.obj = photos;
				msg.arg1 = j;
				handler.sendMessage(msg);
				photos = new ArrayList<PhotoModel>();
				j++;
				sent = true;
			}
		} while (cursor.moveToPrevious());				
		Message msg = new Message();
		msg.obj = photos;
		msg.arg1 = j;
		handler.sendMessage(msg);
		photos = new ArrayList<PhotoModel>();
		return photos;
	}

	/** ������������������������ */
	public List<AlbumModel> getAlbums() {
		List<AlbumModel> albums = new ArrayList<AlbumModel>();
		Map<String, AlbumModel> map = new HashMap<String, AlbumModel>();
		Cursor cursor = resolver.query(Media.EXTERNAL_CONTENT_URI, new String[] { ImageColumns.DATA,
				ImageColumns.BUCKET_DISPLAY_NAME, ImageColumns.SIZE }, null, null, null);
		if (cursor == null || !cursor.moveToNext())
			return new ArrayList<AlbumModel>();
		cursor.moveToLast();
		AlbumModel current = new AlbumModel(RECCENT_PHOTO, 0, cursor.getString(cursor.getColumnIndex(ImageColumns.DATA)), true); // "������������"������
		albums.add(current);
		do {
			if (cursor.getInt(cursor.getColumnIndex(ImageColumns.SIZE)) < 1024 * 10)
				continue;

			current.increaseCount();
			String name = cursor.getString(cursor.getColumnIndex(ImageColumns.BUCKET_DISPLAY_NAME));
			if (map.keySet().contains(name))
				map.get(name).increaseCount();
			else {
				AlbumModel album = new AlbumModel(name, 1, cursor.getString(cursor.getColumnIndex(ImageColumns.DATA)));
				map.put(name, album);
				albums.add(album);
			}
		} while (cursor.moveToPrevious());
		return albums;
	}

	/** ������������������������������ */
	public List<PhotoModel> getAlbum(String name) {
		Cursor cursor = resolver.query(Media.EXTERNAL_CONTENT_URI, new String[] { ImageColumns.BUCKET_DISPLAY_NAME,
				ImageColumns.DATA, ImageColumns.DATE_ADDED, ImageColumns.SIZE }, "bucket_display_name = ?",
				new String[] { name }, ImageColumns.DATE_ADDED);
		if (cursor == null || !cursor.moveToNext())
			return new ArrayList<PhotoModel>();
		List<PhotoModel> photos = new ArrayList<PhotoModel>();
		cursor.moveToLast();
		do {
			if (cursor.getLong(cursor.getColumnIndex(ImageColumns.SIZE)) > 1024 * 10) {
				PhotoModel photoModel = new PhotoModel();
				photoModel.setOriginalPath(cursor.getString(cursor.getColumnIndex(ImageColumns.DATA)));
				photos.add(photoModel);
			}
		} while (cursor.moveToPrevious());
		return photos;
	}
	public List<PhotoModel> getAlbumNew(String name) {
		Cursor cursor = resolver.query(Media.EXTERNAL_CONTENT_URI, new String[] { ImageColumns.BUCKET_DISPLAY_NAME,
				ImageColumns.DATA, ImageColumns.DATE_ADDED, ImageColumns.SIZE }, "bucket_display_name = ?",
				new String[] { name }, ImageColumns.DATE_ADDED);
		if (cursor == null || !cursor.moveToNext())
			return new ArrayList<PhotoModel>();
		List<PhotoModel> photos = new ArrayList<PhotoModel>();
		cursor.moveToLast();
		int i = 0;
		do {
			if (cursor.getLong(cursor.getColumnIndex(ImageColumns.SIZE)) > 1024 * 10) {
				PhotoModel photoModel = new PhotoModel();
				photoModel.setOriginalPath(cursor.getString(cursor.getColumnIndex(ImageColumns.DATA)));
				photos.add(photoModel);
			}
			if (++i >= 300){
				break;
			}
		} while (cursor.moveToPrevious());
		return photos;
	}
}
