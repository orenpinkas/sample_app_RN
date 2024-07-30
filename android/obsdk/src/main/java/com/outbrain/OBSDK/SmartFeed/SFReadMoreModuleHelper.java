package com.outbrain.OBSDK.SmartFeed;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.outbrain.OBSDK.R;
import com.outbrain.OBSDK.SmartFeed.Theme.SFThemeImpl;
import com.outbrain.OBSDK.SmartFeed.viewholders.OutbrainReadMoreItemViewHolder;

import java.lang.ref.WeakReference;
import java.util.HashMap;


/**
 * Helper for binding and animating collapsible items for Read More module.
 * We are collapsing publisher items from publisherStartItemPosition to the SF items.
 * When pressing on "read more" button, we are expand the collapsed items one by one with animation.
 */
public class SFReadMoreModuleHelper {

    private final String LOG_TAG = "SFReadMoreModuleHelper";

    private static final double ANIMATION_TIME = 1.8;

    private String readMoreText = "Read More";
    private final WeakReference<RecyclerView> recyclerViewReference;
    private int publisherStartItemPosition = 0; // Default publisher start item position is 0
    private int publisherStartItemBottomOffsetPx = 0; // Default is 0 PX
    private int gradientViewHeight = 400; // Default gradient view height is 400
    private int readMoreItemPosition = -1;
    private int readMoreItemOriginalHeight = 0;
    private final HashMap<Integer, Integer> publisherItemPositionToOriginalHeight = new HashMap<>();
    private boolean animationInProgress = false;
    private boolean animationFinished = false;

    public SFReadMoreModuleHelper(WeakReference<RecyclerView> recyclerViewReference) {
        this.recyclerViewReference = recyclerViewReference;
    }

    public void setPublisherStartItemPosition(int publisherStartItemPosition) {
        this.publisherStartItemPosition = publisherStartItemPosition;
    }

    public void setPublisherStartItemBottomOffsetPx(int publisherStartItemBottomOffsetPx) {
        this.publisherStartItemBottomOffsetPx = publisherStartItemBottomOffsetPx;
    }

    public void setReadMoreItemPosition(int readMoreItemPosition) {
        this.readMoreItemPosition = readMoreItemPosition;
    }

    public void setGradientViewHeight(int gradientViewHeight) {
        this.gradientViewHeight = gradientViewHeight;
    }

    public void setReadMoreText(String readMoreText) {
        this.readMoreText = readMoreText;

        if (readMoreItemPosition != -1 && recyclerViewReference.get() != null) {
            RecyclerView.Adapter adapter = recyclerViewReference.get().getAdapter();
            if (adapter != null) {
                adapter.notifyItemChanged(readMoreItemPosition);
            }
        }
    }

    private int getMeasuredHeightForViewHolder(RecyclerView.ViewHolder holder) {
        int recyclerViewWidth = recyclerViewReference.get().getWidth();

        holder.itemView.measure(View.MeasureSpec.makeMeasureSpec(recyclerViewWidth, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        return holder.itemView.getMeasuredHeight();
    }

    private void updateViewHolderHeight(RecyclerView.ViewHolder holder, int height) {
        ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
        layoutParams.height = height;
        holder.itemView.requestLayout();
    }

    public void onBindPublisherViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position < publisherStartItemPosition) {
            return;
        }

        // adding gradient view
        if (holder.itemView instanceof LinearLayout && ((LinearLayout) holder.itemView).getOrientation() == LinearLayout.VERTICAL) {
            if (holder.itemView.findViewById(R.id.sf_read_more_gradient) == null) {
                LinearLayout linearLayout = (LinearLayout) holder.itemView;
                linearLayout.addView(new SFReadMoreModuleGradientView(holder.itemView.getContext()));
            }
        } else {
            Log.e(LOG_TAG, "each collapsible item should be a vertical LinearLayout");
        }

        // gradient background
        View gradient = holder.itemView.findViewById(R.id.sf_read_more_gradient);
        gradient.setBackgroundResource(SFThemeImpl.getInstance().getReadMoreModuleGradientResourceId());

        Integer originalItemHeight = publisherItemPositionToOriginalHeight.get(position);
        if (originalItemHeight == null) {
            originalItemHeight = getMeasuredHeightForViewHolder(holder);
            publisherItemPositionToOriginalHeight.put(position, originalItemHeight);
        }

        if (!animationInProgress) {
            if (position == publisherStartItemPosition) {
                int newHeight = originalItemHeight - publisherStartItemBottomOffsetPx;
                updateViewHolderHeight(holder, newHeight);
            } else {
                updateViewHolderHeight(holder, 0);
            }
            updateGradientHeight(holder, gradientViewHeight);
        } else if (animationFinished) {
            updateGradientHeight(holder, 0);
            updateViewHolderHeight(holder, originalItemHeight);
        }
    }

    private void updateGradientHeight(RecyclerView.ViewHolder holder, int height) {
        View gradient = holder.itemView.findViewById(R.id.sf_read_more_gradient);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) gradient.getLayoutParams();
        params.height = height;
        params.topMargin = -height;
        gradient.requestLayout();
    }

    public void onBindOutbrainReadMoreItem(OutbrainReadMoreItemViewHolder holder) {
        if (readMoreItemOriginalHeight == 0) {
            readMoreItemOriginalHeight = getMeasuredHeightForViewHolder(holder);
        }

        int height = animationFinished ? 0 : readMoreItemOriginalHeight;
        updateViewHolderHeight(holder, height);

        TextView readMoreButton = holder.itemView.findViewById(R.id.read_more_button);

        readMoreButton.setText(readMoreText);

        readMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!animationFinished && !animationInProgress) {
                    startExpandAllItemsAnimation();
                }
            }
        });
    }

    private void hideReadMoreAnimation(final RecyclerView.ViewHolder holder) {
        ValueAnimator animator = ValueAnimator.ofInt(readMoreItemOriginalHeight, 0);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(200);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int height = (int) valueAnimator.getAnimatedValue();
                updateViewHolderHeight(holder, height);
            }
        });
        animator.start();
    }

    private void hideReadMoreButton() {
        final RecyclerView.ViewHolder holder = recyclerViewReference.get().findViewHolderForAdapterPosition(readMoreItemPosition);

        if (holder == null) {
            RecyclerView.Adapter adapter = recyclerViewReference.get().getAdapter();
            if (adapter != null && readMoreItemPosition != -1) {
                adapter.notifyItemChanged(readMoreItemPosition);
            }
        } else {
            this.hideReadMoreAnimation(holder);
        }
    }

    private void startExpandAllItemsAnimation() {
        animationInProgress = true;
        expandItemAnimationForPosition(publisherStartItemPosition);
    }

    private void expandItemAnimationForPosition(final int position) {
        RecyclerView recyclerView = recyclerViewReference.get();
        if (recyclerView == null) {
            Log.e(LOG_TAG, "expandItemAnimationForPosition - error getting recyclerView");
            return;
        }

        RecyclerView.Adapter adapter = recyclerView.getAdapter();
        if (adapter == null) {
            Log.e(LOG_TAG, "expandItemAnimationForPosition - error getting recyclerView adapter");
            return;
        }

        final Integer originalHeight = publisherItemPositionToOriginalHeight.get(position);
        if (originalHeight == null) {
            // no more items to expand - expand animation finished
            animationFinished = true;
            hideReadMoreButton();
            return;
        }

        final RecyclerView.ViewHolder holder = recyclerView.findViewHolderForAdapterPosition(position);

        if (holder == null) { // holder is not in the memory
            // notify item at position in order to expand it without animation
            adapter.notifyItemChanged(position);
            // move to the next position
            expandItemAnimationForPosition(position + 1);
            return;
        }

        // animation
        final int prevHeight = position == publisherStartItemPosition ? (originalHeight - publisherStartItemBottomOffsetPx) : 0;
        ValueAnimator animator = ValueAnimator.ofInt(prevHeight, originalHeight);
        animator.setInterpolator(new LinearInterpolator());
        // calculate and set the duration - keep the same speed for all items
        int duration = ((int) ((originalHeight - prevHeight) / ANIMATION_TIME));
        animator.setDuration(duration);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int height = (int) valueAnimator.getAnimatedValue();
                updateViewHolderHeight(holder, height);

                // hide gradient with animation
                if (originalHeight - height <= gradientViewHeight) {
                    updateGradientHeight(holder, originalHeight - height);
                }
            }
        });

        animator.start();

        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animator) {
                // move to the next position
                expandItemAnimationForPosition(position + 1);
            }

            @Override
            public void onAnimationStart(Animator animator) {}

            @Override
            public void onAnimationCancel(Animator animator) {}

            @Override
            public void onAnimationRepeat(Animator animator) {}
        });
    }
}
