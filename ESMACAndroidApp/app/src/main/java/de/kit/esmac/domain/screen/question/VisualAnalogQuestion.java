package de.kit.esmac.domain.screen.question;

import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import de.kit.esmac.R;

public class VisualAnalogQuestion extends AbstractQuestion {
    private String minDescription, maxDescription;

    protected int pointer;
    private SeekBar seekBar;
    private TextView minDescriptionView;
    private TextView maxDescriptionView;
    protected int minBound;
    protected int maxBound;


    public void setMinBound(int minBound) {
        this.minBound = minBound;
    }

    public void setMaxBound(int maxBound) {
        this.maxBound = maxBound;
    }

    public void setPointer(int pointer) {
        this.pointer = pointer;
    }

    public void setMinDescription(String minDescription) {
        this.minDescription = minDescription;
    }

    public void setMaxDescription(String maxDescription) {
        this.maxDescription = maxDescription;
    }

    @Override
    public void drawQuestionUI(LayoutInflater inflater, final RelativeLayout relativeLayout) {
        super.drawQuestionUI(inflater, relativeLayout);
        seekBar = (SeekBar) inflater.inflate(
                R.layout.seeker, null);
        seekBar.setId(getId());
        seekBar.setProgress(getPointer());
        relativeLayout.addView(seekBar, getLayoutParameter(getLastId()));
        increaseId();
        minDescriptionView = new TextView(inflater.getContext());
        maxDescriptionView = new TextView(inflater.getContext());
        minDescriptionView.setText(minDescription);
        minDescriptionView.setId(getId());
        increaseId();
        maxDescriptionView.setText(maxDescription);
        maxDescriptionView.setId(getId());
        increaseId();
        relativeLayout.addView(minDescriptionView,
                getLayoutParameterForLeftDescription(seekBar.getId(), true));
        relativeLayout.addView(maxDescriptionView,
                getLayoutParameterForLeftDescription(seekBar.getId(), false));
        seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Not Used

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Not Used

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateSubmitButton(relativeLayout);
            }
        });

    }

    protected SeekBar getSeekBar() {
        return seekBar;
    }

    @Override
    protected void enableUi(boolean enabled) {
        super.enableUi(enabled);
        seekBar.setEnabled(enabled);
        maxDescriptionView.setEnabled(enabled);
        minDescriptionView.setEnabled(enabled);
    }

    @Override
    public boolean isAnswered() {
        return true;
    }

    @Override
    protected boolean isEnabled() {
        return super.isEnabled() && seekBar.isEnabled()
                && minDescriptionView.isEnabled() && maxDescriptionView.isEnabled();
    }

    @Override
    public String getAnswer() {
        if (seekBar != null) {
            return String.valueOf(getValue());
        } else {
            return "";
        }
    }

    protected int getValue() {
        int difference = maxBound - minBound;
        return Math.round(Math.round((difference * (new Double(seekBar.getProgress()) / 100)) + minBound));
    }

    protected int getPointer() {
        double differencePercentage = 100 / (maxBound - minBound);
        return Math.round(Math.round((pointer - minBound) * differencePercentage));

    }

}
