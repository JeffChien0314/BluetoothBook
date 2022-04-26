package com.ev.bluetooth.phonebook.common;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import com.ev.bluetooth.phonebook.R;

public class LetterTileDrawable extends Drawable {
    private static int[] sColors;
    private static int sDefaultColor;
    private static int sTileFontColor;
    private static float sLetterToTileRatio;
    private static Drawable sDefaultPersonAvatar;
    private static Drawable sDefaultBusinessAvatar;
    private static Drawable sDefaultVoicemailAvatar;
    private static final Paint sPaint = new Paint();
    private static final Rect sRect = new Rect();
    private static final char[] sFirstChar = new char[1];
    public static final int TYPE_PERSON = 1;
    public static final int TYPE_BUSINESS = 2;
    public static final int TYPE_VOICEMAIL = 3;
    public static final int TYPE_DEFAULT = 1;
    private final Paint mPaint = new Paint();
    private String mDisplayName;
    private int mColor;
    private int mContactType = 1;
    private float mScale = 1.0F;
    private float mOffset = 0.0F;
    private boolean mIsCircle = true;

    public LetterTileDrawable(Resources res) {
        this.mPaint.setFilterBitmap(true);
        this.mPaint.setDither(true);
        this.setScale(0.7F);
        if (sColors == null) {
            sDefaultColor = res.getColor(R.color.letter_tile_default_color);
            TypedArray ta = res.obtainTypedArray(R.array.letter_tile_colors);
            if (ta.length() == 0) {
                sColors = new int[]{sDefaultColor};
            } else {
                sColors = new int[ta.length()];

                for (int i = ta.length() - 1; i >= 0; --i) {
                    sColors[i] = ta.getColor(i, sDefaultColor);
                }

                ta.recycle();
            }

            sTileFontColor = res.getColor(R.color.letter_tile_font_color);
            sLetterToTileRatio = res.getFraction(R.fraction.letter_to_tile_ratio, 1, 1);
            sDefaultPersonAvatar = res.getDrawable(R.drawable.ic_person, (Theme) null);
            sDefaultBusinessAvatar = res.getDrawable(R.drawable.ic_person, (Theme) null);
            sDefaultVoicemailAvatar = res.getDrawable(R.drawable.ic_person, (Theme) null);
            sPaint.setTypeface(Typeface.create("sans-serif-light", 0));
            sPaint.setTextAlign(Align.CENTER);
            sPaint.setAntiAlias(true);
        }

    }

    public void draw(Canvas canvas) {
        Rect bounds = this.getBounds();
        if (this.isVisible() && !bounds.isEmpty()) {
            this.drawLetterTile(canvas);
        }
    }

    private void drawDrawableOnCanvas(Drawable drawable, Canvas canvas) {
        Rect destRect = this.copyBounds();
        int halfLength = (int) (this.mScale * (float) Math.min(destRect.width(), destRect.height()) / 2.0F);
        destRect.set(destRect.centerX() - halfLength, (int) ((float) (destRect.centerY() - halfLength) + this.mOffset * (float) destRect.height()), destRect.centerX() + halfLength, (int) ((float) (destRect.centerY() + halfLength) + this.mOffset * (float) destRect.height()));
        drawable.setAlpha(this.mPaint.getAlpha());
        drawable.setColorFilter(sTileFontColor, Mode.SRC_IN);
        drawable.setBounds(destRect);
        drawable.draw(canvas);
    }

    private void drawLetterTile(Canvas canvas) {
        sPaint.setColor(this.mColor);
        sPaint.setAlpha(this.mPaint.getAlpha());
        Rect bounds = this.getBounds();
        int minDimension = Math.min(bounds.width(), bounds.height());
        if (this.mIsCircle) {
            canvas.drawCircle((float) bounds.centerX(), (float) bounds.centerY(), (float) (minDimension / 2), sPaint);
        } else {
            canvas.drawRect(bounds, sPaint);
        }

        if (!TextUtils.isEmpty(this.mDisplayName) && isEnglishLetter(this.mDisplayName.charAt(0))) {
            sFirstChar[0] = Character.toUpperCase(this.mDisplayName.charAt(0));
            sPaint.setTextSize(this.mScale * sLetterToTileRatio * (float) minDimension);
            sPaint.getTextBounds(sFirstChar, 0, 1, sRect);
            sPaint.setColor(sTileFontColor);
            canvas.drawText(sFirstChar, 0, 1, (float) bounds.centerX(), (float) bounds.centerY() + this.mOffset * (float) bounds.height() + (float) (sRect.height() / 2), sPaint);
        } else {
            Drawable drawable = getDrawablepForContactType(this.mContactType);
            this.drawDrawableOnCanvas(drawable, canvas);
        }

    }

    public int getColor() {
        return this.mColor;
    }

    private int pickColor(String identifier) {
        if (!TextUtils.isEmpty(identifier) && this.mContactType != 3) {
            int color = Math.abs(identifier.hashCode()) % sColors.length;
            return sColors[color];
        } else {
            return sDefaultColor;
        }
    }

    private static Drawable getDrawablepForContactType(int contactType) {
        switch (contactType) {
            case 1:
                return sDefaultPersonAvatar;
            case 2:
                return sDefaultBusinessAvatar;
            case 3:
                return sDefaultVoicemailAvatar;
            default:
                return sDefaultPersonAvatar;
        }
    }

    private static boolean isEnglishLetter(char c) {
        return 'A' <= c && c <= 'Z' || 'a' <= c && c <= 'z';
    }

    public void setAlpha(int alpha) {
        this.mPaint.setAlpha(alpha);
    }

    public void setColorFilter(ColorFilter cf) {
        this.mPaint.setColorFilter(cf);
    }

    public int getOpacity() {
        return -1;
    }

    public void setScale(float scale) {
        this.mScale = scale;
    }

    public void setOffset(float offset) {
        this.mOffset = offset;
    }

    public void setContactDetails(String displayName, String identifier) {
        this.mDisplayName = displayName;
        this.mColor = this.pickColor(identifier);
    }

    public void setContactType(int contactType) {
        this.mContactType = contactType;
    }

    public void setIsCircular(boolean isCircle) {
        this.mIsCircle = isCircle;
    }

    public Bitmap toBitmap(int size) {
        Bitmap largeIcon = Bitmap.createBitmap(size, size, Config.ARGB_8888);
        Canvas canvas = new Canvas(largeIcon);
        Rect bounds = this.getBounds();
        this.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        this.draw(canvas);
        this.setBounds(bounds);
        return largeIcon;
    }
}
