/*
 * Copyright (C) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.crunchy.notes;

import com.crunchy.notes.R;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.EditText;

public class NoteEdit extends Activity {
	
    private EditText mBodyText;
    private Long mRowId;
    private NotesDbAdapter mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDbHelper = new NotesDbAdapter(this);
        mDbHelper.open();

        setContentView(R.layout.note_edit);
        setTitle(R.string.edit_note);

        mBodyText = (EditText) findViewById(R.id.body);

        mRowId = (savedInstanceState == null) ? null :
            (Long) savedInstanceState.getSerializable(NotesDbAdapter.KEY_ROWID);
		if (mRowId == null) {
			Bundle extras = getIntent().getExtras();
			mRowId = extras != null ? extras.getLong(NotesDbAdapter.KEY_ROWID)
									: null;
		}

		populateFields();
    }

    private void populateFields() {
        if (mRowId != null) {
            Cursor note = mDbHelper.fetchNote(mRowId);
            startManagingCursor(note);
            mBodyText.setText(note.getString(
                    note.getColumnIndexOrThrow(NotesDbAdapter.KEY_BODY)));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState();
        outState.putSerializable(NotesDbAdapter.KEY_ROWID, mRowId);
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        populateFields();
    }

    private void saveState() {
    	
    	String body = mBodyText.getText().toString();
    	
    	if (body.length() != 0) {
	        String title = getTitleFromBody(body);
	
	        if (mRowId == null) {
	            long id = mDbHelper.createNote(title, body);
	            if (id > 0) {
	                mRowId = id;
	            }
	        } else {
	            mDbHelper.updateNote(mRowId, title, body);
	        }
        }
    }

	private String getTitleFromBody(String body) {
		if (body.length() == 0) {
		  return "";
		}
		
		String firstLine = body.split("\\r?\\n")[0];
		if (firstLine.length() < 35) {
			return firstLine;
		} else {
			return firstLine.substring(0, 35) + "...";
		}
	}

}
