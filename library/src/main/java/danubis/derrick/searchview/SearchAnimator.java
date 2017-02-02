package danubis.derrick.searchview;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
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

        ObjectAnimator anim1 = ObjectAnimator.ofFloat(view, "elevation", 0f, 20f);
        ObjectAnimator anim2 = ObjectAnimator.ofFloat(view, "y", view.getY(), view.getY() - distance);

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
        set.playTogether(anim1, anim2);
        set.setDuration(duration);
        set.start();
    }

    static void fall(final View view, float distance, int duration) {

        view.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        ObjectAnimator anim1 = ObjectAnimator.ofFloat(view, "elevation", 20f, 0f);
        ObjectAnimator anim2 = ObjectAnimator.ofFloat(view, "y", view.getY(), view.getY() + distance);

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
        set.playTogether(anim1, anim2);
        set.setDuration(duration);
        set.start();
    }
}
