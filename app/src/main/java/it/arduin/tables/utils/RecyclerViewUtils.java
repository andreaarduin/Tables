package it.arduin.tables.utils;
/*
 * Copyright 2015, Randy Saborio & Tinbytes, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;

/**
 * Helper class for RecyclerView/Toolbar scroll listener. Original file :
 * https://github.com/rylexr/android-show-hide-toolbar/blob/master/app/src/main/java/com/tinbytes/samples/showhidetoolbar/util/RecyclerViewUtils.java
 * edited to use a drawable as shadow
 */
public final class RecyclerViewUtils {
    public static class ShowHideToolbarOnScrollingListener extends RecyclerView.OnScrollListener {
        public static final String SHOW_HIDE_TOOLBAR_LISTENER_STATE = "show-hide-toolbar-listener-state";

        // The elevation of the toolbar when content is scrolled behind
        private static final float TOOLBAR_ELEVATION = 14f;

        private Toolbar toolbar;
        private LinearLayout toolbarContainer;
        private View shadow;
        private State state;

        public ShowHideToolbarOnScrollingListener(Toolbar toolbar,View shadow) {
            this.toolbar = toolbar;
            this.shadow=shadow;
            this.state = new State();
        }

        public ShowHideToolbarOnScrollingListener(LinearLayout toolbarContainer) {
            this.toolbarContainer=toolbarContainer;
            this.shadow=toolbarContainer.getChildAt(1);
            this.toolbar= (Toolbar) toolbarContainer.getChildAt(0);
            this.state = new State();
        }

        private void toolbarSetElevation(float elevation) {
            if(elevation==0) hideShadow();
            else showShadow();
        }

        private void showShadow() {
            shadow.setVisibility(View.VISIBLE);
        }

        private void hideShadow() {
            shadow.setVisibility(View.INVISIBLE);
        }

        public void toolbarAnimateShow(final int verticalOffset) {
            toolbar.animate()
                    .translationY(0)
                    .setInterpolator(new LinearInterpolator())
                    .setDuration(120)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            toolbarSetElevation(verticalOffset == 0 ? 0 : TOOLBAR_ELEVATION);
                        }
                    });
            shadow.animate()
                    .translationY(0)
                    .setInterpolator(new LinearInterpolator())
                    .setDuration(120)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            toolbarSetElevation(verticalOffset == 0 ? 0 : TOOLBAR_ELEVATION);
                        }
                    });

        }

        private void toolbarAnimateHide() {
            toolbar.animate()
                    .translationY(-toolbar.getHeight())
                    .setInterpolator(new LinearInterpolator())
                    .setDuration(120)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            toolbarSetElevation(0);
                        }
                    });
            shadow.animate()
                    .translationY(-toolbar.getHeight())
                    .setInterpolator(new LinearInterpolator())
                    .setDuration(120)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            toolbarSetElevation(0);
                        }
                    });
        }

        @Override
        public final void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                if (state.scrollingOffset > 0) {
                    if (state.verticalOffset > toolbar.getHeight()) {
                        toolbarAnimateHide();
                    } else {
                        toolbarAnimateShow(state.verticalOffset);
                    }
                } else if (state.scrollingOffset < 0) {
                    if (toolbar.getTranslationY() < toolbar.getHeight() * -0.6 && state.verticalOffset > toolbar.getHeight()) {
                        toolbarAnimateHide();
                    } else {
                        toolbarAnimateShow(state.verticalOffset);
                    }
                }
            }
        }

        @Override
        public final void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            state.verticalOffset = recyclerView.computeVerticalScrollOffset();
            state.scrollingOffset = dy;
            int toolbarYOffset = (int) (dy - toolbar.getTranslationY());
            toolbar.animate().cancel();
            if (state.scrollingOffset > 0) {
                if (toolbarYOffset < toolbar.getHeight()) {
                    if (state.verticalOffset > toolbar.getHeight()) {
                        toolbarSetElevation(TOOLBAR_ELEVATION);
                    }
                    toolbar.setTranslationY(state.translationY = -toolbarYOffset);
                    shadow.setTranslationY(toolbar.getTranslationY());
                } else {
                    toolbarSetElevation(0);
                    toolbar.setTranslationY(state.translationY = -toolbar.getHeight());

                    shadow.setTranslationY(toolbar.getTranslationY());
                }
            } else if (state.scrollingOffset < 0) {
                if (toolbarYOffset < 0) {
                    if (state.verticalOffset <= 0) {
                        toolbarSetElevation(0);
                    }
                    toolbar.setTranslationY(state.translationY = 0);

                    shadow.setTranslationY(toolbar.getTranslationY());
                } else {
                    if (state.verticalOffset > toolbar.getHeight()) {
                        toolbarSetElevation(TOOLBAR_ELEVATION);
                    }
                    toolbar.setTranslationY(state.translationY = -toolbarYOffset);
                    shadow.setTranslationY(toolbar.getTranslationY());
                }
            }
            if(toolbar.getTranslationY()!=0) showShadow();
            else hideShadow();
        }

        public void onRestoreInstanceState(State state) {
            this.state.verticalOffset = state.verticalOffset;
            this.state.scrollingOffset = state.scrollingOffset;
            toolbar.setElevation(state.elevation);
            toolbar.setTranslationY(state.translationY);

        }

        public State onSaveInstanceState() {
            state.translationY = toolbar.getTranslationY();
            state.elevation = toolbar.getElevation();
            return state;
        }

        /**
         * Parcelable RecyclerView/Toolbar state for simpler saving/restoring its current state.
         */
        public static final class State implements Parcelable {
            public static Creator<State> CREATOR = new Creator<State>() {
                public State createFromParcel(Parcel parcel) {
                    return new State(parcel);
                }

                public State[] newArray(int size) {
                    return new State[size];
                }
            };

            // Keeps track of the overall vertical offset in the list
            private int verticalOffset;
            // Determines the scroll UP/DOWN offset
            private int scrollingOffset;
            // Toolbar values
            private float translationY;
            private float elevation;

            State() {
            }

            State(Parcel parcel) {
                this.verticalOffset = parcel.readInt();
                this.scrollingOffset = parcel.readInt();
                this.translationY = parcel.readFloat();
                this.elevation = parcel.readFloat();
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel parcel, int flags) {
                parcel.writeInt(verticalOffset);
                parcel.writeInt(scrollingOffset);
                parcel.writeFloat(translationY);
                parcel.writeFloat(elevation);
            }
        }
    }

    private RecyclerViewUtils() {
    }
}