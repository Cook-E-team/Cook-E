/*
 * Copyright 2016 the Cook-E development team
 *
 * This file is part of Cook-E.
 *
 * Cook-E is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Cook-E is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Cook-E.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.cook_e.cook_e;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import org.cook_e.data.BugReport;
import org.cook_e.data.SQLBugUploader;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.Date;

public class BugReportActivity extends AppCompatActivity {

    private static final String TAG = BugReportActivity.class.getSimpleName();

    private EditText mDescriptionField;

    /**
     * The E-mail address to send reports to
     */
    private static String REPORT_ADDRESS = "samcrow@uw.edu";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bug_report);

        final Toolbar actionBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(actionBar);
        actionBar.setTitle(R.string.report_a_problem);

        mDescriptionField = (EditText) findViewById(R.id.bug_description_field);
    }

    private void sendReport() {
        final String description = mDescriptionField.getText().toString();
        final String systemInfo = getSystemInformation().toString();

        final BugReport report = new BugReport(description, DateTime.now(), systemInfo);
        mailReport(report);
    }

    /**
     * Starts the process of sending an E-mail message with the report
     * @param report
     */
    private void mailReport(BugReport report) {

        final StringBuilder message = new StringBuilder();
        final DateTimeFormatter formatter = ISODateTimeFormat.dateTimeNoMillis();
        message.append("Time: ");
        formatter.printTo(message, report.getDate());
        message.append('\n');
        message.append("User description: ");
        message.append(report.getDesc());
        message.append("\nMetadata: ");
        message.append(report.getMeta());


        ShareCompat.IntentBuilder.from(this)
                .setType("message/rfc822")
                .addEmailTo(REPORT_ADDRESS)
                .setSubject("Cook-E Problem Report")
                .setText(message)
                .setChooserTitle(R.string.mail_report)
                .startChooser();

    }

    /**
     * Uploads a report to the remote database
     * @param report the report to upload
     */
    private void uploadReport(BugReport report) {
        try {
            final SQLBugUploader uploader = new SQLBugUploader();
            uploader.submitBug(report);

            finish();
        } catch (SQLException e) {
            new AlertDialog.Builder(this)
                    .setTitle("Failed to send report")
                    .setMessage(e.getLocalizedMessage())
                    .show();
            Log.w(TAG, "Failed to send bug report", e);
        }
    }

    /**
     * Gathers information about the device running the application and returns it as a JSONObject
     * @return information about the system
     */
    private JSONObject getSystemInformation() {
        return new JSONObject();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_report_bug, menu);

        final MenuItem sendItem = menu.findItem(R.id.item_send_report);
        sendItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                sendReport();
                return true;
            }
        });

        return true;
    }
}
