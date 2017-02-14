package danubis.derrick.searchview;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;


class SearchAnimator {

    static void fadeIn(View view, int duration) {
        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.setDuration(duration);

        view.setAnimation(anim);
        view.setVisibility(View.VISIBLE);
    }

    static void fadeOut(View view, int duration) {
        Animation anim = new AlphaAnimation(1.0f, 0.0f);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.setDuration(duration);

        view.setAnimation(anim);
        view.setVisibility(View.GONE);
    }

    static void rise(final View view, float distance, int duration) {

        view.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        View containerView = view.findViewById(R.id.container);

        ObjectAnimator changeYPosition = ObjectAnimator.ofFloat(view, "y", view.getY(), view.getY() - distance);
        ObjectAnimator changeElevation = ObjectAnimator.ofFloat(containerView, "elevation", 0f, 10f);

        AnimatorSet set = new AnimatorSet();
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                view.setLayerType(View.LAYER_TYPE_NONE, null);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        set.playTogether(changeYPosition, changeElevation);
        set.setDuration(duration);
        set.start();
    }

    static void fall(final View view, float distance, int duration) {

        view.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        View containerView = view.findViewById(R.id.container);

        ObjectAnimator changeYPosition = ObjectAnimator.ofFloat(view, "y", view.getY(), view.getY() + distance);
        ObjectAnimator changeElevation = ObjectAnimator.ofFloat(containerView, "elevation", 10f, 0f);

        AnimatorSet set = new AnimatorSet();
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                view.setLayerType(View.LAYER_TYPE_NONE, null);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        set.playTogether(changeYPosition, changeElevation);
        set.setDuration(duration);
        set.start();
    }
}
