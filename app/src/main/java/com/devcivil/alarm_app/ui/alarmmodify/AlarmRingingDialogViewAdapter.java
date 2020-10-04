package com.devcivil.alarm_app.ui.alarmmodify;

import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.devcivil.alarm_app.R;
import com.devcivil.alarm_app.alarmserver.model.RingType;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class AlarmRingingDialogViewAdapter extends RecyclerView.Adapter<AlarmRingingDialogViewAdapter.ViewHolder> {

    private List<MediaPlayer> mediaPlayers = new ArrayList<>();

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

        holder.player = MediaPlayer.create(holder.itemView.getContext(), ringType.getMusicRes());
        mediaPlayers.add(holder.player);
        final Boolean[] isPlay = {true};
        holder.btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlay[0]) {
                    holder.player.start();
                    holder.btnPlay.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_stop_white_24dp, 0);
                    holder.btnPlay.setText(R.string.stop);
                    holder.player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            holder.btnPlay.setText(R.string.play);
                            holder.btnPlay.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_play_arrow_white_24dp, 0);
                            isPlay[0] = true;
                        }
                    });
                    isPlay[0] = false;
                } else {
                    if (holder.player.isPlaying()) {
                        holder.player.pause();
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
                if (onClickRingtoneListener != null) {
                    onClickRingtoneListener.onClick(ringType);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return RingType.values().length;
    }


    @Override
    public void onViewDetachedFromWindow(@NonNull ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        if (holder.player != null) {
            if (holder.player.isPlaying()) {
                holder.player.release();
            }
        }
    }

    public void killAllAlarms() {
        for (MediaPlayer player :
                mediaPlayers) {
            if (player != null) {
                if (player.isPlaying()){
                    player.release();
                }
            }
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private MediaPlayer player;
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
