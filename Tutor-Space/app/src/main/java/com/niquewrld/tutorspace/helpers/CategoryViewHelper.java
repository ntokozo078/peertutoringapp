package com.niquewrld.tutorspace.helpers;

import android.content.Context;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.niquewrld.tutorspace.R;
import com.niquewrld.tutorspace.models.Category;

public class CategoryViewHelper {

    public static LinearLayout createCategoryView(Context context, Category category) {
        // Main vertical layout for the category
        LinearLayout categoryLayout = new LinearLayout(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMarginStart(12);
        layoutParams.setMarginEnd(12);
        categoryLayout.setLayoutParams(layoutParams);
        categoryLayout.setOrientation(LinearLayout.VERTICAL);
        categoryLayout.setGravity(Gravity.CENTER);

        // Create icon (either TextView or ImageView)

            // Image icon
            ImageView iconImage = new ImageView(context);
            LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(
                    dpToPx(context, 30),
                    dpToPx(context, 30));
            iconImage.setLayoutParams(iconParams);
            iconImage.setImageResource(category.getIconResourceId());
            categoryLayout.addView(iconImage);


        // Create text label
        TextView nameText = new TextView(context);
        LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        nameParams.topMargin = dpToPx(context, 4);
        nameText.setLayoutParams(nameParams);
        nameText.setText(category.getName());
        nameText.setTextColor(context.getColor(android.R.color.black));
        nameText.setTextSize(12);
        categoryLayout.addView(nameText);

        return categoryLayout;
    }

    // Helper method to convert dp to pixels
    private static int dpToPx(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}
