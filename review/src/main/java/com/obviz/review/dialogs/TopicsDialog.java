package com.obviz.review.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import com.obviz.review.Constants;
import com.obviz.review.models.Topic;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by gaylor on 09/11/2015.
 * __FRIDAY_9112015__
 * Dialog to select some topics
 */
public class TopicsDialog extends DialogFragment {

    private List<Topic> mSelectedTopics = new LinkedList<>();
    private OnDismissListener mListener;

    public List<Topic> getSelectedTopics() {
        return mSelectedTopics;
    }

    public void addSelectedTopic(Topic t){
        mSelectedTopics.add(t);
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle states) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose your topics");


        if (getArguments() != null) {

            final List<Topic> topics = getArguments().getParcelableArrayList(Constants.STATE_TOPIC);
            Log.d("TOPICS Dialog onCreate ", "no Topics: "+topics.size());
            CharSequence[] titles = new CharSequence[topics.size()];
            boolean[] checkedItems= new boolean[topics.size()];
            for (int i = 0; i < titles.length; i++) {
                Topic t = topics.get(i);
                titles[i] = t.getTitle();
                if(mSelectedTopics.contains(t))
                    checkedItems[i]=true;
                else
                    checkedItems[i]=false;
            }

            builder.setMultiChoiceItems(titles, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int position, boolean isChecked) {
                    if (isChecked) {
                        mSelectedTopics.add(topics.get(position));
                    } else {
                        mSelectedTopics.remove(topics.get(position));
                    }
                }
            });



            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int i) {
                    mListener.dialogDismiss(mSelectedTopics);
                    dialog.dismiss();
                }
            });
        } else {

            Log.d("__DIALOG__", "Don't receive the list of topics");
        }

        AlertDialog dialog = builder.create();
        /*for (Topic t: mSelectedTopics){
            dialog.getListView().setItemChecked(mSelectedTopics.indexOf(t),true);
        }*/

        return dialog ;
    }

    public void setOnDismissListener(OnDismissListener dismissListener){
        mListener=dismissListener;
    }

    public interface OnDismissListener {
        void dialogDismiss(List<Topic> selectedItems);
    }
}
