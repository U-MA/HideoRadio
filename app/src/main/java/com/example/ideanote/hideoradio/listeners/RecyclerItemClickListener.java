package com.example.ideanote.hideoradio.listeners;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.example.ideanote.hideoradio.Episode;
import com.example.ideanote.hideoradio.RecyclerViewAdapter;

public class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
    private OnItemClickListener itemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, int position, Episode episode);
    }

    GestureDetector gestureDetector;

    public RecyclerItemClickListener(Context context, OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
        /*
        View childView = view.findChildViewUnder(e.getX(), e.getY());
        if (childView != null && itemClickListener != null && gestureDetector.onTouchEvent(e)) {
            int position = view.getChildAdapterPosition(childView);
            RecyclerViewAdapter adapter = (RecyclerViewAdapter) view.getAdapter();
            itemClickListener.onItemClick(view, position, adapter.getAt(position));
            return true;
        }
        */
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView view, MotionEvent e) {
        // do nothing
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        // do nothing
    }
}
