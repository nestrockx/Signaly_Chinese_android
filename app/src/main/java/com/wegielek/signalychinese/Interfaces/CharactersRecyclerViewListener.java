package com.wegielek.signalychinese.Interfaces;

import android.view.View;

public interface CharactersRecyclerViewListener {
    void onItemReleased(int position);
    void onItemPressed(View itemView);
    void onItemCanceled(View itemView);
}