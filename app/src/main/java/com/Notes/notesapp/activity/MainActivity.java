package com.Notes.notesapp.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.Notes.notesapp.adapter.NotesAdapter;
import com.Notes.notesapp.database.NotesDatabase;
import com.Notes.notesapp.entities.Note;
import com.Notes.notesapp.listeners.NotesListener;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NotesListener {

    public static final int REQUEST_CODE_ADD_NOTE = 1;
    public static final int REQUEST_CODE_UPDATE_NOTE = 2;
    public static final int REQUEST_CODE_SHOW_NOTE = 3;
    public static final int REQUEST_CODE_SELECT_IMAGE = 4;
    public static final int REQUEST_CODE_STORAGE_PERMISSION = 5;
    private static final String PREFS_UID = "";


    private RecyclerView noteRecyclerView;

    private List<Note> noteList;
    private NotesAdapter notesAdapter;
    private int noteClickedPosition = -1;
    private AlertDialog dialogAddURL;
    private AlertDialog dialogMenu;
    private String Save_UID;
    private TextView tvProfile;

    private ImageView mainImage;

    Dialog dialog;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final ImageView menu_icon = findViewById(R.id.menu_icon);
        mainImage = findViewById(R.id.main_image);


        dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        menu_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
//
//                PopupMenu popupMenu = new PopupMenu(MainActivity.this,menu_icon);
//                popupMenu.getMenuInflater().inflate(R.menu.itemmenu,popupMenu.getMenu());
//                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                    @Override
//                    public boolean onMenuItemClick(MenuItem menuItem) {
//                        switch (menuItem.getItemId()){
//
//                            case R.id.profile:
//                                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
//                                startActivity(intent);
//                                break;
//                            case R.id.About:
//                                Toast.makeText(MainActivity.this, "about", Toast.LENGTH_SHORT).show();
//
//                                break;
//                            case R.id.logout:
//                                Toast.makeText(MainActivity.this, "logout", Toast.LENGTH_SHORT).show();
//
//                                break;
//                        }
//                        return false;
//                    }
//                });
//
//                popupMenu.show();
//
//
//
//
//
//
                }
        });





        // Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim
        // .fade_in);

        ImageView imageAddNoteMain = findViewById(R.id.imageAddNoteMain);
        imageAddNoteMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                startActivityForResult(new Intent(getApplicationContext(), CreateNoteActivity.class), REQUEST_CODE_ADD_NOTE

                );
//
//                Intent intent = new Intent(getApplicationContext(),CreateNoteActivity.class),REQUEST_CODE_ADD_NOTE;
//                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);

            }
        });


        noteRecyclerView = findViewById(R.id.noteRecyclerView);
        noteRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        );



        noteList = new ArrayList<>();
        notesAdapter = new NotesAdapter(noteList, this);
        noteRecyclerView.setAdapter(notesAdapter);
        getNotes(REQUEST_CODE_SHOW_NOTE, false);
        EditText inputSearch = findViewById(R.id.inputSearch);

        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                notesAdapter.cancelTimer();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (noteList.size() != 0) {
                    notesAdapter.NotesSerach(editable.toString());
                }
            }
        });

        findViewById(R.id.imageAddNotes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(
                        new Intent(getApplicationContext(), CreateNoteActivity.class),
                        REQUEST_CODE_ADD_NOTE
                );
            }
        });
        findViewById(R.id.imageAddImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(
                        getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            MainActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_CODE_STORAGE_PERMISSION
                    );

                } else {
                    selectImage();
                }
            }
        });

        findViewById(R.id.imageAddWebLink).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddUrlDialog();
            }
        });


    }

    private void openDialog() {

        dialog.setContentView(R.layout.popup_layout);
        ImageView cross = dialog.findViewById(R.id.cross);
        TextView tvProfile = dialog.findViewById(R.id.tv_profile);
        TextView tvAbout = dialog.findViewById(R.id.tv_about);
        TextView tvLogout = dialog.findViewById(R.id.tv_logout);
        TextView shearText = dialog.findViewById(R.id.shear_app);
        dialog.show();

        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        tvProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,ProfileActivity.class);
                startActivity(intent);
                dialog.dismiss();
            }
        });

        tvAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,AboutUs.class);
                startActivity(intent);
                dialog.dismiss();
            }
        });


        shearText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT,"My Notes");
                String shearMassage="https://play.google.com/store/apps/details?="+BuildConfig.APPLICATION_ID+"\n\n";
                intent.putExtra(Intent.EXTRA_TEXT,shearMassage);
                startActivity(Intent.createChooser(intent,""));
            }
                catch (Exception e){
                    Toast.makeText(MainActivity.this, "Error Occurred", Toast.LENGTH_SHORT).show();
                }
            }
        });


        tvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(MainActivity.this, "Logout...", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
                dialog.dismiss();
            }
        });


    }





    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectImage();
            } else {
                Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getPathFromUri(Uri contentUri) {

        String filePath;
        Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            filePath = contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex("_data");
            filePath = cursor.getString(index);
            cursor.close();
        }
        return filePath;
    }


    @Override
    public void onNoteClicked(Note note, int position) {
        noteClickedPosition = position;
        Intent intent = new Intent(getApplicationContext(), CreateNoteActivity.class);
        intent.putExtra("isViewOrUpdate", true);
        intent.putExtra("note", note);
        startActivityForResult(intent, REQUEST_CODE_UPDATE_NOTE);

    }

    private void getNotes(final int requestCode, final boolean isNoteDeleted) {

        @SuppressLint("StaticFieldLeak")
        class GetNotesTask extends AsyncTask<Void, Void, List<Note>> {

            @Override
            protected List<Note> doInBackground(Void... voids) {
                return NotesDatabase
                        .getDatabase(getApplicationContext())
                        .noteDao().getAllNotes();
            }

            @Override
            protected void onPostExecute(List<Note> notes) {
                super.onPostExecute(notes);

                if (requestCode == REQUEST_CODE_SHOW_NOTE) {
                    noteList.addAll(notes);
                    notesAdapter.notifyDataSetChanged();
                } else if (requestCode == REQUEST_CODE_ADD_NOTE) {

                    noteList.add(0, notes.get(0));
                    notesAdapter.notifyItemInserted(0);
                    noteRecyclerView.smoothScrollToPosition(0);
                } else if (requestCode == REQUEST_CODE_UPDATE_NOTE) {

                    noteList.remove(noteClickedPosition);

                    if (isNoteDeleted) {
                        notesAdapter.notifyItemRemoved(noteClickedPosition);
                    } else {
                        noteList.add(noteClickedPosition, notes.get(noteClickedPosition));
                        notesAdapter.notifyItemInserted(noteClickedPosition);

                    }
                }

            }
        }
        new GetNotesTask().execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_ADD_NOTE && resultCode == RESULT_OK) {

            getNotes(REQUEST_CODE_ADD_NOTE, false);
        } else if (requestCode == REQUEST_CODE_UPDATE_NOTE && resultCode == RESULT_OK) {
            if (data != null) {
                getNotes(REQUEST_CODE_UPDATE_NOTE, data.getBooleanExtra("isNoteDeleted", false));
            }
        } else if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK) {
            if (data != null) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {

                    try {

                        String selectedImagePath = getPathFromUri(selectedImageUri);
                        Intent intent = new Intent(getApplicationContext(), CreateNoteActivity.class);
                        intent.putExtra("isFromQuickAction", true);
                        intent.putExtra("quickActionType", "image");
                        intent.putExtra("imagePath", selectedImagePath);
                        startActivityForResult(intent, REQUEST_CODE_ADD_NOTE);


                    } catch (Exception exception) {
                        Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }

    }

    private void showAddUrlDialog() {

        if (dialogAddURL == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            View view = LayoutInflater.from(this).inflate(
                    R.layout.layout_add_url,
                    (ViewGroup) findViewById(R.id.layoutAddUrlContainer)
            );
            builder.setView(view);

            dialogAddURL = builder.create();
            if (dialogAddURL.getWindow() != null) {
                dialogAddURL.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            final EditText inputURL = view.findViewById(R.id.inputURL);
            inputURL.requestFocus();

            view.findViewById(R.id.textAdd);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(MainActivity.this, "URL is Added", Toast.LENGTH_SHORT).show();

                    if (inputURL.getText().toString().trim().isEmpty()) {
                        Toast.makeText(MainActivity.this, "Enter Url", Toast.LENGTH_SHORT).show();

                    } else if (!Patterns.WEB_URL.matcher(inputURL.getText().toString()).matches()) {
                        Toast.makeText(MainActivity.this, "Enter Valid URL", Toast.LENGTH_SHORT).show();
                    } else {


                        dialogAddURL.dismiss();
                        Intent intent = new Intent(getApplicationContext(), CreateNoteActivity.class);
                        intent.putExtra("isFromQuickAction", true);
                        intent.putExtra("quickActionType", "URL");
                        intent.putExtra("URL", inputURL.getText().toString());
                        startActivityForResult(intent, REQUEST_CODE_ADD_NOTE);
                    }
                }
            });
            view.findViewById(R.id.textCancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogAddURL.dismiss();
                }
            });

        }
        dialogAddURL.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}