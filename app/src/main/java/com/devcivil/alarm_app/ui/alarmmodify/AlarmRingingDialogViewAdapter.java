package com.devcivil.alarm_app.ui.alarmmodify;

import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.devcivil.alarm_app.R;
import com.devcivil.alarm_app.alarmserver.model.RingType;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class AlarmRingingDialogViewAdapter extends RecyclerView.Adapter<AlarmRingingDialogViewAdapter.ViewHolder> {

    @Nullable
    private OnClickRingtoneListener onClickRingtoneListener;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_alarm_ringing, viewGroup, false);
        return new AlarmRingingDialogViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int i) {
        final RingType ringType = RingType.getById(i);

        if (ringType == null) return;

        holder.txtTitle.setText(holder.itemView.getResources().getTextArray(R.array.ringtone_types)[(int) ringType.getId()]);

        final MediaPlayer player = MediaPlayer.create(holder.itemView.getContext(), ringType.getMusicRes());
        final Boolean[] isPlay = {true};
        holder.btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlay[0]){
                    player.start();
                    holder.btnPlay.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_stop_white_24dp, 0);
                    holder.btnPlay.setText(R.string.stop);
                    isPlay[0] = false;
                }
                else {
                    if (player.isPlaying()){
                        player.pause();
                    }
                    holder.btnPlay.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_play_arrow_white_24dp, 0);
                    holder.btnPlay.setText(R.string.play);
                    isPlay[0] = true;
                }
            }
        });

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickRingtoneListener != null){
                    onClickRingtoneListener.onClick(ringType);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return RingType.values().length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private Button btnPlay;
        private TextView txtTitle;
        private ConstraintLayout layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.constraint_layout_item);
            btnPlay = itemView.findViewById(R.id.button_play);
            txtTitle = itemView.findViewById(R.id.text_view_title);
        }
    }

    public void setOnClickRingtoneListener(@Nullable OnClickRingtoneListener onClickRingtoneListener) {
        this.onClickRingtoneListener = onClickRingtoneListener;
    }

    public interface OnClickRingtoneListener {
        void onClick(RingType ringType);
    }
}
