package com.example.gallerycart.util;

import android.content.Context;
import android.widget.Toast;

public class Utils {
    public static void showToast(Context ctx, String msg){
        Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
    }
}
