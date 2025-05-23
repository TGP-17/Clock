/*
 * Copyright (C) 2015 The Android Open Source Project
 * modified
 * SPDX-License-Identifier: Apache-2.0 AND GPL-3.0-only
 */

package com.best.deskclock.timer;

import static com.best.deskclock.DeskClockApplication.getDefaultSharedPreferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.VibrationAttributes;
import android.os.VibrationEffect;
import android.os.Vibrator;

import com.best.deskclock.data.DataModel;
import com.best.deskclock.data.SettingsDAO;
import com.best.deskclock.ringtone.AsyncRingtonePlayer;
import com.best.deskclock.utils.LogUtils;
import com.best.deskclock.utils.SdkUtils;

/**
 * Manages playing the timer ringtone and vibrating the device.
 */
public abstract class TimerKlaxon {

    private static final long[] VIBRATE_PATTERN = {500, 500};

    private static boolean sStarted = false;
    private static AsyncRingtonePlayer sAsyncRingtonePlayer;

    private TimerKlaxon() {
    }

    public static void stop(Context context) {
        if (sStarted) {
            LogUtils.i("TimerKlaxon.stop()");
            sStarted = false;
            getAsyncRingtonePlayer(context).stop();
            Vibrator vibrator = context.getSystemService(Vibrator.class);
            vibrator.cancel();
        }
    }

    public static void start(Context context) {
        SharedPreferences prefs = getDefaultSharedPreferences(context);
        // Make sure we are stopped before starting
        stop(context);
        LogUtils.i("TimerKlaxon.start()");

        // Look up user-selected timer ringtone.
        if (DataModel.getDataModel().isTimerRingtoneSilent()) {
            // Special case: Silent ringtone
            LogUtils.i("Playing silent ringtone for timer");
        } else {
            final Uri uri = DataModel.getDataModel().getTimerRingtoneUri();
            final long crescendoDuration = SettingsDAO.getTimerCrescendoDuration(prefs);
            getAsyncRingtonePlayer(context).play(uri, crescendoDuration);
        }

        if (SettingsDAO.isTimerVibrate(prefs)) {
            final Vibrator vibrator = context.getSystemService(Vibrator.class);
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            if (SdkUtils.isAtLeastAndroid13()) {
                VibrationAttributes vibrationAttributes = new VibrationAttributes.Builder()
                        .setUsage(VibrationAttributes.USAGE_ALARM)
                        .build();
                VibrationEffect vibrationEffect = VibrationEffect.createWaveform(VIBRATE_PATTERN, 0);
                vibrator.vibrate(vibrationEffect, vibrationAttributes);
            } else if (SdkUtils.isAtLeastAndroid8()) {
                VibrationEffect vibrationEffect = VibrationEffect.createWaveform(VIBRATE_PATTERN, 0);
                vibrator.vibrate(vibrationEffect, audioAttributes);
            } else {
                vibrator.vibrate(VIBRATE_PATTERN, 0, audioAttributes);
            }
        }

        sStarted = true;
    }

    private static synchronized AsyncRingtonePlayer getAsyncRingtonePlayer(Context context) {
        if (sAsyncRingtonePlayer == null) {
            sAsyncRingtonePlayer = new AsyncRingtonePlayer(context.getApplicationContext());
        }

        return sAsyncRingtonePlayer;
    }
}
