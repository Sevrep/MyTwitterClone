package com.sevrep.mytwitterclone;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TwitterUsersActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ListView listView;
    private ArrayList<String> tUsers;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twitter_users);

        FancyToast.makeText(this, "Welcome " + ParseUser.getCurrentUser().getUsername(), Toast.LENGTH_LONG, FancyToast.INFO, true).show();

        tUsers = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_checked, tUsers);

        listView = findViewById(R.id.listView);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        listView.setOnItemClickListener(this);

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
        query.findInBackground((objects, e) -> {
            if (objects.size() > 0 && e == null) {
                for (ParseUser twitterUser : objects) {
                    tUsers.add(twitterUser.getUsername());
                }
                listView.setAdapter(adapter);
                for (String twitterUser : tUsers) {
                    if (ParseUser.getCurrentUser().getList("fanOf") != null) {
                        if (Objects.requireNonNull(ParseUser.getCurrentUser().getList("fanOf")).contains(twitterUser)) {
                            listView.setItemChecked(tUsers.indexOf(twitterUser), true);
                        }
                    }
                }

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.logout_item) {
            ParseUser.getCurrentUser();
            ParseUser.logOutInBackground(e -> {
                Intent intent = new Intent(TwitterUsersActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            });
        } else if (itemId == R.id.sendTweetItem) {
            Intent intent = new Intent(TwitterUsersActivity.this, SendTweetActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        CheckedTextView checkedTextView = (CheckedTextView) view;
        if (checkedTextView.isChecked()) {
            FancyToast.makeText(TwitterUsersActivity.this, tUsers.get(position) + " is now followed!", Toast.LENGTH_SHORT, FancyToast.INFO, true).show();
            ParseUser.getCurrentUser().add("fanOf", tUsers.get(position));
        } else {
            FancyToast.makeText(TwitterUsersActivity.this, tUsers.get(position) + " is now unfollowed!", Toast.LENGTH_SHORT, FancyToast.INFO, true).show();
            Objects.requireNonNull(ParseUser.getCurrentUser().getList("fanOf")).remove(tUsers.get(position));
            List<Object> currentUserFanOfList = ParseUser.getCurrentUser().getList("fanOf");
            ParseUser.getCurrentUser().remove("fanOf");
            assert currentUserFanOfList != null;
            ParseUser.getCurrentUser().put("fanOf", currentUserFanOfList);
        }
        ParseUser.getCurrentUser().saveInBackground(e -> {
            if (e == null) {
                FancyToast.makeText(TwitterUsersActivity.this, "Saved", Toast.LENGTH_SHORT, FancyToast.SUCCESS, true).show();
            }
        });
    }

}
