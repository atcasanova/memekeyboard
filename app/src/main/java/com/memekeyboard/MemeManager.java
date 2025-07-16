package com.memekeyboard;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class MemeManager {

    private static final String PREFS_NAME = "MemeKeyboardPrefs";
    private static final String MEME_KEYWORDS_PREFIX = "meme_keywords_";
    private static final String MEME_TYPE_PREFIX = "meme_type_";
    private static final String MEME_FOLDER_NAME = "memes";

    private Context context;
    private SharedPreferences sharedPreferences;
    private File memeFolder;

    public MemeManager(Context context) {
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.memeFolder = new File(context.getFilesDir(), MEME_FOLDER_NAME);
        if (!memeFolder.exists()) {
            memeFolder.mkdirs();
        }
    }

    public String addMeme(Uri memeUri, Set<String> keywords, boolean asSticker) throws IOException {
        String prefix = asSticker ? "sticker_" : "image_";

        // Preserve the original file extension so the resulting file has a
        // proper MIME type when shared through FileProvider.  This helps
        // messaging apps correctly identify the content type instead of
        // treating it as plain text.
        String mimeType = context.getContentResolver().getType(memeUri);
        String extension = null;
        if (mimeType != null) {
            extension = android.webkit.MimeTypeMap.getSingleton()
                    .getExtensionFromMimeType(mimeType);
        }
        if (extension == null) {
            extension = android.webkit.MimeTypeMap.getFileExtensionFromUrl(
                    memeUri.toString());
        }

        String fileName = prefix + UUID.randomUUID().toString();
        if (extension != null && !extension.isEmpty()) {
            fileName += "." + extension;
        }

        File newMemeFile = new File(memeFolder, fileName);

        try (InputStream inputStream = context.getContentResolver().openInputStream(memeUri);
             FileOutputStream outputStream = new FileOutputStream(newMemeFile)) {
            byte[] buffer = new byte[1024];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
        }

        saveMemeKeywords(fileName, keywords);
        if (mimeType != null) {
            saveMemeType(fileName, mimeType);
        }
        return newMemeFile.getAbsolutePath();
    }

    private void saveMemeKeywords(String memeId, Set<String> keywords) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet(MEME_KEYWORDS_PREFIX + memeId, keywords);
        editor.apply();
    }

    private void saveMemeType(String memeId, String mimeType) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(MEME_TYPE_PREFIX + memeId, mimeType);
        editor.apply();
    }

    public String getMemeMimeType(String memePath) {
        File memeFile = new File(memePath);
        String memeId = memeFile.getName();
        return sharedPreferences.getString(MEME_TYPE_PREFIX + memeId, null);
    }

    public Map<String, Set<String>> getAllMemesWithKeywords() {
        Map<String, Set<String>> memes = new HashMap<>();
        File[] memeFiles = memeFolder.listFiles();
        if (memeFiles != null) {
            for (File file : memeFiles) {
                String memeId = file.getName();
                Set<String> keywords = sharedPreferences.getStringSet(MEME_KEYWORDS_PREFIX + memeId, new HashSet<>());
                memes.put(file.getAbsolutePath(), keywords);
            }
        }
        return memes;
    }

    public Set<String> getKeywordsForMeme(String memePath) {
        File memeFile = new File(memePath);
        String memeId = memeFile.getName();
        return sharedPreferences.getStringSet(MEME_KEYWORDS_PREFIX + memeId, new HashSet<>());
    }

    public File getMemeFile(String memePath) {
        return new File(memePath);
    }

    public Map<String, String> searchMemes(String query) {
        Map<String, String> results = new HashMap<>();
        File[] memeFiles = memeFolder.listFiles();
        if (memeFiles != null) {
            for (File file : memeFiles) {
                String memeId = file.getName();
                Set<String> keywords = sharedPreferences.getStringSet(MEME_KEYWORDS_PREFIX + memeId, new HashSet<>());
                for (String keyword : keywords) {
                    if (keyword.toLowerCase().contains(query.toLowerCase())) {
                        results.put(file.getAbsolutePath(), keyword);
                        break;
                    }
                }
            }
        }
        return results;
    }

    public void deleteMeme(String memePath) {
        File memeFile = new File(memePath);
        if (memeFile.exists()) {
            memeFile.delete();
        }
        String memeId = memeFile.getName();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(MEME_KEYWORDS_PREFIX + memeId);
        editor.remove(MEME_TYPE_PREFIX + memeId);
        editor.apply();
    }
}

