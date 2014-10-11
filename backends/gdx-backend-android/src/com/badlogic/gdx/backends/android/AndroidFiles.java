/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.badlogic.gdx.backends.android;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Environment;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

/** @author mzechner
 * @author Nathan Sweet */
public class AndroidFiles implements Files {
	protected String sdcard = "";
	protected boolean legacyWriting = false;
	protected String modernSdcard = "";
	
	protected final String localpath;

	protected final AssetManager assets;
	
	public AndroidFiles (AssetManager assets, Context context) {
		this.assets = assets;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			// Starting in Android API Level 19 'KitKat', external storage changes have been introduced.
			// But not all devices use this change, so we have to improvise. 
			File testFile = new File(Environment.getExternalStorageDirectory(), ".gdxexternaltest");
			
			if (testFile.exists()) {
				testFile.delete();
			}
			
			try {
				this.legacyWriting = testFile.createNewFile();
			} catch (IOException e) {
				this.legacyWriting = false;
			}
			
			try {
				testFile.delete();
			} catch (Exception e) {
				// Ignored.
			}
			
			this.sdcard = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
			
			if (!legacyWriting) {
				// Legacy behavior is not functional, use new behavior.
				File sdcardFolder = context.getExternalFilesDir(null);
				if (sdcardFolder != null) {
					this.sdcard = sdcardFolder.getAbsolutePath();
					if (!sdcardFolder.exists()) {
						sdcardFolder.mkdirs();
					}
				}
			} 		
		} else {
			// Legacy behavior for Android API Level 18 'Jellybean' and below. Allows unrestricted access to the sdcard
			this.sdcard = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
		}
		localpath = sdcard;
	}

	public AndroidFiles (AssetManager assets, String localpath, Context context) {
		this.assets = assets;
		this.localpath = localpath.endsWith("/") ? localpath : localpath + "/";
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			// Starting in Android API Level 19 'KitKat', external storage changes have been introduced.
			// But not all devices use this change, so we have to improvise
			
			File testFile = new File(Environment.getExternalStorageDirectory(), ".gdxexternaltest");
			
			if (testFile.exists()) {
				testFile.delete();
			}
			
			try {
				this.legacyWriting = testFile.createNewFile();
			} catch (IOException e) {
				this.legacyWriting = false;
			}
			
			try {
				testFile.delete();
			} catch (Exception e) {
				// Ignored.
			}
			
			this.sdcard = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
			
			if (!legacyWriting) {
				// Legacy behavior is not functional, use new behavior.
				File sdcardFolder = context.getExternalFilesDir(null);
				if (sdcardFolder != null) {
					this.sdcard = sdcardFolder.getAbsolutePath();
					if (!sdcardFolder.exists()) {
						sdcardFolder.mkdirs();
					}
				}
			} 		
		} else {
			// Legacy behavior for Android API Level 18 'Jellybean' and below. Allows unrestricted access to the sdcard
			this.sdcard = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
		}
	}

	@Override
	public FileHandle getFileHandle (String path, FileType type) {
		return new AndroidFileHandle(type == FileType.Internal ? assets : null, path, type);
	}

	@Override
	public FileHandle classpath (String path) {
		return new AndroidFileHandle(null, path, FileType.Classpath);
	}

	@Override
	public FileHandle internal (String path) {
		return new AndroidFileHandle(assets, path, FileType.Internal);
	}

	@Override
	public FileHandle external (String path) {
		return new AndroidFileHandle(null, path, FileType.External);
	}

	@Override
	public FileHandle absolute (String path) {
		return new AndroidFileHandle(null, path, FileType.Absolute);
	}

	@Override
	public FileHandle local (String path) {
		return new AndroidFileHandle(null, path, FileType.Local);
	}

	@Override
	public String getExternalStoragePath () {
		return sdcard;
	}

	@Override
	public boolean isExternalStorageAvailable () {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}

	@Override
	public String getLocalStoragePath () {
		return localpath;
	}

	@Override
	public boolean isLocalStorageAvailable () {
		return true;
	}
}
