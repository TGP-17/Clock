/*
 * Copyright (C) 2016 The Android Open Source Project
 * modified
 * SPDX-License-Identifier: Apache-2.0 AND GPL-3.0-only
 */

package com.best.deskclock.ringtone;

import static android.view.View.GONE;

import static com.best.deskclock.DeskClockApplication.getDefaultSharedPreferences;
import static com.best.deskclock.settings.PreferencesDefaultValues.AMOLED_DARK_MODE;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.content.res.AppCompatResources;

import com.best.deskclock.ItemAdapter.ItemViewHolder;
import com.best.deskclock.R;
import com.best.deskclock.data.SettingsDAO;
import com.best.deskclock.utils.ThemeUtils;
import com.google.android.material.color.MaterialColors;

final class AddCustomRingtoneViewHolder extends ItemViewHolder<AddCustomRingtoneHolder>
        implements View.OnClickListener {

    static final int VIEW_TYPE_ADD_NEW = Integer.MIN_VALUE;
    static final int CLICK_ADD_NEW = VIEW_TYPE_ADD_NEW;

    private AddCustomRingtoneViewHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

        final Context context = itemView.getContext();

        final View selectedView = itemView.findViewById(R.id.sound_image_selected);
        selectedView.setVisibility(GONE);

        final TextView nameView = itemView.findViewById(R.id.ringtone_name);
        nameView.setText(itemView.getContext().getString(R.string.add_new_sound));

        final ImageView imageView = itemView.findViewById(R.id.ringtone_image);
        imageView.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_add));
        imageView.getDrawable().setTint(MaterialColors.getColor(context, android.R.attr.colorBackground, Color.BLACK));
        imageView.setBackgroundResource(R.drawable.bg_circle);
        imageView.setBackgroundTintList(ColorStateList.valueOf(
                MaterialColors.getColor(context, com.google.android.material.R.attr.colorPrimary, Color.BLACK))
        );

        final int backgroundColor;
        if (ThemeUtils.isNight(context.getResources())
                && SettingsDAO.getDarkMode(getDefaultSharedPreferences(context)).equals(AMOLED_DARK_MODE)) {
            backgroundColor = Color.BLACK;
        } else {
            backgroundColor = MaterialColors.getColor(context, android.R.attr.colorBackground, Color.BLACK);
        }

        itemView.setBackground(ThemeUtils.rippleDrawable(context, backgroundColor));
    }

    @Override
    public void onClick(View view) {
        notifyItemClicked(AddCustomRingtoneViewHolder.CLICK_ADD_NEW);
    }

    public static class Factory implements ItemViewHolder.Factory {

        private final LayoutInflater mInflater;

        Factory(LayoutInflater inflater) {
            mInflater = inflater;
        }

        @Override
        public ItemViewHolder<?> createViewHolder(ViewGroup parent, int viewType) {
            final View itemView = mInflater.inflate(R.layout.ringtone_item_sound, parent, false);
            return new AddCustomRingtoneViewHolder(itemView);
        }
    }
}
