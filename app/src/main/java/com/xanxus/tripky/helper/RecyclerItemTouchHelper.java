package com.xanxus.tripky.helper;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.xanxus.tripky.R;
import com.xanxus.tripky.adapter.ListTripAdapter;

import org.json.JSONException;

import java.io.IOException;

/**
 * Created by ravi on 29/09/17.
 */

public class RecyclerItemTouchHelper extends ItemTouchHelper.SimpleCallback {

    private ListTripAdapter mAdapter;
    private Drawable icon;
    private final ColorDrawable background = new ColorDrawable(Color.RED);

    public RecyclerItemTouchHelper (ListTripAdapter adapter) {
        super(0,ItemTouchHelper.LEFT);
        mAdapter = adapter;
        icon = ContextCompat.getDrawable(mAdapter.getContext(),
                R.drawable.ic_delete_white_24dp);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        try {
            mAdapter.removeItem(position);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX,
                dY, actionState, isCurrentlyActive);
        View itemView = viewHolder.itemView;

        if (dX < 0) { // Swiping to the left

            icon.draw(c);
            background.setBounds(itemView.getRight() + ((int) dX) ,
                    itemView.getTop(), itemView.getRight(), itemView.getBottom());
            background.draw(c);

        }


    }


}