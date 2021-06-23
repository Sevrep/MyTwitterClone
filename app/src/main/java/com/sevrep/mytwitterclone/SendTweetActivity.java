package com.sevrep.mytwitterclone;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;
import java.util.HashMap;

public class SendTweetActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener {

    private EditText edtTweet;
    private ListView viewTweetsListView;
    private Button btnSendTweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_tweet);

        edtTweet = findViewById(R.id.edtSendTweet);
        edtTweet.setOnKeyListener(this);

        viewTweetsListView = findViewById(R.id.viewTweetsListView);

        btnSendTweet = findViewById(R.id.btnSendTweet);
        btnSendTweet.setOnClickListener(this);

        Button btnViewTweets = findViewById(R.id.btnViewTweets);
        btnViewTweets.setOnClickListener(this);

        /*HashMap<String, Integer> numbers = new HashMap<>();
        numbers.put("Number1", 1);
        numbers.put("Number2", 2);

        FancyToast.makeText(this, numbers.get("Number1") + "", Toast.LENGTH_LONG, FancyToast.WARNING, true).show();*/

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnSendTweet) {
            sendTweet();
        } else if (id == R.id.btnViewTweets) {
            viewTweets();
        }
    }

    @Override
    public void onBackPressed() {
        goToTwitterUsers();
    }

    private void goToTwitterUsers() {
        Intent intent = new Intent(this, TwitterUsersActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (v == edtTweet) {
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                onClick(btnSendTweet);
            }
            return false;
        }
        return true;
    }

    public void sendTweet() {
        ParseObject parseObject = new ParseObject("MyTweet");
        parseObject.put("tweet", edtTweet.getText().toString());
        parseObject.put("user", ParseUser.getCurrentUser().getUsername());

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        parseObject.saveInBackground(e -> {
            if (e == null) {
                FancyToast.makeText(SendTweetActivity.this, ParseUser.getCurrentUser().getUsername() + "'s tweet" + "(" + edtTweet.getText().toString() + ")" + " is saved!!!", Toast.LENGTH_LONG, FancyToast.SUCCESS, true).show();
            } else {
                FancyToast.makeText(SendTweetActivity.this, e.getMessage(), Toast.LENGTH_SHORT, FancyToast.ERROR, true).show();
            }
            progressDialog.dismiss();
        });
        viewTweets();
    }

    private void viewTweets() {
        final ArrayList<HashMap<String, String>> tweetList = new ArrayList<>();
        final SimpleAdapter adapter = new SimpleAdapter(SendTweetActivity.this, tweetList, android.R.layout.simple_list_item_2, new String[]{"tweetUserName", "tweetValue"}, new int[]{android.R.id.text1, android.R.id.text2});
        try {
            ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery("MyTweet");
            parseQuery.whereContainedIn("user", ParseUser.getCurrentUser().getList("fanOf"));
            parseQuery.findInBackground((objects, e) -> {
                if (objects.size() > 0 && e == null) {
                    for (ParseObject tweetObject : objects) {
                        HashMap<String, String> userTweet = new HashMap<>();
                        userTweet.put("tweetUserName", tweetObject.getString("user"));
                        userTweet.put("tweetValue", tweetObject.getString("tweet"));
                        tweetList.add(userTweet);
                    }
                    viewTweetsListView.setAdapter(adapter);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
