package atalay.okay.com.easyormex.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import atalay.okay.com.easyormex.Constant;
import atalay.okay.com.easyormex.DatabaseManager;
import atalay.okay.com.easyormex.R;
import atalay.okay.com.easyormex.dbObject.Information;
import atalay.okay.com.easyormex.dbObject.User;
import okay.atalay.com.easyorm.src.exception.FieldNotFoundException;
import okay.atalay.com.easyorm.src.exception.QueryExecutionException;
import okay.atalay.com.easyorm.src.exception.QueryNotFoundException;

/**
 * Created by 1 on 22.09.2018.
 */

public class Adapter extends BaseAdapter {
    private List<User> userList;
    private LayoutInflater inflater;
    private Context context;

    public Adapter(List<User> userList, Context context) {
        this.userList = userList;
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return userList.size();
    }

    @Override
    public User getItem(int i) {
        return userList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View v, ViewGroup viewGroup) {
        View view = inflater.inflate(R.layout.item_user, null);
        final User user = userList.get(i);
        TextView textViewUser = view.findViewById(R.id.textViewUser);
        TextView textViewAddress = view.findViewById(R.id.textViewAddress);
        TextView textViewPhone = view.findViewById(R.id.textViewPhone);
        ImageView imageViewEdit = view.findViewById(R.id.imageViewEdit);
        ImageView imageViewRemove = view.findViewById(R.id.imageViewRemove);
        textViewUser.setText(user.getID() + " " + user.getName() + " " + user.getLastName() + " " + user.getNumber());
        try {
            List<Information> information = DatabaseManager.easyExecute.select(Constant.GET_INFORMATION, new Object[]{user.getInfoID()}, Information.class);
            if (information.size() == 1) {
                textViewAddress.setText(information.get(0).getAddress());
                textViewPhone.setText(information.get(0).getPhone());
            }
        } catch (QueryNotFoundException e) {
            e.printStackTrace();
        } catch (QueryExecutionException e) {
            e.printStackTrace();
        }
        imageViewEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DialogUpdate(context, user).show();
            }
        });
        imageViewRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    DatabaseManager.easyExecute.delete(Constant.DELETE_INFORMATIONBYID, new Object[]{user.getInfoID()});
                    DatabaseManager.easyExecute.deleteObject(Constant.DELETE_USERBYID, user);
                } catch (QueryNotFoundException e) {
                    e.printStackTrace();
                } catch (QueryExecutionException e) {
                    e.printStackTrace();
                } catch (FieldNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
        return view;
    }
}
