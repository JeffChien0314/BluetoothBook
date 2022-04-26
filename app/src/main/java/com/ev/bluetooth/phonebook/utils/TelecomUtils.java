package com.ev.bluetooth.phonebook.utils;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//


import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import android.net.Uri;
import android.provider.CallLog;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.PhoneLookup;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ev.bluetooth.phonebook.common.LetterTileDrawable;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.Nullable;

public class TelecomUtils {
    private static final String TAG = "CD.TelecomUtils";
    private static final String[] CONTACT_ID_PROJECTION = new String[]{"display_name", "type", "label", "_id"};
    private static String sVoicemailNumber;
    private static TelephonyManager sTelephonyManager;

    public TelecomUtils() {
    }

    public static CharSequence getTypeFromNumber(Context context, String number) {
        if (Log.isLoggable("CD.TelecomUtils", 3)) {
            Log.d("CD.TelecomUtils", "getTypeFromNumber, number: " + number);
        }

        String defaultLabel = "";
        if (TextUtils.isEmpty(number)) {
            return defaultLabel;
        } else {
            ContentResolver cr = context.getContentResolver();
            Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
            Cursor cursor = cr.query(uri, CONTACT_ID_PROJECTION, (String) null, (String[]) null, (String) null);

            CharSequence var11;
            try {
                if (cursor == null || !cursor.moveToFirst()) {
                    return defaultLabel;
                }

                int typeColumn = cursor.getColumnIndex("type");
                int type = cursor.getInt(typeColumn);
                int labelColumn = cursor.getColumnIndex("label");
                String label = cursor.getString(labelColumn);
                CharSequence typeLabel = Phone.getTypeLabel(context.getResources(), type, label);
                var11 = typeLabel;
            } finally {
                if (cursor != null) {
                    cursor.close();
                }

            }

            return var11;
        }
    }

    public static String getVoicemailNumber(Context context) {
        if (sVoicemailNumber == null) {
            sVoicemailNumber = getTelephonyManager(context).getVoiceMailNumber();
        }

        return sVoicemailNumber;
    }

    public static boolean isVoicemailNumber(Context context, String number) {
        return !TextUtils.isEmpty(number) && number.equals(getVoicemailNumber(context));
    }

    public static TelephonyManager getTelephonyManager(Context context) {
        if (sTelephonyManager == null) {
            sTelephonyManager = (TelephonyManager) context.getSystemService("phone");
        }

        return sTelephonyManager;
    }

    public static String getFormattedNumber(Context context, String number) {
        if (Log.isLoggable("CD.TelecomUtils", 3)) {
            Log.d("CD.TelecomUtils", "getFormattedNumber: " + number);
        }

        if (number == null) {
            return "";
        } else {
            String countryIso = getIsoDefaultCountryNumber(context);
            if (Log.isLoggable("CD.TelecomUtils", 3)) {
                Log.d("CD.TelecomUtils", "PhoneNumberUtils.formatNumberToE16, number: " + number + ", country: " + countryIso);
            }

            String e164 = PhoneNumberUtils.formatNumberToE164(number, countryIso);
            String formattedNumber = PhoneNumberUtils.formatNumber(number, e164, countryIso);
            formattedNumber = TextUtils.isEmpty(formattedNumber) ? number : formattedNumber;
            if (Log.isLoggable("CD.TelecomUtils", 3)) {
                Log.d("CD.TelecomUtils", "getFormattedNumber, result: " + formattedNumber);
            }

            return formattedNumber;
        }
    }

    private static String getIsoDefaultCountryNumber(Context context) {
        String countryIso = getTelephonyManager(context).getSimCountryIso().toUpperCase(Locale.US);
        if (countryIso.length() != 2) {
            countryIso = Locale.getDefault().getCountry();
            if (countryIso == null || countryIso.length() != 2) {
                countryIso = "US";
            }
        }

        return countryIso;
    }

    @Nullable
    public static PhoneNumber createI18nPhoneNumber(Context context, String number) {
        try {
            return PhoneNumberUtil.getInstance().parse(number, getIsoDefaultCountryNumber(context));
        } catch (NumberParseException var3) {
            return null;
        }
    }

    public static Pair<String, Uri> getDisplayNameAndAvatarUri(Context context, String number) {
        if (Log.isLoggable("CD.TelecomUtils", 3)) {
            Log.d("CD.TelecomUtils", "getDisplayNameAndAvatarUri: " + number);
        }

        if (TextUtils.isEmpty(number)) {
            return new Pair("context.getString(string.unknown)", (Object)null);
        } else if (isVoicemailNumber(context, number)) {
            return new Pair("context.getString(string.voicemail)", makeResourceUri(context, com.android.internal.R.drawable.ic_camera));
        } else {
            ContentResolver cr = context.getContentResolver();
            Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
            Cursor cursor = null;
            String name = null;
            String photoUriString = null;

            try {
                cursor = cr.query(uri, new String[]{"display_name", "photo_uri"}, (String)null, (String[])null, (String)null);
                if (cursor != null && cursor.moveToFirst()) {
                    name = cursor.getString(0);
                    photoUriString = cursor.getString(1);
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }

            }

            if (name == null) {
                name = getFormattedNumber(context, number);
            }

            if (name == null) {
                name = "context.getString(string.unknown)";
            }

            return TextUtils.isEmpty(photoUriString) ? new Pair(name, (Object)null) : new Pair(name, Uri.parse(photoUriString));
        }
    }

   /* public static String callStateToUiString(Context context, int state) {
        Resources res = context.getResources();
        switch(state) {
            case 0:
            case 9:
                return res.getString(string.call_state_connecting);
            case 1:
            case 8:
                return res.getString(string.call_state_dialing);
            case 2:
                return res.getString(string.call_state_call_ringing);
            case 3:
                return res.getString(string.call_state_hold);
            case 4:
                return res.getString(string.call_state_call_active);
            case 5:
            case 6:
            default:
                throw new IllegalStateException("Unknown Call State: " + state);
            case 7:
                return res.getString(string.call_state_call_ended);
            case 10:
                return res.getString(string.call_state_call_ending);
        }
    }

    public static boolean isNetworkAvailable(Context context) {
        TelephonyManager tm = (TelephonyManager)context.getSystemService("phone");
        return tm.getNetworkType() != 0 && tm.getSimState() == 5;
    }

    public static boolean isAirplaneModeOn(Context context) {
        return System.getInt(context.getContentResolver(), "airplane_mode_on", 0) != 0;
    }

    public static void setContactBitmapAsync(Context context, ImageView icon, @Nullable Contact contact, @Nullable String fallbackDisplayName) {
        Uri avatarUri = contact != null ? contact.getAvatarUri() : null;
        String displayName = contact != null ? contact.getDisplayName() : fallbackDisplayName;
        setContactBitmapAsync(context, icon, avatarUri, displayName);
    }
*/
    public static void setContactBitmapAsync(Context context, ImageView icon, Uri avatarUri, String displayName) {
        LetterTileDrawable letterTileDrawable = createLetterTile(context, displayName);
        if (avatarUri != null) {
            Glide.with(context).load(avatarUri).apply((new RequestOptions()).centerCrop()/*.error(letterTileDrawable)*/).into(icon);
        } else {
           /* icon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            icon.setImageDrawable(letterTileDrawable);*/
        }
    }

    public static LetterTileDrawable createLetterTile(Context context, String displayName) {
        LetterTileDrawable letterTileDrawable = new LetterTileDrawable(context.getResources());
        letterTileDrawable.setContactDetails(displayName, displayName);
        return letterTileDrawable;
    }

   /* public static void setAsPrimaryPhoneNumber(Context context, com.android.car.telephony.common.PhoneNumber phoneNumber) {
        ContentValues values = new ContentValues(1);
        values.put("is_super_primary", 1);
        values.put("is_primary", 1);
        context.getContentResolver().update(ContentUris.withAppendedId(Data.CONTENT_URI, phoneNumber.getId()), values, (String)null, (String[])null);
    }

    public static int setAsFavoriteContact(Context context, Contact contact, boolean isFavorite) {
        if (contact.isStarred() == isFavorite) {
            return 0;
        } else {
            ContentValues values = new ContentValues(1);
            values.put("starred", isFavorite ? 1 : 0);
            String where = "_id = ?";
            String[] selectionArgs = new String[]{Long.toString(contact.getId())};
            return context.getContentResolver().update(Contacts.CONTENT_URI, values, where, selectionArgs);
        }
    }*/

    public static void markCallLogAsRead(Context context, String phoneNumberString) {
        if (context.checkSelfPermission("android.permission.WRITE_CALL_LOG") != 0) {
            Log.w("CD.TelecomUtils", "Missing WRITE_CALL_LOG permission; not marking missed calls as read.");
        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put("new", 0);
            contentValues.put("is_read", 1);
            List<String> selectionArgs = new ArrayList();
            StringBuilder where = new StringBuilder();
            where.append("new");
            where.append(" = 1 AND ");
            where.append("type");
            where.append(" = ?");
            selectionArgs.add(Integer.toString(3));
            if (!TextUtils.isEmpty(phoneNumberString)) {
                where.append(" AND ");
                where.append("number");
                where.append(" = ?");
                selectionArgs.add(phoneNumberString);
            }

            String[] selectionArgsArray = new String[0];

            try {
                context.getContentResolver().update(CallLog.Calls.CONTENT_URI, contentValues, where.toString(), (String[]) selectionArgs.toArray(selectionArgsArray));
            } catch (IllegalArgumentException var7) {
                Log.e("CD.TelecomUtils", "markCallLogAsRead failed", var7);
            }

        }
    }

    private static Uri makeResourceUri(Context context, int resourceId) {
        return (new Uri.Builder()).scheme("android.resource").encodedAuthority(context.getPackageName()).appendEncodedPath(String.valueOf(resourceId)).build();
    }

}
