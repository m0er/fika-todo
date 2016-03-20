/*
 * Copyright (C) 2015 Dift.co
 * http://dift.co
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package co.dift.ui;

import android.animation.Animator;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import todo.fika.fikatodo.util.Logger;

public class SwipeToAction {
    private static final int SWIPE_ANIMATION_DURATION = 300;
    private static final int RESET_ANIMATION_DURATION = 500;

    /**
     * 스와이프 시작 할 때까지의 이동거리.
     */
    private static final int REVEAL_THRESHOLD = 100;

    /**
     * 스와이프를 어느 정도 했을 때 전체 스와이프를 시킬지 여부 결정.
     */
    private static final int SWIPE_THRESHOLD_WIDTH_RATIO = 5;

    private final Logger logger = Logger.Factory.getLogger(getClass());

    private RecyclerView recyclerView;
    private SwipeListener swipeListener;
    private View touchedView;
    private ViewHolder touchedViewHolder;
    private View frontView;
    private View revealLeftView;
    private View revealRightView;

    private float frontViewX;
    private float frontViewW;
    private float frontViewLastX;

    private float downY;
    private float downX;
    private float upX;
    private float upY;

    private long downTime;
    private long upTime;

    private Integer revealThreshold = REVEAL_THRESHOLD;
    private Integer resetAnimationDuration = RESET_ANIMATION_DURATION;
    private Integer swipeThresholdWidthRatio = SWIPE_THRESHOLD_WIDTH_RATIO;

    /**
     * 최대 스와이프 되는 거리.
     */
    private Integer maxSwipeXPosition;

    private Set<View> runningAnimationsOn = new HashSet<>();
    private Queue<Integer> swipeQueue = new LinkedList<>();

    private GestureDetectorCompat gestureDetector;


    /** Constructor **/
    public SwipeToAction(RecyclerView recyclerView, SwipeListener swipeListener) {
        this.recyclerView = recyclerView;
        this.swipeListener = swipeListener;

        init();
    }


    /** Private methods **/
    private void init() {
        gestureDetector = new GestureDetectorCompat(recyclerView.getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public void onLongPress(MotionEvent e) {
                logger.d("onLongPress called!");
                if (touchedViewHolder != null) {
                    swipeListener.onLongClick(touchedViewHolder.getItemData());
                }
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                logger.d("onSingleTapUp called!");
                if (touchedViewHolder != null) {
                    swipeListener.onClick(touchedViewHolder.getItemData());
                    return true;
                }
                return super.onSingleTapUp(e);
            }
        });

        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {

            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent ev) {
                gestureDetector.onTouchEvent(ev);

                switch (ev.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN: {
                        // starting point
                        downX = ev.getX();
                        downY = ev.getY();

                        downTime = new Date().getTime();

                        // which item are we touching
                        resolveItem(downX, downY);

                        break;
                    }

                    case MotionEvent.ACTION_UP: {
                        upX = ev.getX();
                        upY = ev.getY();
                        upTime = new Date().getTime();

                        resolveState();
                        break;
                    }

                    case MotionEvent.ACTION_MOVE: {
                        final float x = ev.getX();
                        final float dx = x - downX;

                        if (!shouldMove(dx)) break;

                        // current position. moving only over x-axis
                        frontViewLastX = frontViewX + dx + (dx > 0 ? -getRevealThreshold() : getRevealThreshold());
                        logger.d("dx: %f, frontViewLastX: %f", dx, frontViewLastX);
                        if (maxSwipeXPosition != null) {
                            if (frontViewLastX > 0 && frontViewLastX > maxSwipeXPosition) {
                                frontViewLastX = maxSwipeXPosition;
                            } else if (frontViewLastX <= -maxSwipeXPosition) {
                                frontViewLastX = -maxSwipeXPosition;
                            }
                        }
                        frontView.setX(frontViewLastX);

                        if (frontViewLastX > 0) {
                            revealRight();
                        } else {
                            revealLeft();
                        }

                        break;
                    }

                    case MotionEvent.ACTION_CANCEL: {
                        resolveState();

                        break;
                    }
                }

                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
                logger.d("onTouchEvent called");
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
                logger.d("onRequestDisallowInterceptTouchEvent called");
            }
        });
    }

    private void resolveItem(float x, float y) {
        touchedView = recyclerView.findChildViewUnder(x, y);
        if (touchedView == null) {
            //no child under
            frontView = null;
            return;
        }

        if (touchedView.getAnimation() == null && runningAnimationsOn.contains(touchedView)) {
            runningAnimationsOn.remove(touchedView);
        }

        // check if the view is being animated. in that case do not allow to move it
        if (runningAnimationsOn.contains(touchedView)) {
            frontView = null;
            return;
        }

        RecyclerView.ViewHolder childViewHolder = recyclerView.getChildViewHolder(touchedView);
        if (childViewHolder instanceof ViewHolder) {
            initViewForItem((ViewHolder) childViewHolder);
        } else {
            touchedViewHolder = null;
            frontView = null;
        }
    }

    private void resolveItem(int adapterPosition) {
        initViewForItem((ViewHolder) recyclerView.findViewHolderForAdapterPosition(adapterPosition));
    }

    private void initViewForItem(ViewHolder viewHolder) {
        touchedViewHolder = viewHolder;
        frontView = viewHolder.getFront();
        revealLeftView = viewHolder.getRevealLeft();
        revealRightView = viewHolder.getRevealRight();
        frontViewX = frontView.getX();
        frontViewW = frontView.getWidth();
    }

    private boolean shouldMove(float dx) {
        if (frontView == null) {
            return false;
        }

        if (dx > 0) {
            return revealRightView != null && Math.abs(dx) > getRevealThreshold();
        } else {
            return revealLeftView != null && Math.abs(dx) > getRevealThreshold();
        }
    }

    private void clear() {
        frontViewX = 0;
        frontViewW = 0;
        frontViewLastX = 0;
        downX = 0;
        downY = 0;
        upX = 0;
        upY = 0;
        downTime = 0;
        upTime = 0;
    }

    private void checkQueue() {
        // workaround in case a swipe call while dragging
        Integer next = swipeQueue.poll();
        if (next != null) {
            int pos = Math.abs(next) - 1;
            if (next < 0) {
                swipeLeft(pos);
            } else {
                swipeRight(pos);
            }
        }
    }

    private void resetPosition() {
        if (frontView == null) {
            return;
        }

        final View animated = touchedView;
        frontView.animate()
                .setDuration(getResetAnimationDuration())
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        runningAnimationsOn.add(animated);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        runningAnimationsOn.remove(animated);
                        swipeListener.completeReset();
                        checkQueue();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        runningAnimationsOn.remove(animated);
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                        runningAnimationsOn.add(animated);
                    }
                })
                .x(frontViewX);
    }

    private void resolveState() {
        if (frontView == null) {
            return;
        }

        if (frontViewLastX > frontViewX + frontViewW / getSwipeThresholdWidthRatio()) {
            swipeRight();
        } else if (frontViewLastX < frontViewX - frontViewW / getSwipeThresholdWidthRatio()) {
            swipeLeft();
        } else {
//            float diffX = Math.abs(downX - upX);
//            float diffY = Math.abs(downY - upY);

//            if (diffX <= 5 && diffY <= 5) {
//                int pressTime = (int) (upTime - downTime);
//                if  (pressTime > LONG_PRESS_TIME) {
//                    swipeListener.onLongClick(touchedViewHolder.getItemData());
//                } else {
//                    swipeListener.onClick(touchedViewHolder.getItemData());
//                }
//            }

            resetPosition();
        }

        clear();
    }

    private void swipeRight() {
        if (frontView == null) {
            return;
        }

        final View animated = touchedView;
        frontView.animate()
                .setDuration(SWIPE_ANIMATION_DURATION)
                .setInterpolator(new AccelerateInterpolator())
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        runningAnimationsOn.add(animated);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        runningAnimationsOn.remove(animated);
                        if (swipeListener.swipeRight(touchedViewHolder.getItemData())) {
                            resetPosition();
                        } else {
                            checkQueue();
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        runningAnimationsOn.remove(animated);
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                        runningAnimationsOn.add(animated);
                    }
                }).x(frontViewX + frontViewW);
    }

    private void swipeLeft() {
        if (frontView == null) {
            return;
        }

        final View animated = touchedView;
        frontView.animate()
                .setDuration(SWIPE_ANIMATION_DURATION)
                .setInterpolator(new DecelerateInterpolator())
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        runningAnimationsOn.add(animated);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        runningAnimationsOn.remove(animated);
                        if (swipeListener.swipeLeft(touchedViewHolder.getItemData())) {
                            resetPosition();
                        } else {
                            checkQueue();
                        }
                    }

                    @Override
                    public void onAnimationCancel (Animator animation){
                        runningAnimationsOn.remove(animated);
                    }

                    @Override
                    public void onAnimationRepeat (Animator animation){
                        runningAnimationsOn.add(animated);
                    }
                }).x(frontViewX - frontViewW);

    }

    private void revealRight() {
        if (revealLeftView != null) {
            revealLeftView.setVisibility(View.GONE);
        }

        if (revealRightView != null) {
            revealRightView.setVisibility(View.VISIBLE);
        }
    }

    private void revealLeft() {
        if (revealRightView != null) {
            revealRightView.setVisibility(View.GONE);
        }

        if (revealLeftView != null) {
            revealLeftView.setVisibility(View.VISIBLE);
        }
    }


    /** Exposed methods **/

    public void swipeLeft(int position) {
        // workaround in case a swipe call while dragging
        if (downTime != 0) {
            swipeQueue.add((position + 1) * -1); //use negative to express direction
            return;
        }
        resolveItem(position);
        revealLeft();
        swipeLeft();
    }

    public void swipeRight(int position) {
        // workaround in case a swipe call while dragging
        if (downTime != 0) {
            swipeQueue.add(position + 1);
            return;
        }
        resolveItem(position);
        revealRight();
        swipeRight();
    }

    public void setRevealThreshold(Integer revealThreshold) {
        this.revealThreshold = revealThreshold;
    }

    public int getRevealThreshold() {
        return revealThreshold;
    }

    public Integer getResetAnimationDuration() {
        return resetAnimationDuration;
    }

    public void setResetAnimationDuration(Integer resetAnimationDuration) {
        this.resetAnimationDuration = resetAnimationDuration;
    }

    public Integer getSwipeThresholdWidthRatio() {
        return swipeThresholdWidthRatio;
    }

    public void setSwipeThresholdWidthRatio(Integer swipeThresholdWidthRatio) {
        this.swipeThresholdWidthRatio = swipeThresholdWidthRatio;
    }

    public Integer getMaxSwipeXPosition() {
        return maxSwipeXPosition;
    }

    public void setMaxSwipeXPosition(Integer maxSwipeXPosition) {
        this.maxSwipeXPosition = maxSwipeXPosition;
    }

    /** Public interfaces & classes */

    public interface SwipeListener<T extends Object> {
        boolean swipeLeft(T itemData);
        boolean swipeRight(T itemData);
        void onClick(T itemData);
        void onLongClick(T itemData);
        void completeReset();
    }

    public static abstract class SimpleSwipeListener<T extends Object> implements SwipeListener<T> {
        @Override
        public boolean swipeLeft(T itemData) {
            return false;
        }

        @Override
        public boolean swipeRight(T itemData) {
            return false;
        }

        @Override
        public void onClick(T itemData) {
        }

        @Override
        public void onLongClick(T itemData) {
        }

        @Override
        public void completeReset() {
        }
    }

    public interface IViewHolder<T extends Object> {
        View getFront();
        View getRevealLeft();
        View getRevealRight();
        <T extends Object> T getItemData();
    }

    public static abstract class ViewHolder<T extends Object>
            extends RecyclerView.ViewHolder implements IViewHolder {

        public T data;
        public View front;
        public View revealLeft;
        public View revealRight;

        public ViewHolder(View v) {
            super(v);

            ViewGroup vg = (ViewGroup) v;
            front = vg.findViewWithTag("front");
            revealLeft = vg.findViewWithTag("reveal-left");
            revealRight = vg.findViewWithTag("reveal-right");

            int childCount = vg.getChildCount();
            if (front == null) {
                if (childCount < 1) {
                    throw new RuntimeException("You must provide a view with tag='front'");
                } else {
                    front = vg.getChildAt(childCount-1);
                }
            }

            if (revealLeft == null || revealRight == null) {
                if (childCount < 2) {
                    throw new RuntimeException("You must provide at least one reveal view.");
                } else {
                    // set next to last as revealLeft view only if no revealRight was found
                    if (revealLeft == null && revealRight == null) {
                        revealLeft = vg.getChildAt(childCount - 2);
                    }

                    // if there are enough children assume the revealRight
                    int i = childCount - 3;
                    if (revealRight == null && i > -1) {
                        revealRight = vg.getChildAt(i);
                    }
                }
            }


        }

        @Override
        public View getFront() {
            return front;
        }

        @Override
        public View getRevealLeft() {
            return revealLeft;
        }

        @Override
        public View getRevealRight() {
            return revealRight;
        }

        @Override
        public T getItemData() { return data; }
    }
}
