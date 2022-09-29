package edu.byu.cs.tweeter.client.view.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.squareup.picasso.Picasso;

import edu.byu.cs.client.R;
import edu.byu.cs.tweeter.client.presenter.MainPresenter;
import edu.byu.cs.tweeter.client.view.login.LoginActivity;
import edu.byu.cs.tweeter.client.view.login.StatusDialogFragment;
import edu.byu.cs.tweeter.model.domain.User;

/**
 * The main activity for the application. Contains tabs for feed, story, following, and followers.
 */
public class MainActivity extends AppCompatActivity implements StatusDialogFragment.Observer, MainPresenter.View {

    private static final String LOG_TAG = "MainActivity";

    public static final String CURRENT_USER_KEY = "CurrentUser";

    private Toast logOutToast;
    private Toast postingToast;
    private User selectedUser;
    private TextView followeeCount;
    private TextView followerCount;
    private Button followButton;


    private MainPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        presenter = new MainPresenter(this);

        selectedUser = (User) getIntent().getSerializableExtra(CURRENT_USER_KEY);
        if (selectedUser == null) {
            throw new RuntimeException("User not passed to activity");
        }

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager(), selectedUser);
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setOffscreenPageLimit(1);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StatusDialogFragment statusDialogFragment = new StatusDialogFragment();
                statusDialogFragment.show(getSupportFragmentManager(), "post-status-dialog");
            }
        });


        updateSelectedUserFollowingAndFollowers();

        TextView userName = findViewById(R.id.userName);
        userName.setText(selectedUser.getName());

        TextView userAlias = findViewById(R.id.userAlias);
        userAlias.setText(selectedUser.getAlias());

        ImageView userImageView = findViewById(R.id.userImage);
        Picasso.get().load(selectedUser.getImageUrl()).into(userImageView);

        followeeCount = findViewById(R.id.followeeCount);
        followeeCount.setText(getString(R.string.followeeCount, "..."));

        followerCount = findViewById(R.id.followerCount);
        followerCount.setText(getString(R.string.followerCount, "..."));

        followButton = findViewById(R.id.followButton);


        presenter.setFollowVisibility(selectedUser);


        followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                followButton.setEnabled(false);

                presenter.toggleFollowButton(followButton.getText().toString(), v.getContext().getString(R.string.following),
                        selectedUser);

            }
        });
    }

    @Override
    public void displayMessage(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void setFollowButtonVisibility(boolean isVisble) {
        if (isVisble) {
            followButton.setVisibility(View.VISIBLE);
        }else {
            followButton.setVisibility(View.GONE);

        }
    }

    @Override
    public void setFollowButtonText(int text) {
        followButton.setText(text);
    }

    @Override
    public void showIsFollowing() {
        followButton.setBackgroundColor(getResources().getColor(R.color.white));
        followButton.setTextColor(getResources().getColor(R.color.lightGray));
    }

    @Override
    public void showNotFollowing() {
        followButton.setBackgroundColor(getResources().getColor(R.color.colorAccent));
    }

    @Override
    public void setFollowerCount(String count) {
        followerCount.setText(getString(R.string.followerCount, count));
    }

    @Override
    public void setFollowingCount(String count) {
        followeeCount.setText(getString(R.string.followerCount, count));
    }

    @Override
    public void updateFollowingFollowers() {
        updateSelectedUserFollowingAndFollowers();
    }

    @Override
    public void setFollowButtonEnabled(boolean value) {
        followButton.setEnabled(value);
    }

    @Override
    public void clearInfoMessage() {
        if (logOutToast != null) {
            logOutToast.cancel();
            logOutToast = null;
        }
        if (postingToast != null) {
            postingToast.cancel();
        }
    }

    @Override
    public void goToLogin() {
        //Revert to login screen.
        Intent intent = new Intent(this, LoginActivity.class);
        //Clear everything so that the main activity is recreated with the login page.
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void setPostingToastText(String message) {
        postingToast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        postingToast.show();
    }

    @Override
    public void displayLogoutMessage(String message) {
        logOutToast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        logOutToast.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logoutMenu) {
            presenter.logout();

            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }



    @Override
    public void onStatusPosted(String post) {
        presenter.makePost(post);

    }





    public void updateSelectedUserFollowingAndFollowers() {
        presenter.updateFollowingFollowers(selectedUser);

    }




}
