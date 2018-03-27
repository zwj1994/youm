package com.bs.youmin.util;

import android.content.Context;


import com.bs.youmin.entity.User;

import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by Mrzhu on 2017/8/24.
 */
public class SaveUserUtil {

    public static String ACCOUNTLOCAL="user_message";

    public static void saveAccount(Context ctx, User data) {
        File file = new File(ctx.getFilesDir(), ACCOUNTLOCAL);
        if (file.exists()) {
            file.delete();
        }

        try {
            ObjectOutputStream oos = new ObjectOutputStream(ctx.openFileOutput(ACCOUNTLOCAL, Context.MODE_PRIVATE));
            oos.writeObject(data);
            oos.close();
        } catch (Exception e) {

        }
    }

    public static User loadAccount(Context ctx) {
        User data = null;
        File file = new File(ctx.getFilesDir(), ACCOUNTLOCAL);
        if (file.exists()) {
            try {
                ObjectInputStream ois = new ObjectInputStream(ctx.openFileInput(ACCOUNTLOCAL));
                data = (User) ois.readObject();
                ois.close();
            } catch (Exception e) {
                e.printStackTrace();

            }
        }

        if (data == null) {
            data = new User();
        }
        return data;
    }
}
