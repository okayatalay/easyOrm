package atalay.okay.com.easyormex.main;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import atalay.okay.com.easyormex.Constant;
import atalay.okay.com.easyormex.DatabaseManager;
import atalay.okay.com.easyormex.R;
import atalay.okay.com.easyormex.dbObject.User;
import okay.atalay.com.easyorm.src.dataSync.DataSyncListener;
import okay.atalay.com.easyorm.src.easyOrm.factory.EasyOrmFactory;
import okay.atalay.com.easyorm.src.exception.QueryExecutionException;
import okay.atalay.com.easyorm.src.exception.QueryNotFoundException;

public class MainActivity extends AppCompatActivity implements DataSyncListener {
    private ListView listView;
    private Adapter adapter;
    private List<User> userList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DatabaseManager databaseManager = new DatabaseManager(this);
        databaseManager.initDatabase();
        EasyOrmFactory.registerDataSync(this);
        adapter = new Adapter(userList, this);
        listView = findViewById(R.id.listView);
        listView.setAdapter(adapter);
        fillAdapter();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                new DialogInsert(MainActivity.this).show();
        }
        return true;
    }

    private void fillAdapter() {
        try {
            List<User> users = DatabaseManager.easyExecute.select(Constant.GETALLUSER, User.class);
            userList.clear();
            userList.addAll(users);
            adapter.notifyDataSetChanged();
        } catch (QueryNotFoundException e) {
            e.printStackTrace();
        } catch (QueryExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void receiveDataSync(String queryName, Object result) {
        fillAdapter();
    }
}
