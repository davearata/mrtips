package mrtips;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

public class TipsDisplayer implements DialogInterface.OnClickListener {
    private static final String PREFERENCE_MRTIPS = "mrtips";

    private String tipId = "";

    private CheckBox checkBox;
    private static ArrayList<String> idList;

    private String checkBoxDialogRes;

    private static TipsDisplayer instance;
    private Context context;

    private TipDismissedListener tipDismissedListener;

    private TipsDisplayer() {
        super();

    }

    public static TipsDisplayer getInstanceOf() {
        if (instance == null) {
            instance = new TipsDisplayer();
            idList = new ArrayList<String>();
        }

        return instance;
    }

    public void setIdArrays(String[] idArray) {
        Collections.addAll(idList, idArray);
    }

    public void showTipsDialog(Context mContext) {
        this.context = mContext;
        //clear this out so we don't show the same tipsDialog Twice
        tipId = "";
        if (this.context instanceof TipDismissedListener) {
            tipDismissedListener = (TipDismissedListener) this.context;
        }
        for (String currentId : idList) {
            if (!isHidden(currentId)) {
                tipId = currentId;
                break;
            }
        }

        Log.d("TipsDisplayer.showTipsDialog()", "currentID = " + tipId);

        if (!tipId.equals("")) {
            String[] myTipItems = context.getResources().getStringArray(
                    context.getResources().getIdentifier(tipId, "array", context.getPackageName()));

            String titleDialogRes = myTipItems[0];
            String imgDialog = myTipItems[1];
            String textDialogRes = myTipItems[2];
            if (myTipItems.length >= 4) {
                checkBoxDialogRes = myTipItems[3];
            }

            // LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            // View layout = inflater.inflate(R.layout.tip_layout, null);
            // layout.findViewById(R.id.layout_root);

            LinearLayout layout = new LinearLayout(context);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

            ScrollView mScrollView = new ScrollView(context);

            // ImageView image = (ImageView) layout.findViewById(R.id.tip_image);
            ImageView image = new ImageView(context);
            image.setImageResource(context.getResources().getIdentifier("drawable/" + imgDialog, null,
                    context.getPackageName()));
            if (!titleDialogRes.equals("")) {
                image.setPadding(0, 0, 0, 0);
            } else {
                image.setPadding(0, 0, 0, 0);
            }
            // Gravity imageGravity = new Gravity();
            // imageGravity.apply(Gravity.CENTER, w, h, container, outRect);
            layout.addView(image);

            // TextView text = (TextView) layout.findViewById(R.id.tip_text);
            TextView text = new TextView(context);
            String textDialogString = context.getString(context.getResources().getIdentifier("string/" + textDialogRes,
                    null, context.getPackageName()));
            text.setText(textDialogString);
            text.setPadding(10, 10, 10, 10);
            layout.addView(text);

            // checkBox = (CheckBox) layout.findViewById(R.id.tip_checkbox);
            if (checkBoxDialogRes != null && checkBoxDialogRes.length() > 0) {
                String checkBoxDialogString = context.getString(context.getResources().getIdentifier(
                        "string/" + checkBoxDialogRes, null, context.getPackageName()));
                checkBox = new CheckBox(context);
                checkBox.setText(checkBoxDialogString);
                layout.addView(checkBox);
            }

            mScrollView.addView(layout);

            Builder tipDialogBuilder = new AlertDialog.Builder(context);

            if (!titleDialogRes.equals("")) {
                String titleDialogString = context.getString(context.getResources().getIdentifier(
                        "string/" + titleDialogRes, null, context.getPackageName()));
                tipDialogBuilder.setTitle(titleDialogString);
            }

            // tipDialogBuilder.setIcon(context.getResources().getIdentifier("drawable/"+imgDialog, null,
            // context.getPackageName()));
            tipDialogBuilder.setView(mScrollView);
            tipDialogBuilder.setCancelable(false);
            tipDialogBuilder.setPositiveButton("OK", this);

            tipDialogBuilder.create().show();
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
        case Dialog.BUTTON_POSITIVE:
            if (checkBox == null || (checkBox.isChecked())) {
                Log.i("TipsDisplayer.onClick()", "checked !");
                setHiddenId(tipId);
            }
            dialog.dismiss();
            if (tipDismissedListener != null) {
                tipDismissedListener.onTipDismissed(tipId);
            }
            break;
        }
    }

    public Boolean isHidden(String id) {
        SharedPreferences mPreferences = context.getSharedPreferences(PREFERENCE_MRTIPS, Context.MODE_PRIVATE);
        return mPreferences.getBoolean(id, false);
    }

    public void setHiddenId(String id) {
        SharedPreferences mPreferences = context.getSharedPreferences(PREFERENCE_MRTIPS, Context.MODE_PRIVATE);
        Editor editor = mPreferences.edit();
        editor.putBoolean(id, true);
        editor.commit();
    }
}
