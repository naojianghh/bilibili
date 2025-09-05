package ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.naojianghh.bilibili3.R;

import base.BaseActivity;
import ui.create.CreateFragment;
import ui.focus.FocusFragment;
import ui.home.HomeFragment;
import ui.login.LoginActivity;
import ui.mine.MineFragment;
import ui.shopping.ShoppingFragment;
import utils.SpUtils;

public class MainActivity extends BaseActivity {

    private Fragment[] fragments;
    private int lastFragmentIndex = 0;
    private MenuItem previousMenuItem = null;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        token = SpUtils.getToken(this);


        if (token == null) {
            Intent intent = getIntent();
            if (intent != null) {
                token = intent.getStringExtra("token");
            }
        }
    }



    @SuppressLint("ResourceAsColor")
    @Override
    protected void initViews() {

        fragments = new Fragment[]{
                new HomeFragment(),
                new FocusFragment(),
                new CreateFragment(),
                new ShoppingFragment(),
                new MineFragment()};
        getSupportFragmentManager().beginTransaction()
                .add(R.id.main_frame,fragments[0])
                .commit();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setItemActiveIndicatorColor(ColorStateList.valueOf(android.R.color.transparent));
        bottomNavigationView.setItemIconTintList(null);
        bottomNavigationView.setItemPaddingBottom(4);
        bottomNavigationView.setItemPaddingTop(4);
        bottomNavigationView.setItemIconSize(240);


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.button_home){
                    switchFragment(0);
                }
                else if (item.getItemId() == R.id.button_focus){
                    switchFragment(1);
                }
                else if (item.getItemId() == R.id.button_create){
                    switchFragment(2);
                }
                else if (item.getItemId() == R.id.button_shopping){
                    switchFragment(3);
                }
                else if (item.getItemId() == R.id.button_mine){
                    if (token!=null && !token.isEmpty()) {
                        switchFragment(4);
                    } else {
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                }

                if (previousMenuItem != null) {
                    previousMenuItem.setChecked(false);
                }
                item.setChecked(true);
                previousMenuItem = item;

                return false;
            }
        });

    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    private void switchFragment(int to){
        if (lastFragmentIndex == to){
            return;
        }
        FragmentTransaction fragmentsTransaction = getSupportFragmentManager().beginTransaction();
        if (!fragments[to].isAdded()){
            fragmentsTransaction.add(R.id.main_frame,fragments[to]);
        } else {
            fragmentsTransaction.show(fragments[to]);
        }
        fragmentsTransaction.hide(fragments[lastFragmentIndex]).commitAllowingStateLoss();
        lastFragmentIndex = to;
    }

}