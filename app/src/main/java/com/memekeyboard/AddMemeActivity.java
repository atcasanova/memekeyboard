package com.memekeyboard;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class AddMemeActivity extends Activity {

    private static final int PICK_MEME_REQUEST = 1;
    private Uri selectedMemeUri;
    private MemeManager memeManager;

    private EditText keywordsEditText;
    private RadioGroup typeRadioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_meme_layout);

        memeManager = new MemeManager(this);

        keywordsEditText = findViewById(R.id.keywords_edit_text);
        typeRadioGroup = findViewById(R.id.type_radio_group);
        Button selectMemeButton = findViewById(R.id.select_meme_button);
        Button saveMemeButton = findViewById(R.id.save_meme_button);

        selectMemeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMemePicker();
            }
        });

        saveMemeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMeme();
            }
        });
    }

    private void openMemePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*"); // Allow all file types for memes (images, gifs, audio, video)
        startActivityForResult(intent, PICK_MEME_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_MEME_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedMemeUri = data.getData();
            Toast.makeText(this, "Meme selecionado: " + selectedMemeUri.getPath(), Toast.LENGTH_SHORT).show();
        }
    }

    private void saveMeme() {
        if (selectedMemeUri == null) {
            Toast.makeText(this, "Por favor, selecione um meme primeiro.", Toast.LENGTH_SHORT).show();
            return;
        }

        String keywordsString = keywordsEditText.getText().toString().trim();
        // Java 8 does not support the "\s" escape sequence. Use a double
        // backslash so the regular expression receives "\s*" instead of the
        // unsupported string escape.
        Set<String> keywords = new HashSet<>(Arrays.asList(
                keywordsString.split(",\\s*")));

        boolean asSticker = typeRadioGroup.getCheckedRadioButtonId() == R.id.sticker_radio;

        try {
            memeManager.addMeme(selectedMemeUri, keywords, asSticker);
            Toast.makeText(this, "Meme salvo com sucesso!", Toast.LENGTH_SHORT).show();
            finish(); // Close activity after saving
        } catch (IOException e) {
            Toast.makeText(this, "Erro ao salvar meme: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}

