package com.memekeyboard;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputConnection;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;

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

    @Override
    public void onCreate() {
        super.onCreate();
        memeManager = new MemeManager(this);
    }

    @Override
    public View onCreateInputView() {
        View keyboardLayout = getLayoutInflater().inflate(R.layout.meme_gallery_layout, null);

        memeGridView = keyboardLayout.findViewById(R.id.meme_grid_view);
        searchEditText = keyboardLayout.findViewById(R.id.meme_search_edit_text);
        keyboardView = keyboardLayout.findViewById(R.id.keyboard_view);
        Button addMemeButton = keyboardLayout.findViewById(R.id.add_meme_button);

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
            Uri contentUri = Uri.fromFile(memeFile);
            InputConnection ic = getCurrentInputConnection();
            if (ic != null) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newUri(getContentResolver(), "Meme", contentUri);
                clipboard.setPrimaryClip(clip);
                ic.commitText(contentUri.toString(), 1);
            }
        }
    }
}

