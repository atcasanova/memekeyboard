package com.memekeyboard;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.net.Uri;
import android.view.inputmethod.EditorInfo;
import android.content.ClipDescription;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import androidx.appcompat.app.AlertDialog;
import android.content.DialogInterface;

import androidx.core.content.FileProvider;
import androidx.core.view.inputmethod.InputConnectionCompat;
import androidx.core.view.inputmethod.InputContentInfoCompat;
import androidx.core.view.inputmethod.EditorInfoCompat;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MemeKeyboardService extends InputMethodService implements KeyboardView.OnKeyboardActionListener {

    private KeyboardView keyboardView;
    private Keyboard keyboard;
    private MemeManager memeManager;
    private GridView memeGridView;
    private MemeAdapter memeAdapter;
    private EditText searchEditText;
    private Button switchKeyboardButton;

    @Override
    public void onCreate() {
        super.onCreate();
        memeManager = new MemeManager(this);
    }

    @Override
    public void onStartInput(EditorInfo attribute, boolean restarting) {
        super.onStartInput(attribute, restarting);
        String[] mimeTypes = new String[] {"image/png", "image/gif", "image/jpeg", "image/webp"};
        EditorInfoCompat.setContentMimeTypes(attribute, mimeTypes);
    }

    @Override
    public View onCreateInputView() {
        View keyboardLayout = getLayoutInflater().inflate(R.layout.meme_gallery_layout, null);

        memeGridView = keyboardLayout.findViewById(R.id.meme_grid_view);
        searchEditText = keyboardLayout.findViewById(R.id.meme_search_edit_text);
        keyboardView = keyboardLayout.findViewById(R.id.keyboard_view);
        Button addMemeButton = keyboardLayout.findViewById(R.id.add_meme_button);
        switchKeyboardButton = keyboardLayout.findViewById(R.id.switch_keyboard_button);

        keyboard = new Keyboard(this, R.xml.qwerty);
        keyboardView.setKeyboard(keyboard);
        keyboardView.setOnKeyboardActionListener(this);

        List<String> allMemePaths = new ArrayList<>(memeManager.getAllMemesWithKeywords().keySet());
        memeAdapter = new MemeAdapter(this, allMemePaths);
        memeGridView.setAdapter(memeAdapter);

        memeGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedMemePath = (String) memeAdapter.getItem(position);
                sendMeme(selectedMemePath);
            }
        });

        memeGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final String memePath = (String) memeAdapter.getItem(position);
                new AlertDialog.Builder(MemeKeyboardService.this)
                        .setTitle(R.string.remove_meme_title)
                        .setMessage(R.string.remove_meme_message)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                memeManager.deleteMeme(memePath);
                                refreshMemeList();
                                android.widget.Toast.makeText(MemeKeyboardService.this,
                                        R.string.meme_removed_toast, android.widget.Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .show();
                return true;
            }
        });

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Map<String, String> searchResults = memeManager.searchMemes(s.toString());
                memeAdapter.updateData(new ArrayList<>(searchResults.keySet()));
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        addMemeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MemeKeyboardService.this, AddMemeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        if (switchKeyboardButton != null) {
            switchKeyboardButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.showInputMethodPicker();
                    }
                }
            });
        }

        return keyboardLayout;
    }

    @Override
    public void onPress(int primaryCode) {
    }

    @Override
    public void onRelease(int primaryCode) {
    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        if (searchEditText == null) {
            return;
        }
        switch (primaryCode) {
            case Keyboard.KEYCODE_DELETE:
                int length = searchEditText.getText().length();
                if (length > 0) {
                    searchEditText.getText().delete(length - 1, length);
                }
                break;
            case Keyboard.KEYCODE_DONE:
                InputConnection ic = getCurrentInputConnection();
                if (ic != null) {
                    ic.commitText("\n", 1);
                }
                break;
            case Keyboard.KEYCODE_MODE_CHANGE:
                // switch to next keyboard
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    switchToNextInputMethod(false);
                } else {
                    requestShowSelf(0);
                }
                break;
            case ' ':
                searchEditText.append(" ");
                break;
            default:
                char code = (char) primaryCode;
                searchEditText.append(String.valueOf(code));
                break;
        }
    }

    @Override
    public void onText(CharSequence text) {
    }

    @Override
    public void swipeLeft() {
    }

    @Override
    public void swipeRight() {
    }

    @Override
    public void swipeDown() {
    }

    @Override
    public void swipeUp() {
    }

    public void sendMeme(String memePath) {
        File memeFile = memeManager.getMemeFile(memePath);
        if (memeFile != null && memeFile.exists()) {
            Uri contentUri = FileProvider.getUriForFile(this,
                    getPackageName() + ".fileprovider", memeFile);
            InputConnection ic = getCurrentInputConnection();
            if (ic != null) {
                String mimeType = memeManager.getMemeMimeType(memePath);
                if (mimeType == null) {
                    mimeType = getContentResolver().getType(contentUri);
                }
                if (mimeType == null) {
                    mimeType = "image/*";
                }

                boolean isSticker = memeFile.getName().startsWith("sticker_");
                Bundle opts = new Bundle();
                opts.putBoolean("IS_STICKER", isSticker);

                InputContentInfoCompat info = new InputContentInfoCompat(contentUri,
                        new ClipDescription(memeFile.getName(), new String[]{mimeType}), null);

                try {
                    info.requestPermission();
                } catch (Exception e) {
                    // Ignore; requesting permission may fail on older APIs
                }

                EditorInfo editorInfo = getCurrentInputEditorInfo();
                if (InputConnectionCompat.commitContent(ic, editorInfo, info,
                        InputConnectionCompat.INPUT_CONTENT_GRANT_READ_URI_PERMISSION, opts)) {
                    info.releasePermission();
                    return;
                }

                if (mimeType.startsWith("audio/") || mimeType.startsWith("video/")) {
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType(mimeType);
                    share.putExtra(Intent.EXTRA_STREAM, contentUri);
                    share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    Intent chooser = Intent.createChooser(share, getString(R.string.share_meme));
                    chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(chooser);
                    return;
                }

                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newUri(getContentResolver(), "Meme", contentUri);
                clipboard.setPrimaryClip(clip);
                ic.commitText(contentUri.toString(), 1);
                android.widget.Toast.makeText(this,
                        R.string.unsupported_direct_share,
                        android.widget.Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void refreshMemeList() {
        String query = searchEditText != null ? searchEditText.getText().toString() : "";
        if (query.isEmpty()) {
            List<String> all = new ArrayList<>(memeManager.getAllMemesWithKeywords().keySet());
            memeAdapter.updateData(all);
        } else {
            Map<String, String> searchResults = memeManager.searchMemes(query);
            memeAdapter.updateData(new ArrayList<>(searchResults.keySet()));
        }
    }
}

